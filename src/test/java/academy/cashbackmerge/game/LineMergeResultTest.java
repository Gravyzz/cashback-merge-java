package academy.cashbackmerge.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineMergeResultTest {

    @Test
    @DisplayName("Изменение исходного массива не меняет результат слияния")
    void changingSourceArrayDoesNotChangeResult() {
        int[] arr = {2, 4, 0, 0};
        LineMergeResult result = new LineMergeResult(arr, 2);
        arr[0] = 8;
        assertThat(result.cells()).containsExactly(2, 4, 0, 0);
        assertThat(result.getScoreGain()).isEqualTo(2);
    }

    @Test
    @DisplayName("Изменение выданного массива не меняет результат слияния")
    void changingReturnedArrayDoesNotChangeResult() {
        int[] arr = {2, 4, 0, 0};
        LineMergeResult result = new LineMergeResult(arr, 2);
        int[] returnedCells = result.cells();
        returnedCells[0] = 8;
        assertThat(result.cells()).containsExactly(2, 4, 0, 0);
    }

    @Test
    @DisplayName("Результат слияния нельзя создать без массива клеток")
    void nullCellsAreRejected() {
        assertThatThrownBy(() -> new LineMergeResult(null, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cells");
    }

    @Test
    @DisplayName("Отрицательный прирост очков отклоняется")
    void negativeScoreGainIsRejected() {

        int[] arr = {2, 4, 0, 0};
        assertThatThrownBy(() -> new LineMergeResult(arr, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("scoreGain");
    }

    @Test
    @DisplayName("Нулевой прирост очков допускается")
    void zeroScoreGainIsAccepted() {
        int[] arr = {2, 4, 0, 0};
        LineMergeResult result = new LineMergeResult(arr, 0);
        assertThat(result.getScoreGain()).isZero();
    }
}
