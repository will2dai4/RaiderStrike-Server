public enum GameState {
    PREGAME{
        public GameState nextState(){ return LOADING; }
    },
    LOADING{
        public GameState nextState(){ return INGAME; }
    },
    INGAME{
        public GameState nextState(){ return BUYMENU; }
    },
    BUYMENU{
        public GameState nextState(){ return INGAME; }
    };

    public abstract GameState nextState();
}
