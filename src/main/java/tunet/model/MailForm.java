package tunet.model;

import tunet.Util.JsonParser;

public class MailForm {
    private final String email;

    public MailForm(String email) {
        this.email = email;
    }
    public static MailForm createFromJson(String body) {
        return JsonParser.fromJson(body, MailForm.class);
    }

    public String getEmail() {
        return email;
    }
}
