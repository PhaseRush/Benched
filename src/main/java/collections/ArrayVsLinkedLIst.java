package collections;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.LinkedList;
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
public class ArrayVsLinkedLIst {

    @Param({"10", "1000"})
    private int N;

    private List<String> LIST;
    private String[] ARRAY;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(ArrayVsLinkedLIst.class.getSimpleName())
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
        return Stream.iterate("a", a -> a + a).limit(N).collect(Collectors.toCollection(LinkedList::new));
    }

    private String[] createArray() {
        return Stream.iterate("a", a -> a + a).limit(N).toArray(String[]::new);
    }

}
