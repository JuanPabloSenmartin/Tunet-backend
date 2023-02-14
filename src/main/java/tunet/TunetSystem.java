package tunet;

import tunet.Mail.MailManager;
import tunet.Util.Base64Parser;
import tunet.Util.LocationManager;
import tunet.model.*;
import tunet.model.forms.*;
import tunet.persistence.EntityManagers;
import tunet.repository.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

public class TunetSystem {

    private final EntityManagerFactory factory;

    private TunetSystem(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public static TunetSystem create(String persistenceUnitName) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory(persistenceUnitName);
        EntityManagers.setFactory(factory);
        return new TunetSystem(factory);
    }

    public User registerUser(RegistrationUserForm form) {
        return runInTransaction(datasource -> {
            final Users users = datasource.users();
            return users.exists(form.getEmail()) ? null : users.createUser(form);
        });
    }
    public Object editProfile(EditProfileForm form) {
        return runInTransaction(
                ds -> {
                    User user = ds.users().findByEmail(form.getEmail()).get();
                    user.setDescription(form.getDescription());
                    user.setCoordinates(LocationManager.getCoordinates(form.getLocation()));
                    user.setLocation(form.getLocation());
                    user.setUsername(form.getUsername());
                    user.setPhoneNumber(form.getPhoneNumber());
                    return user;
                }
        );
    }

    public Optional<User> findUserByEmail(String email) {
        return runInTransaction(
                ds -> ds.users().findByEmail(email)
        );
    }

    public boolean validPassword(String password, User foundUser) {
        // Super dummy implementation. Zero security
        return foundUser.getPassword().equals(password);
    }


