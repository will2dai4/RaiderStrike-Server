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
    Map map;
    boolean gameStarted;
    boolean inGame;

    ArrayList<Thread> playerThreads;
    ConnectionHandler connectionHandler;

    public void go() throws Exception{
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
                    this.load();
                }
            } catch (Exception e) {e.printStackTrace();}
        }
    }
    private void setUp() throws Exception{
        this.state = GameState.PREGAME;
        this.newPlayers = new LinkedList<Socket>();
        this.players = new HashMap<>();
        this.blueTeam = new Team(1);
        this.redTeam = new Team(0);
        this.map = new Map(Const.AUGUSTA_MAP_PATHNAME); this.map.buildMap();

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
        */ // TODO: reimplement with allLoaded() when done testing
    }
    private boolean allLoaded(){
        if(this.players.size() >= 2){
            for(Player player: this.players.values()){
                if(!player.getLoaded()){
                    return false;
                }
            } 
            return true;
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
            this.printAll(mapLine);
            mapLine = mapReader.readLine();
        }

        for(Player player: players.values()){
            this.printAll("NAME " + player.getPlayerId() + " " + player.getName());
            this.printAll("AGENT " + player.getPlayerId() + " " + player.getAgent().toString());
        }
    }
    private void load(){
        this.printAll("START");
        this.state = this.state.nextState();
        this.inGame = true;

        int roleFlip = (int)(Math.random()*2);
        this.redTeam.setRole(roleFlip); this.blueTeam.setRole(1-roleFlip);

        int[] defenderSpawns = map.getDefenderRoom().getSpawn();
        int[] attackerSpawns = map.getAttackerRoom().getSpawn();
        Team attack; Team defend;
        if(this.redTeam.getRole() == 0){
            attack = this.blueTeam; defend = this.redTeam;
        } else {
            attack = this.redTeam; defend = this.blueTeam;
        }

        for(int i=0;i<defend.getTeamSize();i++){
            Player defender = defend.getPlayer(i);
            defender.setX(defenderSpawns[i*2]);
            defender.setY(defenderSpawns[(i*2)+1]);
            this.printAll("PLAYER_LOCATION " + defender.getPlayerId() + " " + defender.getX() + " " + defender.getY());
        }
        for(int i=0;i<attack.getTeamSize();i++){
            Player attacker = defend.getPlayer(i);
            attacker.setX(attackerSpawns[i*2]);
            attacker.setY(attackerSpawns[(i*2)+1]);
            this.printAll("PLAYER_LOCATION " + attacker.getPlayerId() + " " + attacker.getX() + " " + attacker.getY());
        }
    }
//------------------------------------------------------------------------------------------------------

    public void printAll(String text){
        for(Player player: players.values()){
            player.print(text);
        }
    }
    public void printTeam(String text, Team team){
        for(Player player: players.values()){
            if(player.getTeam() == (team.getTeamNum())){
                player.print(text);
            }
        }
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
            if(id != player.getPlayerId()){
                player.print("PLAYER " + id);
            } // Gives client its id
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