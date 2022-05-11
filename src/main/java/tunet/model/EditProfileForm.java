package tunet.model;

import tunet.Util.JsonParser;

public class EditProfileForm {
    private final String email;

    private final String profilePictureUrl;

    private final String description;

    private final String pictureUrl;

    private final String artistVideoUrl;

    private final String location;

    private final String username;

    private final String phoneNumber;

    public EditProfileForm(String email, String profilePictureUrl, String description, String pictureUrl, String artistVideoUrl, String location, String username, String phoneNumber) {
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.artistVideoUrl = artistVideoUrl;
        this.location = location;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public static EditProfileForm createFromJson(String body) {
        return JsonParser.fromJson(body, EditProfileForm.class);
    }

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

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
    public String getPhoneNumber(){return phoneNumber;}
}
