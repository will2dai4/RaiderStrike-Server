import java.net.*;
import java.util.*;
import java.io.*;

public enum StateMachine {
    MenuState(new String[]{"NAME", "TEAM", "AGENT", "READY"}) {
        public void update(){

        }
        
        public StateMachine nextState(){
            return LoadingState;
        }
    },
    LoadingState(new String[]{"LOADED"}){
        public void update(){

        }
        
        public StateMachine nextState(){
            return GameState;
        }
    },
    GameState(new String[]{"SWAP", "AIM", "MOVE", "FIRE", "RELOAD", "UTIL", "BOMB", "PICKUP"}){
        public void update(){

        }
        
        public StateMachine nextState(){
            return BuyState;
        }
    },
    BuyState(new String[]{"BUY"}){
        public void update(){

        }

        public StateMachine nextState(){
            return GameState;
        }
    };

//----------------------------------------------------------------------------------------------------

    Socket socket;
    BufferedReader input;
    PrintWriter output;
    String[] validCommands;

    StateMachine(String[] valid) {
        this.validCommands = valid;
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void setSocket(Socket socket){
        this.socket = socket;
    }

    public abstract void update();
    public abstract StateMachine nextState();
}
