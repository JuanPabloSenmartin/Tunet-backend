package tunet.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SONGS")
public class Song {
    @Id
    private String id;

    private String email;

    private String songUrl;

    public Song(){}

    public Song(String id, String email, String songUrl) {
        this.id = id;
        this.email = email;
        this.songUrl = songUrl;
    }
    public static Song create(String id, String email, String songUrl) {
        return new Song(id, email, songUrl);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getId() {
        return id;
    }
}
