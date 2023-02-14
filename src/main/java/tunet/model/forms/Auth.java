package tunet.model.forms;

public class Auth {
    private final String token;

    private Auth(String token) {
        this.token = token;
    }

    public static Auth create(String token) {
        return new Auth(token);
    }

    public String getToken() {
        return token;
    }
}