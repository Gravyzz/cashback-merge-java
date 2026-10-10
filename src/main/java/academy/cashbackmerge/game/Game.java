package academy.cashbackmerge.game;

public final class Game {
    private final GameState state;
    private final MoveEngine engine;

    public Game(GameState state, MoveEngine engine) {
        if (state == null) {
            throw new IllegalArgumentException("state должен быть не null");
        }

        if (engine == null) {
            throw new IllegalArgumentException("engine должен быть не null");
        }

        this.state = state;
        this.engine = engine;
    }

    public GameState getState() {
        return state;
    }

    public MoveResult applyMove(Direction direction) {
        return engine.applyMove(state, direction);
    }
}
