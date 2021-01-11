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
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx40G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                                                     (N)  Mode  Cnt        Score          Error  Units
ArrayDequeVsLinkedList.arraydeque_init                         10  avgt    5        1.101 ±        0.032  us/op
ArrayDequeVsLinkedList.arraydeque_init                       1000  avgt    5        4.516 ±        0.012  us/op
ArrayDequeVsLinkedList.arraydeque_init                   10000000  avgt    5    92174.662 ±   231510.419  us/op
ArrayDequeVsLinkedList.arraydeque_init                  100000000  avgt    5   682196.480 ±  1688984.231  us/op

ArrayDequeVsLinkedList.arraydeque_init_preSized                10  avgt    5        1.102 ±        0.007  us/op
ArrayDequeVsLinkedList.arraydeque_init_preSized              1000  avgt    5        4.040 ±        0.101  us/op
ArrayDequeVsLinkedList.arraydeque_init_preSized          10000000  avgt    5    50533.598 ±    16634.005  us/op
ArrayDequeVsLinkedList.arraydeque_init_preSized         100000000  avgt    5  1917268.242 ± 11645801.391  us/op

ArrayDequeVsLinkedList.arraydeque_traverse                     10  avgt    5        1.076 ±        0.038  us/op
ArrayDequeVsLinkedList.arraydeque_traverse                   1000  avgt    5        3.861 ±        0.083  us/op
ArrayDequeVsLinkedList.arraydeque_traverse               10000000  avgt    5    42233.569 ±    21792.968  us/op
ArrayDequeVsLinkedList.arraydeque_traverse              100000000  avgt    5   433594.004 ±    99847.736  us/op

ArrayDequeVsLinkedList.arraydeque_traverseAndRemoveAll         10  avgt    5        1.065 ±        0.002  us/op
ArrayDequeVsLinkedList.arraydeque_traverseAndRemoveAll       1000  avgt    5        3.844 ±        0.049  us/op
ArrayDequeVsLinkedList.arraydeque_traverseAndRemoveAll   10000000  avgt    5    33981.403 ±    11242.209  us/op
ArrayDequeVsLinkedList.arraydeque_traverseAndRemoveAll  100000000  avgt    5   449730.860 ±   547815.400  us/op

ArrayDequeVsLinkedList.linkedList_manualPopulate               10  avgt    5        1.087 ±        0.011  us/op
ArrayDequeVsLinkedList.linkedList_manualPopulate             1000  avgt    5        5.004 ±        0.052  us/op
ArrayDequeVsLinkedList.linkedList_manualPopulate         10000000  avgt    5   836581.908 ±  1542827.240  us/op
ArrayDequeVsLinkedList.linkedList_manualPopulate        100000000  avgt    5  5605762.325 ±  5444754.681  us/op

ArrayDequeVsLinkedList.linkedlist_init                         10  avgt    5        1.079 ±        0.013  us/op
ArrayDequeVsLinkedList.linkedlist_init                       1000  avgt    5        4.836 ±        0.259  us/op
ArrayDequeVsLinkedList.linkedlist_init                   10000000  avgt    5   543756.146 ±   966702.813  us/op
ArrayDequeVsLinkedList.linkedlist_init                  100000000  avgt    5  7181044.910 ± 10077727.767  us/op

ArrayDequeVsLinkedList.linkedlist_traverse                     10  avgt    5        1.071 ±        0.002  us/op
ArrayDequeVsLinkedList.linkedlist_traverse                   1000  avgt    5        3.933 ±        0.037  us/op
ArrayDequeVsLinkedList.linkedlist_traverse               10000000  avgt    5   110545.058 ±    37049.515  us/op
ArrayDequeVsLinkedList.linkedlist_traverse              100000000  avgt    5  1354218.609 ±   957037.882  us/op

ArrayDequeVsLinkedList.linkedlist_traverseAndRemoveAll         10  avgt    5        1.072 ±        0.008  us/op
ArrayDequeVsLinkedList.linkedlist_traverseAndRemoveAll       1000  avgt    5        4.307 ±        0.087  us/op
ArrayDequeVsLinkedList.linkedlist_traverseAndRemoveAll   10000000  avgt    5   132132.785 ±   201443.090  us/op
ArrayDequeVsLinkedList.linkedlist_traverseAndRemoveAll  100000000  avgt    5  1701792.392 ±  1759202.243  us/op

Process finished with exit code 0
 */
public class ArrayDequeVsLinkedList {

    @Param({"10", "1000", "10000000", "100000000"})
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
    public void linkedlist_traverse(Blackhole bh) {
        for (Integer integer : ll) {
            bh.consume(integer);
        }
    }

    @Benchmark
    public void arraydeque_traverse(Blackhole bh) {
        for (Integer integer : ad) {
            bh.consume(integer);
        }
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
