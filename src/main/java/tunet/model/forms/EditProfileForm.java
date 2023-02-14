package tunet.model.forms;

import tunet.Util.JsonParser;

public class EditProfileForm {
    private final String email;

    private final String description;

    private final String location;

    private final String username;

    private final String phoneNumber;

    public EditProfileForm(String email, String description, String location, String username, String phoneNumber, String genres) {
        this.email = email;
        this.description = description;
        this.location = location;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public static EditProfileForm createFromJson(String body) {
        return JsonParser.fromJson(body, EditProfileForm.class);
    }


    public String getDescription() {
        return description;
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
