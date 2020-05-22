package math;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms6G", "-Xmx6G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                 Mode  Cnt    Score   Error  Units
StoiBench.baselineDouble  avgt    5    3.604 ± 0.004  ns/op
StoiBench.baselineString  avgt    5    3.649 ± 0.112  ns/op
StoiBench.doubleToString  avgt    5   79.848 ± 2.180  ns/op
StoiBench.stringToDouble  avgt    5  147.321 ± 1.438  ns/op
 */
public class StoiBench {
    String s;
    double d;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StoiBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        d = ThreadLocalRandom.current().nextDouble();
        s = String.valueOf(d);
    }

    @Benchmark
    public void baselineString(Blackhole bh) {
        bh.consume(s);
    }

    @Benchmark
    public void baselineDouble(Blackhole bh) {
        bh.consume(d);
    }

    @Benchmark
    public void doubleToString(Blackhole bh) {
        bh.consume(String.valueOf(d));
    }

    @Benchmark
    public void stringToDouble(Blackhole bh) {
        bh.consume(Double.valueOf(s));
    }
}
