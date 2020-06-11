package ServerSide;

import java.io.IOException;
import java.util.logging.Logger;


public class Server{
    private static Logger logger=Logger.getLogger(Server.class.getName());

    private String name ="Server";

    public String getName() {
        return name;
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        logger.info("Server is starting connection");
    ServerConnection serverConnection = new ServerConnection();
    serverConnection.work();

}
}
