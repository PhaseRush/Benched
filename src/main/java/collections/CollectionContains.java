package collections;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms50G", "-Xmx60G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                                         (M)        (N)  Mode  Cnt   Score    Error  Units
CollectionContains.arrayList_single_exists       1000  100000000  avgt    5   3.590 ±  0.962  us/op
CollectionContains.hashSet_single_exists         1000  100000000  avgt    5   5.378 ±  2.614  us/op
CollectionContains.linkedHashSet_single_exists   1000  100000000  avgt    5   7.836 ± 12.144  us/op
CollectionContains.stack_single_exists           1000  100000000  avgt    5   3.506 ±  0.958  us/op
CollectionContains.treeSet_single_exists         1000  100000000  avgt    5   5.112 ±  3.650  us/op

CollectionContains.arrayList_single_missing      1000  100000000  avgt    5   3.772 ±  1.436  us/op
CollectionContains.hashSet_single_missing        1000  100000000  avgt    5   6.258 ±  9.275  us/op
CollectionContains.linkedHashSet_single_missing  1000  100000000  avgt    5   5.238 ±  2.762  us/op
CollectionContains.stack_single_missing          1000  100000000  avgt    5   3.883 ±  2.001  us/op
CollectionContains.treeSet_single_missing        1000  100000000  avgt    5   4.959 ±  3.696  us/op

CollectionContains.arrayList_multiple            1000  100000000  avgt    5  35.591 ± 35.250  us/op
CollectionContains.hashSet_multiple              1000  100000000  avgt    5  69.646 ± 70.235  us/op
CollectionContains.linkedHashSet_multiple        1000  100000000  avgt    5  67.089 ± 38.233  us/op
CollectionContains.stack_multiple                1000  100000000  avgt    5  33.482 ± 39.999  us/op
CollectionContains.treeSet_multiple              1000  100000000  avgt    5  39.097 ± 39.287  us/op

Benchmark                                            (M)        (N)  Mode  Cnt      Score      Error  Units
CollectionContains.arrayList_single_exists       1000000  100000000  avgt    5      4.931 ±    1.493  us/op
CollectionContains.hashSet_single_exists         1000000  100000000  avgt    5      6.328 ±    2.054  us/op
CollectionContains.linkedHashSet_single_exists   1000000  100000000  avgt    5      6.132 ±    1.829  us/op
CollectionContains.stack_single_exists           1000000  100000000  avgt    5      4.553 ±    2.096  us/op
CollectionContains.treeSet_single_exists         1000000  100000000  avgt    5      6.076 ±    3.873  us/op

CollectionContains.arrayList_single_missing      1000000  100000000  avgt    5      5.001 ±    3.838  us/op
CollectionContains.hashSet_single_missing        1000000  100000000  avgt    5      8.535 ±   15.291  us/op
CollectionContains.linkedHashSet_single_missing  1000000  100000000  avgt    5      6.425 ±    1.658  us/op
CollectionContains.stack_single_missing          1000000  100000000  avgt    5      4.833 ±    2.518  us/op
CollectionContains.treeSet_single_missing        1000000  100000000  avgt    5      5.071 ±    2.254  us/op

CollectionContains.arrayList_multiple            1000000  100000000  avgt    5    293.711 ±   25.025  us/op
CollectionContains.hashSet_multiple              1000000  100000000  avgt    5  91242.225 ± 5235.116  us/op
CollectionContains.linkedHashSet_multiple        1000000  100000000  avgt    5  97421.753 ± 7467.232  us/op
CollectionContains.stack_multiple                1000000  100000000  avgt    5   1269.770 ±   12.870  us/op
CollectionContains.treeSet_multiple              1000000  100000000  avgt    5    295.932 ±   31.529  us/op
 */
public class CollectionContains {

    @Param({"100000000"})
    private int N;

    @Param({"1000000"})
    private int M;

    private String[] checks;

