import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.geom.*;
import java.awt.*;

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
    Round round;
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
                for (Player player: players.values()){
                    player.update();
                }
                switch(this.state){
                    case PREGAME:
                        while (!this.newPlayers.isEmpty()) {
                            this.addPlayer(this.newPlayers.poll());
                        }
                        if(!gameStarted && canStart()){
                            this.start();
                        }
                        break;
                    case LOADING:
                        if(!inGame && allLoaded()){
                            this.load();
                            this.round.start();
                        }
                        break;
                    case BUYPERIOD:
                        if(!this.round.roundTimer.finished()){
                            this.state.nextState();
                        }
                        break;
                    case INGAME:
                        break;
                    case ENDGAME:
                        break;
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
    
    private void start() throws IOException{
        this.printAll("START");  
        this.gameStarted = true;
        this.state = this.state.nextState();
        this.printAll("MAP " + this.map.getName());

        for(Player player: players.values()){
            this.printAll("NAME " + player.getPlayerId() + " " + player.getName());
            this.printAll("AGENT " + player.getPlayerId() + " " + player.getAgent().toString());
            this.printAll("PLAYER_TEAM " + player.getPlayerId() + " " + player.getTeam());
        }
    }
    private void load(){
        this.printAll("START");
        // Decide team roles and set spawn locations
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
            defender.setRoom(this.map.getDefenderRoom());
            this.map.getDefenderRoom().addPlayer(defender);
            this.printAll("PLAYER_ROOM " + defender.getPlayerId() + " " + defender.getRoom().getId());
            this.printAll("PLAYER_LOCATION " + defender.getPlayerId() + " " + defender.getX() + " " + defender.getY());
        }
        for(int i=0;i<attack.getTeamSize();i++){
            Player attacker = attack.getPlayer(i);
            attacker.setX(attackerSpawns[i*2]);
            attacker.setY(attackerSpawns[(i*2)+1]);
            attacker.setRoom(this.map.getAttackerRoom());
            this.map.getAttackerRoom().addPlayer(attacker);
            this.printAll("PLAYER_ROOM " + attacker.getPlayerId() + " " + attacker.getRoom().getId());
            this.printAll("PLAYER_LOCATION " + attacker.getPlayerId() + " " + attacker.getX() + " " + attacker.getY());
        }

        // Give players starting items
        for(Player player: players.values()){
            player.setAlive(true);
            player.setGun(Const.SECONDARY_SLOT, new Gun("Robin", GunModel.Robin.getMaxAmmo()));
            player.getHolding().setActive(true);
            player.setCredits(Const.STARTING_CREDITS);
            this.printAll("PLAYER_GUN " + player.getPlayerId() + " " + player.getHolding().getModel());
            player.print("CREDS " + player.getCredits());
            player.print("AMMO " + player.getHoldingSlot() + " " + player.getHolding().getAmmo());
        }

        this.round = new Round(defend.getTeamNum(), attack.getTeamNum());
        this.round.start();
        this.printAll("ROUND_START");
        this.state = this.state.nextState();
        this.inGame = true;
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
    public void shoot(BulletTracer tracer, Player shooter){
        ArrayList<Obstacle> roomObstacles = shooter.getRoom().getObstacles();
        ArrayList<Player> roomPlayers = shooter.getRoom().getPlayers();
        roomPlayers.remove(shooter);

        ArrayList<GameObject> hits = tracer.hits(roomObstacles, roomPlayers);
        if(roomObstacles.contains(hits.get(0))){
            tracer.closestIntersection(hits.get(0));
            hits.remove(0);
        }

        ArrayList<Player> playerHits = new ArrayList<>();
        for(GameObject object: hits){
            playerHits.add((Player) object);
        }

        for(Player player: playerHits){
            if(player.getTeam() != shooter.getTeam()){
                this.printAll("DAMAGE " + player.getPlayerId());
                player.setHealth(player.getHealth() - tracer.getDamage());
                if(player.getHealth() <= 0){
                    player.setAlive(false);
                    this.printAll("DEATH " + player.getPlayerId() + " " + shooter.getPlayerId() + " " + shooter.getHolding().getModel());
                }
            }
        }
        
        this.printAll("BULLET " + shooter.getRoom().getId() + " " + (int)tracer.getX1() + " " + (int)tracer.getY1() + " " + (int)tracer.getX2() + " " + (int)tracer.getY2());
        shooter.print("AMMO " + shooter.getHoldingSlot() + " " + shooter.getHolding().getAmmo());
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