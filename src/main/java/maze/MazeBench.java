package maze;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark               Mode  Cnt       Score      Error  Units
 * MazeBench.$21x17        avgt    5       1.137 ±    0.042  us/op
 * MazeBench.$101x101      avgt    5      14.850 ±    0.595  us/op
 * MazeBench.$201x201      avgt    5      51.803 ±    1.840  us/op
 * MazeBench.$301x301      avgt    5     113.938 ±    0.621  us/op
 * MazeBench.$401x401      avgt    5     200.490 ±   22.845  us/op
 * MazeBench.$501x501      avgt    5     316.163 ±   50.993  us/op
 * MazeBench.$1001x1001    avgt    5    1203.753 ±   62.709  us/op
 * MazeBench.$10001x10001  avgt    5  128118.019 ±  105.188  us/op
 */
public class MazeBench {
    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(MazeBench.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .jvmArgs("-Xmx64G")
                .warmupIterations(3)
                .measurementIterations(5)
                .build()).run();
    }

    @Benchmark
    public void $21x17(Blackhole bh) {
        bh.consume(new Maze(21, 17));
//        var m = new Maze(21, 17);

    }


    @Benchmark
    public void $101x101(Blackhole bh) {
        bh.consume(new Maze(101, 101));
    }

    @Benchmark
    public void $201x201(Blackhole bh) {
        bh.consume(new Maze(201, 201));
    }

    @Benchmark
    public void $301x301(Blackhole bh) {
        bh.consume(new Maze(301, 301));
    }

    @Benchmark
    public void $401x401(Blackhole bh) {
        bh.consume(new Maze(401, 401));
    }

    @Benchmark
    public void $501x501(Blackhole bh) {
        bh.consume(new Maze(501, 501));
    }

    @Benchmark
    public void $1001x1001(Blackhole bh) {
        bh.consume(new Maze(1001, 1001));
    }

    @Benchmark
    public void $10001x10001(Blackhole bh) {
        bh.consume(new Maze(10001, 10001));
    }

}
