package tunet.model;

import tunet.Util.JsonParser;

public class PostForm {
    private final String localEmail;
    private final String description;
    private final String title;
    private final String date;

    public PostForm(String localEmail, String description, String title, String date) {
        this.localEmail = localEmail;
        this.description = description;
        this.title = title;
        this.date = date;
    }

    public static PostForm createFromJson(String body) {
        return JsonParser.fromJson(body, PostForm.class);
    }



    public String getLocalEmail() {
        return localEmail;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public boolean isComplete() {
        return !localEmail.equals("") && !description.equals("") && !title.equals("") && !date.equals("");
    }
}
