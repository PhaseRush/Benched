package sudoku.oak.model;

import sudoku.oak.model.exceptions.IllegalSudokuMoveException;
import sudoku.oak.model.interfaces.Sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An array implementation of Sudoku
 */
public class ArraySudoku implements Sudoku, Cloneable {
    private final int STANDARD_SIZE = 9 * 9;

    private final List<Cell> cells;

    public ArraySudoku(int[] inputSudoku) {
        this();
        IntStream.range(0, inputSudoku.length)
                .filter(i -> inputSudoku[i] != 0)
                .forEach(i -> setCellValue(i, inputSudoku[i]));
    }

    public ArraySudoku() {
        this.cells = new ArrayList<>();
        for (int i = 0; i < STANDARD_SIZE; i++) {
            cells.add(new Cell(i, 0));
        }
    }

    public ArraySudoku(ArraySudoku sudoku) {
        this.cells = sudoku.cells.stream()
                .map(c -> (Cell)c.clone())
                .collect(Collectors.toList());
    }

    @Override
    public List<Cell> getRow(int rowNumber) {
        int startIndex = rowNumber * 9;
        int endIndex = startIndex + 9;
        return cells.subList(startIndex, endIndex);
    }

    @Override
    public List<Cell> getColumn(int colNumber) {
        List<Cell> col = new ArrayList<>();
        for (int i = colNumber; i < cells.size(); i += 9) {
            col.add(cells.get(i));
        }
        return col;
    }

    @Override
    public List<Cell> getSquare(int squareNumber) {
        int squareRow = squareNumber / 3;
        int squareCol = squareNumber % 3;
        int firstCellRow = squareRow * 3;
        int firstCellCol = squareCol * 3;

        List<Cell> square = new ArrayList<>();

        for (int row = firstCellRow; row < firstCellRow + 3; row++) {
            for (int col = firstCellCol; col < firstCellCol + 3; col++) {
                square.add(cells.get(convertRowColToCellNumber(col, row)));
            }
        }
        return square;
    }

    @Override
    public List<Cell> getAllRelatedCells(int cellNumber) {
        final Cell cell = cells.get(cellNumber);
        final int rowNumber = cell.getPosition().getRow();
        final int colNumber = cell.getPosition().getCol();
        final int squareNumber = cell.getPosition().getSquareNumber();
        return Stream.concat(
                Stream.concat(
                        getRow(rowNumber).stream(),
                        getColumn(colNumber).stream()
                ),
                getSquare(squareNumber).stream())
                .filter(c -> c != cell)
                .collect(Collectors.toList());
    }

    @Override
    public List<Cell> getAllRelatedCells(int colNumber, int rowNumber) {
        int cellNumber = convertRowColToCellNumber(colNumber, rowNumber);
        return getAllRelatedCells(cellNumber);
    }

    @Override
    public Cell getCell(int cellNumber) {
        return cells.get(cellNumber);
    }

    @Override
    public Cell getCell(int colNumber, int rowNumber) {
        int cellNumber = convertRowColToCellNumber(colNumber, rowNumber);
        return cells.get(cellNumber);
    }

    /**
     * Sets a cell's value and updates the relevant candidates for related cells.
     * @param cellNumber the cell's number
     * @param value the new value
     * @throws IllegalSudokuMoveException if cell has value or move is not among candidates
     */
    @Override
    public void setCellValue(int cellNumber, int value)
            throws IllegalSudokuMoveException {

        Cell cell = cells.get(cellNumber);
        if (cell.hasValue() || !cell.getCandidates().contains(value)) {
            throw new IllegalSudokuMoveException();
        }
        cell.setValue(value);

        getAllRelatedCells(cellNumber).forEach(c -> c.removeCandidate(value));
    }



    @Override
    public void setCellValue(int colNumber, int rowNumber, int value) {
        int cellNumber = convertRowColToCellNumber(colNumber, rowNumber);
        setCellValue(cellNumber, value);
    }

    @Override
    public int getCellValue(int cellNumber) {
        return getCell(cellNumber).getValue();
    }

    @Override
    public int getCellValue(int rowNumber, int cellNumber) {
        return getCell(rowNumber, cellNumber).getValue();
    }

    @Override
    public Set<Integer> getCellCandidates(int cellNumber) {
        return cells.get(cellNumber).getCandidates();
    }

    @Override
    public Set<Integer> getCellCandidates(int colNumber, int rowNumber) {
        int cellNumber = convertRowColToCellNumber(colNumber, rowNumber);
        return cells.get(cellNumber).getCandidates();
    }

    @Override
    public List<Cell> getCells() {
        return cells;
    }

    @Override
    public boolean isSolvable() {
        return cells.stream()
                .noneMatch(c -> !c.hasValue() && c.getCandidates().size() == 0);
    }

    @Override
    public boolean isSolved() {
        return cells.stream().allMatch(Cell::hasValue);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) {
            if (i != 0 && i % 9 == 0) {
                sb.append('\n');
            }
            sb.append(cells.get(i));
            sb.append(' ');

        }
        return sb.toString();
    }

    private int convertRowColToCellNumber(int colNumber, int rowNumber) {
        return colNumber + rowNumber * 9;
    }

}
