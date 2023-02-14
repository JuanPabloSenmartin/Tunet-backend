package tunet.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "NOTIFICATIONS")
public class Notification {
    @Id
    private String id;

    private String userMail;

    private String notification;
    private String profileLink;
    private String seen;

    public Notification(String id, String userMail, String notification, String profileLink) {
        this.id = id;
        this.userMail = userMail;
        this.notification = notification;
        this.profileLink = profileLink;
        this.seen = "FALSE";
    }
    public Notification(){}

    public static Notification create(String id, String userMail, String notification, String profileLink){
        return new Notification(id, userMail, notification, profileLink);
    }

    public String getId() {
        return id;
    }

    public String getUserMail() {
        return userMail;
    }

    public String getNotification() {
        return notification;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public boolean wasSeen() {
        return !seen.equals("FALSE");
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}
