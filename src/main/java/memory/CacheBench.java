package memory;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)

/*
13700k 6000mhz 34 44 44 84 128 480 2T
Benchmark                    (N)  Mode  Cnt       Score       Error  Units
CacheBench.columnMajorSum     10  avgt    5       0.029 ±     0.001  us/op
CacheBench.rowMajorSum        10  avgt    5       0.018 ±     0.001  us/op

CacheBench.columnMajorSum    100  avgt    5       2.820 ±     0.075  us/op
CacheBench.rowMajorSum       100  avgt    5       1.903 ±     0.043  us/op

CacheBench.columnMajorSum   1000  avgt    5     743.567 ±    12.221  us/op
CacheBench.rowMajorSum      1000  avgt    5     191.222 ±     1.026  us/op

CacheBench.columnMajorSum  10000  avgt    5  641226.098 ± 14037.398  us/op
CacheBench.rowMajorSum     10000  avgt    5   36648.540 ±   225.435  us/op

M1 Pro 8 core
Benchmark                    (N)  Mode  Cnt       Score      Error  Units
CacheBench.columnMajorSum     10  avgt    5       0.046 ±    0.001  us/op
CacheBench.rowMajorSum        10  avgt    5       0.028 ±    0.001  us/op

CacheBench.columnMajorSum    100  avgt    5       3.352 ±    0.005  us/op
CacheBench.rowMajorSum       100  avgt    5       3.198 ±    0.003  us/op

CacheBench.columnMajorSum   1000  avgt    5     917.940 ±    3.243  us/op
CacheBench.rowMajorSum      1000  avgt    5     320.177 ±    0.545  us/op

CacheBench.columnMajorSum  10000  avgt    5  457774.828 ± 1537.626  us/op
CacheBench.rowMajorSum     10000  avgt    5   32750.609 ±   36.334  us/op

Ryzen 7 5800x DDR4 C18 1T
Benchmark                    (N)  Mode  Cnt       Score      Error  Units
CacheBench.columnMajorSum     10  avgt    5       0.039 ±    0.001  us/op
CacheBench.rowMajorSum        10  avgt    5       0.022 ±    0.001  us/op

CacheBench.columnMajorSum    100  avgt    5       3.154 ±    0.012  us/op
CacheBench.rowMajorSum       100  avgt    5       2.070 ±    0.005  us/op

CacheBench.columnMajorSum   1000  avgt    5     958.767 ±    7.110  us/op
CacheBench.rowMajorSum      1000  avgt    5     208.441 ±    0.390  us/op

CacheBench.columnMajorSum  10000  avgt    5  596847.942 ± 9710.131  us/op
CacheBench.rowMajorSum     10000  avgt    5   31717.749 ±  899.836  us/op

Ryzen 5 3600 @ 4100Mhz, DDR4 3800MHz CL 16 1T
Benchmark                    (N)  Mode  Cnt     Score    Error  Units
CacheBench.columnMajorSum     10  avgt    5    53.766 ±   1.654  ns/op
CacheBench.rowMajorSum        10  avgt    5    31.054 ±   1.079  ns/op

CacheBench.columnMajorSum    100  avgt    5  4843.406 ± 575.563  ns/op
CacheBench.rowMajorSum       100  avgt    5  2468.049 ±  36.058  ns/op

CacheBench.columnMajorSum   1000  avgt    5     1.341 ±  0.350  ms/op
CacheBench.rowMajorSum      1000  avgt    5     0.256 ±  0.008  ms/op

CacheBench.columnMajorSum  10000  avgt    5  1245.935 ± 37.960  ms/op
CacheBench.rowMajorSum     10000  avgt    5    41.639 ±  3.543  ms/op

Ryzen 5 3600 @ 4100Mhz, DDR4 2400MHz CL 16 2T
CacheBench.columnMajorSum  10000  avgt    5  1195.510 ± 124.247  ms/op
CacheBench.rowMajorSum     10000  avgt    5    52.250 ±   3.087  ms/op
 */
public class CacheBench {

    @Param({"10", "100", "1000", "10000"})
    private int N;

    private long[][] matrix;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(CacheBench.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .jvmArgs("-Xmx64G")
                .warmupIterations(3)
                .measurementIterations(5)
                .build()).run();
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
