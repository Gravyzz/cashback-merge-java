package academy.cashbackmerge.game;

import java.util.ArrayList;
import java.util.List;

public final class Board {
    private final int size;
    private final int[][] cells;

    public Board(int size) {
        if (size < 2) {
            throw new IllegalArgumentException("Сторона поля должна быть не меньше 2: size=" + size);
        }
        this.size = size;
        cells = new int[size][size];
    }

    private void validateCoordinates(int row, int column) {
        if (row < 0 || row >= size || column < 0 || column >= size) {
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
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (isEmpty(row, column)) {
                    positions.add(createPosition(row, column));
                }
            }
        }
        return List.copyOf(positions);
    }

    public Position createPosition(int row, int column) {
        return new Position(row, column);
    }

    public int getSize() {
        return size;
    }

    public final class Position {
        private final int row;
        private final int column;

        private Position(int row, int column) {
            validateCoordinates(row, column);
            this.row = row;
            this.column = column;
        }

        public int row() {
            return row;
        }

        public int column() {
            return column;
        }

        private Board owner() {
            return Board.this;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof Board.Position other)) {
                return false;
            }
            return Board.this == other.owner() && row == other.row && column == other.column;
        }

        @Override
        public int hashCode() {
            int result = System.identityHashCode(Board.this);
            result = 31 * result + row;
            return 31 * result + column;
        }
    }
}
