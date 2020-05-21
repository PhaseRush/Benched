package memory;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
 * Ryzen 5 3600 @ 4100Mhz, DDR4 3800MHz CL 16 1T
 * Benchmark                    (N)  Mode  Cnt     Score    Error  Units
 * CacheBench.columnMajorSum  10000  avgt    5  1245.935 ± 37.960  ms/op
 * CacheBench.rowMajorSum     10000  avgt    5    41.639 ±  3.543  ms/op
 * CacheBench.columnMajorSum   1000  avgt    5     1.341 ±  0.350  ms/op
 * CacheBench.rowMajorSum      1000  avgt    5     0.256 ±  0.008  ms/op
 *

 * CacheBench.columnMajorSum  100  avgt    5  4843.406 ± 575.563  ns/op
 * CacheBench.rowMajorSum     100  avgt    5  2468.049 ±  36.058  ns/op
 *
 * CacheBench.columnMajorSum   10  avgt    5  53.766 ± 1.654    ns/op
 * CacheBench.rowMajorSum      10  avgt    5  31.054 ± 1.079    ns/op
 *
 *
 *
 * Ryzen 5 3600 @ 4100Mhz, DDR4 2400MHz CL 16 2T
 * CacheBench.columnMajorSum  10000  avgt    5  1195.510 ± 124.247  ms/op
 * CacheBench.rowMajorSum     10000  avgt    5    52.250 ±   3.087  ms/op
 */
public class CacheBench {

    @Param({"10"})
    private int N;

    private long[][] matrix;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(CacheBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        matrix = new long[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrix[i][j] = N * i + j;
            }
        }
    }

    @Benchmark
    public void rowMajorSum(Blackhole bh) {
        long sum = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                sum += matrix[i][j];
            }
        }
        bh.consume(sum);
    }

    @Benchmark
    public void columnMajorSum(Blackhole bh) {
        long sum = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                sum += matrix[j][i];
            }
        }
        bh.consume(sum);
    }
}
