package tunet;

import spark.Response;
import tunet.Util.Base64Parser;
import tunet.model.*;
import tunet.persistence.EntityManagers;
import tunet.persistence.Transactions;
import tunet.repository.ArtistLists;
import tunet.repository.Posts;
import tunet.repository.Users;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
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
    public void editProfile(EditProfileForm form) {
        User user = findUserByEmail(form.getEmail()).get();
        Transactions.update(user, form);
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
            map.put("audioUrl", Base64Parser.convertToBase64(user.getArtistAudioUrl()));
            map.put("username", user.getUsername());
            map.put("description", user.getDescription());
            map.put("pictureUrl", Base64Parser.convertToBase64(user.getPictureUrl()));
            map.put("profilePictureUrl", Base64Parser.convertToBase64(user.getProfilePictureUrl()));
            map.put("location", user.getLocation());
            map.put("phoneNumber", user.getPhoneNumber());
        }
        else {
            map.put("email", user.getEmail());
            map.put("username", user.getUsername());
            map.put("description", user.getDescription());
            map.put("pictureUrl", Base64Parser.convertToBase64(user.getPictureUrl()));
            map.put("profilePictureUrl", Base64Parser.convertToBase64(user.getProfilePictureUrl()));
            map.put("location", user.getLocation());
            map.put("phoneNumber", user.getPhoneNumber());
        }
        return map;
    }

    public Post addPost(PostForm form){
        return runInTransaction(datasource -> {
            final Posts posts = datasource.posts();
            return posts.createPost(form);
        });
    }

    public ArtistListInPost addArtistList(String postID, String artistEmail, Response res){
        return runInTransaction(datasource -> {
            final ArtistLists artistLists = datasource.artistLists();
            if (isRepeated(postID, artistEmail, artistLists)){
                res.status(409);
                res.body("repeated");
                return null;
            }
            else{
                return artistLists.createArtistList(postID, artistEmail);
            }
        });
    }
    private boolean isRepeated(String postID, String artistEmail, ArtistLists artistLists){
        List<String> postsIDs = artistLists.getPostIdsFromMail(artistEmail);
        for (String postsID : postsIDs) {
            if (postsID.equals(postID)) return true;
        }
        return false;
    }


    public List<Post> getPosts(String mail) {
        return runInTransaction(
                ds -> ds.posts().listFromMail(mail)
        );
    }
    public List<ArtistListInPost> getArtistList(String postID) {
        return runInTransaction(
                ds -> ds.artistLists().listFromPostID(postID)
        );
    }

    public List<Post> getAllPosts() {

        return runInTransaction(
                ds -> {//List<String> postsIDs = ds.artistLists().getPostIdsFromMail(mail);
                    return ds.posts().listAllPosts();
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
        Chat chat = getChat(emailME, emailHIM);
        if (chat == null){
            if (isArtistMe){
                createChat(emailHIM, emailME, "2" + messageME);
            }
            else{
                createChat(emailME, emailHIM, "1" + messageME);
            }
        }
        else{
            if (isArtistMe){
                Transactions.updateChat(chat, "~2" + messageME);
            }
            else{
                Transactions.updateChat(chat, "~1" + messageME);
            }
        }
    }
    private Chat createChat(String emailHIM, String emailME, String initialMessage) {
        return runInTransaction(
                ds -> {
                    return ds.chats().createChat(emailME, emailHIM, initialMessage);
                }
        );
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
        Chat chat = getChat(emailME, emailHIM);
        if (chat == null) {
            chat = createChat(emailME, emailHIM, "");
        }
        User me = findUserByEmail(emailME).get();
        User him = findUserByEmail(emailHIM).get();
        return new ChatForm(
                chat.getId(),
                emailME,
                him.getEmail(),
                Base64Parser.convertToBase64(him.getPictureUrl()),
                Base64Parser.convertToBase64(me.getPictureUrl()),
                him.getUsername(),
                me.getUsername(),
                String.valueOf(me.isArtist()));
    }

    private Chat getChat(String emailME, String emailHIM){
        boolean isArtistME = findUserByEmail(emailME).get().isArtist();
        return runInTransaction(
                ds -> ds.chats().certainChat(emailME,emailHIM, isArtistME)
        );
    }

    public String getMessages(String emailME, String emailHIM) {
        Chat chat = getChat(emailME, emailHIM);
        if (chat == null) {
            chat = createChat(emailME, emailHIM, "");
        }
        return chat.getMessages();
    }
}
