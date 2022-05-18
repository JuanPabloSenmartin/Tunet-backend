package tunet.model;

import tunet.Util.JsonParser;

public class ArtistListForm {
    private final String token;
    private final String postID;

    public ArtistListForm(String token, String postID) {
        this.token = token;
        this.postID = postID;
    }
    public static ArtistListForm createFromJson(String body) {
        return JsonParser.fromJson(body, ArtistListForm.class);
    }
    public boolean isComplete() {
        return !token.equals("") && !postID.equals("");
    }

    public String getToken() {
        return token;
    }

    public String getPostID() {
        return postID;
    }
}
