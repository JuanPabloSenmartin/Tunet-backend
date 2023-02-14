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

    public static <R> R persist(R entity, EntityManager entityManager) {
        entityManager.persist(entity);
        return entity;
    }
    public static Chat persistNewChat(Chat chat, EntityManager entityManager) {
        entityManager.persist(chat);
        return chat;

    }

    public static <R> void remove(R entity, EntityManager entityManager){
            entityManager.remove(entity);
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
