package academy.cashbackmerge.game;

import academy.cashbackmerge.config.SpawnRule;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public final class TileSpawner {
    private final List<SpawnRule> spawnRules;
    private final Random random;

    public TileSpawner(List<SpawnRule> spawnRules, Random random) {
        if (spawnRules == null || spawnRules.isEmpty()) {
            throw new IllegalArgumentException("Список правил должен существовать");
        }
        if (random == null) {
            throw new IllegalArgumentException("Генератор случайных чисел не должен быть null");
        }
        this.spawnRules = List.copyOf(spawnRules);
        this.random = random;
    }

    public void spawn(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Доска не должна быть null");
        }

        List<Board.Position> emptyPositions = board.getEmptyPositions();
        if (emptyPositions.isEmpty()) {
            throw new IllegalStateException("На поле нет свободных клеток");
        }

        int randomIndex = random.nextInt(emptyPositions.size());
        Board.Position position = emptyPositions.get(randomIndex);
        int cashback = selectCashback();

        board.placeTile(position.row(), position.column(), cashback);
    }

    private int selectCashback() {
        BigDecimal randomValue = BigDecimal.valueOf(random.nextDouble());
        BigDecimal cumulativeProbability = BigDecimal.ZERO;

        for (SpawnRule spawnRule : spawnRules) {
            cumulativeProbability = cumulativeProbability.add(spawnRule.probability());
            if (randomValue.compareTo(cumulativeProbability) < 0) {
                return spawnRule.cashback();
            }
        }

        throw new IllegalStateException("Не удалось выбрать кешбэк по spawn-правилам");
    }
}
