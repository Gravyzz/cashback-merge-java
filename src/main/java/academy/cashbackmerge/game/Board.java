package academy.cashbackmerge.game;

import java.util.ArrayList;
import java.util.List;

public final class Board {
    public static final int SIZE = 4;
    private final int[][] cells;

    public Board() {
        this.cells = new int[SIZE][SIZE];
    }

    static void validateCoordinates(int row, int column) {
        if (row < 0 || row >= SIZE || column < 0 || column >= SIZE) {
            throw new IndexOutOfBoundsException("Координаты вне поля: row=" + row + ", column=" + column);
        }
    }

    public int get(int row, int column) {
        validateCoordinates(row, column);
        return cells[row][column];
    }

    public void placeTile(int row, int column, int cashback) {
        validateCoordinates(row, column);
        if (cashback <= 0) {
            throw new IllegalArgumentException("Кешбэк должен быть положительным");
        }
        if (cells[row][column] != 0) {
            throw new IllegalStateException("Клетка уже занята");
        }
        cells[row][column] = cashback;
    }

    public boolean isEmpty(int row, int column) {
        return get(row, column) == 0;
    }

    public List<Position> getEmptyPositions() {
        List<Position> positions = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (isEmpty(row, column)) {
                    positions.add(new Position(row, column));
                }
            }
        }
        return List.copyOf(positions);
    }
}
