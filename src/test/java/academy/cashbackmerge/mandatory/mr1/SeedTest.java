package academy.cashbackmerge.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.game.GameFactory;
import academy.cashbackmerge.game.GameState;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Disabled;
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
                    List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15"))));
            GameState first = GameFactory.createInitialState(config, 42);
            GameState second = GameFactory.createInitialState(config, 42);
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
                4, 10, List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15"))));
        GameState firstState = GameFactory.createInitialState(config, 42);
        GameState secondState = GameFactory.createInitialState(config, 42);
        for (int row = 0; row < firstState.getBoard().getSize(); row++) {
            for (int column = 0; column < firstState.getBoard().getSize(); column++) {
                assertThat(firstState.getBoard().get(row, column))
                        .isEqualTo(secondState.getBoard().get(row, column));
            }
        }
    }

    @Test
    @Disabled("MR3: реализуй тест и удали эту строку")
    @DisplayName("Одинаковый конфиг, seed и стратегия дают одинаковый отчёт симуляции")
    void sameInputsProduceSameSimulationReport() {
        fail("Тест не реализован");
    }
}
