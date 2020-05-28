package sudoku.oak.model;

/**
 * Represents a coordinate for a Sudoku cell
 */
public class CellCoordinate implements Cloneable {
    private int col;
    private int row;

    public CellCoordinate(int col, int row) {
        this.col = col;
        this.row = row;
    }

    /**
     * Takes a 1d coordinate as a parameter and converts it to a 2d coordinate
     * @param index an array index
     */
    public CellCoordinate(int index) {
        this.col = index % 9;
        this.row = index / 9;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getNumber() {
        return col + row * 9;
    }

    public int getSquareNumber() {
        int squareCol = col / 3;
        int squareRow = row / 3;

        return squareCol + squareRow * 3;
    }

    @Override
    public Object clone() {
        return new CellCoordinate(col, row);
    }
}
