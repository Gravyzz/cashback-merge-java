package academy.cashbackmerge.mandatory.mr3;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.RewardTier;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.game.Board;
import academy.cashbackmerge.game.TileSpawner;
import academy.cashbackmerge.reward.RewardCalculator;
import academy.cashbackmerge.simulation.GameSimulationResult;
import academy.cashbackmerge.simulation.SimulationReport;
import academy.cashbackmerge.simulation.SimulationReportCalculator;
import academy.cashbackmerge.simulation.SimulationStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: награды, симуляция и отчёт. */
@DisplayName("MR3. Награды и симуляция")
class RewardAndSimulationTest {

    @Test
    @DisplayName("Максимальная плитка 16% даёт награду соответствующего tier")
    void rewardMatchesHighestTile() {
        List<RewardTier> rewardTable = List.of(
                new RewardTier(2, 0),
                new RewardTier(4, 50),
                new RewardTier(8, 150),
                new RewardTier(16, 500),
                new RewardTier(32, 2000));

        int reward = RewardCalculator.calculate(16, rewardTable);
        assertThat(reward).isEqualTo(500);
    }

    @Test
    @DisplayName("Weighted random учитывает вероятности из конфига")
    void spawnRespectsConfiguredProbabilities() {
        List<SpawnRule> rules =
                List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15")));

        TileSpawner spawner = new TileSpawner(rules, new Random(42));
        int oneCount = 0;
        int twoCount = 0;
        int attempts = 10_000;

        for (int i = 0; i < attempts; i++) {
            Board board = new Board(2);
            spawner.spawn(board);
            int cashback = board.getMaxCashback();

            if (cashback == 1) {
                oneCount++;
            } else if (cashback == 2) {
                twoCount++;
            }
        }

        assertThat(oneCount + twoCount).isEqualTo(attempts);
        assertThat(oneCount).isBetween(8_200, 8_800);
        assertThat(twoCount).isBetween(1_200, 1_800);
    }

    @Test
    @DisplayName("Отчёт содержит распределение максимальных плиток и наград")
    void reportContainsDistributions() {
        CampaignConfig config = createConfig("0.50");

        List<GameSimulationResult> results = List.of(
                new GameSimulationResult(16, 500, 100),
                new GameSimulationResult(16, 500, 200),
                new GameSimulationResult(32, 2000, 300));

        SimulationReport report = SimulationReportCalculator.calculate(results, config, 42L, "random");
        assertThat(report.maxCashbackDistribution()).isEqualTo(Map.of(16, 2, 32, 1));

        assertThat(report.rewardDistribution()).isEqualTo(Map.of(500, 2, 2000, 1));
    }

    @Test
    @DisplayName("Отчёт содержит долю игроков с максимальной наградой")
    void reportContainsMaxRewardRate() {
        CampaignConfig config = createConfig("0.50");
        List<GameSimulationResult> results = List.of(
                new GameSimulationResult(16, 500, 100),
                new GameSimulationResult(32, 2000, 200),
                new GameSimulationResult(32, 2000, 300),
                new GameSimulationResult(8, 150, 50));

        SimulationReport report = SimulationReportCalculator.calculate(results, config, 42L, "random");

        assertThat(report.actualMaxRewardRate()).isEqualByComparingTo("0.5000");
    }

    @Test
    @DisplayName("Статус OK, если actualMaxRewardRate <= maxRewardAllowedRate, иначе FAILED")
    void reportStatusReflectsBusinessConstraint() {
        List<GameSimulationResult> results = List.of(
                new GameSimulationResult(16, 500, 100),
                new GameSimulationResult(32, 2000, 200),
                new GameSimulationResult(32, 2000, 300),
                new GameSimulationResult(8, 150, 50));

        SimulationReport allowedReport =
                SimulationReportCalculator.calculate(results, createConfig("0.50"), 42L, "random");
        SimulationReport rejectedReport =
                SimulationReportCalculator.calculate(results, createConfig("0.49"), 42L, "random");

        assertThat(allowedReport.status()).isEqualTo(SimulationStatus.OK);
        assertThat(rejectedReport.status()).isEqualTo(SimulationStatus.FAILED);
    }

    private CampaignConfig createConfig(String maxRewardAllowedRate) {
        return new CampaignConfig(
                4,
                100,
                List.of(new SpawnRule(1, BigDecimal.ONE)),
                List.of(new RewardTier(8, 150), new RewardTier(16, 500), new RewardTier(32, 2000)),
                32,
                new BigDecimal(maxRewardAllowedRate));
    }
}
