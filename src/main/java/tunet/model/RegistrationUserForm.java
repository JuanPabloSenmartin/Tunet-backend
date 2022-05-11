package tunet.model;

import tunet.Util.JsonParser;

public class RegistrationUserForm {
    private final String email;
    private final String password;
    private final String username;
    private final String isArtist;

    public RegistrationUserForm(String email, String username, String password, String isArtist) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.isArtist = isArtist;
    }

    public static RegistrationUserForm createFromJson(String body) {
        return JsonParser.fromJson(body, RegistrationUserForm.class);
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String isArtist() {return isArtist;}

    public boolean isComplete() {
        return !email.equals("") && !password.equals("") && !username.equals("") && !isArtist.equals("");
    }
}
