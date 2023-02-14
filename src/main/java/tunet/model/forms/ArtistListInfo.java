package tunet.model.forms;

public class ArtistListInfo {
    private final String id;

    private final String artistEmail;

    private final int artistRating;

    private final String artistProfPic;

    public ArtistListInfo(String id, String artistEmail, int artistRating, String artistProfPic) {
        this.id = id;
        this.artistEmail = artistEmail;
        this.artistRating = artistRating;
        this.artistProfPic = artistProfPic;
    }
}
