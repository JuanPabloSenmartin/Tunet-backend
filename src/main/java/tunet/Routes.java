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
            return getProfile(res, user, mail);
        });


        //Gives current profile values

        post("/viewProfile", (req, res) -> {
            String token = removeFirstandLast(req.body());;
            String mail = emailByToken.getIfPresent(token);
            Optional<User> user = system.findUserByEmail(mail);
            return getProfile(res, user, mail);
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

            final List<LocalPostInfo> posts = system.getPostsOfLocal(mail);
            res.status(201);
            res.body(JsonParser.toJson(posts));
            return res.body();
        });

        //add artist to an artist list of a post
        //submit to a post
        post("/artistList", (req, res) -> {
            ArtistListForm form = ArtistListForm.createFromJson(req.body());

            String mail = emailByToken.getIfPresent(form.getToken());

            ArtistListInPost artistList = system.addArtistList(form.getPostID(), mail);
            if (artistList != null) res.status(201);
            res.body("");
            return res.body();
        });

        //unsubmit from post
        post("/deleteArtistFromPostList", (req, res) -> {
            ArtistListForm form = ArtistListForm.createFromJson(req.body());
            String mail = emailByToken.getIfPresent(form.getToken());
            system.deleteArtistList(form.getPostID(), mail);
            res.status(201);
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

        post("/getAllPosts", ((req, res) -> {
            FilterForm form = FilterForm.createFromJson(req.body());
            final List<PostInfo> posts = system.getAllPosts(form);
            res.status(201);
            res.body(JsonParser.toJson(posts));
            return res.body();
        }));


        //gets profile pic of a user
        post("/getPicFromMail", (req, res) -> {
            String mail = removeFirstandLast(req.body());
            String profPic = system.getProfPic(mail);
            res.status(201);
            res.body(JsonParser.toJson(profPic));
            return res.body();
        });

        //gets chat data from user
        post("/chatUsers", (req, res) -> {
            String token = removeFirstandLast(req.body());
            String mail = emailByToken.getIfPresent(token);

            final List<ChatForm> chatForms = system.getChatsInfo(mail);
            res.status(201);
            res.body(JsonParser.toJson(chatForms));
            return res.body();
        });

        //get chat of a user him
        post("/getChatOfaUser", (req, res) -> {
            String str = removeFirstandLast(req.body());
            String [] s = str.split("~");
            String emailME = s[0];
            String emailHIM = s[1];
            ChatForm chat = system.getCertainChat(emailME, emailHIM);
            res.status(201);
            res.body(JsonParser.toJson(chat));
            return res.body();
        });

        //get chat of users
        post("/getMessages", (req, res) -> {
            String str = removeFirstandLast(req.body());
            String [] s = str.split("~");
            String emailME = s[0];
            String emailHIM = s[1];
            String messages = system.getMessages(emailME, emailHIM);
            res.status(201);
            System.out.println(messages);
            res.body(JsonParser.toJson(messages));
            return res.body();
        });
        //add rating
        post("/rating", (req, res) -> {
            String str = removeFirstandLast(req.body());
            String [] s = str.split("~");
            int rating = Integer.parseInt(s[0]);
            String email = s[1];

            system.addRating(email, rating);
            res.status(201);
            res.body("");
            return res.body();
        });

        //returns all gallery images from a user
        post("/getGalleryImages", (req, res) -> {
            String mail = removeFirstandLast(req.body());
            final List<String> images = system.getGalleryImagesFromEmail(mail);
            res.status(201);
            res.body(JsonParser.toJson(images));
            return res.body();
        });

        //adds an image to gallery
        post("/addGalleryImage", (req, res) -> {
            GalleryImageForm imageForm = GalleryImageForm.createFromJson(req.body());
            system.addImageToGallery(imageForm);
            res.status(201);
            res.body("");
            return res.body();
        });

        //deletes an image from gallery
        post("/deleteGalleryImage", (req, res) -> {
            GalleryImageForm imageForm = GalleryImageForm.createFromJson(req.body());
            system.deleteImageFromGallery(imageForm);
            res.status(201);
            res.body("");
            return res.body();
        });

        //returns all songs
        post("/getSongs", (req, res) -> {
            String mail = removeFirstandLast(req.body());
            final List<String> songs = system.getSongsFromEmail(mail);
            res.status(201);
            res.body(JsonParser.toJson(songs));
            return res.body();
        });

        //adds song
        post("/addSong", (req, res) -> {
            GallerySongForm songForm = GallerySongForm.createFromJson(req.body());
            system.addSongToGallery(songForm);
            res.status(201);
            res.body("");
            return res.body();
        });

        //deletes song
        post("/deleteSong", (req, res) -> {
            GallerySongForm songForm = GallerySongForm.createFromJson(req.body());
            system.deleteSongFromGallery(songForm);
            res.status(201);
            res.body("");
            return res.body();
        });

        //deletes an image from gallery
        post("/changeProfilePic", (req, res) -> {
            GalleryImageForm imageForm = GalleryImageForm.createFromJson(req.body());
            system.changeProfilePic(imageForm.getEmail(), imageForm.getImageUrl());
            res.status(201);
            res.body("");
            return res.body();
        });


        //get old posts
        post("/getOldPosts", (req, res) -> {
            String token = removeFirstandLast(req.body());
            String mail = emailByToken.getIfPresent(token);

            final List<OldPostInfo> posts = system.getOldPosts(mail);
            res.status(201);
            res.body(JsonParser.toJson(posts));
            return res.body();
        });


        post("/getPostFeed", (req, res) -> {
            FilterForm form = FilterForm.createFromJson(req.body());
            String mail = emailByToken.getIfPresent(form.getToken());
            final List<PostInfo> posts = system.getFilteredPosts(mail, form);
            res.status(201);
            res.body(JsonParser.toJson(posts));
            return res.body();
        });

        //get posts
        post("/getPostsInfo", (req, res) -> {
            PostTypeForm form = PostTypeForm.createFromJson(req.body());
            String mail = emailByToken.getIfPresent(form.getToken());
            final List<PostInfo> posts = system.getSpecificPosts(mail, form);
            res.status(201);
            res.body(JsonParser.toJson(posts));
            return res.body();
        });

        //get posts
        post("/getLocalPostsInfo", (req, res) -> {
            PostTypeForm form = PostTypeForm.createFromJson(req.body());
            String mail = emailByToken.getIfPresent(form.getToken());
            final List<LocalPostInfo> posts = system.getLocalSpecificPosts(mail, form);
            res.status(201);
            res.body(JsonParser.toJson(posts));
            return res.body();
        });

        //accept artist
        post("/acceptArtistInPost", (req, res) -> {
            AcceptArtistForm form = AcceptArtistForm.createFromJson(req.body());
            system.acceptArtistInPost(form);
            res.status(201);
            res.body("");
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
    private Object getProfile(Response res, Optional<User> user, String mail) throws IOException {
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

    //deletes



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




