package academy.cashbackmerge.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Позиция на игровом поле")
class PositionTest {

    @Test
    @DisplayName("Границы позиции определяются размером её поля")
    void positionBoundsFollowItsBoardSize() {
        Board small = new Board(3);
        Board large = new Board(5);
        Board.Position position = large.createPosition(4, 4);
        assertThat(position.row()).isEqualTo(4);
        assertThat(position.column()).isEqualTo(4);
        assertThatThrownBy(() -> small.createPosition(4, 4)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Позиции равны только при совпадении поля и координат")
    void positionEqualityIncludesBoardAndCoordinates() {
        Board board = new Board(4);
        Board otherBoard = new Board(4);
        Board.Position first = board.createPosition(1, 2);
        Board.Position same = board.createPosition(1, 2);
        assertThat(first).isEqualTo(same);
        assertThat(same).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(same.hashCode());
        assertThat(first).isNotEqualTo(board.createPosition(2, 1));
        assertThat(first).isNotEqualTo(otherBoard.createPosition(1, 2));
        assertThat(first.equals(null)).isFalse();
        assertThat(first.equals(new Object())).isFalse();
        board.placeTile(1, 2, 4);
        assertThat(first).isEqualTo(same);
        assertThat(first.hashCode()).isEqualTo(same.hashCode());
    }

    @Test
    @DisplayName("Валидная позиция хранит строку и столбец")
    void validPositionStoresCoordinates() {
        Board board = new Board(4);
        Board.Position position = board.createPosition(1, 2);
        assertThat(position.row()).isEqualTo(1);
        assertThat(position.column()).isEqualTo(2);
    }

    @Test
    @DisplayName("Позиция за границами поля отклоняется")
    void positionOutsideBoardIsRejected() {
        Board board = new Board(3);
        assertThatThrownBy(() -> board.createPosition(-1, 0)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> board.createPosition(board.getSize(), 0))
                .isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> board.createPosition(0, -1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> board.createPosition(0, board.getSize()))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }
}
