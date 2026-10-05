package academy.cashbackmerge.config;

import java.math.BigDecimal;

public record SpawnRule(int cashback, BigDecimal probability) {
    public SpawnRule {
        if (probability == null) {
            throw new IllegalArgumentException("Вероятность не может быть null");
        }
        if (cashback <= 0) {
            throw new IllegalArgumentException("Кешбэк должен быть положительный");
        }

        if (probability.compareTo(BigDecimal.ZERO) < 0 || probability.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Вероятности указаны вне диапазона");
        }
    }
}
