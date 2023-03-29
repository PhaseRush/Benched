package algorithm;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
/*
Benchmark                       (N)  Mode  Cnt         Score        Error  Units
LargestRange.sortMethod         100  avgt    5         0.065 ±      0.002  us/op
LargestRange.sortMethod     1000000  avgt    5       681.931 ±     70.942  us/op
LargestRange.sortMethod   100000000  avgt    5     94286.291 ±   2341.084  us/op

Benchmark                       (N)  Mode  Cnt         Score        Error  Units
LargestRange.graphMethod        100  avgt    5         0.632 ±      0.014  us/op
LargestRange.graphMethod    1000000  avgt    5     60670.491 ±  15969.869  us/op
LargestRange.graphMethod  100000000  avgt    5  17145509.540 ± 888630.727  us/op
 */
public class LargestRange {

    //                             {1, 3, 6, 8, 9, 10, 11, 12}
    private static final int[] A = {9, 6, 1, 3, 8, 10, 12, 11};
    @Param({"100000000"})
    private static long N;

    private static int[] B;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(LargestRange.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .jvmArgs("-Xmx64G")
                .warmupIterations(3)
                .measurementIterations(5)
                .build()).run();
    }

    @Setup()
    public void setup() {
        B = ThreadLocalRandom.current()
                .ints()
                .limit(N)
                .toArray();
    }

//    @Benchmark
    public int[] sortMethod() {
        Arrays.sort(B); // in-place, technically
        int currentRun = 1, bestRun = 1, upperBound = B[0];

        for (int i = 1; i < N; i++) {
            if (B[i] == B[i - 1]) continue; // duplicate, skip but don't add
            if (B[i] == B[i - 1] + 1) {     // valid, update current run and update max
                if (++currentRun > bestRun) {
                    bestRun = currentRun;
                    upperBound = B[i];
                }
            } else {                        // invalid, reset current run
                currentRun = 1;
            }
        }
        return new int[]{upperBound - bestRun + 1, upperBound};
    }

    /**
     * Construct an implicit graph based on neighbours of any given value.
     * <p>
     * The input array {1, 2, 4} will result in the graph:
     * 1 <-> 2
     * 4
     * <p>
     * Note 1: Neighbours can be implicitly found by checking the set for +- 1 value.
     * E.g., when we encounter 4, we will check the graph for 3 and 5, neither
     * of which exist.
     * <p>
     * Iterate through populating the set of Nodes, then iterate through input
     * array, treating each as a test value.
     * <p>
     * Note 2: Given any test value, we will also test the value's +-1 repeatedly until
     * no more neighbours are found. This will be the run for not only the test
     * value, but all values that were reached via this traversal.
     * <p>
     * <p>
     * Side note: this is like the largest dynamic islands problem, but only with
     * a Node set. Edges are implicitly +- 1 value.
     *
     * @return
     */
    @Benchmark
    public int[] graphMethod() {
        // 1. JDK HashSet
        Set<Integer> nodes = new HashSet<>();
        for (int i : B) {
            nodes.add(i);
        }

        // 2. Eclipse Collections
//        IntHashSet nodes = IntHashSet.newSetWith(B);

        // 3. FastUtils
//        IntOpenHashSet nodes = new IntOpenHashSet(B);

        int bestRun = 0, bestLower = 0, bestUpper = 0;
        int currRun = 0, above = 0, below = 0;


        for (int test : B) {
            if (!nodes.remove(test)) continue; // already visited and therefore already counted due to note 2
            currRun = 1; // reset curr to 1, since this is a new test value

            while (true) { // check values above test
                if (!nodes.remove(++above + test)) break; // value above does not exist
                ++currRun;
            }

            while (true) { // check vlaues below test
                if (!nodes.remove(--below + test)) break; // value below does not exist
                ++currRun;
            }
            if (currRun > bestRun) {
                bestRun = currRun;
                bestLower = test + below + 1; // use prev value since curr failed
                bestUpper = test + above - 1; // use prev value since curr failed
            }
            above = 0;
            below = 0;
        }
        return new int[]{bestLower, bestUpper};
    }

}
