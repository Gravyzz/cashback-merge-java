package academy.cashbackmerge.game;

public final class GameState {
    private final Board board;
    private final int movesLimit;
    private int movesUsed;

    public GameState(Board board, int movesLimit) {
        if (board == null) {
            throw new IllegalArgumentException("Доска не должна быть null");
        }
        if (movesLimit <= 0) {
            throw new IllegalArgumentException("Количество ходов должно быть положительным");
        }

        this.board = board;
        this.movesLimit = movesLimit;
        this.movesUsed = 0;
    }

    public Board getBoard() {
        return board;
    }

    public int getMovesUsed() {
        return movesUsed;
    }

    public int getMovesRemaining() {
        return movesLimit - movesUsed;
    }

    public boolean isFinished() {
        return movesUsed == movesLimit;
    }

    public void recordValidMove() {
        if (isFinished()) {
            throw new IllegalStateException("Партия уже завершена");
        }
        movesUsed++;
    }
}
