package academy.cashbackmerge.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.cashbackmerge.game.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardRendererTest {

    @Test
    @DisplayName("Поле отображается как текстовая сетка")
    void boardIsRenderedAsTextGrid() {
        Board board = new Board();
        board.placeTile(0, 0, 1);
        board.placeTile(1, 2, 2);
        String expected = String.join(System.lineSeparator(), "1% . . .", ". . 2% .", ". . . .", ". . . .");
        String actual = BoardRenderer.render(board);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Нельзя отобразить отсутствующее поле")
    void nullBoardIsRejected() {

        assertThatThrownBy(() -> BoardRenderer.render(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
