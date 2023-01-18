import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    ServerSocket serverSocket;
    Socket clientSocket;
    PrintWriter output;
    BufferedReader input;
    int clientCounter;

    StateMachine state; /* TODO: implement */
    HashMap<Integer, Player> players;
    Team blueTeam;
    Team redTeam;
    boolean gameStarted;

    ArrayList<Thread> playerThreads;
    ConnectionHandler connectionHandler;

    public void go() throws IOException{
        System.out.println("Starting Server...");
        this.serverSocket = new ServerSocket(Const.PORT);
        System.out.println("Connected at port " + serverSocket.getLocalPort());
        this.setUp();

        while(true){
            try {
                for (Player player: players.values()){
                    player.update();
                }
                if(!gameStarted || canStart()){
                    printAll("START");  
                    gameStarted = true;
                }
            } catch (Exception e) {e.printStackTrace();}
        }
    }
    public void setUp(){
        this.players = new HashMap<>();
        this.blueTeam = new Team();
        this.redTeam = new Team();
        this.playerThreads = new ArrayList<Thread>();
        this.connectionHandler = new ConnectionHandler(this); this.connectionHandler.start();
    }
    public boolean canStart(){
        return false;
        /* for(Player player: players.values()){
            if(!player.getReady()){
                return false;
            }
        } 
        return false;*/ // TODO: fix
    }
    public void printAll(String text){
        System.out.println(players.size());
        for(Player player: players.values()){
            player.print(text);
        }
    }

    class ConnectionHandler extends Thread{
        Server server;
        ConnectionHandler(Server server) {
            this.server = server;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    clientSocket = serverSocket.accept();
                    Player player = new Player(clientCounter++, clientSocket, server);
                    System.out.println(clientCounter + " clients connected.");
                    players.put(player.getPlayerId(), player);
                    Thread playerThread = new Thread(player);
                    playerThreads.add(playerThread);
                    playerThread.start();
                    System.out.println("test?");
                    player.print("ID " + player.getPlayerId());
                    player.print("TEAM " + redTeam.getTeamSize() + " " + blueTeam.getTeamSize());
                    for (Integer id: players.keySet()) {
                        player.print("PLAYER " + id); // Gives client its id
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}