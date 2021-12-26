package misc;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@State(Scope.Benchmark)

/*
Benchmark                     (N)  Mode  Cnt       Score       Error  Units
FindMin.arrayList         1000000  avgt    5     196.084 ±     2.311  ms/op
FindMin.baseline          1000000  avgt    5       2.515 ±     0.205  ms/op
FindMin.forLoop           1000000  avgt    5       4.107 ±     3.167  ms/op
FindMin.parallelStream    1000000  avgt    5       0.418 ±     0.022  ms/op
FindMin.singleStream      1000000  avgt    5      11.911 ±     4.192  ms/op
FindMin.treeMap           1000000  avgt    5     738.996 ±    63.495  ms/op

FindMin.arrayList       100000000  avgt    5   70878.125 ±  2453.326  ms/op
FindMin.baseline        100000000  avgt    5     790.105 ±    21.928  ms/op
FindMin.forLoop         100000000  avgt    5     722.242 ±    22.396  ms/op
FindMin.parallelStream  100000000  avgt    5     408.638 ±    13.346  ms/op
FindMin.singleStream    100000000  avgt    5    2279.633 ±   142.910  ms/op
FindMin.treeMap         100000000  avgt    5  257994.164 ± 25634.865  ms/op
 */
public class FindMin {
    @Param({"1000000"})
    int N;

    List<Integer> feeder;

    TreeMap<Integer, Integer> treeMap;
    ArrayList<Integer> arrayList;
    int[] array;


    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(FindMin.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .jvmArgs("-Xmx64G")
                .warmupIterations(3)
                .measurementIterations(5)
                .build()).run();
    }

    @Setup()
    public void setup() {
        feeder = IntStream.range(0, N).boxed()
                .collect(Collectors.toList());
        Collections.shuffle(feeder);

        array = new int[N];
        for (int i = 0; i < feeder.size(); i++) {
            array[i] = feeder.get(i);
        }

    }

//    @Benchmark
//    public int baseline() {
//        int[] array = new int[N];
//        for (int i = 0; i < feeder.size(); i++) {
//            array[i] = feeder.get(i);
//        }
//        return array[0];
//    }
//
//    @Benchmark
//    public int forLoop() {
//        int min = feeder.get(0);
//        for (int i = 1; i < feeder.size(); i++) {
//            if (feeder.get(i) < min) min = feeder.get(i);
//        }
//        return min;
//    }

    private int recursive_helper(int currMin, int currIdx) {
        if (currIdx == feeder.size()) return currMin;
        int curr = feeder.get(currIdx);
        if (curr < currMin) return recursive_helper(curr, currIdx+1);
        else return recursive_helper(currMin, currIdx+1);
    }

    @Benchmark
    public int recursive() {
        return recursive_helper(feeder.get(0), 1);
    }

//    @Benchmark
//    public int arrayList() {
//        arrayList = new ArrayList<>(N);
//        arrayList.addAll(feeder);
//        Collections.sort(arrayList);
//        return arrayList.get(0);
//    }
//
//    @Benchmark
//    public int treeMap() {
//        treeMap = new TreeMap<>();
//        for (Integer integer : feeder) {
//            treeMap.put(integer, 0);
//        }
//        return treeMap.firstKey();
//    }
//
//    @Benchmark
//    public int singleStream() {
//        return feeder.stream().min(Integer::compare).orElse(feeder.get(0));
//    }
//
//    @Benchmark
//    public int parallelStream() {
//        return feeder.stream().parallel().min(Integer::compare).orElse(feeder.get(0));
//    }
}

