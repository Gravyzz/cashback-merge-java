package academy.cashbackmerge.game;

public final class MoveEngine {
    private final TileSpawner spawner;

    public MoveEngine(TileSpawner spawner) {
        if (spawner == null) {
            throw new IllegalArgumentException("spawner не должен быть null");
        }
        this.spawner = spawner;
    }

    public MoveResult applyMove(GameState state, Direction direction) {
        if (state == null) {
            throw new IllegalArgumentException("state не должен быть null");
        }
        if (direction == null) {
            throw new IllegalArgumentException("direction не должен быть null");
        }
        if (state.isFinished()) {
            throw new IllegalStateException("Партия уже завершена");
        }

        Board board = state.getBoard();
        int size = board.getSize();
        int[][] newCells = new int[size][size];
        int scoreGain = 0;

        for (int lineIndex = 0; lineIndex < size; lineIndex++) {
            int[] line = readLine(board, direction, lineIndex);
            LineMergeResult result = LineMerger.merge(line);
            int[] mergedLine = result.cells();
            scoreGain += result.getScoreGain();
            writeLine(newCells, direction, lineIndex, mergedLine);
        }
        boolean changed = hasChanges(board, newCells);
        if (!changed) {
            return new MoveResult(false, 0);
        }

        board.replaceCells(newCells);
        state.recordValidMove(scoreGain);
        spawner.spawn(board);
        return new MoveResult(true, scoreGain);
    }

    private int[] readLine(Board board, Direction direction, int lineIndex) {
        int size = board.getSize();
        int[] line = new int[size];
        for (int cellIndex = 0; cellIndex < size; cellIndex++) {
            switch (direction) {
                case LEFT:
                    line[cellIndex] = board.get(lineIndex, cellIndex);
                    break;
                case RIGHT:
                    line[cellIndex] = board.get(lineIndex, size - 1 - cellIndex);
                    break;
                case UP:
                    line[cellIndex] = board.get(cellIndex, lineIndex);
                    break;
                case DOWN:
                    line[cellIndex] = board.get(size - 1 - cellIndex, lineIndex);
                    break;
            }
        }

        return line;
    }

    private void writeLine(int[][] newCells, Direction direction, int lineIndex, int[] mergedLine) {
        int size = newCells.length;

        for (int cellIndex = 0; cellIndex < size; cellIndex++) {
            switch (direction) {
                case LEFT:
                    newCells[lineIndex][cellIndex] = mergedLine[cellIndex];
                    break;
                case RIGHT:
                    newCells[lineIndex][size - 1 - cellIndex] = mergedLine[cellIndex];
                    break;
                case UP:
                    newCells[cellIndex][lineIndex] = mergedLine[cellIndex];
                    break;
                case DOWN:
                    newCells[size - 1 - cellIndex][lineIndex] = mergedLine[cellIndex];
                    break;
            }
        }
    }

    private boolean hasChanges(Board board, int[][] newCells) {
        int size = board.getSize();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (board.get(row, column) != newCells[row][column]) {
                    return true;
                }
            }
        }
        return false;
    }
}
