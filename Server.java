import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    ServerSocket serverSocket;
    Socket clientSocket;
    PrintWriter output;
    BufferedReader input;
    int clientCounter;

    GameState state;
    StateMachine stateMachine; /* TODO: ignore for now */

    Queue<Socket> newPlayers;
    HashMap<Integer, Player> players;
    Team blueTeam;
    Team redTeam;
    boolean gameStarted;
    boolean inGame;

    ArrayList<Thread> playerThreads;
    ConnectionHandler connectionHandler;

    public void go() throws IOException{
        System.out.println("Starting Server...");
        this.serverSocket = new ServerSocket(Const.PORT);
        System.out.println("Connected at port " + serverSocket.getLocalPort());
        this.setUp();

        while(true){
            try {
                while (!this.newPlayers.isEmpty()) {
                    this.addPlayer(this.newPlayers.poll());
                }
                for (Player player: players.values()){
                    player.update();
                }

                if(!gameStarted && canStart()){
                    this.start();
                }
                if(!inGame && allLoaded()){
                    this.inGame = true;
                    this.state = this.state.nextState();
                }
            } catch (Exception e) {e.printStackTrace();}
        }
    }
    private void setUp(){
        this.state = GameState.PREGAME;
        this.newPlayers = new LinkedList<Socket>();
        this.players = new HashMap<>();
        this.blueTeam = new Team(1);
        this.redTeam = new Team(0);
        this.playerThreads = new ArrayList<Thread>();
        this.connectionHandler = new ConnectionHandler(this); this.connectionHandler.start();
    }
    private boolean canStart(){
        if(this.players.size() >= 2){
            for(Player player: this.players.values()){
                if(!player.getReady()){
                    return false;
                }
            } 

            return true;
        }
        return false;
        /* 
        if(this.players.size() == Const.MAX_PLAYER_COUNT){
            for(Player player: this.players.values()){
                if(!player.getReady()){
                    return false;
                }
            } 
            return true;
        }
        return false;
        */ // TODO: reimplement when done testing
    }
    private boolean allLoaded(){
        for(Player player: this.players.values()){
            if(!player.getLoaded()){
                return false;
            }
        } 
        return false;
    }

    private void start() throws IOException{
        this.printAll("START");  
        this.gameStarted = true;
        this.state = this.state.nextState();

        this.printAll("MAP" + "\n");
        BufferedReader mapReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Maps/AugustaMap.txt"))));
        String mapLine = mapReader.readLine();
        while(mapLine != null){
            System.out.println(mapLine);
            this.printAll(mapLine);
            mapLine = mapReader.readLine();
        }
    }
//------------------------------------------------------------------------------------------------------

    public void printAll(String text){
        for(Player player: players.values()){
            player.print(text);
        }
        //System.out.println(text);
    }
    private void addPlayer(Socket socket) throws IOException {
        Player player = new Player(clientCounter++, clientSocket, this);
        this.players.put(player.getPlayerId(), player);
        Thread playerThread = new Thread(player);
        this.playerThreads.add(playerThread); playerThread.start();
        System.out.println(clientCounter + " clients connected.");

        player.print("ID " + player.getPlayerId());
        player.print("TEAM " + redTeam.getTeamSize() + " " + blueTeam.getTeamSize());
        this.printAll("PLAYER " + player.getPlayerId());
        for (Integer id: players.keySet()) {
            player.print("PLAYER " + id); // Gives client its id
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
                    newPlayers.add(clientSocket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}