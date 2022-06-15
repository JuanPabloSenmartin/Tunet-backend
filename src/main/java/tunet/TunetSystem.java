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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        user.printUser();
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

    public Chat addChats(String emailME, String emailHIM, String messageME) {
        return runInTransaction(
                ds -> {
                    boolean isMEartist = isMEartist(emailME, ds.users());
                    return ds.chats().addChat(emailME, emailHIM, messageME, isMEartist);
                }
        );
    }

    private boolean isMEartist(String emailME, Users users) {
        Optional<User> user = users.findByEmail(emailME);
        return user.get().isArtist();
    }
}
