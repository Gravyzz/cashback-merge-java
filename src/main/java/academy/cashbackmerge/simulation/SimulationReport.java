package academy.cashbackmerge.simulation;

import java.math.BigDecimal;
import java.util.Map;

public record SimulationReport(
        int players,
        int movesLimit,
        long seed,
        String strategy,
        Map<Integer, Integer> maxCashbackDistribution,
        Map<Integer, Integer> rewardDistribution,
        BigDecimal averageScore,
        int maxScore,
        BigDecimal actualMaxRewardRate,
        SimulationStatus status) {
    public SimulationReport {
        maxCashbackDistribution = Map.copyOf(maxCashbackDistribution);
        rewardDistribution = Map.copyOf(rewardDistribution);
    }
}
