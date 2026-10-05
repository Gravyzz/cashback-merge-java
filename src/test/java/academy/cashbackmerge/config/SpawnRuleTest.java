package academy.cashbackmerge.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SpawnRuleTest {

    @Test
    @DisplayName("Корректное правило сохраняет кэшбэк и вероятность")
    void validSpawnRuleStoresValues() {
        SpawnRule rule1 = new SpawnRule(1, new BigDecimal("0.85"));
        assertThat(rule1.cashback()).isEqualTo(1);
        assertThat(rule1.probability()).isEqualTo(new BigDecimal("0.85"));
    }

    @Test
    @DisplayName("Вероятность больше единицы отклоняется")
    void probabilityAboveOneIsRejected() {
        assertThatThrownBy(() -> new SpawnRule(1, new BigDecimal("1.20"))).isInstanceOf(IllegalArgumentException.class);
    }
}
