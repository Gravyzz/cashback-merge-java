package academy.cashbackmerge.game;

import academy.cashbackmerge.config.CampaignConfig;
import java.util.Random;

public final class GameFactory {
    private static final int INITIAL_TILES = 2;

    private GameFactory() {}

    public static GameState createInitialState(CampaignConfig config, long seed) {
        if (config == null) {
            throw new IllegalArgumentException("Конфиг должен существовать");
        }
        Random random = new Random(seed);
        Board board = new Board();
        TileSpawner spawner = new TileSpawner(config.getSpawnRules(), random);
        for (int i = 0; i < INITIAL_TILES; i++) {
            spawner.spawn(board);
        }
        return new GameState(board, config.getMovesLimit());
    }
}
