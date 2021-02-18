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
