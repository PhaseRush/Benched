package misc;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms6G", "-Xmx6G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                               Mode  Cnt     Score     Error  Units
RandomBench.baseline                    avgt    5     3.667 ±   0.090  ns/op
RandomBench.mathRandom                  avgt    5    11.979 ±   0.249  ns/op
RandomBench.mathRandom_12Thread         avgt    5   428.907 ±  33.085  ns/op
RandomBench.newRandom                   avgt    5    48.178 ±   1.991  ns/op
RandomBench.newRandom_12Thread          avgt    5   258.083 ±   6.543  ns/op
RandomBench.secureRandom                avgt    5  3299.528 ± 138.694  ns/op
RandomBench.threadLocalRandom           avgt    5     4.465 ±   0.147  ns/op
RandomBench.threadLocalRandom_12Thread  avgt    5     7.521 ±   0.529  ns/op
 */
public class RandomBench {
    double x;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RandomBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        x = Math.random();
    }

    @Benchmark
    public void baseline(Blackhole bh) {
        bh.consume(x * Math.E);
    }

    @Benchmark
    public void threadLocalRandom(Blackhole bh) {
        bh.consume(ThreadLocalRandom.current().nextDouble());
    }

    @Threads(12)
    @Benchmark
    public void threadLocalRandom_12Thread(Blackhole bh) {
        bh.consume(ThreadLocalRandom.current().nextDouble());
    }

    @Benchmark
    public void mathRandom(Blackhole bh) {
        bh.consume(Math.random());
    }

    @Threads(12)
    @Benchmark
    public void mathRandom_12Thread(Blackhole bh) {
        bh.consume(Math.random());
    }

    @Benchmark
    public void newRandom(Blackhole bh) {
        bh.consume(new Random().nextDouble());
    }

    @Threads(12)
    @Benchmark
    public void newRandom_12Thread(Blackhole bh) {
        bh.consume(new Random().nextDouble());
    }

    @Benchmark
    public void secureRandom(Blackhole bh) {
        bh.consume(new SecureRandom().nextDouble());
    }
}
