package sudoku.oak.model.interfaces;

/**
 * Defines a solver for a sudoku
 */
public interface Solver {
    boolean solve();
    Sudoku getSudoku();
}
