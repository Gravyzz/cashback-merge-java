package academy.cashbackmerge.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.cashbackmerge.config.SpawnRule;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Появление новых плиток")
class TileSpawnerTest {

    @Test
    @DisplayName("Спавнер добавляет одну настроенную плитку на пустое поле")
    void spawnAddsOneConfiguredTileToEmptyBoard() {
        List<SpawnRule> spawnRules = List.of(new SpawnRule(1, BigDecimal.ONE));
        Random random = new Random(42);
        Board board = new Board();
        TileSpawner spawner = new TileSpawner(spawnRules, random);
        spawner.spawn(board);
        assertThat(board.getEmptyPositions()).hasSize(15);
        int totalCashback = 0;
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                totalCashback += board.get(row, column);
            }
        }
        assertThat(totalCashback).isEqualTo(1);
    }

    @Test
    @DisplayName("Два вызова спавнера занимают две разные клетки")
    void twoSpawnsOccupyTwoDifferentCells() {
        List<SpawnRule> spawnRules = List.of(new SpawnRule(1, BigDecimal.ONE));
        Random random = new Random(42);
        Board board = new Board();
        TileSpawner spawner = new TileSpawner(spawnRules, random);
        spawner.spawn(board);
        spawner.spawn(board);
        assertThat(board.getEmptyPositions()).hasSize(14);
        int totalCashback = 0;
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                totalCashback += board.get(row, column);
            }
        }
        assertThat(totalCashback).isEqualTo(2);
    }

    @Test
    @DisplayName("Нельзя добавить плитку на заполненное поле")
    void spawnOnFullBoardIsRejected() {

        Board board = new Board();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                board.placeTile(row, column, 1);
            }
        }
        List<SpawnRule> spawnRules = List.of(new SpawnRule(1, BigDecimal.ONE));
        Random random = new Random(42);
        TileSpawner spawner = new TileSpawner(spawnRules, random);
        assertThatThrownBy(() -> spawner.spawn(board)).isInstanceOf(IllegalStateException.class);
        assertThat(board.getEmptyPositions()).isEmpty();
    }

    @Test
    @DisplayName("Отсутствующий или пустой список spawn-правил отклоняется")
    void nullOrEmptySpawnRulesAreRejected() {
        List<SpawnRule> spawnRulesEmpty = List.of();
        Random random = new Random(42);
        assertThatThrownBy(() -> new TileSpawner(spawnRulesEmpty, random)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TileSpawner(null, random)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Спавнер нельзя создать без генератора случайных чисел")
    void nullRandomIsRejected() {
        List<SpawnRule> spawnRules = List.of(new SpawnRule(1, BigDecimal.ONE));
        assertThatThrownBy(() -> new TileSpawner(spawnRules, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Нельзя добавить плитку на отсутствующее поле")
    void nullBoardIsRejected() {
        List<SpawnRule> spawnRules = List.of(new SpawnRule(1, BigDecimal.ONE));
        Random random = new Random(42);
        TileSpawner spawner = new TileSpawner(spawnRules, random);
        assertThatThrownBy(() -> spawner.spawn(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
