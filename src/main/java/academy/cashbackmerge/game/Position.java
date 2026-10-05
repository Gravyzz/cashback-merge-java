package academy.cashbackmerge.game;

public record Position(int row, int column) {
    public Position {
        Board.validateCoordinates(row, column);
    }
}
