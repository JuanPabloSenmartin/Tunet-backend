package tunet.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    private String email;

    private String password;

    private String username;

    private String isArtist;

    private String profilePictureUrl;

    private String description;

    private String pictureUrl;

    private String artistVideoUrl;

    private String location;

    private String phoneNumber;

    public User() {
    }

    public User(String email, String username, String password, String isArtist) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.isArtist = isArtist;
    }

    public static User create(String email,String username, String password, String isArtist) {
        return new User(email,username, password, isArtist);
    }


    //GETTERS AND SETTERS
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername(){return username;}

    public boolean isArtist(){return isArtist.equals("TRUE");}

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getArtistVideoUrl() {
        return artistVideoUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setArtistVideoUrl(String artistVideoUrl) {
        this.artistVideoUrl = artistVideoUrl;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void printUser(){
        System.out.println("email: " + email);
        System.out.println("password: " + password);
        System.out.println("username: " + username);
        System.out.println("isArtist: " + isArtist);
        System.out.println("profilePictureUrl: " + profilePictureUrl);
        System.out.println("description: " + description);
        System.out.println("pictureUrl: " + pictureUrl);
        System.out.println("artistVideoUrl: " + artistVideoUrl);
        System.out.println("location: " + location);
        System.out.println("phoneNumber: " + phoneNumber);
    }
}
