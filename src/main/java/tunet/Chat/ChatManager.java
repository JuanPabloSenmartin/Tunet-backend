package tunet.Chat;
import org.eclipse.jetty.websocket.api.*;
import tunet.TunetSystem;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {
    static Map<String, Session> emailSessionMap = new ConcurrentHashMap<>();
    private static TunetSystem system;

    public ChatManager(TunetSystem system) {
        ChatManager.system = system;
    }

    public static void sendBackMessages(String message, Session session) throws IOException {
        //emailME~emailHIM~message
        String [] strings = message.split("~");
        checkIfMEexist(strings[0], session);
        Session sessionHIM = HIMexist(strings[1]);
        String messageHIM = "2" + strings[2]; //2 means that he receives message
        if (sessionHIM != null){
            sessionHIM.getRemote().sendString(messageHIM);
        }
        String messageME = "1" + strings[2]; //1 means that its my message
        session.getRemote().sendString(messageME); // and send it back
        persistOperation(strings[0], strings[1], strings[2]);
    }

    private static void persistOperation(String emailME, String emailHIM, String messageME) {
        system.addChats(emailME, emailHIM, messageME);
    }

    private static Session HIMexist(String him) {
        if (emailSessionMap.containsKey(him)) return emailSessionMap.get(him);
        return null;
    }

    private static void checkIfMEexist(String me, Session session) {
        if (!emailSessionMap.containsKey(me)) emailSessionMap.put(me, session);
    }
}