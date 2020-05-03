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
Benchmark                             Mode  Cnt    Score    Error  Units
NumDigitsBench.double300Log           avgt    5   11.918 ±  0.404  ns/op
NumDigitsBench.double300StringLength  avgt    5  874.786 ± 14.934  ns/op

NumDigitsBench.double5Log             avgt    5   11.845 ±  0.337  ns/op
NumDigitsBench.double5StringLength    avgt    5  108.378 ±  6.811  ns/op

NumDigitsBench.int5IfNest             avgt    5    3.920 ±  0.046  ns/op
NumDigitsBench.int5Log                avgt    5   12.145 ±  0.206  ns/op
NumDigitsBench.int5StringLength       avgt    5    9.387 ±  0.403  ns/op

NumDigitsBench.int9IfNest             avgt    5    3.984 ±  0.073  ns/op
NumDigitsBench.int9Log                avgt    5   12.071 ±  0.814  ns/op
NumDigitsBench.int9StringLength       avgt    5   13.362 ±  0.405  ns/op

Process finished with exit code 0

 */
public class NumDigitsBench {
    double base;

    double d_5;
    double d_300;

    int i_5;
    int i_9;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NumDigitsBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        base = ThreadLocalRandom.current().nextDouble();
        d_5 = base * Math.pow(10, 5);
        d_300 = base * Math.pow(10, 300);

        i_5 = (int) (base * Math.pow(10, 5));
        i_9 = Integer.MAX_VALUE;
    }

    // d_5
    @Benchmark
    public void double5StringLength(Blackhole bh) {
        bh.consume(String.valueOf(d_5).length());
    }

    @Benchmark
    public void double5Log(Blackhole bh) {
        bh.consume(d_5 == 0 ? 1 : (1 + (int) Math.floor(Math.log10(Math.abs(d_5)))));
    }

    // d_300
    @Benchmark
    public void double300StringLength(Blackhole bh) {
        bh.consume(String.valueOf(d_300).length());
    }

    @Benchmark
    public void double300Log(Blackhole bh) {
        bh.consume(d_300 == 0 ? 1 : (1 + (int) Math.floor(Math.log10(Math.abs(d_300)))));
    }

    // i_5
    @Benchmark
    public void int5StringLength(Blackhole bh) {
        bh.consume(String.valueOf(i_5).length());
    }

    @Benchmark
    public void int5Log(Blackhole bh) {
        bh.consume(i_5 == 0 ? 1 : (1 + (int) Math.floor(Math.log10(Math.abs(i_5)))));
    }

    @Benchmark
    public void int5IfNest(Blackhole bh) {
        bh.consume(i_5 < 100000 ? i_5 < 100 ? i_5 < 10 ? 1 : 2 : i_5 < 1000 ? 3 : i_5 < 10000 ? 4 : 5 : i_5 < 10000000 ? i_5 < 1000000 ? 6 : 7 : i_5 < 100000000 ? 8 : i_5 < 1000000000 ? 9 : 10);
    }

    // i_9
    @Benchmark
    public void int9StringLength(Blackhole bh) {
        bh.consume(String.valueOf(i_9).length());
    }

    @Benchmark
    public void int9Log(Blackhole bh) {
        bh.consume(i_9 == 0 ? 1 : (1 + (int) Math.floor(Math.log10(Math.abs(i_9)))));
    }

    @Benchmark
    public void int9IfNest(Blackhole bh) {
        bh.consume(i_9 < 100000 ? i_9 < 100 ? i_9 < 10 ? 1 : 2 : i_9 < 1000 ? 3 : i_9 < 10000 ? 4 : 5 : i_9 < 10000000 ? i_9 < 1000000 ? 6 : 7 : i_9 < 100000000 ? 8 : i_9 < 1000000000 ? 9 : 10);
    }

}
