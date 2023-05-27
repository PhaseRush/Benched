package bfs;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

public class LMictoria {

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(LMictoria.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void run(LMazeState state, Blackhole bh) {
        int result = l_bfs(state.mat, state.N);
        assert result == state.answer;
//        System.out.printf("Result=%d, Answer=%d", result, state.answer);
        bh.consume(result);
    }

    private int l_bfs(char[][] mat, int N) {
        boolean[][] visited = new boolean[N][N];
        int pathLen = 0;
        Node start = find(mat, 'A');
        visited[start.x][start.y] = true;
        Queue<Node> q = new ArrayDeque<>();
        q.add(start);
        q.add(Node.SENTINEL);
        Node goal = find(mat, 'B');
        while (!q.isEmpty()) {
            Node curr = q.poll();
            if (curr == Node.SENTINEL) {
                pathLen++;
                q.add(Node.SENTINEL);
                if (q.peek() == Node.SENTINEL) {
                    //System.out.println("IMPOSSIBLE");
                    return -1;
                }
            } else if (curr.equals(goal)) {
                //System.out.println(pathLen);
                return pathLen;
            } else {
                // up
                if (isValid(curr.x, curr.y - 1, N) && !visited[curr.x][curr.y - 1]) {
                    q.add(Node.of(curr.x, curr.y - 1));
                    visited[curr.x][curr.y - 1] = true;
                }
                // right
                if (isValid(curr.x + 1, curr.y, N) && !visited[curr.x + 1][curr.y]) {
                    q.add(Node.of(curr.x + 1, curr.y));
                    visited[curr.x + 1][curr.y] = true;
                }
                // down
                if (isValid(curr.x, curr.y + 1, N) && !visited[curr.x][curr.y + 1]) {
                    q.add(Node.of(curr.x, curr.y + 1));
                    visited[curr.x][curr.y + 1] = true;
                }
                // left
                if (isValid(curr.x - 1, curr.y, N) && !visited[curr.x - 1][curr.y]) {
                    q.add(Node.of(curr.x - 1, curr.y));
                    visited[curr.x + 1][curr.y] = true;
                }
            }
        }
        return -1;
    }

    private static Node find(char[][] mat, char c) {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat.length; j++) {
                if (mat[i][j] == c) return Node.of(i, j);
            }
        }
        return null; // not possible
    }

    private static boolean isValid(int x, int y, int size) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }


    static class Node {
        static final Node SENTINEL = of(-1, -1);
        int x, y;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        static Node of(int x, int y) {
            return new Node(x, y);
        }

        @Override
        public boolean equals(Object o) {
            Node node = (Node) o;
            return x == node.x &&
                    y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
