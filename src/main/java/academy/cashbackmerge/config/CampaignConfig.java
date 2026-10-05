package academy.cashbackmerge.config;

import java.math.BigDecimal;
import java.util.List;

public final class CampaignConfig {
    private static final int REQUIRED_BOARD_SIZE = 4;
    private final int boardSize;
    private final int movesLimit;
    private final List<SpawnRule> spawnRules;

    public CampaignConfig(int boardSize, int movesLimit, List<SpawnRule> spawnRules) {
        if (boardSize != REQUIRED_BOARD_SIZE) {
            throw new IllegalArgumentException("Размер стороны поля допускается только " + REQUIRED_BOARD_SIZE);
        }
        if (movesLimit <= 0) {
            throw new IllegalArgumentException("Лимит ходов должен быть больше 0");
        }
        if (spawnRules == null || spawnRules.isEmpty()) {
            throw new IllegalArgumentException("Список должен быть не пуст");
        }

        BigDecimal probability = BigDecimal.ZERO;
        for (SpawnRule spawnRule : spawnRules) {
            if (spawnRule == null) {
                throw new IllegalArgumentException();
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
