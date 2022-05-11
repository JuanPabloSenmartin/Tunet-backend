package tunet.persistence;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;

public class DatabaseServer {

    final private static String DB_LOCATION = "/tmp/db";
    private Server server;

    public void startDBServer() {
        HsqlProperties props = new HsqlProperties();
        props.setProperty("server.database.0", "file:" + DB_LOCATION + "mydb;");
        props.setProperty("server.dbname.0", "xdb");
        server = new org.hsqldb.Server();
        try {
            server.setProperties(props);
        } catch (Exception e) {
            return;
        }
        server.start();
    }

    public void stopDBServer() {
        server.shutdown();
    }

}
