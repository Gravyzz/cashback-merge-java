package academy.cashbackmerge.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.game.Board;
import academy.cashbackmerge.game.Direction;
import academy.cashbackmerge.game.GameState;
import academy.cashbackmerge.game.MoveEngine;
import academy.cashbackmerge.game.MoveResult;
import academy.cashbackmerge.game.TileSpawner;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: движок ходов — что считается ходом и что после него происходит. */
@DisplayName("MR2. Движок ходов")
class MoveEngineTest {

    @Test
    @DisplayName("Невалидный ход не меняет поле, не тратит ход и не создаёт плитку")
    void invalidMoveChangesNothing() {
        Board board = new Board(2);
        board.placeTile(0, 1, 2);
        GameState gameState = new GameState(board, 5);
        SpawnRule spawnRule = new SpawnRule(1, BigDecimal.ONE);
        Random random = new Random(42);
        TileSpawner spawner = new TileSpawner(List.of(spawnRule), random);
        MoveEngine engine = new MoveEngine(spawner);
        MoveResult result = engine.applyMove(gameState, Direction.RIGHT);

        assertThat(result.changed()).isFalse();
        assertThat(result.scoreGain()).isZero();
        assertThat(gameState.getScore()).isZero();
        assertThat(gameState.getMovesUsed()).isEqualTo(0);
        assertThat(gameState.getMovesRemaining()).isEqualTo(5);
        assertThat(board.get(0, 1)).isEqualTo(2);
        assertThat(board.get(0, 0)).isZero();
        assertThat(board.get(1, 0)).isZero();
        assertThat(board.get(1, 1)).isZero();
        assertThat(board.getEmptyPositions()).hasSize(3);
    }

    @Test
    @DisplayName("После валидного хода появляется ровно одна новая плитка")
    void validMoveSpawnsExactlyOneTile() {
        Board board = new Board(2);
        board.placeTile(0, 0, 2);
        GameState gameState = new GameState(board, 5);
        SpawnRule spawnRule = new SpawnRule(1, BigDecimal.ONE);
        Random random = new Random(42);
        TileSpawner spawner = new TileSpawner(List.of(spawnRule), random);
        MoveEngine engine = new MoveEngine(spawner);
        MoveResult result = engine.applyMove(gameState, Direction.RIGHT);

        assertThat(result.changed()).isTrue();
        assertThat(result.scoreGain()).isZero();
        assertThat(gameState.getScore()).isZero();
        assertThat(gameState.getMovesUsed()).isEqualTo(1);
        assertThat(gameState.getMovesRemaining()).isEqualTo(4);
        assertThat(board.get(0, 1)).isEqualTo(2);
        assertThat(board.getEmptyPositions()).hasSize(2);
    }

    @Test
    @DisplayName("После N валидных ходов игра завершена")
    void gameEndsAfterMovesLimit() {
        Board board = new Board(3);
        board.placeTile(0, 0, 2);
        GameState gameState = new GameState(board, 2);
        SpawnRule spawnRule = new SpawnRule(1, BigDecimal.ONE);
        Random random = new Random(42);
        TileSpawner spawner = new TileSpawner(List.of(spawnRule), random);
        MoveEngine engine = new MoveEngine(spawner);
        MoveResult result = engine.applyMove(gameState, Direction.RIGHT);
        assertThat(result.changed()).isTrue();
        assertThat(gameState.getMovesUsed()).isEqualTo(1);
        assertThat(gameState.getMovesRemaining()).isEqualTo(1);
        assertThat(gameState.isFinished()).isFalse();
        result = engine.applyMove(gameState, Direction.DOWN);
        assertThat(result.changed()).isTrue();
        assertThat(gameState.getMovesUsed()).isEqualTo(2);
        assertThat(gameState.getMovesRemaining()).isEqualTo(0);
        assertThat(gameState.isFinished()).isTrue();
        int scoreBefore = gameState.getScore();
        int emptyCellsBefore = board.getEmptyPositions().size();
        assertThatThrownBy(() -> engine.applyMove(gameState, Direction.LEFT)).isInstanceOf(IllegalStateException.class);
        assertThat(gameState.getScore()).isEqualTo(scoreBefore);
        assertThat(board.getEmptyPositions()).hasSize(emptyCellsBefore);
        assertThat(gameState.getMovesUsed()).isEqualTo(2);
        assertThat(gameState.getMovesRemaining()).isEqualTo(0);
        assertThat(gameState.isFinished()).isTrue();
    }

    @Test
    @DisplayName("Достижение плитки 32% не заканчивает игру — доигрываем до лимита ходов")
    void reachingMaxTileDoesNotEndGame() {
        Board board = new Board(3);
        board.placeTile(0, 0, 16);
        board.placeTile(0, 1, 16);
        GameState gameState = new GameState(board, 5);
        SpawnRule spawnRule = new SpawnRule(1, BigDecimal.ONE);
        Random random = new Random(42);
        TileSpawner spawner = new TileSpawner(List.of(spawnRule), random);
        MoveEngine engine = new MoveEngine(spawner);
        MoveResult result = engine.applyMove(gameState, Direction.LEFT);
        assertThat(result.changed()).isTrue();
        assertThat(result.scoreGain()).isEqualTo(32);
        assertThat(gameState.getScore()).isEqualTo(32);
        assertThat(gameState.getMovesUsed()).isEqualTo(1);
        assertThat(gameState.getMovesRemaining()).isEqualTo(4);
        assertThat(board.get(0, 0)).isEqualTo(32);
        assertThat(gameState.isFinished()).isFalse();
        result = engine.applyMove(gameState, Direction.DOWN);
        assertThat(result.changed()).isTrue();
        assertThat(gameState.getMovesUsed()).isEqualTo(2);
        assertThat(gameState.getMovesRemaining()).isEqualTo(3);
        assertThat(gameState.isFinished()).isFalse();
    }

    @Test
    @DisplayName("Score считается по правилам и растёт только при слияниях")
    void scoreIsCalculatedOnMerges() {
        Board board = new Board(3);
        board.placeTile(0, 0, 16);
        board.placeTile(0, 1, 16);
        GameState gameState = new GameState(board, 3);
        SpawnRule spawnRule = new SpawnRule(1, BigDecimal.ONE);
        Random random = new Random(42);
        TileSpawner spawner = new TileSpawner(List.of(spawnRule), random);
        MoveEngine engine = new MoveEngine(spawner);
        MoveResult result = engine.applyMove(gameState, Direction.LEFT);
        assertThat(result.changed()).isTrue();
        assertThat(result.scoreGain()).isEqualTo(32);
        assertThat(gameState.getScore()).isEqualTo(32);
        assertThat(gameState.getMovesUsed()).isEqualTo(1);
        assertThat(gameState.getMovesRemaining()).isEqualTo(2);
        assertThat(gameState.isFinished()).isFalse();
        result = engine.applyMove(gameState, Direction.DOWN);
        assertThat(result.changed()).isTrue();
        assertThat(gameState.isFinished()).isFalse();
        assertThat(result.scoreGain()).isEqualTo(0);
        assertThat(gameState.getScore()).isEqualTo(32);
        assertThat(gameState.getMovesUsed()).isEqualTo(2);
        assertThat(gameState.getMovesRemaining()).isEqualTo(1);
    }
}
