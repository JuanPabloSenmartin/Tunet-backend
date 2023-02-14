package tunet.model.forms;

import tunet.Util.JsonParser;

public class GalleryImageForm {
    private final String email;
    private final String imageUrl;

    public GalleryImageForm(String email, String imageUrl) {
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public static GalleryImageForm createFromJson(String body) {
        return JsonParser.fromJson(body, GalleryImageForm.class);
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
