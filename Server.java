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

    StateMachine state; /* TODO: implement */
    HashSet<PlayerHandler> handlers;
    HashMap<Integer, Player> players;

    HashMap<Integer, Player> handlerMap;

    public final Object playerLock = new Object();

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
            synchronized (playerLock) {
                for (Integer id: this.players.keySet()) {
                    System.out.println("ID " + id); // Gives client its id
                }
            }
        }
    }
    public void setUp(){
        this.state = StateMachine.MenuState;
        this.handlers = new HashSet<>();
        this.players = new HashMap<>();
        this.handlerMap = new HashMap<>();
    }

    public Player createPlayer(PlayerHandler playerHandler){
        Player player = new Player(idCounter++);
        synchronized(playerLock){
            this.players.put(player.getPlayerId(), player);
        }
        this.handlerMap.put(player.getPlayerId(), player);
        playerHandler.print("PLAYER " + player.getPlayerId()); // Gives client player ID
        return player;
    }

    class PlayerHandler extends Thread{
        Socket socket;
        PrintWriter output;
        BufferedReader input;
        Player player;
        PlayerHandler(Socket socket){
            this.socket = socket;
        }

        public void inputName(String name){
            player.setName(name);
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
                    String msg = input.readLine();
                    if(msg.length() > 0){
                        System.out.println("input: " + msg);
                        msg = state.validInput(msg); // checks if the presented command is possible
                        String[] args = msg.split(" ", 2);
                        String command = args[0];
                        try {
                            switch(command){
                                case "ERROR":
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

        public void print(String text) {
            if (this.socket == null) {
                System.out.println("Dead socket, message send failure");
                return;
            };
            output.println(text);
            output.flush();
        }
    }
    class PlayerThread extends Thread{

    }
    class GameThread extends Thread{

    }
}
