package stream;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.Random;
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
 * Benchmark                               (N)  Mode  Cnt   Score   Error  Units
 * StreamBench.parallelMapFilterSum    1000000  avgt    5   3.383 ± 0.163  ms/op
 * StreamBench.sequentialMapFilterSum  1000000  avgt    5  11.906 ± 3.249  ms/op
 */
public class StreamBench {

    @Param({"1000000"})
    private int N;

    private List<Wrapper> list;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StreamBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        list = IntStream.range(0, N)
                .mapToObj(i -> new Wrapper(String.valueOf(i), i))
                .collect(Collectors.toList());
    }

    @Benchmark
    public void sequentialMapFilterSum(Blackhole bh) {
        int sum = list.stream()
                .map(wrapper -> wrapper.i)
                .filter(i -> i % 2 == 0)
                .reduce(0, Integer::sum);
        bh.consume(sum);
    }

    @Benchmark
    public void parallelMapFilterSum(Blackhole bh) {
        int sum = list.parallelStream()
                .map(wrapper -> wrapper.i)
                .filter(i -> i % 2 == 0)
                .reduce(0, Integer::sum);
        bh.consume(sum);
    }


    static class Wrapper {
        static final Random rand = new Random(1L);
        String string;
        int i;

        public Wrapper() {
            i = rand.nextInt();
            string = String.valueOf(i);
        }

        public Wrapper(String string, int i) {
            this.string = string;
            this.i = i;
        }
    }
}
