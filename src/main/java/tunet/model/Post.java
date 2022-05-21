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


    public Post(String newID, String localEmail, String description, String title, String date) {
        this.id = newID;
        this.localEmail = localEmail;
        this.description = description;
        this.title = title;
        this.date = date;
    }


    public Post() {

    }

    public static Post create(String newID, String localEmail, String description, String title, String date) {
        return new Post(newID, localEmail, description, title, date);
    }

    public String getId() {
        return id;
    }

    public String getLocalEmail() {
        return localEmail;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public void printUser(){
        System.out.println("id: " + id);
        System.out.println("localEmail: " + localEmail);
        System.out.println("description: " + description);
        System.out.println("title: " + title);
        System.out.println("date: " + date);
    }
}
