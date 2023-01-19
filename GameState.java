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
    };

    public abstract GameState nextState();
}
