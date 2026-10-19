package academy.cashbackmerge.simulation;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.game.Game;
import academy.cashbackmerge.game.GameFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BatchSimulator {
    private BatchSimulator() {}

    public static List<GameSimulationResult> simulateAllGames(CampaignConfig config, int players, long seed) {
        if (config == null) {
            throw new IllegalArgumentException("config должен быть не null");
        }
        if (players <= 0) {
            throw new IllegalArgumentException("Число игроков должно быть положительным");
        }

        Random random = new Random(seed);
        List<GameSimulationResult> results = new ArrayList<>();
        for (int i = 0; i < players; i++) {
            long gameSeed = random.nextLong();
            long strategySeed = random.nextLong();
            Game game = GameFactory.createGame(config, gameSeed);
            RandomMoveStrategy strategy = new RandomMoveStrategy(new Random(strategySeed));
            GameSimulationResult result = GameSimulator.simulate(game, strategy, config.getRewardTable());
            results.add(result);
        }
        return List.copyOf(results);
    }
}
