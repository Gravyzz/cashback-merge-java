package academy.cashbackmerge.game;

public final class LineMergeResult {

    private final int[] cells;
    private final int scoreGain;

    public LineMergeResult(int[] cells, int scoreGain) {
        if (cells == null) {
            throw new IllegalArgumentException("cells не должен быть null");
        }
        if (scoreGain < 0) {
            throw new IllegalArgumentException("scoreGain не должен быть отрицательным");
        }
        this.cells = cells.clone();
        this.scoreGain = scoreGain;
    }

    public int[] cells() {
        return cells.clone();
    }

    public int getScoreGain() {
        return scoreGain;
    }
}
