package academy.cashbackmerge.reward;

import academy.cashbackmerge.config.RewardTier;
import java.util.List;

public final class RewardCalculator {
    private RewardCalculator() {}

    public static int calculate(int maxCashback, List<RewardTier> rewardTable) {
        int reward = 0;
        for (RewardTier rewardTier : rewardTable) {
            int currentReward = rewardTier.reward();
            if (rewardTier.cashback() > maxCashback) {
                break;
            }
            reward = currentReward;
        }
        return reward;
    }
}
