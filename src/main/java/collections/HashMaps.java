package collections;

import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;
import com.koloboke.collect.map.hash.HashIntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms85G", "-Xmx85G"}, warmups = 3)
@Measurement(iterations = 5)
public class HashMaps {
    //    @Param({"1", "100", "10000", "1000000", "1000000000"})
    @Param({"100"})
    private int N;

    @Param({"10"})
    private int FREQUENCY;

    private List<Integer> feederList;

    private HashMap<Integer, Integer> javaHashMap;
    private Map<Integer, Integer> javaImmutableMap;
    private Hashtable<Integer, Integer> javaHashTable;
    private ConcurrentHashMap<Integer, Integer> javaConcurrentHashMap;
    private TreeMap<Integer, Integer> javaTreeMap;

    private Int2IntOpenHashMap fastutilPrimitiveHashMap;

    private HashIntIntMap kolobokePrimitiveHashMap;
    private HashIntIntMap kolobokeImmutablePrimitiveHashMap;

    private IntIntHashMap eclipseGsPrimitiveHashMap;
    private MutableMap<Integer, Integer> eclipseGsMutableHashMap;
    private MutableMap<Integer, Integer> eclipseGsMutableConcurrentHashMap;
    private MutableMap<Integer, Integer> eclipseGsConcurrentHashMap;
    private ImmutableMap<Integer, Integer> eclipseGsImmutableMap;
    private ImmutableMap<Integer, Integer> eclipseGsImmutableConcurrentMap;


    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(HashMaps.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }


    @Setup
    public void setup() {
        feederList = ThreadLocalRandom.current().ints(N / FREQUENCY).boxed().collect(Collectors.toList());
        for (int i = 0; i < FREQUENCY; i++)
            //noinspection CollectionAddedToSelf
            feederList.addAll(feederList);

        var freqMap = feederList.stream()
                .collect(Collectors.groupingBy(i -> i,
                        Collectors.summingInt(acc -> 1)));

        javaHashMap = new HashMap<>(freqMap);
        javaImmutableMap = Map.copyOf(freqMap);
        javaHashTable = new Hashtable<>(freqMap);
        javaConcurrentHashMap = new ConcurrentHashMap<>(freqMap);
        javaTreeMap = new TreeMap<>(freqMap);

        fastutilPrimitiveHashMap = new Int2IntOpenHashMap(freqMap);

        kolobokeImmutablePrimitiveHashMap = HashIntIntMaps.newImmutableMap(freqMap);
        kolobokePrimitiveHashMap = HashIntIntMaps.newImmutableMap(freqMap);

//        eclipseGsHashMap = new com.gs.collections.impl.map.mutable.Concurrent<>();
        eclipseGsPrimitiveHashMap = new IntIntHashMap();
        freqMap.forEach((k, v) -> eclipseGsPrimitiveHashMap.put(k, v));
        eclipseGsMutableHashMap = new UnifiedMap<>(freqMap);
        eclipseGsMutableConcurrentHashMap = new com.gs.collections.impl.map.mutable.ConcurrentHashMap<>();
        eclipseGsImmutableConcurrentMap = new com.gs.collections.impl.map.immutable.ImmutableUnifiedMap<>();
//        eclipseGsMutableConcurrentHashMap
    }

    @Benchmark
    public void f(Blackhole bh) {

    }
}
