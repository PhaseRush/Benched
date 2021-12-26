package system;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/*
Benchmark                           (N)  Mode  Cnt    Score    Error  Units
ExceptionBench.nullCheck       10000000  avgt    5    0.420 ±  0.002  ns/op
ExceptionBench.nullException   10000000  avgt    5    2.176 ±  0.059  ns/op
ExceptionBench.rangeCheck      10000000  avgt    5    0.422 ±  0.009  ns/op
ExceptionBench.rangeException  10000000  avgt    5    2.242 ±  0.111  ns/op
ExceptionBench.throwCustom     10000000  avgt    5  682.789 ± 27.814  ns/op
 */
@State(Scope.Benchmark)
public class ExceptionBench {
    @Param({"10000000"})
    static int N;
    static int Nplus1;

    static int[] arr;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(ExceptionBench.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .jvmArgs("-Xmx64G")
                .warmupIterations(3)
                .measurementIterations(5)
                .build()).run();
    }

    @Setup
    public static void setup() {
        arr = new int[N];
        Nplus1 = N + 1;
    }


    @Benchmark
    public void rangeCheck(Blackhole bh) {
        if (Nplus1 <= N) {
            bh.consume(arr[Nplus1]);
        }
    }

    @Benchmark
    public void rangeException(Blackhole bh) {
        try {
            bh.consume(arr[Nplus1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            bh.consume(e);
        }
    }

    @Benchmark
    @SuppressWarnings("all")
    public void nullCheck(Blackhole bh) {
        String nullString = null;
        if (nullString != null) {
            bh.consume(nullString.substring(1));
        }
    }

    @Benchmark
    public void nullException(Blackhole bh) {
        String nullString = null;
        try {
            bh.consume(nullString.substring(1));
        } catch (NullPointerException e) {
            bh.consume(e);
        }
    }

    @Benchmark
    public void throwCustom(Blackhole bh) {
        try {
            throw new CustomException("");
        } catch (CustomException e) {
            bh.consume(e);
        }
    }

    static class CustomException extends Exception {
        CustomException(String error) {
            super(error);
        }
    }

}
