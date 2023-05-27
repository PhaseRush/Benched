package collections;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms60G", "-Xmx60G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
# VM version: JDK 18.0.1, OpenJDK 64-Bit Server VM, 18.0.1+10-FR
Benchmark                              (K)  (N)  Mode  Cnt   Score   Error  Units
HashMapOverhead.hashset_int_get          1   20  avgt    5   3.550 ± 0.200  ns/op

HashMapOverhead.arr_int_get_first        1   20  avgt    5   2.126 ± 0.021  ns/op
HashMapOverhead.arr_int_get_mid          1   20  avgt    5   3.896 ± 0.035  ns/op
HashMapOverhead.arr_int_get_last         1   20  avgt    5   6.041 ± 0.100  ns/op

HashMapOverhead.list_int_get_first       1   20  avgt    5   3.188 ± 0.110  ns/op
HashMapOverhead.list_int_get_mid         1   20  avgt    5   7.491 ± 0.131  ns/op
HashMapOverhead.list_int_get_last        1   20  avgt    5  10.669 ± 3.638  ns/op


HashMapOverhead.hashset_string_get       1   20  avgt    5   2.742 ± 0.006  ns/op

HashMapOverhead.arr_string_get_first     1   20  avgt    5   2.180 ± 0.010  ns/op
HashMapOverhead.arr_string_get_mid       1   20  avgt    5  20.371 ± 0.110  ns/op
HashMapOverhead.arr_string_get_last      1   20  avgt    5  35.992 ± 1.464  ns/op

HashMapOverhead.list_string_get_first    1   20  avgt    5   2.503 ± 0.723  ns/op
HashMapOverhead.list_string_get_mid      1   20  avgt    5  22.711 ± 1.991  ns/op
HashMapOverhead.list_string_get_last     1   20  avgt    5  23.690 ± 0.511  ns/op
 */
public class HashMapOverhead {

    @Param({"20"})
    private int N;

    @Param({"1"})
    private int K;

    private Set<String> stringSet;
    private List<String> stringList;
    private String[] stringArr;

    private Set<Integer> intSet;

    private List<Integer> intList;

    private int[] intArr;

    private String firstString, midString, lastString;
    private int firstInt, midInt, lastInt;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(HashMapOverhead.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        stringSet = new HashSet<>();
        stringList = new ArrayList<>();
        stringArr = new String[N];

        intSet = new HashSet<>();
        intList = new ArrayList<>();
        intArr = new int[N];

        for (int i = 0; i < N; i++) {
            var str = RandomStringUtils.randomAlphanumeric(K);
            var randomI = ThreadLocalRandom.current().nextInt();

            if (i == 0) {
                firstString = str;
                firstInt = randomI;
            } else if (i == N / 2) {
                midString = str;
                midInt = randomI;
            } else if (i == N - 1) {
                lastString = str;
                lastInt = randomI;
            }

            stringList.add(str);
            stringSet.add(str);
            stringArr[i] = str;

            intList.add(randomI);
            intSet.add(randomI);
            intArr[i] = randomI;
        }
    }


    @Benchmark
    public boolean hashset_string_get() {
        return stringSet.contains(midString);
    }

    @Benchmark
    public boolean list_string_get_first() {
        return stringList.contains(firstString);
    }


    @Benchmark
    public boolean list_string_get_last() {
        return stringList.contains(lastString);
    }

    @Benchmark
    public boolean list_string_get_mid() {
        return stringList.contains(midString);
    }

    @Benchmark
    public boolean arr_string_get_first() {
        for (int i = 0; i < stringArr.length; i++) {
            if (stringArr[i].equals(firstString)) return true;
        }
        return false;
    }


    @Benchmark
    public boolean arr_string_get_last() {
        for (int i = 0; i < stringArr.length; i++) {
            if (stringArr[i].equals(lastString)) return true;
        }
        return false;
    }

    @Benchmark
    public boolean arr_string_get_mid() {
        for (int i = 0; i < stringArr.length; i++) {
            if (stringArr[i].equals(midString)) return true;
        }
        return false;
    }


    @Benchmark
    public boolean hashset_int_get() {
        return intSet.contains(midInt);
    }

    @Benchmark
    public boolean list_int_get_first() {
        return intList.contains(firstInt);
    }


    @Benchmark
    public boolean list_int_get_last() {
        return intList.contains(lastInt);
    }

    @Benchmark
    public boolean list_int_get_mid() {
        return intList.contains(midInt);
    }

    @Benchmark
    public boolean arr_int_get_first() {
        for (int i = 0; i < intArr.length; i++) {
            if (intArr[i] == firstInt) return true;
        }
        return false;
    }


    @Benchmark
    public boolean arr_int_get_last() {
        for (int i = 0; i < intArr.length; i++) {
            if (intArr[i] == lastInt) return true;
        }
        return false;
    }

    @Benchmark
    public boolean arr_int_get_mid() {
        for (int i = 0; i < intArr.length; i++) {
            if (intArr[i] == midInt) return true;
        }
        return false;
    }


}
