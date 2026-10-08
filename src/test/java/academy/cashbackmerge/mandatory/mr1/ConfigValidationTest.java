package academy.cashbackmerge.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.SpawnRule;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: конфиг кампании. */
@DisplayName("MR1. Конфиг кампании")
class ConfigValidationTest {

    @Test
    @DisplayName("Конфиг принимает размеры поля от двух и сохраняет их")
    void configurableBoardSizesAreAccepted() {
        for (int size : new int[] {2, 3, 5}) {
            CampaignConfig config = new CampaignConfig(size, 10, List.of(new SpawnRule(1, BigDecimal.ONE)));
            assertThat(config.getBoardSize()).isEqualTo(size);
        }
    }

    @Test
    @DisplayName("Размер поля меньше двух отклоняется с указанием значения")
    void boardSizeBelowTwoIsRejected() {
        for (int size : new int[] {-1, 0, 1}) {
            assertThatThrownBy(() -> new CampaignConfig(size, 10, List.of(new SpawnRule(1, BigDecimal.ONE))))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("boardSize=" + size);
        }
    }

    @Test
    @DisplayName("Отсутствующий список и null в spawn-правилах отклоняются с контекстом")
    void nullSpawnRulesAreRejectedWithContext() {
        assertThatThrownBy(() -> new CampaignConfig(4, 10, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("spawnRules");
        List<SpawnRule> rules = Arrays.asList(new SpawnRule(1, BigDecimal.ONE), null);
        assertThatThrownBy(() -> new CampaignConfig(4, 10, rules))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("spawnRules")
                .hasMessageContaining("null");
    }

    @Test
    @DisplayName("Валидный конфиг принимается")
    void validConfigIsAccepted() {
        CampaignConfig config = new CampaignConfig(
                4, 10, List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15"))));
        assertThat(config.getBoardSize()).isEqualTo(4);
        assertThat(config.getMovesLimit()).isEqualTo(10);
        assertThat(config.getSpawnRules())
                .containsExactly(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15")));
    }

    @Test
    @DisplayName("Сумма вероятностей spawn-правил не равна 1 — конфиг отклоняется")
    void probabilitiesNotSummingToOneAreRejected() {
        assertThatThrownBy(() -> new CampaignConfig(
                        4,
                        10,
                        List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.10")))))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CampaignConfig(
                        4,
                        10,
                        List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.25")))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("movesLimit <= 0 — конфиг отклоняется")
    void nonPositiveMovesLimitIsRejected() {
        assertThatThrownBy(() -> new CampaignConfig(
                        4,
                        0,
                        List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15")))))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CampaignConfig(
                        4,
                        -1,
                        List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(2, new BigDecimal("0.15")))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Пустой список spawn-правил — конфиг отклоняется")
    void emptySpawnRulesAreRejected() {
        assertThatThrownBy(() -> new CampaignConfig(4, 4, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("spawnRules");
    }

    @Test
    @DisplayName("Повторяющийся уровень кешбэка отклоняется")
    void duplicateCashbackIsRejected() {
        assertThatThrownBy(() -> new CampaignConfig(
                        4,
                        16,
                        List.of(new SpawnRule(1, new BigDecimal("0.85")), new SpawnRule(1, new BigDecimal("0.15")))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cashback");
    }
}
