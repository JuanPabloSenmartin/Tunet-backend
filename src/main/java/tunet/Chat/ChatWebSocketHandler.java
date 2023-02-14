package tunet.Chat;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;

@WebSocket
public class ChatWebSocketHandler {
    private final ChatManager chatManager;
    public ChatWebSocketHandler(ChatManager chatManager){
        this.chatManager = chatManager;
    }
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        System.out.println("connect");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        //emailME~emailHIM~message
        if(message.charAt(0) == '#') chatManager.emailSessionMap.put(message.substring(1), session);
        else chatManager.handleMessage(message, session);
        }

}