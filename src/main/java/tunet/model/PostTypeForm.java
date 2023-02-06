package tunet.model;

import tunet.Util.JsonParser;

public class PostTypeForm {
    private final String token;
    private final String type;

    public PostTypeForm(String token, String type) {
        this.token = token;
        this.type = type;
    }
    public static PostTypeForm createFromJson(String body) {
        return JsonParser.fromJson(body, PostTypeForm.class);
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }
}
