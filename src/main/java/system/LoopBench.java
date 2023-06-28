package system;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)
/**
 Benchmark                         (N)  Mode  Cnt  Score   Error  Units
 LoopBench.foreachAccumulator  1000000  avgt    5  3.044 ± 1.052  ms/op
 LoopBench.foreachLoop         1000000  avgt    5  2.841 ± 0.337  ms/op
 LoopBench.streamParallelSum   1000000  avgt    5  0.217 ± 0.033  ms/op
 LoopBench.streamSum           1000000  avgt    5  2.941 ± 0.992  ms/op
 */
public class LoopBench {

    @Param({"1000000"})
    private int N;

    private List<Integer> list;

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
    }

    @Benchmark
    public void foreachLoop(Blackhole bh) {
        int sum = 0;
        for (Integer i : list) {
            sum += i;
        }
        bh.consume(sum);
    }

    @Benchmark
    public void foreachAccumulator(Blackhole bh) {
        final int[] sum = {0};
        list.forEach(i -> sum[0] += i);
        bh.consume(sum[0]);
    }

    @Benchmark
    public void streamSum(Blackhole bh) {
        int sum = list.stream().mapToInt(i -> i).sum();
        bh.consume(sum);
    }

    @Benchmark
    public void streamParallelSum(Blackhole bh) {
        int sum = list.parallelStream().mapToInt(i -> i).sum();
        bh.consume(sum);
    }
}
