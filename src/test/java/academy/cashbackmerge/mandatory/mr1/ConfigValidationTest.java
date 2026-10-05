package academy.cashbackmerge.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.SpawnRule;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: конфиг кампании. */
@DisplayName("MR1. Конфиг кампании")
class ConfigValidationTest {

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
        assertThatThrownBy(() -> new CampaignConfig(4, 4, List.of())).isInstanceOf(IllegalArgumentException.class);
    }
}
