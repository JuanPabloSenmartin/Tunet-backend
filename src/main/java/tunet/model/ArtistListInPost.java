package tunet.model;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "ARTISTLISTS")
public class ArtistListInPost {
    @Id
    private String id;

    private String postID;

    private String artistEmail;

    public ArtistListInPost(String newID, String postID, String artistEmail) {
        this.id = newID;
        this.postID = postID;
        this.artistEmail = artistEmail;
    }
    public ArtistListInPost(){

    }

    public static ArtistListInPost create(String newID, String postID, String artistEmail) {
        return new ArtistListInPost(newID, postID, artistEmail);
    }

    public String getId() {
        return id;
    }


    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getArtistEmail() {
        return artistEmail;
    }

    public void setArtistEmail(String artistEmail) {
        this.artistEmail = artistEmail;
    }

    public void printUser(){
        System.out.println("id: " + id);
        System.out.println("postID: " + postID);
        System.out.println("artistEmail: " + artistEmail);
    }
}
