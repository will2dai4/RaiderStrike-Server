public class StateMachine {
    State[] states = new State[]{
        new MenuState(), new LoadingState(), new GameState()
    };
}