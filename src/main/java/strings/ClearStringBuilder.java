package strings;

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
@Fork(value = 2, jvmArgs = {"-Xms50G", "-Xmx50G"})
@Warmup(iterations = 5)
@Measurement(iterations = 5)
/*
Benchmark                         (N)  Mode  Cnt      Score     Error  Units
ClearStringBuilder.delete           1  avgt   50      2.240 ±   0.011  ns/op
ClearStringBuilder.setLength        1  avgt   50      2.364 ±   0.011  ns/op
ClearStringBuilder.createNew        1  avgt    5      4.212 ±   0.021  ns/op

ClearStringBuilder.delete     1000000  avgt   50      2.235 ±   0.015  ns/op
ClearStringBuilder.setLength  1000000  avgt   50      2.352 ±   0.002  ns/op
ClearStringBuilder.createNew  1000000  avgt    5  54662.354 ± 203.577  ns/op
 */
public class ClearStringBuilder {

    @Param({"1", "1000000"})
    int N;
    StringBuilder sb1;
    StringBuilder sb2;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ClearStringBuilder.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        sb1 = new StringBuilder(N);
        sb1.append("R".repeat(N));

        sb2 = new StringBuilder(N);
        sb2.append("R".repeat(N));
    }

//    @Benchmark
//    public void delete(Blackhole bh) {
//        bh.consume(sb1.delete(0, N));
//    }
//
//    @Benchmark
//    public void setLength(Blackhole bh) {
//        sb2.setLength(0);
//        bh.consume(sb2);
//    }

    @Benchmark
    public void createNew(Blackhole bh) {
        bh.consume(new StringBuilder(N));
    }
}
