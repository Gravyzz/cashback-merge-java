package academy.cashbackmerge.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Игровое поле")
class BoardTest {

    @Test
    @DisplayName("Поля разных размеров независимы и содержат все свои клетки")
    void boardsOfDifferentSizesAreIndependent() {
        for (int size : new int[] {2, 3, 5}) {
            Board board = new Board(size);
            assertThat(board.getSize()).isEqualTo(size);
            assertThat(board.getEmptyPositions()).hasSize(size * size).doesNotHaveDuplicates();
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    assertThat(board.get(row, column)).isZero();
                    assertThat(board.getEmptyPositions()).contains(board.createPosition(row, column));
                }
            }
        }
        Board small = new Board(3);
        Board large = new Board(5);
        large.placeTile(4, 4, 2);
        assertThat(large.get(4, 4)).isEqualTo(2);
        assertThat(small.getEmptyPositions()).hasSize(9);
        assertThatThrownBy(() -> small.get(3, 0)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> small.isEmpty(0, 3)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Размер поля меньше двух отклоняется")
    void boardSizeBelowTwoIsRejected() {
        for (int size : new int[] {-1, 0, 1}) {
            assertThatThrownBy(() -> new Board(size))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("size=" + size);
        }
    }

    @Test
    @DisplayName("Новое поле содержит только пустые клетки")
    void newBoardContainsOnlyEmptyCells() {
        Board board = new Board(4);
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                assertThat(board.get(i, j)).isEqualTo(0);
            }
        }
    }

    @Test
    @DisplayName("Плитка размещается в пустой клетке")
    void tileCanBePlacedInEmptyCell() {
        Board board = new Board(4);
        board.placeTile(1, 2, 4);
        assertThat(board.get(1, 2)).isEqualTo(4);
    }

    @Test
    @DisplayName("Пустая клетка определяется как пустая")
    void emptyCellIsReportedAsEmpty() {
        Board board = new Board(4);
        assertThat(board.isEmpty(0, 0)).isTrue();
    }

    @Test
    @DisplayName("Занятая клетка не определяется как пустая")
    void occupiedCellIsReportedAsNotEmpty() {
        Board board = new Board(4);
        board.placeTile(0, 0, 4);
        assertThat(board.isEmpty(0, 0)).isFalse();
    }

    @Test
    @DisplayName("Новое поле возвращает все 16 позиций как свободные")
    void newBoardReturnsAllPositionsAsEmpty() {
        Board board = new Board(4);
        List<Board.Position> emptyPositions = board.getEmptyPositions();
        assertThat(emptyPositions)
                .hasSize(16)
                .contains(board.createPosition(0, 0), board.createPosition(3, 3))
                .doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Занятая позиция не входит в список свободных")
    void occupiedPositionIsExcludedFromEmptyPositions() {
        Board board = new Board(4);
        board.placeTile(1, 2, 4);
        List<Board.Position> emptyPositions = board.getEmptyPositions();
        assertThat(emptyPositions)
                .hasSize(15)
                .doesNotContain(board.createPosition(1, 2))
                .doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Координаты вне игрового поля отклоняются")
    void coordinatesOutsideBoardAreRejected() {
        Board board = new Board(4);
        assertThatThrownBy(() -> board.placeTile(-1, 0, 4)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> board.placeTile(board.getSize(), 0, 4)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> board.placeTile(0, -1, 4)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> board.placeTile(0, board.getSize(), 4)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Неположительный кешбэк отклоняется")
    void nonPositiveCashbackIsRejected() {
        Board board = new Board(4);
        assertThatThrownBy(() -> board.placeTile(1, 2, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> board.placeTile(1, 2, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(board.isEmpty(1, 2)).isTrue();
    }

    @Test
    @DisplayName("Нельзя разместить плитку в занятой клетке")
    void placingTileIntoOccupiedCellIsRejected() {
        Board board = new Board(4);
        board.placeTile(1, 2, 4);
        assertThatThrownBy(() -> board.placeTile(1, 2, 2)).isInstanceOf(IllegalStateException.class);
        assertThat(board.get(1, 2)).isEqualTo(4);
    }

    @Test
    @DisplayName("Замена содержимого поля обновляет плитки и освобождает клетки")
    void replacingCellsUpdatesTilesAndClearsCells() {
        Board board = new Board(2);
        board.placeTile(0, 0, 1);
        board.placeTile(1, 1, 2);
        int[][] newCells = {
            {4, 0},
            {2, 0}
        };
        board.replaceCells(newCells);
        assertThat(board.get(0, 0)).isEqualTo(4);
        assertThat(board.get(0, 1)).isEqualTo(0);
        assertThat(board.get(1, 0)).isEqualTo(2);
        assertThat(board.get(1, 1)).isEqualTo(0);
    }

    @Test
    @DisplayName("Изменение переданного массива после замены не меняет поле")
    void changingReplacementArrayDoesNotChangeBoard() {
        Board board = new Board(2);
        int[][] newCells = {
            {4, 0},
            {2, 0}
        };
        board.replaceCells(newCells);
        newCells[0][0] = 8;
        assertThat(board.get(0, 0)).isEqualTo(4);
    }

    @Test
    @DisplayName("Ошибка в последней клетке при замене не меняет исходное поле")
    void invalidReplacementLeavesBoardUnchanged() {
        Board board = new Board(2);
        board.placeTile(0, 0, 1);
        board.placeTile(1, 1, 2);
        int[][] newCells = {
            {4, 4},
            {4, -1}
        };
        assertThatThrownBy(() -> board.replaceCells(newCells)).isInstanceOf(IllegalArgumentException.class);
        assertThat(board.get(0, 0)).isEqualTo(1);
        assertThat(board.get(0, 1)).isEqualTo(0);
        assertThat(board.get(1, 0)).isEqualTo(0);
        assertThat(board.get(1, 1)).isEqualTo(2);
    }

    @Test
    @DisplayName("Замена поля отклоняет массив неправильной формы")
    void replacementWithInvalidShapeIsRejected() {
        Board board = new Board(2);
        int[][] newCells1 = {{1, 0}};
        int[][] newCells2 = {{1, 0}, {2}};
        int[][] newCells3 = {{1, 0}, null};
        assertThatThrownBy(() -> board.replaceCells(newCells1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> board.replaceCells(newCells2)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> board.replaceCells(newCells3)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Замена поля отклоняет отсутствующий массив")
    void nullReplacementArrayIsRejected() {
        Board board = new Board(2);
        assertThatThrownBy(() -> board.replaceCells(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("newCells");
    }
}
