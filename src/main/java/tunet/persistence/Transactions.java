package tunet.persistence;

import tunet.Util.Base64Parser;
import tunet.model.EditProfileForm;
import tunet.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public static void update(User user, EditProfileForm form){
        tx(entityManager -> {
            entityManager.merge(user);
            user.setDescription(form.getDescription());
            user.setLocation(form.getLocation());
            user.setUsername(form.getUsername());
            user.setProfilePictureUrl(Base64Parser.createImageFile(form.getProfilePictureUrl(), form.getEmail(), "profilePicture"));
            user.setPictureUrl(Base64Parser.createImageFile(form.getPictureUrl(), form.getEmail(), "normalPicture"));
            user.setArtistAudioUrl(Base64Parser.createImageFile(form.getArtistAudioUrl(), form.getEmail(), "audio"));
            user.setPhoneNumber(form.getPhoneNumber());
            return user;
        });
    }

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
