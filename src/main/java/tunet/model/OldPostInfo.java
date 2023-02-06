package tunet.model;

public class OldPostInfo {
    private final String date;
    private final String description;
    private final String localEmail;
    private final String title;

    private final String artistEmail;

    public OldPostInfo(String date, String description, String localEmail, String title, String artistEmail) {
        this.date = date;
        this.description = description;
        this.localEmail = localEmail;
        this.title = title;
        this.artistEmail = artistEmail;
    }
}
