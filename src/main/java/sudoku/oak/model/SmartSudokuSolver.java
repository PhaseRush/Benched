package sudoku.oak.model;

import sudoku.oak.model.interfaces.Solver;
import sudoku.oak.model.interfaces.Sudoku;

import java.util.*;
import java.util.stream.Collectors;

public class SmartSudokuSolver implements Solver {
    private Sudoku sudoku;
    private final Stack<Sudoku> stack;

    public SmartSudokuSolver(Sudoku sudoku) {
        this.sudoku = sudoku;
        this.stack = new Stack<>();
    }

    @Override
    public boolean solve() {
//        System.out.println(sudoku + "\n");
        boolean b = false;
        while (noEndConditionReached()) {
            solveIteration();
            b = !b;
            //System.out.println(sudoku + "\n");
        }
        return b;
    }

    public void solveIteration() {
        if (!resolveCellsWithSoleCandidate()) {
            if (!resolveCellsWithUniqueCandidate()) {
                Cell cellWithFewestCandidates = findCellWithFewestCandidates();
//                System.out.println("Cell with fewest candidates: " + cellWithFewestCandidates.getCandidates());
                if (cellWithFewestCandidates.getCandidates().isEmpty()) {
                    return;
                }
                // This is safe because I have already checked that the set isn't empty
                int candidate = cellWithFewestCandidates.getCandidates().stream().findFirst().get();
                storeSudokuOnStack();
                int cellNumber = cellWithFewestCandidates.getPosition().getNumber();
                stack.peek().getCell(cellNumber).removeCandidate(candidate);
                sudoku.setCellValue(cellNumber, candidate);
            }
        }
    }

    public boolean noEndConditionReached() {
        if (sudoku.isSolved()) {
            return false;
        }
        if (!sudoku.isSolvable() && stack.isEmpty()) {
            return false;
        }
        if (!sudoku.isSolvable()) {
            sudoku = stack.pop();
        }
        return true;
    }

    private void storeSudokuOnStack() {
        stack.push(new ArraySudoku((ArraySudoku) sudoku));
    }

    private Cell findCellWithFewestCandidates() {
        Comparator<Cell> byNumberOfCandidates = Comparator.comparingInt(c -> c.getCandidates().size());
        return sudoku.getCells().stream()
                .filter(c -> !c.hasValue())
                .min(byNumberOfCandidates)
                .orElse(null);
    }

    private boolean resolveCellsWithUniqueCandidate() {
        boolean changeMade = false;
        for (Cell cell : sudoku.getCells()) {
            int rowNumber = cell.getPosition().getRow();
            int colNumber = cell.getPosition().getCol();
            int squareNumber = cell.getPosition().getSquareNumber();

            Set<Integer> rowCandidates = sudoku.getRow(rowNumber).stream()
                    .filter(c -> c != cell)
                    .flatMap(c -> c.getCandidates().stream())
                    .collect(Collectors.toSet());
            Set<Integer> colCandidates = sudoku.getColumn(colNumber).stream()
                    .filter(c -> c != cell)
                    .flatMap(c -> c.getCandidates().stream())
                    .collect(Collectors.toSet());
            Set<Integer> squareCandidates = sudoku.getSquare(squareNumber).stream()
                    .filter(c -> c != cell)
                    .flatMap(c -> c.getCandidates().stream())
                    .collect(Collectors.toSet());

            int uniqueCandidate = 0;
            for (int candidate : cell.getCandidates()) {
                if (!rowCandidates.contains(candidate) ||
                        !colCandidates.contains(candidate) ||
                        !squareCandidates.contains(candidate)) {

                    uniqueCandidate = candidate;
                    break;
                }
            }
            if (uniqueCandidate != 0) {
                changeMade = true;
                sudoku.setCellValue(cell.getPosition().getNumber(), uniqueCandidate);
            }
        }
        return changeMade;
    }

    private boolean resolveCellsWithSoleCandidate() {
        List<Cell> cellsWithSoleCandidate = sudoku.getCells().stream()
                .filter(Cell::hasSoleCandidate)
                .collect(Collectors.toList());
        boolean changeMade = !cellsWithSoleCandidate.isEmpty();
        cellsWithSoleCandidate.forEach(c -> {
            if (c.hasSoleCandidate())
                sudoku.setCellValue(c.getPosition().getNumber(), c.getFirstCandidate());
        });
        return changeMade;
    }

    @Override
    public Sudoku getSudoku() {
        return sudoku;
    }

}
