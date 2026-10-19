package academy.cashbackmerge.simulation;

import academy.cashbackmerge.config.CampaignConfig;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SimulationReportCalculator {
    private static final int RATE_SCALE = 4;

    private SimulationReportCalculator() {}

    public static SimulationReport calculate(
            List<GameSimulationResult> results, CampaignConfig config, long seed, String strategy) {
        if (results == null || results.isEmpty()) {
            throw new IllegalArgumentException("results не должен быть null или пуст");
        }
        if (config == null) {
            throw new IllegalArgumentException("config не должен быть null");
        }
        if (strategy == null || strategy.isBlank()) {
            throw new IllegalArgumentException("strategy не должна быть null или пустой");
        }

        Map<Integer, Integer> maxCashbackDistribution = new HashMap<>();
        Map<Integer, Integer> rewardDistribution = new HashMap<>();
        long totalScore = 0;
        int maxScore = 0;
        int maxRewardPlayers = 0;

        for (GameSimulationResult result : results) {
            maxCashbackDistribution.put(
                    result.maxCashback(), maxCashbackDistribution.getOrDefault(result.maxCashback(), 0) + 1);
            rewardDistribution.put(result.reward(), rewardDistribution.getOrDefault(result.reward(), 0) + 1);
            totalScore += result.score();
            if (result.score() > maxScore) {
                maxScore = result.score();
            }

            if (result.maxCashback() >= config.getMaxRewardCashback()) {
                maxRewardPlayers++;
            }
        }
        BigDecimal totalScoreDecimal = BigDecimal.valueOf(totalScore);
        BigDecimal playersDecimal = BigDecimal.valueOf(results.size());
        BigDecimal averageScore = totalScoreDecimal.divide(playersDecimal, 2, RoundingMode.HALF_UP);
        BigDecimal maxRewardPlayersDecimal = BigDecimal.valueOf(maxRewardPlayers);
        BigDecimal actualMaxRewardRate =
                maxRewardPlayersDecimal.divide(playersDecimal, RATE_SCALE, RoundingMode.HALF_UP);
        BigDecimal allowedMaxRewardPlayers = config.getMaxRewardAllowedRate().multiply(playersDecimal);
        SimulationStatus status = SimulationStatus.FAILED;
        if (maxRewardPlayersDecimal.compareTo(allowedMaxRewardPlayers) <= 0) {
            status = SimulationStatus.OK;
        }
        return new SimulationReport(
                results.size(),
                config.getMovesLimit(),
                seed,
                strategy,
                maxCashbackDistribution,
                rewardDistribution,
                averageScore,
                maxScore,
                actualMaxRewardRate,
                status);
    }
}
