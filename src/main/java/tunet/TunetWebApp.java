package tunet;


import tunet.Chat.ChatManager;
import tunet.Chat.ChatWebSocketHandler;
import tunet.persistence.DatabaseServer;
import spark.Spark;

import java.io.IOException;

import static spark.Spark.*;

public class TunetWebApp {

    private final Routes routes = new Routes();
    private final DatabaseServer db = new DatabaseServer();

    public void start() throws IOException {
        startDatabase();
        startWebServer();

    }

    public void stop() {
        stopWebServer();
        stopDatabase();
    }

    private void startDatabase() {
        db.startDBServer();
    }

    private void startWebServer() throws IOException {
        staticFiles.location("public");
        port(4321);
        final TunetSystem system = TunetSystem.create("tunet-db");
        ChatManager chatManager = new ChatManager(system);
        webSocket("/chat", new ChatWebSocketHandler(chatManager));
        routes.create(system);
    }

    private void stopDatabase() {
        db.stopDBServer();
    }

    private void stopWebServer() {
        Spark.stop();
    }

}
