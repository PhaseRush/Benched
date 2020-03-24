package collections;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class CollectionBench {

    @Param({"1000000"})
    private int N;

    private List<Integer> LIST;
    private int[] ARRAY;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(CollectionBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }


    @Setup
    public void setup() {
        LIST = createList();
        ARRAY = createArray();
    }

    @Benchmark
    public void list_loopFor(Blackhole bh) {
        for (Integer integer : LIST) {
            bh.consume(integer);
        }
    }

    @Benchmark
    public void array_listFor(Blackhole bh) {
        for (int i1 = 0; i1 < ARRAY.length; i1++) {
            bh.consume(ARRAY[i1]);
        }
    }

    @Benchmark
    public void list_loopWhile(Blackhole bh) {
        int i = 0;
        while (i < LIST.size()) {
            bh.consume(LIST.get(i));
            i++;
        }
    }

    @Benchmark
    public void array_loopWhile(Blackhole bh) {
        int i = 0;
        while (i < ARRAY.length) {
            bh.consume(ARRAY[i]);
            i++;
        }
    }

    @Benchmark
    public void list_iterator(Blackhole bh) {
        Iterator<Integer> iterator = LIST.iterator();
        while (iterator.hasNext()) {
            bh.consume(iterator.next());
        }
    }

    @Benchmark
    public void sum_even_stream_list(Blackhole bh) {
        bh.consume(LIST.stream()
                .filter(i -> i % 2 == 0)
                .reduce(0, Integer::sum)
        );
    }

    @Benchmark
    public void sum_even_loop_list(Blackhole bh) {
        int sum = 0;
        for (int i : LIST) {
            if (i % 2 == 0) sum += i;
        }
        bh.consume(sum);
    }

    @Benchmark
    public void sum_even_stream_array(Blackhole bh) {
        bh.consume(Arrays.stream(ARRAY)
                .filter(i -> i % 2 == 0)
                .reduce(0, Integer::sum)
        );
    }

    @Benchmark
    public void sum_even_stream_array_primitive(Blackhole bh) {
        bh.consume(Arrays.stream(ARRAY).sum());
    }

    @Benchmark
    public void sum_even_loop_array(Blackhole bh) {
        int sum = 0;
        for (int i : ARRAY) {
            if (i % 2 == 0) sum += i;
        }
        bh.consume(sum);
    }

    @Benchmark
    public void array_iteratorUsingGuava(Blackhole bh) {
        PrimitiveIterator.OfInt iterator = Arrays.stream(ARRAY).iterator();
        while (iterator.hasNext()) {
            bh.consume(iterator.next());
        }
    }

    @Benchmark
    public void list_replace(Blackhole bh) {
        for (int i = 0; i < N; i++) {
            LIST.set(i, Integer.MAX_VALUE);
        }
    }

    @Benchmark
    public void array_replace(Blackhole bh) {
        for (int i = 0; i < N; i++) {
            ARRAY[i] = Integer.MAX_VALUE;
        }
    }

    private List<Integer> createList() {
        return IntStream.range(0, N).boxed().collect(Collectors.toList());
    }

    private int[] createArray() {
        return IntStream.range(0, N).toArray();
    }

}
