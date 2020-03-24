package bfs;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@State(Scope.Benchmark)
public class LMazeState {
    public char[][] mat;
    public int answer, N;

    @Setup(Level.Invocation)
    public void setup() throws IOException {
        String content = Files.readString(Paths.get(System.getProperty("user.dir") + "/src/main/java/bfs/maze.txt"), StandardCharsets.UTF_8);
        String[] split = content.split("\r");
        N = Integer.parseInt(split[0].split(" ")[0]);
        answer = Integer.parseInt(split[0].split(" ")[1]);
        mat = new char[N][N];
        for (int i = 1; i < N + 1; i++) {
            String s = split[i].trim();
            mat[i - 1] = s.toCharArray();
        }

        //Arrays.stream(map).forEach(line -> System.out.println(Arrays.toString(line)));
    }
}
