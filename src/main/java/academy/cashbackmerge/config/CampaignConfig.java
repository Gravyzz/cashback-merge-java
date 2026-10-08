package academy.cashbackmerge.config;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CampaignConfig {
    private final int boardSize;
    private final int movesLimit;
    private final List<SpawnRule> spawnRules;

    public CampaignConfig(int boardSize, int movesLimit, List<SpawnRule> spawnRules) {
        if (boardSize < 2) {
            throw new IllegalArgumentException("Сторона поля должна быть не меньше 2: boardSize=" + boardSize);
        }
        if (movesLimit <= 0) {
            throw new IllegalArgumentException("Лимит ходов должен быть больше 0");
        }
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

        this.boardSize = boardSize;
        this.movesLimit = movesLimit;
        this.spawnRules = List.copyOf(spawnRules);
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
}