    private String singleCheckExists;
    private String singleCheckMissing;

    private Stack<String> stack;
    private List<String> arrayList;
    private Set<String> hashSet;
    private Set<String> treeSet;
    private Set<String> linkedHashSet;

    // https://stackoverflow.com/a/36391959
    private static <T> Collector<T, ?, List<T>> toShuffledList() {
        return Collectors.collectingAndThen(
                Collectors.toCollection(ArrayList::new),
                list -> {
                    Collections.shuffle(list);
                    return list;
                });
    }

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(CollectionContains.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup(Level.Invocation)
    public void setup() {
        stack = new Stack<>();
        arrayList = new ArrayList<>();
        hashSet = new HashSet<>();
        treeSet = new TreeSet<>();
        linkedHashSet = new LinkedHashSet<>();

        singleCheckExists = String.valueOf(ThreadLocalRandom.current().nextInt(N));
        singleCheckMissing = String.valueOf(ThreadLocalRandom.current().nextInt(N + 1, N * 2));

        checks = IntStream.range(0, N * 2)
                .mapToObj(String::valueOf)
                .collect(toShuffledList())
                .stream().limit(M)
                .toArray(String[]::new);
    }

    @Benchmark
    public void stack_single_exists(Blackhole bh) {
        bh.consume(stack.contains(singleCheckExists));
    }

    @Benchmark
    public void stack_single_missing(Blackhole bh) {
        bh.consume(stack.contains(singleCheckMissing));
    }

    @Benchmark
    public void stack_multiple(Blackhole bh) {
        boolean b = false; // dummy
        for (int i = 0; i < M; i++) {
            b = stack.contains(checks[i]);
        }
        bh.consume(b);
    }

    @Benchmark
    public void arrayList_single_exists(Blackhole bh) {
        bh.consume(arrayList.contains(singleCheckExists));
    }

    @Benchmark
    public void arrayList_single_missing(Blackhole bh) {
        bh.consume(arrayList.contains(singleCheckMissing));
    }

    @Benchmark
    public void arrayList_multiple(Blackhole bh) {
        boolean b = false; // dummy
        for (int i = 0; i < M; i++) {
            b = arrayList.contains(checks[i]);
        }
        bh.consume(b);
    }

    @Benchmark
    public void hashSet_single_exists(Blackhole bh) {
        bh.consume(hashSet.contains(singleCheckExists));
    }

    @Benchmark
    public void hashSet_single_missing(Blackhole bh) {
        bh.consume(hashSet.contains(singleCheckMissing));
    }

    @Benchmark
    public void hashSet_multiple(Blackhole bh) {
        boolean b = false; // dummy
        for (int i = 0; i < M; i++) {
            b = hashSet.contains(checks[i]);
        }
        bh.consume(b);
    }

    @Benchmark
    public void treeSet_single_exists(Blackhole bh) {
        bh.consume(treeSet.contains(singleCheckExists));
    }

    @Benchmark
    public void treeSet_single_missing(Blackhole bh) {
        bh.consume(treeSet.contains(singleCheckMissing));
    }

    @Benchmark
    public void treeSet_multiple(Blackhole bh) {
        boolean b = false; // dummy
        for (int i = 0; i < M; i++) {
            b = treeSet.contains(checks[i]);
        }
        bh.consume(b);
    }

    @Benchmark
    public void linkedHashSet_single_exists(Blackhole bh) {
        bh.consume(linkedHashSet.contains(singleCheckExists));
    }

    @Benchmark
    public void linkedHashSet_single_missing(Blackhole bh) {
        bh.consume(linkedHashSet.contains(singleCheckMissing));
    }

    @Benchmark
    public void linkedHashSet_multiple(Blackhole bh) {
        boolean b = false; // dummy
        for (int i = 0; i < M; i++) {
            b = linkedHashSet.contains(checks[i]);
        }
        bh.consume(b);
    }

}
