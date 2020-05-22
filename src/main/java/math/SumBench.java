package math;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms6G", "-Xmx6G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                    (N)  Mode  Cnt      Score      Error  Units
SumBench.loop          100000000  avgt    5  85552.456 ± 3191.902  us/op
SumBench.math          100000000  avgt    5      0.004 ±    0.001  us/op
SumBench.streamReduce  100000000  avgt    5  45508.830 ± 1680.532  us/op
 */
public class SumBench {
    @Param({"100000000"})
    long N;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(SumBench.class.getSimpleName())
                .forks(1)
                .build())
                .run();
    }

    @Benchmark
    public void math(Blackhole bh) {
        bh.consume(N * (N + 1) / 2);
    }

    @Benchmark
    public void loop(Blackhole bh) {
        long count = 0;
        for (int i = 0; i < N; i++) {
            count += i;
        }
        bh.consume(count);
    }

    @Benchmark
    public void streamReduce(Blackhole bh) {
        bh.consume(LongStream.range(0, N)
                .reduce(0, Long::sum));
    }
}
