package academy.cashbackmerge.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.RewardTier;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.game.GameFactory;
import academy.cashbackmerge.game.GameState;
import academy.cashbackmerge.simulation.BatchSimulator;
import academy.cashbackmerge.simulation.GameSimulationResult;
import academy.cashbackmerge.simulation.SimulationReport;
import academy.cashbackmerge.simulation.SimulationReportCalculator;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: воспроизводимость по seed. */
@DisplayName("MR1. Воспроизводимость по seed")
class SeedTest {

    @Test
    @DisplayName("Seed воспроизводит начальное поле при разных настроенных размерах")
    void seedReproducesBoardsOfDifferentConfiguredSizes() {
        for (int size : new int[] {2, 3, 5}) {
            CampaignConfig config = new CampaignConfig(
                    size,
                    10,
                    List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15"))),
                    List.of(new RewardTier(32, 2000)),
                    32,
                    new BigDecimal("0.20"));
            GameState first = GameFactory.createGame(config, 42).getState();
            GameState second = GameFactory.createGame(config, 42).getState();
            assertThat(first.getBoard().getSize()).isEqualTo(size);
            assertThat(second.getBoard().getSize()).isEqualTo(size);
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    assertThat(first.getBoard().get(row, column))
                            .isEqualTo(second.getBoard().get(row, column));
                }
            }
        }
    }

    @Test
    @DisplayName("Одинаковый конфиг и seed дают одинаковое начальное поле")
    void sameConfigAndSeedProduceSameInitialBoard() {
        CampaignConfig config = new CampaignConfig(
                4,
                10,
                List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15"))),
                List.of(new RewardTier(32, 2000)),
                32,
                new BigDecimal("0.20"));
        GameState firstState = GameFactory.createGame(config, 42).getState();
        GameState secondState = GameFactory.createGame(config, 42).getState();
        for (int row = 0; row < firstState.getBoard().getSize(); row++) {
            for (int column = 0; column < firstState.getBoard().getSize(); column++) {
                assertThat(firstState.getBoard().get(row, column))
                        .isEqualTo(secondState.getBoard().get(row, column));
            }
        }
    }

    @Test
    @DisplayName("Одинаковый конфиг, seed и стратегия дают одинаковый отчёт симуляции")
    void sameInputsProduceSameSimulationReport() {
        CampaignConfig config = new CampaignConfig(
                4,
                10,
                List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15"))),
                List.of(
                        new RewardTier(2, 0),
                        new RewardTier(4, 50),
                        new RewardTier(8, 150),
                        new RewardTier(16, 500),
                        new RewardTier(32, 2000)),
                32,
                new BigDecimal("0.20"));
        long seed = 42L;
        int players = 100;

        List<GameSimulationResult> firstResults = BatchSimulator.simulateAllGames(config, players, seed);
        List<GameSimulationResult> secondResults = BatchSimulator.simulateAllGames(config, players, seed);
        SimulationReport firstReport = SimulationReportCalculator.calculate(firstResults, config, seed, "random");
        SimulationReport secondReport = SimulationReportCalculator.calculate(secondResults, config, seed, "random");

        assertThat(firstReport).isEqualTo(secondReport);
    }
}
