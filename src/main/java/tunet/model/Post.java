package tunet.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "POSTS")
public class Post {
    @Id
    private String id;

    private String localEmail;

    private String description;

    private String title;

    private String date;


    public Post(String localEmail, String description, String title, String date) {
        this.id = newID();
        this.localEmail = localEmail;
        this.description = description;
        this.title = title;
        this.date = date;
    }

    public static String newID(){
        idNumbers.addPostID();
        return String.valueOf(idNumbers.postID);
    }

    public Post() {

    }

    public static Post create(String localEmail, String description, String title, String date) {
        return new Post(localEmail, description, title, date);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalEmail() {
        return localEmail;
    }

    public void setLocalEmail(String localEmail) {
        this.localEmail = localEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void printUser(){
        System.out.println("id: " + id);
        System.out.println("localEmail: " + localEmail);
        System.out.println("description: " + description);
        System.out.println("title: " + title);
        System.out.println("date: " + date);
    }
}
