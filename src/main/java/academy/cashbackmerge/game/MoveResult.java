package academy.cashbackmerge.game;

public record MoveResult(boolean changed, int scoreGain) {
    public MoveResult {
        if (scoreGain < 0) {
            throw new IllegalArgumentException("scoreGain не должен быть отрицательным");
        }

        if (!changed && scoreGain > 0) {
            throw new IllegalArgumentException("Неизменившееся поле не может приносить очки");
        }
    }
}
