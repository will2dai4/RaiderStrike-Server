/*
 * Class that dictates the available functions based on following game states
 */

public enum GameState {
    PREGAME{
        public GameState nextState(){ return LOADING; }
    },
    LOADING{
        public GameState nextState(){ return BUYPERIOD; }
    },
    BUYPERIOD{
        public GameState nextState(){ return INGAME; }
    },
    INGAME{
        public GameState nextState(){ return BUYPERIOD; }
    },
    ENDGAME{
        public GameState nextState(){ return null; }
    };

    public abstract GameState nextState();
}
