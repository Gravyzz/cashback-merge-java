package academy.cashbackmerge.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineMergerTest {

    @Test
    @DisplayName("Одинаковые плитки объединяются через пустые клетки")
    void equalTilesMergeAcrossEmptyCells() {
        int[] arr = {0, 1, 0, 1};
        LineMergeResult mergeResult = LineMerger.merge(arr);
        assertThat(mergeResult.cells()).containsExactly(2, 0, 0, 0);
        assertThat(mergeResult.getScoreGain()).isEqualTo(2);
        assertThat(arr).containsExactly(0, 1, 0, 1);
    }

    @Test
    @DisplayName("Созданная плитка не объединяется повторно на том же ходу")
    void mergedTileDoesNotMergeAgain() {
        int[] arr = {1, 1, 2, 0};
        LineMergeResult mergeResult = LineMerger.merge(arr);
        assertThat(mergeResult.cells()).containsExactly(2, 2, 0, 0);
        assertThat(mergeResult.getScoreGain()).isEqualTo(2);
        assertThat(arr).containsExactly(1, 1, 2, 0);
    }

    @Test
    @DisplayName("Две независимые пары объединяются с суммированием очков")
    void twoIndependentPairsMergeAndAccumulateScore() {
        int[] arr = {2, 2, 4, 4};
        LineMergeResult mergeResult = LineMerger.merge(arr);
        assertThat(mergeResult.cells()).containsExactly(4, 8, 0, 0);
        assertThat(mergeResult.getScoreGain()).isEqualTo(12);
        assertThat(arr).containsExactly(2, 2, 4, 4);
    }

    @Test
    @DisplayName("Разные плитки сдвигаются без начисления очков")
    void differentTilesSlideWithoutScoreGain() {
        int[] arr = {0, 2, 0, 4};
        LineMergeResult mergeResult = LineMerger.merge(arr);
        assertThat(mergeResult.cells()).containsExactly(2, 4, 0, 0);
        assertThat(mergeResult.getScoreGain()).isEqualTo(0);
        assertThat(arr).containsExactly(0, 2, 0, 4);
    }

    @Test
    @DisplayName("Пустая линия остаётся пустой без начисления очков")
    void emptyLineRemainsEmptyWithoutScoreGain() {
        int[] arr = {0, 0, 0, 0};
        LineMergeResult mergeResult = LineMerger.merge(arr);
        assertThat(mergeResult.cells()).containsExactly(0, 0, 0, 0);
        assertThat(mergeResult.getScoreGain()).isEqualTo(0);
        assertThat(arr).containsExactly(0, 0, 0, 0);
    }

    @Test
    @DisplayName("Из трёх одинаковых плиток объединяются только первые две")
    void threeEqualTilesMergeOnlyFirstPair() {
        int[] arr = {1, 1, 1, 0};
        LineMergeResult mergeResult = LineMerger.merge(arr);
        assertThat(mergeResult.cells()).containsExactly(2, 1, 0, 0);
        assertThat(mergeResult.getScoreGain()).isEqualTo(2);
        assertThat(arr).containsExactly(1, 1, 1, 0);
    }

    @Test
    @DisplayName("Отсутствующая линия отклоняется с понятным сообщением")
    void nullLineIsRejected() {
        assertThatThrownBy(() -> LineMerger.merge(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cells");
    }

    @Test
    @DisplayName("Линия с отрицательным значением клетки отклоняется")
    void negativeCellValueIsRejected() {
        int[] arr = {1, -2, 0, 0};
        assertThatThrownBy(() -> LineMerger.merge(arr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("отрицательным");
    }
}
