package tunet.persistence;

import tunet.Util.Base64Parser;
import tunet.Util.LocationManager;
import tunet.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.function.Function;
import java.util.function.Supplier;

import static tunet.persistence.EntityManagers.currentEntityManager;

public class Transactions {

    private Transactions() {
    }

    public static <R> R persist(R entity) {
        return tx(entityManager -> {
            entityManager.persist(entity);
            return entity;
        });
    }
    public static <R> R persist(R entity, EntityManager entityManager) {
        entityManager.persist(entity);
        return entity;
    }
    public static Chat persistNewChat(Chat chat, EntityManager entityManager) {
        entityManager.persist(chat);
        return chat;

    }
    public static <R> Object remove(R entity){
        return tx(entityManager -> {
            entityManager.remove(entity);
            return null;
        });
    }
    public static <R> void remove(R entity, EntityManager entityManager){
            entityManager.remove(entity);
    }
//    public static User updateRating(User user, String rating) {
//        return tx(entityManager -> {
//            entityManager.merge(user);
//            user.setRating(rating);
//            return user;
//        });
//    }
//    public static ArtistListInPost updateArtistListInPost(ArtistListInPost artistListInPost, boolean isAccepted){
//        return tx(entityManager -> {
//            artistListInPost.setAccepted(isAccepted ? "TRUE" : "FALSE");
//            return artistListInPost;
//        });
//    }
//    public static void updatePost(Post post, boolean isAccepted, String artistMail){
//        tx(entityManager -> {
//            if (isAccepted){
//                post.setIsAccepted("TRUE");
//                post.setAcceptedArtistEmail(artistMail);
//            }
//            else{
//                post.setIsAccepted("FALSE");
//                post.setAcceptedArtistEmail(null);
//            }
//            return post;
//        });
//    }

//    public static void updateUserProfilePicture(User user, String profilePic){
//        tx(entityManager -> {
//            Base64Parser.deletePath(user.getProfilePictureUrl());
//            entityManager.merge(user);
//            user.setProfilePictureUrl(Base64Parser.createImageFile(profilePic, user.getEmail(), "profilePicture"));
//            return user;
//        });
//    }
//    public static Chat updateChat(Chat chat, String message, EntityManager entityManagera){
//        return tx(entityManager -> {
//            entityManager.merge(chat);
//            chat.setMessages(chat.getMessages() + message);
//            return chat;
//        });
//    }

    public static <R> R tx(Function<EntityManager, R> s) {
        final EntityTransaction tx = currentEntityManager().getTransaction();

        try {
            tx.begin();

            R r = s.apply(currentEntityManager());

            tx.commit();
            return r;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }
    public static <R> R txWithGivenEntity(EntityManager entityManager, Supplier<R> s) {
        final EntityTransaction tx = entityManager.getTransaction();

        try {
            tx.begin();

            R r = s.get();

            tx.commit();
            return r;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    public static <R> R tx(Supplier<R> s) {
        final EntityTransaction tx = currentEntityManager().getTransaction();

        try {
            tx.begin();

            R r = s.get();

            tx.commit();
            return r;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    public static void tx(Runnable r) {
        final EntityTransaction tx = currentEntityManager().getTransaction();

        try {
            tx.begin();

            r.run();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }


}
