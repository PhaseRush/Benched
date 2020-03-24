package bfs;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayDeque;
import java.util.Deque;

public class TMictoria {
    // variables
    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(TMictoria.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void run(TMazeState state, Blackhole bh) {
        int result = t_bfs(state.map, state.N);
        assert result == state.answer;
        //System.out.printf("Result=%d, Answer=%d", result, state.answer);
        bh.consume(result);
    }


    private static int t_bfs(String[][] map, int N) {
        int[][] visited = new int[N][N];
        int nodes = 0;
        Deque<Integer> rq = new ArrayDeque<>();
        Deque<Integer> cq = new ArrayDeque<>();
        Deque<Integer> sq = new ArrayDeque<>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j].equals("A")) {
                    rq.add(i);
                    cq.add(j);
                }
            }
        }
        int result = 0;
        int layer = 0;
        while (rq.size() > 0) {
            checkNeighbours(map, visited, nodes, nodes, rq, cq);
            //System.out.println("RQ Size: " + rq.size() + " | " + nodes);
            layer--;
            if (layer <= 0) {
                layer = nodes;
                nodes = 0;
                result++;
            }

        }
        return result;
    }

    private static void checkNeighbours(String[][] map, int[][] visited, int added, int nodes, Deque<Integer> rq, Deque<Integer> cq) {
        int newRow = rq.remove();
        int newCol = cq.remove();
        if (map[newRow][newCol].equals("B")) {
            rq.clear();
        } else if ((map[newRow][newCol].equals(".") || map[newRow][newCol].equals("A")) && visited[newRow][newCol] == 0) {
            //Check all directions
            visited[newRow][newCol] = 1;
            int size = map.length;
            if (newRow > 0) {
                rq.add(newRow - 1);
                cq.add(newCol);
                nodes++;
            }
            if (newCol > 0) {
                rq.add(newRow);
                cq.add(newCol - 1);
                nodes++;
            }
            if (newRow < size - 1) {
                rq.add(newRow + 1);
                cq.add(newCol);
                nodes++;
            }
            if (newCol < size - 1) {
                rq.add(newRow);
                cq.add(newCol + 1);
                nodes++;
            }

        }
    }
}
