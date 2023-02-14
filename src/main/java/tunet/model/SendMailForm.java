package tunet.model;

import tunet.Util.JsonParser;

public class SendMailForm {
    private final String emailTo;
    private final String type;
    private final String token;
    private final String postTitle;


    public SendMailForm(String emailTo, String type, String token, String postTitle) {
        this.emailTo = emailTo;
        this.type = type;
        this.token = token;
        this.postTitle = postTitle;
    }
    public static SendMailForm createFromJson(String body) {
        return JsonParser.fromJson(body, SendMailForm.class);
    }

    public String getEmailTo() {
        return emailTo;
    }

    public String getType() {
        return type;
    }
    public String getToken(){
        return token;
    }

    public String getPostTitle() {
        return postTitle;
    }
}
