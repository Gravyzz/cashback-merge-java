package academy.cashbackmerge.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Позиция на игровом поле")
class PositionTest {

    @Test
    @DisplayName("Валидная позиция хранит строку и столбец")
    void validPositionStoresCoordinates() {
        Position position = new Position(1, 2);
        assertThat(position.row()).isEqualTo(1);
        assertThat(position.column()).isEqualTo(2);
    }

    @Test
    @DisplayName("Позиция за границами поля отклоняется")
    void positionOutsideBoardIsRejected() {
        assertThatThrownBy(() -> new Position(-1, 0)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> new Position(Board.SIZE, 0)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> new Position(0, -1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> new Position(0, Board.SIZE)).isInstanceOf(IndexOutOfBoundsException.class);
    }
}
