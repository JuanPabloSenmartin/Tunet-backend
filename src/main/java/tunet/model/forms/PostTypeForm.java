package tunet.model.forms;

import tunet.Util.JsonParser;

public class PostTypeForm {
    private final String token;
    private final String type;
    private final String[] date;

    public PostTypeForm(String token, String type, String[] date) {
        this.token = token;
        this.type = type;
        this.date = date;
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

    public String[] getDate() {
        return date;
    }
}
