import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    ServerSocket serverSocket;
    Socket clientSocket;
    PrintWriter output;
    BufferedReader input;
    int clientCounter;
    int idCounter = Integer.MIN_VALUE;

    HashSet<PlayerHandler> handlers;
    HashMap<Integer, Player> players;

    public final Object playerLock = new Object();

    public static void main(String[] args) throws IOException{
        Server server = new Server();
        server.runServer();
    }

    public void runServer() throws IOException{
        System.out.println("Starting Server...");
        this.serverSocket = new ServerSocket(Const.PORT);
        System.out.println("Connected at port " + serverSocket.getLocalPort());
        this.setUp();

        while(true){
            clientSocket = serverSocket.accept();
            clientCounter++;
            System.out.println(clientCounter + " clients connected.");
            PlayerHandler playerHandler = new PlayerHandler(clientSocket);
            handlers.add(playerHandler);
            playerHandler.start();
            synchronized(playerLock){
                for(Integer id: this.players.keySet()){
                    this.printNew(this.players.get(id), playerHandler);
                }
            }
        }
    }
    public void setUp(){

    }

    public void printNew(Player player, PlayerHandler playerHandler){
        /* TODO: print statement here */
    }

    class PlayerHandler extends Thread{
        Socket socket;
        PrintWriter output;
        BufferedReader input;
        Player player;
        PlayerHandler(Socket socket){
            this.socket = socket;
        }

        public boolean getAlive(){
            return this.player.getAlive();
        }
        public void kill(){
            Gun gunDrop;
            if(player.getPrimGun() != null){
                gunDrop = this.player.getPrimGun();
            } else {
                gunDrop = this.player.getSecGun();
            }
            this.player.setAlive(false);
            /* TODO: Drop gun function */
        }
        public void run(){
            /* TODO: do this */
        }
    }
    class PlayerThread extends Thread{

    }
    class GunThread extends Thread{

    }
    class GameThread extends Thread{

    }
}
