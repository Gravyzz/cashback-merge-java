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
        Board board = new Board();
        GameState state = new GameState(board, 10);
        assertThat(state.getBoard()).isSameAs(board);
        assertThat(state.getMovesUsed()).isEqualTo(0);
        assertThat(state.getMovesRemaining()).isEqualTo(10);
        assertThat(state.isFinished()).isFalse();
    }

    @Test
    @DisplayName("Валидные ходы учитываются до достижения лимита")
    void validMovesAreRecordedUntilLimit() {
        Board board = new Board();
        GameState state = new GameState(board, 2);
        state.recordValidMove();
        assertThat(state.getMovesUsed()).isEqualTo(1);
        assertThat(state.getMovesRemaining()).isEqualTo(1);
        assertThat(state.isFinished()).isFalse();
        state.recordValidMove();
        assertThat(state.getMovesUsed()).isEqualTo(2);
        assertThat(state.getMovesRemaining()).isEqualTo(0);
        assertThat(state.isFinished()).isTrue();
    }

    @Test
    @DisplayName("Нельзя записать ход после завершения партии")
    void moveAfterGameFinishedIsRejected() {
        Board board = new Board();
        GameState state = new GameState(board, 1);
        state.recordValidMove();
        assertThatThrownBy(() -> state.recordValidMove()).isInstanceOf(IllegalStateException.class);
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
        Board board = new Board();
        assertThatThrownBy(() -> new GameState(board, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new GameState(board, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
