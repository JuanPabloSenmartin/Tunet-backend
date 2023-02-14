package tunet.model.forms;

import tunet.Util.JsonParser;

import java.util.List;

public class AuthRequest {
    private final String email;
    private final String password;
    private boolean isArtist;


    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static AuthRequest create(List<String> email, List<String> password) {
        return new AuthRequest(email.get(0), password.get(0));
    }

    public static AuthRequest createFromJson(String body) {
        return JsonParser.fromJson(body, AuthRequest.class);
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isArtist() {return isArtist;}

    public void setIsArtist(boolean isArtist) {
        this.isArtist = isArtist;
    }
}
