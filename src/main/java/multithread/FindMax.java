package multithread;

import misc.StringBench;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.*;

@Fork(value = 1)
@Warmup(iterations = 5, time = 3)
@Measurement(iterations = 10, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)


/*
Benchmark                      (length)  Mode  Cnt   Score    Error  Units
FindMax.findMaxForLoopLanger       1000  avgt   10  ≈ 10⁻⁴           ms/op
FindMax.findMaxForLoopLanger     100000  avgt   10   0.016 ±  0.001  ms/op
FindMax.findMaxForLoopLanger   10000000  avgt   10   2.673 ±  0.103  ms/op
FindMax.findMaxStream              1000  avgt   10  ≈ 10⁻³           ms/op
FindMax.findMaxStream            100000  avgt   10   0.039 ±  0.001  ms/op
FindMax.findMaxStream          10000000  avgt   10   4.698 ±  0.061  ms/op
FindMax.findMaxStreamParallel      1000  avgt   10   0.018 ±  0.001  ms/op
FindMax.findMaxStreamParallel    100000  avgt   10   0.020 ±  0.002  ms/op
FindMax.findMaxStreamParallel  10000000  avgt   10   0.826 ±  0.050  ms/op
 */
public class FindMax {
    //@Param({"500000"})
    @Param({"1000", "100000", "10000000"})
    private int length;
    private int[] ints;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(FindMax.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        ints = ThreadLocalRandom.current().ints(length).toArray();
    }

    @Benchmark
    public int findMaxForLoopLanger() {
        int[] a = ints;
        int e = ints.length;
        int m = Integer.MIN_VALUE;
        for (int i = 0; i < e; i++)
            if (a[i] > m) m = a[i];
        return m;
    }

    @Benchmark
    public int findMaxStream() {
        return Arrays.stream(ints)
                .reduce(Integer.MIN_VALUE, Math::max);
    }

    @Benchmark
    public int findMaxStreamParallel() {
        return Arrays.stream(ints)
                .parallel()
                .reduce(Integer.MIN_VALUE, Math::max);
    }
}