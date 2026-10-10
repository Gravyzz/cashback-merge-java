package academy.cashbackmerge.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Состояние игры")
class GameStateTest {

    @Test
    @DisplayName("Новая партия начинается без использованных ходов")
    void newStateHasNoUsedMoves() {
        Board board = new Board(4);
        GameState state = new GameState(board, 10);
        assertThat(state.getBoard()).isSameAs(board);
        assertThat(state.getMovesUsed()).isEqualTo(0);
        assertThat(state.getMovesRemaining()).isEqualTo(10);
        assertThat(state.isFinished()).isFalse();
    }

    @Test
    @DisplayName("Валидные ходы учитываются до достижения лимита")
    void validMovesAreRecordedUntilLimit() {
        Board board = new Board(4);
        GameState state = new GameState(board, 2);
        state.recordValidMove(0);
        assertThat(state.getMovesUsed()).isEqualTo(1);
        assertThat(state.getMovesRemaining()).isEqualTo(1);
        assertThat(state.isFinished()).isFalse();
        state.recordValidMove(0);
        assertThat(state.getMovesUsed()).isEqualTo(2);
        assertThat(state.getMovesRemaining()).isEqualTo(0);
        assertThat(state.isFinished()).isTrue();
    }

    @Test
    @DisplayName("Очки нескольких валидных ходов складываются")
    void validMovesAccumulateScore() {
        GameState state = new GameState(new Board(2), 3);
        state.recordValidMove(2);
        state.recordValidMove(4);

        assertThat(state.getScore()).isEqualTo(6);
        assertThat(state.getMovesUsed()).isEqualTo(2);
        assertThat(state.getMovesRemaining()).isEqualTo(1);
    }

    @Test
    @DisplayName("Отрицательный прирост очков отклоняется без изменения состояния")
    void negativeScoreGainLeavesStateUnchanged() {
        GameState state = new GameState(new Board(2), 3);
        state.recordValidMove(2);

        assertThatThrownBy(() -> state.recordValidMove(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("scoreGain");
        assertThat(state.getScore()).isEqualTo(2);
        assertThat(state.getMovesUsed()).isEqualTo(1);
        assertThat(state.getMovesRemaining()).isEqualTo(2);
        assertThat(state.isFinished()).isFalse();
    }

    @Test
    @DisplayName("Нельзя записать ход после завершения партии")
    void moveAfterGameFinishedIsRejected() {
        Board board = new Board(4);
        GameState state = new GameState(board, 1);
        state.recordValidMove(0);
        assertThatThrownBy(() -> state.recordValidMove(0)).isInstanceOf(IllegalStateException.class);
        assertThat(state.getMovesUsed()).isEqualTo(1);
        assertThat(state.getMovesRemaining()).isEqualTo(0);
        assertThat(state.isFinished()).isTrue();
    }

    @Test
    @DisplayName("Состояние игры нельзя создать без доски")
    void nullBoardIsRejected() {
        assertThatThrownBy(() -> new GameState(null, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Неположительный лимит ходов отклоняется")
    void nonPositiveMovesLimitIsRejected() {
        Board board = new Board(4);
        assertThatThrownBy(() -> new GameState(board, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new GameState(board, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
