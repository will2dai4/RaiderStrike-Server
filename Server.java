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

    ArrayList<Thread> playerThreads;
    GameThread gameThread;

    public void go() throws IOException{
        System.out.println("Starting Server...");
        this.serverSocket = new ServerSocket(Const.PORT);
        System.out.println("Connected at port " + serverSocket.getLocalPort());
        this.setUp();

        while(true){
            clientSocket = serverSocket.accept();
            Player player = new Player(clientCounter++, clientSocket, this);
            System.out.println(clientCounter + " clients connected.");
            players.put(player.getPlayerId(), player);
            Thread playerThread = new Thread(player);
            playerThreads.add(playerThread);
            playerThread.start();
            player.print("ID " + player.getPlayerId());
            for (Integer id: this.players.keySet()) {
                player.print("PLAYER " + id); // Gives client its id
            }
        }
    }
    public void setUp(){
        this.players = new HashMap<>();
        this.playerThreads = new ArrayList<Thread>();
        this.gameThread = new GameThread(); this.gameThread.start();
    }
    public void printAll(String text){
        for(Player player: players.values()){
            player.print(text);
        }
    }

    class PlayerThread extends Thread{

    }
    class GameThread extends Thread{
        @Override
        public void run(){
            for(Player player: players.values()){
                player.update();
            }
        }
    }
}
