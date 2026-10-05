package academy.cashbackmerge.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Игровое поле")
class BoardTest {

    @Test
    @DisplayName("Новое поле содержит только пустые клетки")
    void newBoardContainsOnlyEmptyCells() {
        Board board = new Board();
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                assertThat(board.get(i, j)).isEqualTo(0);
            }
        }
    }

    @Test
    @DisplayName("Плитка размещается в пустой клетке")
    void tileCanBePlacedInEmptyCell() {
        Board board = new Board();
        board.placeTile(1, 2, 4);
        assertThat(board.get(1, 2)).isEqualTo(4);
    }

    @Test
    @DisplayName("Пустая клетка определяется как пустая")
    void emptyCellIsReportedAsEmpty() {
        Board board = new Board();
        assertThat(board.isEmpty(0, 0)).isTrue();
    }

    @Test
    @DisplayName("Занятая клетка не определяется как пустая")
    void occupiedCellIsReportedAsNotEmpty() {
        Board board = new Board();
        board.placeTile(0, 0, 4);
        assertThat(board.isEmpty(0, 0)).isFalse();
    }

    @Test
    @DisplayName("Новое поле возвращает все 16 позиций как свободные")
    void newBoardReturnsAllPositionsAsEmpty() {
        Board board = new Board();
        List<Position> emptyPositions = board.getEmptyPositions();
        assertThat(emptyPositions)
                .hasSize(16)
                .contains(new Position(0, 0), new Position(3, 3))
                .doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Занятая позиция не входит в список свободных")
    void occupiedPositionIsExcludedFromEmptyPositions() {
        Board board = new Board();
        board.placeTile(1, 2, 4);
        List<Position> emptyPositions = board.getEmptyPositions();
        assertThat(emptyPositions)
                .hasSize(15)
                .doesNotContain(new Position(1, 2))
                .doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Координаты вне игрового поля отклоняются")
    void coordinatesOutsideBoardAreRejected() {
        Board board = new Board();
        assertThatThrownBy(() -> board.placeTile(-1, 0, 4)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> board.placeTile(Board.SIZE, 0, 4)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> board.placeTile(0, -1, 4)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> board.placeTile(0, Board.SIZE, 4)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Неположительный кешбэк отклоняется")
    void nonPositiveCashbackIsRejected() {
        Board board = new Board();
        assertThatThrownBy(() -> board.placeTile(1, 2, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> board.placeTile(1, 2, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(board.isEmpty(1, 2)).isTrue();
    }

    @Test
    @DisplayName("Нельзя разместить плитку в занятой клетке")
    void placingTileIntoOccupiedCellIsRejected() {
        Board board = new Board();
        board.placeTile(1, 2, 4);
        assertThatThrownBy(() -> board.placeTile(1, 2, 2)).isInstanceOf(IllegalStateException.class);
        assertThat(board.get(1, 2)).isEqualTo(4);
    }
}
