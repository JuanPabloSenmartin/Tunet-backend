package tunet;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import tunet.Util.JsonParser;
import tunet.model.*;

import java.util.*;

import static java.util.concurrent.TimeUnit.MINUTES;
import static spark.Spark.*;
import static tunet.Util.JsonParser.toJson;

public class Routes {


    /**
     * ROUTES
     **/


    public static final String REGISTER_ROUTE = "/register";
    public static final String LOGIN_ROUTE = "/login";


    private static TunetSystem system;

    public void create(TunetSystem system) {
        this.system = system;
        routes();
    }

    private void routes() {

        //fix CORS
        before((req, resp) -> {
            resp.header("Access-Control-Allow-Origin", "*");
            resp.header("Access-Control-Allow-Headers", "*");
            resp.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, OPTIONS");
        });
        options("/*", (req, resp) -> {
            resp.status(200);
            return "ok";
        });

        //REGISTER
        post(REGISTER_ROUTE, (req, res) -> {
            final RegistrationUserForm form = RegistrationUserForm.createFromJson(req.body());

            if (!form.isComplete()){
                res.status(408);
                res.body("Uncompleted form");
                return res.body();
            }

            User user = system.registerUser(form);
            if(user != null){
                res.status(201);
                res.body("user created");
            }
            else {
                res.status(409);
                res.body("user already exists");
            }
            return res.body();
        });

        //LOG IN
        post(LOGIN_ROUTE, (req, res) -> {
            final AuthRequest authReq = AuthRequest.createFromJson(req.body());
            authenticate(authReq)
                    .ifPresentOrElse(token -> {
                        if (authReq.isArtist()){
                            res.status(201);
                        }
                        else {
                            res.status(200);
                        }
                        res.body(toJson(Auth.create(token)));
                    }, () -> {
                        res.status(401);
                        res.body("");
                    });

            return res.body();
        });

        //LOG OUT
        authorizedDelete(LOGIN_ROUTE, (req, res) -> {
            getToken(req)
                    .ifPresentOrElse(token -> {
                        emailByToken.invalidate(token);
                        res.status(204);
                    }, () -> {
                        res.status(404);
                    });

            return "";
        });


        //VIEW PROFILE

        post("/viewOtherProfile", (req, res) -> {
            String mail = removeFirstandLast(req.body());
            Optional<User> user = system.findUserByEmail(mail);
            return getProfile(res, user);
        });


        //Gives current profile values

        post("/viewProfile", (req, res) -> {
            String token = removeFirstandLast(req.body());;
            String mail = emailByToken.getIfPresent(token);
            Optional<User> user = system.findUserByEmail(mail);
            return getProfile(res, user);
        });

        //PERSIST CHANGES OF PROFILE

        post("/editProfile", (req, res) -> {
            EditProfileForm editProfileForm = EditProfileForm.createFromJson(req.body());
            system.editProfile(editProfileForm);
            res.status(201);
            res.body("");
            return res.body();
        });


        //LIST OF USERS
        /*
        authorizedGet("/users", (req, res) -> {
            final List<User> users = system.listUsers();
            return JsonParser.toJson(users);
        });

        authorizedGet("/users", (req, res) -> getToken(req).map(JsonParser::toJson));
        authorizedGet("/users", (req, res) -> getToken(req).map(JsonParser::toJson));
        */
    }
    private static String removeFirstandLast(String str) {
        //in token there are extra "" at the first and last characters.
        //this function removes the extra ""
        StringBuilder sb = new StringBuilder(str);
        sb.deleteCharAt(str.length() - 1);
        sb.deleteCharAt(0);
        return sb.toString();
    }
    private Object getProfile(Response res, Optional<User> user) {
        if (user.isEmpty()){
            res.body("ERROR");
            res.status(404);
        }
        else{
            final Map<String, String> data = system.getProfileData(user.get());
            res.status(201);
            res.body(JsonParser.toJson(data));
        }
        return res.body();
    }


    private void authorizedGet(final String path, final Route route) {
        get(path, (request, response) -> authorize(route, request, response));
    }

    private void authorizedDelete(final String path, final Route route) {
        delete(path, (request, response) -> authorize(route, request, response));
    }

    private Object authorize(Route route, Request request, Response response) throws Exception {
        if (isAuthorized(request)) {
            return route.handle(request, response);
        } else {
            response.status(401);
            return "Unauthorized";
        }
    }

    private Optional<User> getUser(Request req) {
        return getToken(req)
                .map(emailByToken::getIfPresent)
                .flatMap(email -> system.findUserByEmail(email));
    }

    private final Cache<String, String> emailByToken = CacheBuilder.newBuilder()
            .expireAfterAccess(30, MINUTES)
            .build();

    private Optional<String> authenticate(AuthRequest req) {
        return system.findUserByEmail(req.getEmail()).flatMap(foundUser -> {
            if (system.validPassword(req.getPassword(), foundUser)) {
                final String token = UUID.randomUUID().toString();
                emailByToken.put(token, foundUser.getEmail());
                req.setIsArtist(foundUser.isArtist());
                return Optional.of(token);
            } else {
                return Optional.empty();
            }
        });
    }

    private boolean isAuthorized(Request request) {
        return getToken(request).map(this::isAuthenticated).orElse(false);
    }

    private static Optional<String> getToken(Request request) {
        return Optional.ofNullable(request.headers("Authorization"))
                .map(Routes::getTokenFromHeader);
    }

    private static String getTokenFromHeader(String authorizationHeader) {
        return authorizationHeader.replace("Bearer ", "");
    }

    private boolean isAuthenticated(String token) {
        return emailByToken.getIfPresent(token) != null;
    }

    private static Optional<User> getAuthenticatedUser(Request request) {
        final String email = request.session().attribute("email");
        return Optional.ofNullable(email).flatMap(system::findUserByEmail);
    }
}




