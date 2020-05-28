package sudoku;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import sudoku.oak.model.ArraySudoku;
import sudoku.oak.model.SmartSudokuSolver;
import sudoku.oak.model.interfaces.Sudoku;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"})
@Warmup(iterations = 1)
@Measurement(iterations = 5)
/*
Benchmark                        (N)  Mode  Cnt      Score   Error  Units
OakSudokuBench.solveAll        10000  avgt       51952.877          ms/op
OakSudokuBench.solveEmpty      10000  avgt          19.103          ms/op
OakSudokuBench.solveExpert     10000  avgt           4.425          ms/op
OakSudokuBench.solveSeventeen  10000  avgt           1.073          ms/op
OakSudokuBench.solveSimple     10000  avgt           0.116          ms/op
 */
public class OakSudokuBench {

    @Param({"10000"})
    private int N;

    private Set<Sudoku> sudokus;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(OakSudokuBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    int[] simpleSudokuArray = {
            9, 0, 6, 0, 7, 0, 0, 0, 0,
            0, 0, 0, 2, 0, 0, 0, 9, 0,
            8, 5, 1, 0, 0, 9, 7, 0, 0,
            5, 6, 0, 0, 2, 0, 9, 0, 3,
            0, 0, 0, 0, 1, 0, 6, 8, 0,
            0, 0, 7, 6, 0, 0, 2, 0, 4,
            0, 1, 9, 0, 0, 4, 0, 3, 8,
            7, 0, 4, 5, 0, 8, 1, 6, 0,
            0, 8, 5, 0, 3, 0, 4, 7, 0
    };
    int[] expertSudokuArray = {
            0, 0, 0, 1, 0, 0, 6, 0, 5,
            0, 0, 0, 0, 0, 6, 0, 0, 0,
            0, 0, 2, 5, 0, 9, 0, 3, 0,
            9, 0, 0, 0, 7, 0, 0, 6, 0,
            2, 0, 0, 0, 3, 0, 1, 0, 0,
            0, 0, 0, 0, 0, 0, 3, 0, 2,
            7, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 4, 0, 0, 0, 0, 0, 2, 0,
            0, 0, 1, 0, 6, 5, 4, 0, 0
    };
    int[] emptySudokuArray = {
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    int[] seventeenClueSudokuArray = {
            0, 0, 0, 7, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 4, 3, 0, 2, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 6,
            0, 0, 0, 5, 0, 9, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 4, 1, 8,
            0, 0, 0, 0, 8, 1, 0, 0, 0,
            0, 0, 2, 0, 0, 0, 0, 5, 0,
            0, 4, 0, 0, 0, 0, 3, 0, 0
    };

    @Setup
    public void setup() {
        try {
            URL sudokuUrl = new URL("https://raw.githubusercontent.com/maxbergmark/sudoku-solver/master/all_17_clue_sudokus.txt");
            HttpsURLConnection conn = (HttpsURLConnection) sudokuUrl.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sudokus = br.lines()
                    .filter(line -> line.length() == 81)
                    .map(line -> line.chars().map(c -> c - '0').toArray())
                    .limit(N)
                    .map(ArraySudoku::new)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            sudokus = new HashSet<>();
            e.printStackTrace();
        }
    }

    @Benchmark
    public void solveSimple(Blackhole bh) {
        new SmartSudokuSolver(new ArraySudoku(simpleSudokuArray)).solve();
    }

    @Benchmark
    public void solveExpert(Blackhole bh) {
        new SmartSudokuSolver(new ArraySudoku(expertSudokuArray)).solve();
    }

    @Benchmark
    public void solveEmpty(Blackhole bh) {
        new SmartSudokuSolver(new ArraySudoku(emptySudokuArray)).solve();
    }

    @Benchmark
    public void solveSeventeen(Blackhole bh) {
        new SmartSudokuSolver(new ArraySudoku(seventeenClueSudokuArray)).solve();
    }

    @Benchmark
    public void solveN(Blackhole bh) {
        sudokus.forEach(s -> new SmartSudokuSolver(s).solve());
    }
}
