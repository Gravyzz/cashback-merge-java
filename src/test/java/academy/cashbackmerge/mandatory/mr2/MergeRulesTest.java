package academy.cashbackmerge.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.game.Board;
import academy.cashbackmerge.game.Direction;
import academy.cashbackmerge.game.GameState;
import academy.cashbackmerge.game.LineMergeResult;
import academy.cashbackmerge.game.LineMerger;
import academy.cashbackmerge.game.MoveEngine;
import academy.cashbackmerge.game.MoveResult;
import academy.cashbackmerge.game.TileSpawner;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Обязательные тесты: правила слияния плиток. */
@DisplayName("MR2. Слияние плиток")
class MergeRulesTest {

    @ParameterizedTest
    @CsvSource({
        "'1,1,.,.', '2,.,.,.'",
        "'1,1,1,.', '2,1,.,.'",
        "'1,1,1,1', '2,2,.,.'",
        "'2,2,4,4', '4,8,.,.'",
    })
    @DisplayName("Сдвиг влево: {0} -> {1}")
    void rowShiftsLeftAndMerges(String before, String expected) {
        int[] sourceCells = parseLine(before);
        int[] expectedCells = parseLine(expected);
        LineMergeResult result = LineMerger.merge(sourceCells);
        assertThat(result.cells()).containsExactly(expectedCells);
    }

    @Test
    @DisplayName("Одна плитка не сливается дважды за ход: [1,1,2,.] -> [2,2,.,.]")
    void tileDoesNotMergeTwiceInOneMove() {
        LineMergeResult result = LineMerger.merge(new int[] {1, 1, 2, 0});
        assertThat(result.cells()).containsExactly(2, 2, 0, 0);
        assertThat(result.getScoreGain()).isEqualTo(2);
    }

    @Test
    @DisplayName("Слияние работает во всех четырёх направлениях")
    void mergeWorksInAllDirections() {

        for (Direction direction : Direction.values()) {
            Board board = new Board(2);
            board.placeTile(0, 0, 1);
            board.placeTile(0, 1, 1);
            board.placeTile(1, 0, 1);
            board.placeTile(1, 1, 1);
            GameState gameState = new GameState(board, 5);
            SpawnRule spawnRule = new SpawnRule(1, BigDecimal.ONE);
            Random random = new Random(42);
            TileSpawner spawner = new TileSpawner(List.of(spawnRule), random);
            MoveEngine engine = new MoveEngine(spawner);
            MoveResult result = engine.applyMove(gameState, direction);
            assertThat(result.changed()).isTrue();
            assertThat(result.scoreGain()).isEqualTo(4);
            switch (direction) {
                case LEFT:
                    assertThat(board.get(0, 0)).isEqualTo(2);
                    assertThat(board.get(1, 0)).isEqualTo(2);
                    break;
                case RIGHT:
                    assertThat(board.get(0, 1)).isEqualTo(2);
                    assertThat(board.get(1, 1)).isEqualTo(2);
                    break;
                case UP:
                    assertThat(board.get(0, 0)).isEqualTo(2);
                    assertThat(board.get(0, 1)).isEqualTo(2);
                    break;
                case DOWN:
                    assertThat(board.get(1, 0)).isEqualTo(2);
                    assertThat(board.get(1, 1)).isEqualTo(2);
                    break;
            }

            assertThat(board.getEmptyPositions()).hasSize(1);
        }
    }

    private int[] parseLine(String text) {
        String[] parts = text.split(",");
        int[] cells = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(".")) {
                cells[i] = 0;
            } else {
                cells[i] = Integer.parseInt(parts[i]);
            }
        }
        return cells;
    }
}
