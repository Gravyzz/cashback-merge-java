package academy.cashbackmerge.game;

public final class LineMerger {
    private LineMerger() {}

    public static LineMergeResult merge(int[] cells) {
        if (cells == null) {
            throw new IllegalArgumentException("cells не должен быть null");
        }

        int[] compacted = new int[cells.length];
        int tileCount = 0;

        for (int i = 0; i < cells.length; i++) {
            int current = cells[i];
            if (current < 0) {
                throw new IllegalArgumentException("Значение не может быть отрицательным");
            }
            if (current == 0) {
                continue;
            }
            compacted[tileCount] = current;
            tileCount++;
        }

        int[] merged = new int[cells.length];
        int readIndex = 0;
        int writeIndex = 0;
        int scoreGain = 0;

        while (readIndex < tileCount) {
            int current = compacted[readIndex];
            if (readIndex + 1 < tileCount && current == compacted[readIndex + 1]) {
                current = current * 2;
                merged[writeIndex] = current;
                scoreGain += current;
                readIndex += 2;
            } else {
                merged[writeIndex] = current;
                readIndex += 1;
            }

            writeIndex += 1;
        }
        return new LineMergeResult(merged, scoreGain);
    }
}
