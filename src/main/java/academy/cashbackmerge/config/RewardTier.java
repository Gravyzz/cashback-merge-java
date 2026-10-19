package academy.cashbackmerge.config;

public record RewardTier(int cashback, int reward) {
    public RewardTier {
        if (cashback < 1) {
            throw new IllegalArgumentException("Кешбэк должен быть положительным");
        }

        if (reward < 0) {
            throw new IllegalArgumentException("Награда должна быть не отрицательной");
        }
    }
}
