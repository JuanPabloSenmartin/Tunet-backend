package tunet;


import tunet.persistence.DatabaseServer;
import tunet.persistence.EntityManagers;
import spark.Spark;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.io.IOException;

import static spark.Spark.port;
import static spark.Spark.staticFiles;

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
        routes.create(system);
    }

    private void stopDatabase() {
        db.stopDBServer();
    }

    private void stopWebServer() {
        Spark.stop();
    }

}
