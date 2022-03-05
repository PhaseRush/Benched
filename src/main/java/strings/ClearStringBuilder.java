package strings;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms50G", "-Xmx50G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)
/*
Benchmark                         (N)  Mode  Cnt  Score    Error  Units
ClearStringBuilder.delete           1  avgt    5  0.027 ±  0.001  us/op
ClearStringBuilder.delete     1000000  avgt    5  0.038 ±  0.004  us/op
ClearStringBuilder.setLength        1  avgt    5  0.026 ±  0.001  us/op
ClearStringBuilder.setLength  1000000  avgt    5  0.037 ±  0.001  us/op
 */
public class ClearStringBuilder {

    @Param({"1", "1000000"})
    int N;
    StringBuilder sb;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ClearStringBuilder.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup(Level.Invocation)
    public void setup() {
        sb = new StringBuilder(N);
    }

    @Benchmark
    public void delete(Blackhole bh) {
        bh.consume(sb.delete(0, sb.length()));
    }

    @Benchmark
    public void setLength(Blackhole bh) {
        sb.setLength(0);
        bh.consume(sb);
    }
}
