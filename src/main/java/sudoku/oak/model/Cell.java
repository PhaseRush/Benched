package sudoku.oak.model;

import java.util.*;

/**
 * A Cell class for a sudoku game. Stores information about the cells in the Sudoku grid.
 */
public class Cell implements Cloneable {
    private final Set<Integer> candidates;
    private int value;
    private CellCoordinate position;

    public Cell(int col, int row, int value) {
        this(col + row * 9, value);
    }

    /**
     * Constructs a new Cell given a 1d coordinate and a value
     * @param index
     * @param value
     */
    public Cell(int index, int value) {
        this.candidates = new HashSet<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        this.value = value;
        this.position = new CellCoordinate(index);
    }

    /**
     * Checks whether a cell has a value
     * @return true if cell has value other than 0
     */
    public boolean hasValue() {
        return this.value > 0;
    }

    /**
     * Gets the viable candidates for a cell
     * @return set of candidates
     */
    public Set<Integer> getCandidates() {
        return this.candidates;
    }

    public int getFirstCandidate() {
        if(candidates.isEmpty()) {
            return 0;
        }
        return (int)candidates.toArray()[0];
    }

    public void removeCandidate(int candidate) {
        this.candidates.remove(candidate);
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        candidates.clear();
        this.value = value;
    }

    public CellCoordinate getPosition() {
        return this.position;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Cell)) {
            return false;
        }
        Cell other = (Cell)o;
        return this.value == other.getValue();
    }

    private Cell(Set<Integer> candidates, int value, CellCoordinate position) {
        this.candidates = candidates;
        this.value = value;
        this.position = position;
    }

    public int getNumberOfCandidates() {
        return candidates.size();
    }

    public boolean hasSoleCandidate() {
        return candidates.size() == 1;
    }

    @Override
    public Object clone() {
        return new Cell(
                new HashSet<>(candidates),
                value,
                (CellCoordinate)position.clone()
        );
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
