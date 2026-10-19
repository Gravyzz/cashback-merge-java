package academy.cashbackmerge.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CampaignConfig {
    private final int boardSize;
    private final int movesLimit;
    private final List<SpawnRule> spawnRules;
    private final List<RewardTier> rewardTable;
    private final int maxRewardCashback;
    private final BigDecimal maxRewardAllowedRate;

    public CampaignConfig(
            int boardSize,
            int movesLimit,
            List<SpawnRule> spawnRules,
            List<RewardTier> rewardTable,
            int maxRewardCashback,
            BigDecimal maxRewardAllowedRate) {
        if (boardSize < 2) {
            throw new IllegalArgumentException("Сторона поля должна быть не меньше 2: boardSize=" + boardSize);
        }
        if (movesLimit <= 0) {
            throw new IllegalArgumentException("Лимит ходов должен быть больше 0");
        }
        validateSpawnRules(spawnRules);
        validateRewardTable(rewardTable);

        if (maxRewardAllowedRate == null) {
            throw new IllegalArgumentException("maxRewardAllowedRate не должен быть null");
        }

        if (maxRewardAllowedRate.compareTo(BigDecimal.ZERO) < 0 || maxRewardAllowedRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("допустим диапазон от 0 до 1");
        }

        List<RewardTier> sortedRewards = new ArrayList<>(rewardTable);
        sortedRewards.sort(Comparator.comparingInt(RewardTier::cashback));

        if (maxRewardCashback != sortedRewards.getLast().cashback()) {
            throw new IllegalArgumentException("maxRewardCashback должен быть равен наибольшему порогу таблицы");
        }
        this.boardSize = boardSize;
        this.movesLimit = movesLimit;
        this.spawnRules = List.copyOf(spawnRules);
        this.rewardTable = List.copyOf(sortedRewards);
        this.maxRewardCashback = maxRewardCashback;
        this.maxRewardAllowedRate = maxRewardAllowedRate;
    }

    private static void validateSpawnRules(List<SpawnRule> spawnRules) {
        if (spawnRules == null || spawnRules.isEmpty()) {
            throw new IllegalArgumentException("spawnRules не должен быть пустым");
        }

        BigDecimal probability = BigDecimal.ZERO;
        Set<Integer> seenCashbacks = new HashSet<>();
        for (SpawnRule spawnRule : spawnRules) {
            if (spawnRule == null) {
                throw new IllegalArgumentException("spawnRules не должен содержать null");
            }
            if (!seenCashbacks.add(spawnRule.cashback())) {
                throw new IllegalArgumentException("Повторяется cashback в spawnRules: " + spawnRule.cashback());
            }
            probability = probability.add(spawnRule.probability());
        }
        if (probability.compareTo(BigDecimal.ONE) != 0) {
            throw new IllegalArgumentException("Сумма вероятностей должна быть равна 1");
        }
    }

    private static void validateRewardTable(List<RewardTier> rewardTable) {
        if (rewardTable == null || rewardTable.isEmpty()) {
            throw new IllegalArgumentException("rewardTable должен иметь значения");
        }
        Set<Integer> seenRewardCashbacks = new HashSet<>();
        for (RewardTier rewardTier : rewardTable) {
            if (rewardTier == null) {
                throw new IllegalArgumentException("rewardTable не должен содержать null");
            }
            if (!seenRewardCashbacks.add(rewardTier.cashback())) {
                throw new IllegalArgumentException("Пороги cashback не должны повторяться");
            }
        }
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getMovesLimit() {
        return movesLimit;
    }

    public List<SpawnRule> getSpawnRules() {
        return spawnRules;
    }

    public List<RewardTier> getRewardTable() {
        return rewardTable;
    }

    public int getMaxRewardCashback() {
        return maxRewardCashback;
    }

    public BigDecimal getMaxRewardAllowedRate() {
        return maxRewardAllowedRate;
    }
}
