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

    private String location;

    private String phoneNumber;

    private String rating;//SumOfStars-AmountOfRatingsGiven
    private String coordinates;



    public User() {
    }

    public User(String email, String username, String password, String isArtist) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.isArtist = isArtist;
        this.rating = "0-0";
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

    public boolean isArtist(){return isArtist.toLowerCase().equals("true");}

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getDescription() {
        return description;
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


    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRating() {
        return rating;
    }


    public void setRating(String rating) {
        this.rating = rating;
    }
    public int getIntRating(){
        if (rating == null || rating.equals("")) return 0;
        String [] str = rating.split("-");
        int sumOfStars = Integer.parseInt(str[0]);
        int amountOfRatingsGiven = Integer.parseInt(str[1]);
        if(amountOfRatingsGiven == 0) return 0;
        return Math.round(sumOfStars / amountOfRatingsGiven);
    }

    public double[] getCoordinates() {
        if (coordinates == null || coordinates.isEmpty()) return new double[]{};
        String[] s = coordinates.split(",");
        return new double[]{Double.parseDouble(s[0]), Double.parseDouble(s[1])};
    }


    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public void printUser(){
        System.out.println("email: " + email);
        System.out.println("password: " + password);
        System.out.println("username: " + username);
        System.out.println("isArtist: " + isArtist);
        System.out.println("profilePictureUrl: " + profilePictureUrl);
        System.out.println("description: " + description);
        System.out.println("location: " + location);
        System.out.println("phoneNumber: " + phoneNumber);
    }
}
