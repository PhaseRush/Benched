package collections;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms6G", "-Xmx6G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)
/*
M1 Pro 8 core
Benchmark                           (N)  Mode  Cnt        Score        Error  Units
ArrayVsList2.array_listFor           25  avgt    5       69.982 ±      0.317  ns/op
ArrayVsList2.array_loopWhile         25  avgt    5       70.111 ±      0.307  ns/op
ArrayVsList2.array_replace           25  avgt    5       34.465 ±      0.058  ns/op
ArrayVsList2.list_loopFor            25  avgt    5       84.873 ±      0.201  ns/op
ArrayVsList2.list_loopWhile          25  avgt    5       77.040 ±      0.204  ns/op
ArrayVsList2.list_replace            25  avgt    5       51.204 ±      0.089  ns/op
ArrayVsList2.sum_even_loop_array     25  avgt    5       66.006 ±      1.193  ns/op
ArrayVsList2.sum_even_loop_list      25  avgt    5       86.658 ±      0.825  ns/op
ArrayVsList2.sum_even_stream_array   25  avgt    5  2615052.908 ± 130270.470  ns/op
ArrayVsList2.sum_even_stream_list    25  avgt    5  2581911.157 ±  34747.176  ns/op

Benchmark                              (N)  Mode  Cnt         Score        Error  Units
CollectionBench.array_listFor           25  avgt    5       104.498 ±      1.904  ns/op
CollectionBench.list_loopFor            25  avgt    5       162.450 ±      1.338  ns/op
CollectionBench.array_loopWhile         25  avgt    5       107.971 ±      1.135  ns/op
CollectionBench.list_loopWhile          25  avgt    5       118.241 ±      4.213  ns/op
CollectionBench.array_replace           25  avgt    5        42.050 ±      1.270  ns/op
CollectionBench.list_replace            25  avgt    5        77.353 ±      1.457  ns/op
CollectionBench.sum_even_loop_array     25  avgt    5       145.000 ±      2.605  ns/op
CollectionBench.sum_even_loop_list      25  avgt    5       169.395 ±      6.059  ns/op
CollectionBench.sum_even_stream_array   25  avgt    5  11645976.896 ± 308528.549  ns/op
CollectionBench.sum_even_stream_list    25  avgt    5  11900203.804 ± 689803.392  ns/op
 */
public class ArrayVsList2 {
    @Param({"25"})
    private int N;

    private List<String> LIST;
    private String[] ARRAY;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(ArrayVsList2.class.getSimpleName())
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
        for (String string : LIST) {
            bh.consume(string);
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
    public void sum_even_stream_list(Blackhole bh) {
        bh.consume(LIST.stream()
                .filter(s -> s.length() % 2 == 0)
                .reduce("", (a, b) -> a + b)
        );
    }

    @Benchmark
    public void sum_even_loop_list(Blackhole bh) {
        for (String s : LIST) {
            if (s.length() % 2 == 0) {
                bh.consume(s);
            }
        }
    }

    @Benchmark
    public void sum_even_stream_array(Blackhole bh) {
        bh.consume(Arrays.stream(ARRAY)
                .filter(s -> s.length() % 2 == 0)
                .reduce("", (a, b) -> a + b)
        );
    }

    @Benchmark
    public void sum_even_loop_array(Blackhole bh) {
        for (String s : ARRAY) {
            if (s.length() % 2 == 0) {
                bh.consume(s);
            }
        }
    }

    @Benchmark
    public void list_replace(Blackhole bh) {
        for (int i = 0; i < N; i++) {
            LIST.set(i, "a");
        }
        bh.consume(LIST);
    }

    @Benchmark
    public void array_replace(Blackhole bh) {
        for (int i = 0; i < N; i++) {
            ARRAY[i] = "a";
        }
        bh.consume(ARRAY);
    }

    private List<String> createList() {
        return Stream.iterate("a", a -> a + a).limit(N).collect(Collectors.toList());
    }

    private String[] createArray() {
        return Stream.iterate("a", a -> a + a).limit(N).toArray(String[]::new);
    }
}

