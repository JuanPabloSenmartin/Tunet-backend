package tunet.repository;

import tunet.Util.Base64Parser;
import tunet.model.Image;
import tunet.model.Notification;
import tunet.model.User;
import tunet.persistence.Transactions;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class Notifications {
    private final EntityManager entityManager;

    public Notifications(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Notification createNotification(String email, String notification, String profileLink) {
        int lastId = getMaxId();
        String newID = String.valueOf(lastId + 1);
        final Notification newNotification = Notification.create(newID, email, notification, profileLink);

        return Transactions.persist(newNotification, entityManager);
    }

    public int getMaxId() {
        List<Notification> list = getNotificationList();
        int max = 1;
        for (int i = list.size() - 1; i >= 0; i--) {
            int num = Integer.parseInt(list.get(i).getId());
            if (num > max) {
                max = num;
            }
        }
        return max;
    }
    private List<Notification> getNotificationList() {
        return entityManager
                .createQuery("SELECT u FROM Notification u", Notification.class)
                .getResultList();
    }

    public List<Notification> getNotificationsFromUserMail(String userMail) {
        return entityManager
                .createQuery("SELECT u FROM Notification u WHERE u.userMail LIKE :userMail", Notification.class)
                .setParameter("userMail", userMail).getResultList();
    }
    public Optional<Notification> findById(String id) {
        return entityManager.createQuery("SELECT u FROM Notification u WHERE u.id LIKE :id", Notification.class)
                .setParameter("id", id).getResultList().stream()
                .findFirst();
    }

    public Notification deleteNotification(String notificationId) {
        Optional<Notification> notification = findById(notificationId);
        if (notification.isPresent()){
            Transactions.remove(notification.get(), entityManager);
        }

        return null;
    }
}
