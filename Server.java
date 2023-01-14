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

    StateMachine states;
    HashSet<PlayerHandler> handlers;
    HashMap<Integer, Player> players;

    public final Object playerLock = new Object();

    public static void main(String[] args) throws IOException{
        Server server = new Server();
        server.go();
    }

    public void go() throws IOException{
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
        this.states = new StateMachine();
        this.handlers = new HashSet<>();
        this.players = new HashMap<>();
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
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream());
                while(true){
                    String msg;
                    msg = input.readLine();
                    if(msg != null){
                        System.out.println("input: " + msg);
                        String[] args = msg.split(" ");
                        String command = args[0];
                        try {
                            switch(command){
                                case "NAME":
                                case "TEAM":
                                case "AGENT":
                                case "READY":
                                /* TODO: fill in conditions */
                            }
                        } catch (Exception e) {
                            /* TODO: Error message */
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class PlayerThread extends Thread{

    }
    class GunThread extends Thread{

    }
    class GameThread extends Thread{

    }
}
