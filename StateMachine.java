import java.util.Arrays;

public enum StateMachine {
    MenuState(new String[]{"NAME", "TEAM", "AGENT", "READY"}) {
        public StateMachine nextState(){
            return LoadingState;
        }
    },
    LoadingState(new String[]{"LOADED"}){
        public StateMachine nextState(){
            return GameState;
        }
    },
    GameState(new String[]{"SWAP", "AIM", "MOVE", "FIRE", "RELOAD", "UTIL", "BOMB", "PICKUP"}){
        public StateMachine nextState(){
            return BuyState;
        }
    },
    BuyState(new String[]{"BUY"}){
        public StateMachine nextState(){
            return GameState;
        }
    };

    String[] validCommands;
    StateMachine(String[] valid){
        this.validCommands = valid;
    }

    public String validInput(String input){
        String command = input.split(" ", 2)[0];
        if(Arrays.asList(validCommands).contains(command)){
            return input;
        }
        return "ERROR Invalid command";
    }

    public abstract StateMachine nextState();
}
