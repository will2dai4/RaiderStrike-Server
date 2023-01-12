import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    ServerSocket serverSocket;
    Socket clientSocket;
    PrintWriter output;
    BufferedReader input;
    int clientCounter;

    HashSet<PlayerHandler> handlers;

    public static void main(String[] args) throws IOException{
        Server server = new Server();
    }

    public void runServer() throws IOException{
        System.out.println("Starting Server");
        this.serverSocket = new ServerSocket(Const.PORT);
        System.out.println("Connected at port " + serverSocket.getLocalPort());

        while(true){
            clientSocket = serverSocket.accept();
            clientCounter++;
        }
    }
    
    public void setUp(){

    }

    class PlayerHandler extends Thread{

    }
}
