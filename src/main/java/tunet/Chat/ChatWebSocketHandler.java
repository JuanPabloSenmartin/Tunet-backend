package tunet.Chat;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;

@WebSocket
public class ChatWebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {

    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        //emailME~emailHIM~message
        System.out.println(message);   // Print message
        ChatManager.sendBackMessages(message, session);
    }

}