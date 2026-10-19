package academy.cashbackmerge.simulation;

import academy.cashbackmerge.config.RewardTier;
import academy.cashbackmerge.game.Direction;
import academy.cashbackmerge.game.Game;
import academy.cashbackmerge.reward.RewardCalculator;
import java.util.List;

public final class GameSimulator {
    private GameSimulator() {}

    public static GameSimulationResult simulate(Game game, MoveStrategy strategy, List<RewardTier> rewardTable) {
        if (game == null) {
            throw new IllegalArgumentException("game должно быть не null");
        }
        if (strategy == null) {
            throw new IllegalArgumentException("strategy должно быть не null");
        }
        if (rewardTable == null) {
            throw new IllegalArgumentException("rewardTable должно быть не null");
        }

        while (!game.getState().isFinished()) {
            if (!tryMakeMove(game, strategy)) {
                break;
            }
        }

        int maxCashback = game.getState().getBoard().getMaxCashback();
        int reward = RewardCalculator.calculate(maxCashback, rewardTable);
        int score = game.getState().getScore();
        return new GameSimulationResult(maxCashback, reward, score);
    }

    private static boolean tryMakeMove(Game game, MoveStrategy strategy) {
        List<Direction> directions = strategy.getMoveOrder(game.getState());
        for (Direction direction : directions) {
            if (game.applyMove(direction).changed()) {
                return true;
            }
        }
        return false;
    }
}
