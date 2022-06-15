package tunet;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import spark.*;
import tunet.Util.JsonParser;
import tunet.model.*;


import java.io.*;
import java.util.*;

import static java.util.concurrent.TimeUnit.MINUTES;
import static spark.Spark.*;
import static tunet.Util.JsonParser.toJson;

public class Routes {

    public static final String REGISTER_ROUTE = "/register";
    public static final String LOGIN_ROUTE = "/login";
    public static final String POST_ROUTE = "/post";



    private static TunetSystem system;

    public void create(TunetSystem system) throws IOException {
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
                        res.status(201);
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



        //persist a new post
        post(POST_ROUTE, (req, res) -> {
            PostForm form = PostForm.createFromJson(req.body());
            if (!form.isComplete()){
                res.status(408);
                res.body("Uncompleted form");
                return res.body();
            }
            Post post = system.addPost(form);

            if(post != null){
                res.status(201);
                res.body("post created");
            }
            else {
                res.status(409);
                res.body("error");
            }
            return res.body();
        });

        //get mail from token
        post("/mail", (req, res) -> {
            String token = removeFirstandLast(req.body());;
            String mail = emailByToken.getIfPresent(token);

            if (mail == null || mail.equals("")){
                res.body("ERROR");
                res.status(404);
            }
            else{
                res.status(201);
                res.body(JsonParser.toJson(mail));
            }
            return res.body();
        });

        //get posts values of a user
        post("/posts", (req, res) -> {
            String token = removeFirstandLast(req.body());
            String mail = emailByToken.getIfPresent(token);

            final List<Post> posts = system.getPosts(mail);
            res.status(201);
            res.body(JsonParser.toJson(posts));
            return res.body();
        });

        //add artist to an artist list of a post
        post("/artistList", (req, res) -> {
            ArtistListForm form = ArtistListForm.createFromJson(req.body());
            if (!form.isComplete()){
                res.body("ERROR");
                res.status(404);
            }
            String mail = emailByToken.getIfPresent(form.getToken());

            ArtistListInPost artistList = system.addArtistList(form.getPostID(), mail, res);
            if (artistList != null) res.status(201);
            res.body("");
            return res.body();
        });

        //get artist list from postID

        post("/getArtistList", (req,res)-> {
            String postID = removeFirstandLast(req.body());

            List<ArtistListInPost> artistList = system.getArtistList(postID);
            res.status(201);
            res.body(JsonParser.toJson(artistList));
            return res.body();
        });

        //get all posts

        get("/getAllPosts", (req, res) -> {
            //String token = removeFirstandLast(req.body());
            //String mail = emailByToken.getIfPresent(token);

            final List<Post> posts = system.getAllPosts();
            res.status(201);
            res.body(JsonParser.toJson(posts));
            return res.body();
        });

        //gets profile pic of a user
        post("/getPicFromMail", (req, res) -> {
            String mail = removeFirstandLast(req.body());
            String profPic = system.getProfPic(mail);
            res.status(201);
            res.body(JsonParser.toJson(profPic));
            return res.body();
        });

    }


    //in token there are extra "" at the first and last characters.
    //this function removes the extra ""
    private static String removeFirstandLast(String str) {
        StringBuilder sb = new StringBuilder(str);
        sb.deleteCharAt(str.length() - 1);
        sb.deleteCharAt(0);
        return sb.toString();
    }

    //returns a json of a map that has attributes of the given user
    private Object getProfile(Response res, Optional<User> user) throws IOException {
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

    private final Cache<String, String> emailByToken = CacheBuilder.newBuilder()
            .expireAfterAccess(30, MINUTES)
            .build();

    //authenticates the user
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

}




