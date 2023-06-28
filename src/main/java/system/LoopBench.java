package system;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"})
@Warmup(iterations = 5)
@Measurement(iterations = 10)
/**
 Benchmark                                  (N)  Mode  Cnt   Score   Error  Units
 LoopBench.foreachLoop_array               1000  avgt   10   0.214 ± 0.001  us/op
 LoopBench.streamForeachAccumulator_array  1000  avgt   10   0.233 ± 0.001  us/op
 LoopBench.streamParallelSum_array         1000  avgt   10  11.661 ± 0.206  us/op
 LoopBench.streamSum_array                 1000  avgt   10   0.234 ± 0.017  us/op

 LoopBench.foreachLoop_list                1000  avgt   10   0.344 ± 0.013  us/op
 LoopBench.streamForeachAccumulator_list   1000  avgt   10   0.343 ± 0.005  us/op
 LoopBench.streamParallelSum_list          1000  avgt   10  11.977 ± 0.705  us/op
 LoopBench.streamSum_list                  1000  avgt   10   0.510 ± 0.010  us/op


 Benchmark                         (N)  Mode  Cnt  Score   Error  Units
 LoopBench.foreachAccumulator  1000000  avgt    5  3.044 ± 1.052  ms/op
 LoopBench.foreachLoop         1000000  avgt    5  2.841 ± 0.337  ms/op
 LoopBench.streamParallelSum   1000000  avgt    5  0.217 ± 0.033  ms/op
 LoopBench.streamSum           1000000  avgt    5  2.941 ± 0.992  ms/op
 */
public class LoopBench {

    @Param({"1000"})
    private int N;

    private List<Integer> list;

    private int[] array;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LoopBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        list = IntStream.range(0, N)
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(list);

        array = list.stream().mapToInt(i -> i).toArray();
    }

    @Benchmark
    public void foreachLoop_list(Blackhole bh) {
        int sum = 0;
        for (Integer i : list) {
            sum += i;
        }
        bh.consume(sum);
    }

    @Benchmark
    public void foreachLoop_array(Blackhole bh) {
        int sum = 0;
        for (int i : array) {
            sum += i;
        }
        bh.consume(sum);
    }

    @Benchmark
    public void streamForeachAccumulator_list(Blackhole bh) {
        final int[] sum = {0};
        list.forEach(i -> sum[0] += i);
        bh.consume(sum[0]);
    }

    @Benchmark
    public void streamForeachAccumulator_array(Blackhole bh) {
        final int[] sum = {0};
        Arrays.stream(array).forEach(i -> sum[0] += i);
        bh.consume(sum[0]);
    }

    @Benchmark
    public void streamSum_list(Blackhole bh) {
        int sum = list.stream().mapToInt(i -> i).sum();
        bh.consume(sum);
    }

    @Benchmark
    public void streamSum_array(Blackhole bh) {
        int sum = Arrays.stream(array).sum();
        bh.consume(sum);
    }

    @Benchmark
    public void streamParallelSum_list(Blackhole bh) {
        int sum = list.parallelStream().mapToInt(i -> i).sum();
        bh.consume(sum);
    }

    @Benchmark
    public void streamParallelSum_array(Blackhole bh) {
        int sum = Arrays.stream(array).parallel().sum();
        bh.consume(sum);
    }
}
