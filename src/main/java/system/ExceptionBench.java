package system;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

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

}