    private <E> E runInTransaction(Function<MySystemRepository, E> closure) {
        final EntityManager entityManager = factory.createEntityManager();
        final MySystemRepository ds = MySystemRepository.create(entityManager);

        try {
            entityManager.getTransaction().begin();
            final E result = closure.apply(ds);
            entityManager.getTransaction().commit();
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public Map<String, String> getProfileData(User user) throws IOException {
        Map<String, String> map = new HashMap<>();
        if (user.isArtist()){
            map.put("email", user.getEmail());
            map.put("username", user.getUsername());
            map.put("description", user.getDescription());
            map.put("location", user.getLocation());
            map.put("phoneNumber", user.getPhoneNumber());
            map.put("rating", rating(user.getRating()));
        }
        else {
            map.put("email", user.getEmail());
            map.put("username", user.getUsername());
            map.put("description", user.getDescription());
            map.put("location", user.getLocation());
            map.put("phoneNumber", user.getPhoneNumber());
            map.put("rating", rating(user.getRating()));
        }
        return map;
    }

    private String rating(String rating) {
        String [] str = rating.split("-");
        int sumOfStars = Integer.parseInt(str[0]);
        int amountOfRatingsGiven = Integer.parseInt(str[1]);
        if(amountOfRatingsGiven == 0) return "0";
        return String.valueOf(Math.round(sumOfStars/amountOfRatingsGiven));
    }

    public Post addPost(PostForm form){
        return runInTransaction(datasource -> {
            final Posts posts = datasource.posts();
            return posts.createPost(form);
        });
    }

    public ArtistListInPost addArtistList(String postID, String artistEmail){
        return runInTransaction(datasource -> {
            final ArtistLists artistLists = datasource.artistLists();
                return artistLists.createArtistList(postID, artistEmail);
        });
    }

    public List<ArtistListInPost> getArtistList(String postID) {
        return runInTransaction(
                ds -> ds.artistLists().listFromPostID(postID)
        );
    }

    public List<PostInfo> getFilteredPosts(String artistMail, FilterForm form) {
        return runInTransaction(
                ds -> {
                    Posts posts = ds.posts();
                    Users users = ds.users();
                    List<String> postsIDs = ds.artistLists().getPostIdsFromMail(artistMail, posts.listAllPosts());
                    try {
                        return posts.listThesePosts(postsIDs, users, users.findByEmail(artistMail).get().getCoordinates(), form);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

        );
    }
    public List<PostInfo> getAllPosts(FilterForm form) {
        return runInTransaction(
                ds -> {
                    Posts posts = ds.posts();
                    try {
                        return posts.listFilteredPostsInDiscover(posts.listAllPosts(), ds.users(), form);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

        );
    }

    public String getProfPic(String mail) {
        return runInTransaction(
                ds -> {
                    try {
                        return ds.users().getProfPicFromMail(mail);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        );
    }

    public void addChats(String emailME, String emailHIM, String messageME, boolean isArtistMe) {
        runInTransaction(
                ds -> {
                    Chats chats = ds.chats();
                    Users users = ds.users();
                    Chat chat = getChat(emailME, emailHIM, chats, isArtistMe);
                    if (chat == null){
                        if (isArtistMe){
                            createChat(emailHIM, emailME, "2" + messageME, chats);
                        }
                        else{
                            createChat(emailME, emailHIM, "1" + messageME, chats);
                        }
                    }
                    else{
                        if (isArtistMe){
                            updateChat(chat, (chat.getMessages().equals("") ? "2" : "~2") + messageME);
                        }
                        else{
                            updateChat(chat, (chat.getMessages().equals("") ? "1" : "~1") + messageME);
                        }
                    }
                    return chat;
                }
        );
    }

    private Chat updateChat(Chat chat, String message){
        chat.setMessages(chat.getMessages() + message);
        return chat;
    }
    private Chat createChat(String emailLocal, String emailArtist, String initialMessage, Chats chats) {
        return chats.createChat(emailLocal, emailArtist, initialMessage);
    }
    public List<ChatForm> getChatsInfo(String mail) throws IOException {
        List<ChatForm> chatForms = new ArrayList<>();
        User me = findUserByEmail(mail).get();
        User him;
        List<Chat> chats = getChatsOfUser(mail, me.isArtist());
        for (Chat chat : chats) {
            if (me.isArtist()) him = findUserByEmail(chat.getEmail1()).get();
            else him = findUserByEmail(chat.getEmail2()).get();
            //start completing chat forms
            chatForms.add(new ChatForm(
                    chat.getId(),
                    mail,
                    him.getEmail(),
                    Base64Parser.convertToBase64(him.getProfilePictureUrl()),
                    Base64Parser.convertToBase64(me.getProfilePictureUrl()),
                    him.getUsername(),
                    me.getUsername(),
                    String.valueOf(me.isArtist())
                    ));
        }
        return chatForms;
    }
    private List<Chat> getChatsOfUser(String mail, boolean isArtistME){
        return runInTransaction(
                ds -> ds.chats().chatsFromMail(mail, isArtistME)
        );
    }

    public ChatForm getCertainChat(String emailME, String emailHIM) throws IOException {
        return runInTransaction(
                ds -> {
                    Chats chats = ds.chats();
                    Users users = ds.users();
                    boolean isArtistMe = users.findByEmail(emailME).get().isArtist();
                    Chat chat = getChat(emailME, emailHIM, chats, isArtistMe);
                    User me = users.findByEmail(emailME).get();
                    User him = users.findByEmail(emailHIM).get();
                    if (chat == null) {
                        try {
                            return new ChatForm(
                                    "0",
                                    emailME,
                                    him.getEmail(),
                                    Base64Parser.convertToBase64(him.getProfilePictureUrl()),
                                    Base64Parser.convertToBase64(me.getProfilePictureUrl()),
                                    him.getUsername(),
                                    me.getUsername(),
                                    String.valueOf(me.isArtist()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        try {
                            return new ChatForm(
                                    chat.getId(),
                                    emailME,
                                    him.getEmail(),
                                    Base64Parser.convertToBase64(him.getProfilePictureUrl()),
                                    Base64Parser.convertToBase64(me.getProfilePictureUrl()),
                                    him.getUsername(),
                                    me.getUsername(),
                                    String.valueOf(me.isArtist()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }

    private Chat getChat(String emailME, String emailHIM, Chats chats, boolean isArtistMe){
        return chats.certainChat(emailME, emailHIM, isArtistMe);
    }

    public String getMessages(String emailME, String emailHIM) {
        return runInTransaction(
                ds -> {
                    Chats chats = ds.chats();
                    Users users = ds.users();
                    boolean isArtistMe = users.findByEmail(emailME).get().isArtist();
                    Chat chat = getChat(emailME, emailHIM, chats, isArtistMe);
                    if (chat == null) {
                        if (isArtistMe){
                            chat = createChat(emailHIM, emailME, "", chats);
                        }
                        else{
                            chat = createChat(emailME, emailHIM, "", chats);
                        }
                    }
                    return chat.getMessages();
                }
        );
    }

    public void addRating(String email, int rating) {
        runInTransaction(
                ds -> ds.users().updateRating(email, rating)
        );
    }


    public Image addImageToGallery(GalleryImageForm imageForm) {
        return runInTransaction(
                ds -> {
                    Images images = ds.images();
                    int id = images.getMaxId() + 1;
                    return images.createImage(imageForm.getEmail(), Base64Parser.createImageFile(imageForm.getImageUrl(), imageForm.getEmail(), "galleryImage", id));
                }
        );
    }

    public List<String> getGalleryImagesFromEmail(String mail) {
        return runInTransaction(
                ds -> {
                    List<Image> images = ds.images().getAllImagesFromEmail(mail);
                    List<String> base64Imgs = new ArrayList<>();
                    for(Image image : images){
                        try {
                            base64Imgs.add(Base64Parser.convertToBase64(image.getImageUrl()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return base64Imgs;
                }
        );

    }

    public Image deleteImageFromGallery(GalleryImageForm imageForm) {
        return runInTransaction(
                ds -> {
                    try {
                        return ds.images().deleteImage(imageForm.getImageUrl(), imageForm.getEmail());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public User changeProfilePic(String email, String imageUrl) {
        return runInTransaction(
                ds -> {
                    Users users = ds.users();
                    User user = users.findByEmail(email).get();
                    return updateProfilePicture(user, imageUrl);
                }
        );
    }
    public User updateProfilePicture(User user, String profilePic){
        if (!user.getProfilePictureUrl().equals("src\\main\\resources\\images\\defaultPicture.jpg")){
            Base64Parser.deletePath(user.getProfilePictureUrl());
        }
        user.setProfilePictureUrl(Base64Parser.createImageFile(profilePic, user.getEmail(), "profilePicture"));
        return user;
    }

    public List<String> getSongsFromEmail(String mail) {
        return runInTransaction(
                ds -> {
                    List<Song> songs = ds.songs().getAllSongsFromEmail(mail);
                    List<String> base64Songs = new ArrayList<>();
                    for(Song image : songs){
                        try {
                            base64Songs.add(Base64Parser.convertToBase64(image.getSongUrl()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return base64Songs;
                }
        );
    }

    public Song addSongToGallery(GallerySongForm songForm) {
        return runInTransaction(
                ds -> {
                    Songs songs = ds.songs();
                    int id = songs.getMaxId() + 1;
                    return songs.createSong(songForm.getEmail(), Base64Parser.createImageFile(songForm.getSongUrl(), songForm.getEmail(), "song", id));
                }
        );
    }

    public Song deleteSongFromGallery(GallerySongForm songForm) {
        return runInTransaction(
                ds -> {
                    try {
                        return ds.songs().deleteSong(songForm.getSongUrl(), songForm.getEmail());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }



    public List<OldPostInfo> getOldPosts(String mail) {
        return runInTransaction(
                ds -> {
                    List<OldPostInfo> result = new ArrayList<>();
                    ArtistLists artistLists = ds.artistLists();
                    Posts posts = ds.posts();
                    List<Post> list = posts.listFromMail(mail);
                    for (Post post : list) {
                        if (post.getConvertedDate().isBefore(LocalDate.now())){
                            String accepterUser = artistLists.getAcceptedUser(post.getId());
                            result.add(new OldPostInfo(post.getDate(), post.getDescription(), post.getLocalEmail(), post.getTitle(), accepterUser));
                        }
                    }
                    return result;
                }
        );
    }

    public List<LocalPostInfo> getPostsOfLocal(String mail) {
        return runInTransaction(
                ds -> {
                    ArtistLists artistLists = ds.artistLists();
                    Posts posts = ds.posts();
                    Users users = ds.users();
                    List<LocalPostInfo> result = new ArrayList<>();
                    List<Post> postList = posts.listFromMail(mail);
                    for (Post post : postList) {
                        if (post.getConvertedDate().isAfter(LocalDate.now()) && !post.isAccepted()){
                            try {
                                result.add(new LocalPostInfo(post.getId(), post.getLocalEmail(), post.getDescription(),post.getTitle(), post.getDate(), post.getGenres(), artistLists.getArtistListData(post.getId(), users)));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    return result;
                }
        );
    }

    public ArtistListInPost deleteArtistList(String postID, String mail) {
        return runInTransaction(ds -> {
            return ds.artistLists().deleteArtistList(postID, mail);
        });
    }

    public List<PostInfo> getSpecificPosts(String mail, PostTypeForm form) {
        return runInTransaction(
                ds -> {
                    Posts posts = ds.posts();
                    Users users = ds.users();
                    ArtistLists artistLists = ds.artistLists();
                    List<PostInfo> result = new ArrayList<>();
                    List<ArtistListInPost> list = artistLists.getArtistsFromMail(mail);
                    User me = users.findByEmail(mail).get();
                    for (ArtistListInPost artistListInPost : list) {
                        Optional<Post> post = posts.findByPostID(artistListInPost.getPostID());
                        if (post.isPresent()) {
                            Post p = post.get();
                            Optional<User> localUser = users.findByEmail(p.getLocalEmail());
                            if (localUser.isPresent()){
                                boolean valid = false;
                                switch (form.getType()){
                                    case "postulated": {
                                        valid = isPostulatedPost(p, artistListInPost);
                                        break;
                                    }
                                    case "accepted": {
                                        valid = isAcceptedPost(p, artistListInPost);
                                        break;
                                    }
                                    case "previous":{
                                        valid = isPreviousPost(p, artistListInPost);
                                        break;
                                    }
                                    default:break;
                                }
                                if (valid){
                                    valid = posts.passesDateRangeFilter(p.getConvertedDate(), form.getDate());
                                }
                                if (valid){
                                    User u = localUser.get();
                                    try {
                                        result.add(new PostInfo(p.getId(), p.getDate(), p.getDescription(), p.getLocalEmail(), p.getTitle(), p.getGenres(), u.getIntRating(), Base64Parser.convertToBase64(u.getProfilePictureUrl()), "" + LocationManager.getDistance(u.getCoordinates(), me.getCoordinates()), mail));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }
                    return result;
                }

        );
    }
    private boolean isPostulatedPost(Post post, ArtistListInPost artistListInPost){
        return (post.getConvertedDate().isAfter(LocalDate.now()) || post.getConvertedDate().isEqual(LocalDate.now())) && !artistListInPost.isAccepted();
    }
    private boolean isAcceptedPost(Post post, ArtistListInPost artistListInPost){
        return (post.getConvertedDate().isAfter(LocalDate.now()) || post.getConvertedDate().isEqual(LocalDate.now())) && artistListInPost.isAccepted();
    }
    private boolean isPreviousPost(Post post, ArtistListInPost artistListInPost){
        return post.getConvertedDate().isBefore(LocalDate.now()) && artistListInPost.isAccepted();
    }

    public void acceptArtistInPost(AcceptArtistForm form) {
        runInTransaction(
                ds -> {
                    Posts posts = ds.posts();
                    ArtistLists artistLists = ds.artistLists();
                    ArtistListInPost artistListInPost = findArtistListInPostById(form.getArtistListId(), artistLists);
                    Post post = findPostById(form.getPostId(), posts);
                    updatePostIsAccepted(post, true, artistListInPost.getArtistEmail());
                    updateArtistListInPostIsAccepted(artistListInPost,true);
                    return null;
                }
        );
    }
    private void updateArtistListInPostIsAccepted(ArtistListInPost artistListInPost, boolean isAccepted){
            artistListInPost.setAccepted(isAccepted ? "TRUE" : "FALSE");
    }
    private void updatePostIsAccepted(Post post, boolean isAccepted, String artistMail){
        if (isAccepted){
            post.setIsAccepted("TRUE");
            post.setAcceptedArtistEmail(artistMail);
        }
        else{
            post.setIsAccepted("FALSE");
            post.setAcceptedArtistEmail(null);
        }
    }
    private Post findPostById(String id, Posts posts){
        return posts.findByPostID(id).get();
    }
    private ArtistListInPost findArtistListInPostById(String id, ArtistLists artistLists){
        return artistLists.findByID(id).get();
    }

    public List<LocalPostInfo> getLocalSpecificPosts(String mail, PostTypeForm form) {
        return runInTransaction(
                ds -> {
                    Posts posts = ds.posts();
                    Users users = ds.users();
                    ArtistLists artistLists = ds.artistLists();
                    List<LocalPostInfo> result = new ArrayList<>();

                    List<Post> list = posts.listFromMail(mail);
                    User localEmail = users.findByEmail(mail).get();
                    for (Post post : list) {
                        boolean valid = false;
                        switch (form.getType()){
                            case "accepted": {
                                valid = isAcceptedLocalPost(post);
                                break;
                            }
                            case "previous":{
                                valid = isPreviousLocalPost(post);
                                break;
                            }
                            default: continue;
                        }
                        if (valid){
                            valid = posts.passesDateRangeFilter(post.getConvertedDate(), form.getDate());
                        }
                        Optional<User> artistOptional = users.findByEmail(post.getAcceptedArtistEmail());
                        if (valid && artistOptional.isPresent()){
                            User artist = artistOptional.get();
                            List<ArtistListInfo> a = new ArrayList<>();
                            ArtistListInPost artistListInPost = artistLists.getEntity(post.getId(), artist.getEmail()).get();
                            try {
                                a.add(new ArtistListInfo(artistListInPost.getId(), artist.getEmail(), artist.getIntRating(), Base64Parser.convertToBase64(artist.getProfilePictureUrl())));
                                result.add(new LocalPostInfo(post.getId(), post.getLocalEmail(), post.getDescription(),post.getTitle(), post.getDate(), post.getGenres(), a));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    return result;
                }
        );
    }
    private boolean isAcceptedLocalPost(Post post){
        return (post.getConvertedDate().isAfter(LocalDate.now()) || post.getConvertedDate().isEqual(LocalDate.now())) && post.isAccepted();
    }
    private boolean isPreviousLocalPost(Post post){
        return post.getConvertedDate().isBefore(LocalDate.now()) && post.isAccepted();
    }

    public void sendEmail(SendMailForm form, String emailMe) {
        String header;
        String body;
        String usernameMe = getUsername(emailMe);
        String usernameHim = getUsername(form.getEmailTo());
        switch(form.getType()){
            case "accepted": {
                //local me
                createNotification(usernameMe + " has accepted your postulation", form.getEmailTo(), "/viewLocalProfile/" + emailMe);
                header = "You have been selected";
                body = "Hello " + usernameHim + ", you have been accepted in your postulation to " + form.getPostTitle() + " by " + usernameMe + "\n\n link to profile: http://localhost:3000/viewLocalProfile/" + emailMe;
                break;
            }
            case "postulated": {
                //artist me
                createNotification(usernameMe + " has postulated to your post", form.getEmailTo(), "/viewArtistProfile/" + emailMe);
                header = "A user has postulated to your post";
                body = "Hello " + usernameHim + ", the user " + usernameMe + " has postulated to your post " + form.getPostTitle() + "\n\n link to profile: http://localhost:3000/viewArtistProfile/" + emailMe;
                break;
            }
            default:{
                header = "default";
                body = "default";
                break;
            }
        }
        MailManager.sendMail(form.getEmailTo(), header, body);
    }
    private String getUsername(String mail){
        return runInTransaction(
                ds -> {
                    return ds.users().findByEmail(mail).get().getUsername();
                }
        );
    }

    public void createNotification(String notification, String userMail, String profileLink){
        runInTransaction(
                ds -> {
                    return ds.notifications().createNotification(userMail, notification, profileLink);
                }
        );
    }

    public List<Notification> getNotifications(String mail) {
        return runInTransaction(
                ds -> {
                    return ds.notifications().getNotificationsFromUserMail(mail);
                }
        );
    }

    public Notification deleteNotification(String notificationId) {
        return runInTransaction(
                ds -> {
                    return ds.notifications().deleteNotification(notificationId);
                }
        );
    }

    public void seeNotifications(String mail) {
        runInTransaction(
                ds-> {
                    List<Notification> notifications = ds.notifications().getNotificationsFromUserMail(mail);
                    for (Notification notification : notifications){
                        if (!notification.wasSeen()) notification.setSeen("TRUE");
                    }
                    return null;
                }
        );
    }

    public Post deletePost(String id) {
        return runInTransaction(
                ds -> {
                    return ds.posts().deletePost(id);
                }
        );
    }

    public Post rejectAcceptedArtist(String id) {
        return runInTransaction(
                ds -> {
                    ArtistListInPost artistListInPost = ds.artistLists().findByID(id).get();
                    Post post = ds.posts().findByPostID(artistListInPost.getPostID()).get();
                    updatePostIsAccepted(post, false, null);
                    updateArtistListInPostIsAccepted(artistListInPost, false);
                    return null;
                }
        );
    }
}
