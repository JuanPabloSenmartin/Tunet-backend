package tunet.model.forms;


public class ChatForm {
    private final String id;
    private final String emailME;
    private final String emailHIM;
    private final String profPicHIM;
    private final String profPicME;
    private final String usernameHIM;
    private final String usernameME;
    private final String isArtistME;


    public ChatForm(String id, String emailME, String emailHIM, String profPicHIM, String profPicME, String usernameHIM, String usernameME, String isArtistME) {
        this.id = id;
        this.emailME = emailME;
        this.emailHIM = emailHIM;
        this.profPicHIM = profPicHIM;
        this.profPicME = profPicME;
        this.usernameHIM = usernameHIM;
        this.usernameME = usernameME;
        this.isArtistME = isArtistME;
    }

}
