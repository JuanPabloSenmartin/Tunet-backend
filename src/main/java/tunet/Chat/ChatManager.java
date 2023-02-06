package tunet.Chat;
import org.eclipse.jetty.websocket.api.*;
import tunet.TunetSystem;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {
    public Map<String, Session> emailSessionMap = new ConcurrentHashMap<>();
    private final TunetSystem system;

    public ChatManager(TunetSystem system) {
        this.system = system;
    }

    public void handleMessage(String message, Session session) throws IOException {
        //emailME~emailHIM~message
        String [] strings = message.split("~");
        //checkIfMEexist(strings[0], session);
        Session sessionHIM = getHIMsession(strings[1]);
        String messageHIM = "2" + strings[2]; //2 means that he receives message
        if (sessionHIM != null){
            sessionHIM.getRemote().sendString(messageHIM);
        }
        String messageME = "1" + strings[2]; //1 means that it's my message
        session.getRemote().sendString(messageME); // and send it back
        persistOperation(strings[0], strings[1], strings[2]);
    }

    private boolean isArtistMe(String mail) {
        return system.findUserByEmail(mail).get().isArtist();
    }

    public void persistOperation(String emailME, String emailHIM, String messageME) {
        system.addChats(emailME, emailHIM, messageME, isArtistMe(emailME));
    }

    private Session getHIMsession(String him) {
        if (emailSessionMap.containsKey(him)) return emailSessionMap.get(him);
        return null;
    }


}