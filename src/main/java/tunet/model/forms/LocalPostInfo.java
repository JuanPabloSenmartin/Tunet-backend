package tunet.model.forms;

import java.util.List;

public class LocalPostInfo {
    private final String id;

    private final String localEmail;

    private final String description;

    private final String title;

    private final String date;

    private final String genres;

    private final List<ArtistListInfo> artistList;

    public LocalPostInfo(String id, String localEmail, String description, String title, String date, String genres, List<ArtistListInfo> artistList) {
        this.id = id;
        this.localEmail = localEmail;
        this.description = description;
        this.title = title;
        this.date = date;
        this.genres = genres;
        this.artistList = artistList;
    }
}
