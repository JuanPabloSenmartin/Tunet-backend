package tunet.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "POSTS")
public class Post {
    @Id
    private String id;

    private String localEmail;

    private String description;

    private String title;

    private String date; // y/m/d

    private String genres;//rock,pop,...

    private String isAccepted;

    private String acceptedArtistEmail;


    public Post(String newID, String localEmail, String description, String title, String date, String genres) {
        this.id = newID;
        this.localEmail = localEmail;
        this.description = description;
        this.title = title;
        this.date = date;
        this.genres = genres;
        this.isAccepted = "FALSE";
        this.acceptedArtistEmail = "";
    }


    public Post() {

    }

    public static Post create(String newID, String localEmail, String description, String title, String date, String genres) {
        return new Post(newID, localEmail, description, title, date, genres);
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

    public String getGenres() {
        return genres;
    }
    public String[] getGenresArray(){
        if (genres == null || genres.isEmpty()) {
            String[] a = {};
            return a;
        }
        return genres.split(",");
    }
    public boolean isAccepted(){
        return isAccepted != null && !isAccepted.equals("FALSE");
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setIsAccepted(String isAccepted) {
        this.isAccepted = isAccepted;
    }

    public void setAcceptedArtistEmail(String acceptedArtistEmail) {
        this.acceptedArtistEmail = acceptedArtistEmail;
    }

    public String getAcceptedArtistEmail(){
        return acceptedArtistEmail;
    }

    public LocalDate getConvertedDate(){
        String[] strs = date.split("-");
        int year = Integer.parseInt(strs[0]);
        int month = Integer.parseInt(strs[1]);
        int day = Integer.parseInt(strs[2]);
        return LocalDate.of(year, month, day);
    }


}
