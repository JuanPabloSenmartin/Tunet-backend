package tunet.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityManagers {

  final static private ThreadLocal<EntityManager> emRef = new ThreadLocal<>();

  private static EntityManagerFactory emf;

  public static void setFactory(EntityManagerFactory emf) {
    EntityManagers.emf = emf;
  }

  public static EntityManager currentEntityManager() {
    final EntityManager em = emRef.get();
    if (em == null) {
      emRef.set(emf.createEntityManager());
    }
    return emRef.get();
  }

  public static void close() {
    final EntityManager em = emRef.get();
    if (em != null && em.isOpen()) {
      em.close();
    }
    emRef.remove();
  }

}
