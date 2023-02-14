package tunet.model.forms;
import tunet.Util.JsonParser;

public class GallerySongForm {
    private final String email;
    private final String songUrl;

    public GallerySongForm(String email, String songUrl) {
        this.email = email;
        this.songUrl = songUrl;
    }

    public static GallerySongForm createFromJson(String body) {
        return JsonParser.fromJson(body, GallerySongForm.class);
    }

    public String getEmail() {
        return email;
    }

    public String getSongUrl() {
        return songUrl;
    }
}
