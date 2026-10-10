package academy.cashbackmerge.game;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MoveResultTest {

    @Test
    @DisplayName("Результат хода отклоняет отрицательные очки и очки без изменения поля")
    void invalidMoveResultsAreRejected() {
        assertThatThrownBy(() -> new MoveResult(true, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("scoreGain");
        assertThatThrownBy(() -> new MoveResult(false, 4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Неизменившееся поле");
    }
}
