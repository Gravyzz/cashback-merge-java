package academy.cashbackmerge.console;

import academy.cashbackmerge.game.Board;

public final class BoardRenderer {
    private BoardRenderer() {}

    public static String render(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Доска должна быть не null");
        }
        StringBuilder result = new StringBuilder();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                if (column > 0) {
                    result.append(' ');
                }
                if (board.isEmpty(row, column)) {
                    result.append('.');
                } else {
                    result.append(board.get(row, column)).append('%');
                }
            }
            if (row < Board.SIZE - 1) {
                result.append(System.lineSeparator());
            }
        }
        return result.toString();
    }
}
