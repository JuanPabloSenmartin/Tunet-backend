package tunet.model.forms;

public class PostulatedPostInfo {
    private final String date;
    private final String description;
    private final String localEmail;
    private final String title;
    private final String accepted;

    public PostulatedPostInfo(String date, String description, String localEmail, String title, String accepted) {
        this.date = date;
        this.description = description;
        this.localEmail = localEmail;
        this.title = title;
        this.accepted = accepted;
    }
}
