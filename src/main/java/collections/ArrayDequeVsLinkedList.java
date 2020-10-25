package collections;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                                                    (N)  Mode  Cnt    Score     Error  Units
ArrayDequeVsLinkedList.linkedlist_traverseAndRemoveAll  10000000  avgt    5  176.541 ±  78.923  ms/op
ArrayDequeVsLinkedList.arraydeque_traverseAndRemoveAll  10000000  avgt    5   76.658 ±  12.289  ms/op

ArrayDequeVsLinkedList.linkedlist_init                  10000000  avgt    5  896.525 ± 883.049  ms/op
ArrayDequeVsLinkedList.arraydeque_init                  10000000  avgt    5   66.907 ±   7.474  ms/op

ArrayDequeVsLinkedList.linkedList_manualPopulate        10000000  avgt    5  721.984 ± 170.096  ms/op
ArrayDequeVsLinkedList.arraydeque_init_preSized         10000000  avgt    5   65.927 ±  15.507  ms/op
 */
public class ArrayDequeVsLinkedList {

    @Param({"10000000"})
    private int N;

    private Deque<Integer> ad;
    private Deque<Integer> ll;

    private List<Integer> feeder; // ArrayList

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(ArrayDequeVsLinkedList.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }


    @Setup(Level.Invocation)
    public void setup() {
        ad = createDeque(ArrayDeque::new);
        ll = createDeque(LinkedList::new);

        feeder = IntStream.range(0, N).boxed().collect(Collectors.toList());
    }

    @TearDown(Level.Invocation)
    public void teardown() {
//        ad_preInit = new ArrayDeque<>(N + 2);
    }

    @Benchmark
    public void linkedlist_traverseAndRemoveAll(Blackhole bh) {
        while (!ll.isEmpty()) {
            bh.consume(ll.poll());
        }
    }

    @Benchmark
    public void arraydeque_traverseAndRemoveAll(Blackhole bh) {
        while (!ad.isEmpty()) {
            bh.consume(ad.poll());
        }
    }

    @Benchmark
    public void linkedlist_init(Blackhole bh) {
        bh.consume(new LinkedList<>(feeder));
    }

    @Benchmark
    public void arraydeque_init(Blackhole bh) {
        bh.consume(new ArrayDeque<>(feeder));
    }

    @Benchmark
    public void linkedList_manualPopulate(Blackhole bh) {
        LinkedList<Integer> ll = new LinkedList<>();
        for (int i = 0; i < feeder.size(); i++) {
            ll.push(feeder.get(i));
        }
        bh.consume(ll);
    }

    @Benchmark
    public void arraydeque_init_preSized(Blackhole bh) {
        ArrayDeque<Integer> ad_preInit = new ArrayDeque<>(N + 2);
        for (int i = 0; i < feeder.size(); i++) {
            ad_preInit.push(feeder.get(i));
        }
        bh.consume(ad_preInit);
    }

    private Deque<Integer> createDeque(Supplier<Deque<Integer>> supplier) {
        return IntStream.range(0, N).boxed().collect(Collectors.toCollection(supplier));
    }

}
