package tunet.model;

import tunet.Util.JsonParser;

public class AcceptArtistForm {
    private final String token;
    private final String artistListId;
    private final String postId;


    public AcceptArtistForm(String token, String artistListId, String postId) {
        this.token = token;
        this.artistListId = artistListId;
        this.postId = postId;
    }

    public static AcceptArtistForm createFromJson(String body) {
        return JsonParser.fromJson(body, AcceptArtistForm.class);
    }

    public String getToken() {
        return token;
    }

    public String getArtistListId() {
        return artistListId;
    }

    public String getPostId() {
        return postId;
    }
}
