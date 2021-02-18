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
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx40G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                                           (M)       (N)  Mode  Cnt     Score      Error  Units
CollectionContains.hashSet_multiple                   1        10  avgt    5     1.021 ±    0.004  us/op
CollectionContains.hashSet_multiple                   1  10000000  avgt    5     5.138 ±    1.299  us/op
CollectionContains.hashSet_multiple              100000  10000000  avgt    5  5119.828 ± 1074.545  us/op
CollectionContains.hashSet_single_exists              1        10  avgt    5     1.022 ±    0.020  us/op
CollectionContains.hashSet_single_exists              1  10000000  avgt    5     4.882 ±    1.001  us/op
CollectionContains.hashSet_single_exists         100000        10  avgt    5     1.022 ±    0.034  us/op
CollectionContains.hashSet_single_exists         100000  10000000  avgt    5     5.000 ±    0.853  us/op
CollectionContains.hashSet_single_missing             1        10  avgt    5     1.018 ±    0.002  us/op
CollectionContains.hashSet_single_missing             1  10000000  avgt    5     4.871 ±    1.854  us/op
CollectionContains.hashSet_single_missing        100000        10  avgt    5     1.018 ±    0.012  us/op
CollectionContains.hashSet_single_missing        100000  10000000  avgt    5     5.267 ±    0.703  us/op
CollectionContains.linkedHashSet_multiple             1        10  avgt    5     1.031 ±    0.023  us/op
CollectionContains.linkedHashSet_multiple             1  10000000  avgt    5     5.362 ±    1.379  us/op
CollectionContains.linkedHashSet_multiple        100000  10000000  avgt    5  5124.894 ± 1587.096  us/op
CollectionContains.linkedHashSet_single_exists        1        10  avgt    5     1.031 ±    0.041  us/op
CollectionContains.linkedHashSet_single_exists        1  10000000  avgt    5     4.992 ±    1.106  us/op
CollectionContains.linkedHashSet_single_exists   100000        10  avgt    5     1.008 ±    0.007  us/op
CollectionContains.linkedHashSet_single_exists   100000  10000000  avgt    5     5.449 ±    0.241  us/op
CollectionContains.linkedHashSet_single_missing       1        10  avgt    5     1.032 ±    0.030  us/op
CollectionContains.linkedHashSet_single_missing       1  10000000  avgt    5     5.129 ±    0.381  us/op
CollectionContains.linkedHashSet_single_missing  100000        10  avgt    5     1.030 ±    0.029  us/op
CollectionContains.linkedHashSet_single_missing  100000  10000000  avgt    5     5.674 ±    0.903  us/op
CollectionContains.stack_multiple                     1        10  avgt    5     1.029 ±    0.020  us/op
CollectionContains.stack_multiple                     1  10000000  avgt    5     3.780 ±    0.768  us/op
CollectionContains.stack_multiple                100000  10000000  avgt    5   694.080 ± 4999.220  us/op
CollectionContains.stack_single_exists                1        10  avgt    5     1.051 ±    0.016  us/op
CollectionContains.stack_single_exists                1  10000000  avgt    5     3.502 ±    0.328  us/op
CollectionContains.stack_single_exists           100000        10  avgt    5     1.025 ±    0.023  us/op
CollectionContains.stack_single_exists           100000  10000000  avgt    5     3.984 ±    0.936  us/op
CollectionContains.stack_single_missing               1        10  avgt    5     1.031 ±    0.040  us/op
CollectionContains.stack_single_missing               1  10000000  avgt    5     3.644 ±    0.831  us/op
CollectionContains.stack_single_missing          100000        10  avgt    5     1.028 ±    0.024  us/op
CollectionContains.stack_single_missing          100000  10000000  avgt    5     3.856 ±    1.344  us/op
CollectionContains.treeSet_multiple                   1        10  avgt    5     1.027 ±    0.033  us/op
CollectionContains.treeSet_multiple                   1  10000000  avgt    5     4.605 ±    1.350  us/op
CollectionContains.treeSet_multiple              100000  10000000  avgt    5    28.314 ±    9.557  us/op
CollectionContains.treeSet_single_exists              1        10  avgt    5     0.988 ±    0.037  us/op
CollectionContains.treeSet_single_exists              1  10000000  avgt    5     4.486 ±    0.709  us/op
CollectionContains.treeSet_single_exists         100000        10  avgt    5     1.015 ±    0.027  us/op
CollectionContains.treeSet_single_exists         100000  10000000  avgt    5     4.967 ±    1.208  us/op
CollectionContains.treeSet_single_missing             1        10  avgt    5     1.027 ±    0.028  us/op
CollectionContains.treeSet_single_missing             1  10000000  avgt    5     4.542 ±    0.926  us/op
CollectionContains.treeSet_single_missing        100000        10  avgt    5     1.017 ±    0.015  us/op
CollectionContains.treeSet_single_missing        100000  10000000  avgt    5     4.874 ±    0.914  us/op
 */
public class CollectionContains {

    @Param({"10", "10000000"})
    private int N;

    @Param({"1", "100000"})
    private int M;

    private String[] checks;

    private String singleCheckExists;
    private String singleCheckMissing;

    private Stack<String> stack;
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
