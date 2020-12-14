package collections;

import com.koloboke.collect.set.hash.HashIntSet;
import com.koloboke.collect.set.hash.HashIntSetFactory;
import com.koloboke.collect.set.hash.HashIntSets;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms85G", "-Xmx85G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                                            (N)  Mode  Cnt         Score         Error  Units
HashSets.populate_JavaHashSet                  100000000  avgt    5  22758073.820 ± 7199082.177  us/op
HashSets.populate_JavaImmutableCollectionsSet  100000000  avgt    5  35930058.780 ± 4094597.734  us/op
HashSets.populate_JavaStaticHashSet            100000000  avgt    5  24818409.840 ± 2394653.955  us/op
HashSets.populate_fastUtilIntHashSet           100000000  avgt    5   6418923.840 ±  312410.904  us/op
 */
public class HashSets {
    //    @Param({"1", "100", "10000", "1000000", "1000000000"})
    @Param({"100000000"})
    private int N;

    private static List<Integer> feederList;
    private static int[] feederArray;

    private Set<Integer> javaImmutableCollectionsSet;
    private Set<Integer> javaHashSet;
    private static HashSet<Integer> javaStaticHashSet;
    private IntSet fastUtilsHashSet;
    private HashIntSet kolobokeHashSet;


    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(HashSets.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }


    @Setup
    public void setup() {
        feederList = ThreadLocalRandom.current().ints(N).boxed().collect(Collectors.toList());
        feederArray = feederList.stream().mapToInt(i -> i).toArray();

        javaImmutableCollectionsSet = Set.copyOf(feederList);
        javaHashSet = new HashSet<>(feederList);
        javaStaticHashSet = new HashSet<>();
        fastUtilsHashSet = new IntOpenHashSet(feederList);

//        HashIntSetFactory factory = HashIntSets.getDefaultFactory();
//        kolobokeHashSet = factory.newUpdatableSet(feederList);
    }

    @Benchmark
    public Set<Integer> populate_JavaImmutableCollectionsSet() {
        return Set.copyOf(feederList);
    }

    @Benchmark
    public Set<Integer> populate_JavaHashSet() {
        return new HashSet<>(feederList);
    }

    @Benchmark
    public void populate_JavaStaticHashSet(Blackhole bh) {
        bh.consume(javaStaticHashSet.addAll(feederList));
    }

    @Benchmark
    public void populate_fastUtilIntHashSet(Blackhole bh) {
        bh.consume(fastUtilsHashSet.addAll(feederList));
    }
}
