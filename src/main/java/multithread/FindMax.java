package multithread;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Fork(value = 1, jvmArgs = {"-Xms32G", "-Xmx58G"})
@Warmup(iterations = 5, time = 3)
@Measurement(iterations = 10, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)


/*
Benchmark                             (length)  Mode  Cnt       Score       Error  Units
FindMax.findMaxForLoopLanger_array        1000  avgt   10       0.192 ±     0.001  us/op
FindMax.findMaxForLoopLanger_array      100000  avgt   10      15.929 ±     0.055  us/op
FindMax.findMaxForLoopLanger_array    10000000  avgt   10    2663.729 ±    98.380  us/op
FindMax.findMaxForLoopLanger_array   100000000  avgt   10   26254.763 ±   701.635  us/op
FindMax.findMaxForLoopLanger_array   500000000  avgt   10  131290.594 ±  2851.121  us/op
FindMax.findMaxForLoopLanger_list         1000  avgt   10       0.480 ±     0.002  us/op
FindMax.findMaxForLoopLanger_list       100000  avgt   10      59.727 ±     1.222  us/op
FindMax.findMaxForLoopLanger_list     10000000  avgt   10   14647.995 ±   511.937  us/op
FindMax.findMaxForLoopLanger_list    100000000  avgt   10  213144.359 ±  6957.416  us/op
FindMax.findMaxForLoopLanger_list    500000000  avgt   10  998926.327 ± 53871.569  us/op
FindMax.findMaxStreamParallel_array       1000  avgt   10      19.029 ±     2.207  us/op
FindMax.findMaxStreamParallel_array     100000  avgt   10      21.175 ±     2.208  us/op
FindMax.findMaxStreamParallel_array   10000000  avgt   10     788.521 ±    55.693  us/op
FindMax.findMaxStreamParallel_array  100000000  avgt   10   10032.164 ±   303.213  us/op
FindMax.findMaxStreamParallel_array  500000000  avgt   10   49927.317 ±  1596.925  us/op
FindMax.findMaxStreamParallel_list        1000  avgt   10      19.775 ±     3.196  us/op
FindMax.findMaxStreamParallel_list      100000  avgt   10     143.674 ±     2.829  us/op
FindMax.findMaxStreamParallel_list    10000000  avgt   10   23956.855 ±   539.292  us/op
FindMax.findMaxStreamParallel_list   100000000  avgt   10  246686.228 ±  7663.080  us/op
FindMax.findMaxStream_array               1000  avgt   10       0.434 ±     0.004  us/op
FindMax.findMaxStream_array             100000  avgt   10      39.211 ±     0.213  us/op
FindMax.findMaxStream_array           10000000  avgt   10    4734.146 ±    78.922  us/op
FindMax.findMaxStream_array          100000000  avgt   10   46377.436 ±   422.110  us/op
FindMax.findMaxStream_array          500000000  avgt   10  234416.495 ±  5001.193  us/op
FindMax.findMaxStream_list                1000  avgt   10       3.044 ±     0.466  us/op
FindMax.findMaxStream_list              100000  avgt   10     343.606 ±    46.400  us/op
FindMax.findMaxStream_list            10000000  avgt   10   57560.206 ±  7512.181  us/op
FindMax.findMaxStream_list           100000000  avgt   10  613204.818 ± 76014.536  us/op
 */
public class FindMax {
    @Param({"1000", "100000", "10000000", "100000000", "500000000"})
    private int length;
    private int[] ints;
    private ArrayList<Integer> intList;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(FindMax.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        ints = ThreadLocalRandom.current().ints(length).toArray();
        // could just say toList but want to make it obvious to people who dont know that it defaults to arraylist
        intList = ThreadLocalRandom.current().ints().limit(length).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    @Benchmark
    public int findMaxForLoopLanger_array() {
        int[] a = ints;
        int e = ints.length;
        int m = Integer.MIN_VALUE;
        for (int i = 0; i < e; i++)
            if (a[i] > m) m = a[i];
        return m;
    }

    @Benchmark
    public int findMaxStream_array() {
        return Arrays.stream(ints)
                .reduce(Integer.MIN_VALUE, Math::max);
    }

    @Benchmark
    public int findMaxStreamParallel_array() {
        return Arrays.stream(ints)
                .parallel()
                .reduce(Integer.MIN_VALUE, Math::max);
    }

    @Benchmark
    public int findMaxForLoopLanger_list() {
        int max = Integer.MIN_VALUE;
        for (Integer i : intList) {
            if (i > max) max = i;
        }
        return max;
    }

    @Benchmark
    public int findMaxStream_list() {
        return intList.stream()
                .reduce(Integer.MIN_VALUE, Math::max);
    }

    @Benchmark
    public int findMaxStreamParallel_list() {
        return intList.stream()
                .parallel()
                .reduce(Integer.MIN_VALUE, Math::max);
    }
}