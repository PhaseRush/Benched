package collections;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
 */
public class ArrayListVsIterator {

    @Param({"10000000"})
    private int N;

    private List<Integer> LIST;
    private Iterator<Integer> ITER;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(ArrayListVsIterator.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }


    @Setup
    public void setup() {
        LIST = createList();
        ITER = createIter();
    }


    @Benchmark
    public void list_create(Blackhole bh) {
        List<Integer> list = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            list.add(i);
        }
        bh.consume(list);
    }

    @Benchmark
    public void iter_create(Blackhole bh) {
        Iterator<Integer> iter = new Iterator<>() {
            int curr = 0;
            int max = N;

            @Override
            public boolean hasNext() {
                return curr < max;
            }

            @Override
            public Integer next() {
                curr++;
                return curr - 1;
            }
        };
        bh.consume(iter);
    }

    @Benchmark
    public void list_iterate(Blackhole bh) {
        for (int i = 0; i < N; i++) {
            bh.consume(LIST.get(i));
        }
    }

    @Benchmark
    public void iter_iterate(Blackhole bh) {
        while(ITER.hasNext()) {
            bh.consume(ITER.next());
        }
    }

    private List<Integer> createList() {
        return IntStream.range(0, N).boxed().collect(Collectors.toList());
    }

    private Iterator<Integer> createIter() {
        return createList().iterator();
    }

}
