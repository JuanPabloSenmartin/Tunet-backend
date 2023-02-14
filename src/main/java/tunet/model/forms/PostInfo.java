package tunet.model.forms;

public class PostInfo {
    private final String id;
    private final String date;
    private final String genre;
    private final int rating;
    private final String description;
    private final String localEmail;
    private final String title;
    private final String picture;
    private final String distance;
    private final String artistEmail;

    public PostInfo(String id, String date, String description, String localEmail, String title, String genre, int rating, String picture, String distance, String artistEmail) {
        this.id = id;
        this.genre = genre;
        this.rating = rating;
        this.date = date;
        this.description = description;
        this.localEmail = localEmail;
        this.title = title;
        this.picture = picture;
        this.distance = distance;
        this.artistEmail = artistEmail;
    }
}
