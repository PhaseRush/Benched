package math.sumlist;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

/**
 * Credit goes to itsHobbes https://gist.github.com/itsHobbes/87cbf4ffce197136b6558d5d96ddfc30
 */
/*
5800x @ 4.8 Ghz
96 GB DDR4 @ 3600 - 18 22 22 42 83 1T
L123 cache = 512 kib + 4 mib + 32 mib

Benchmark                                          (N)  Mode  Cnt      Score     Error  Units     thrpt
SumDoubleList.doubleAdder                           10  avgt   10      7.707 ±   0.054  us/op    129752
SumDoubleList.forLoop                               10  avgt   10      0.006 ±   0.001  us/op 166666666
SumDoubleList.mapToDoubleSum                        10  avgt   10      0.058 ±   0.001  us/op  17241379
SumDoubleList.mapToDoubleSumParallel                10  avgt   10      8.260 ±   0.032  us/op    121065
SumDoubleList.reduceSumDouble                       10  avgt   10      0.056 ±   0.001  us/op  17857142
SumDoubleList.reduceSumDoubleParallel               10  avgt   10      8.117 ±   0.035  us/op    123198
SumDoubleList.summingCollectorDouble                10  avgt   10      0.064 ±   0.001  us/op  15625000
SumDoubleList.summingCollectorDoubleParallel        10  avgt   10      8.427 ±   0.037  us/op    118666

SumDoubleList.doubleAdder                        10000  avgt   10     29.342 ±   0.086  us/op     34080
SumDoubleList.forLoop                            10000  avgt   10      6.269 ±   0.012  us/op    159515
SumDoubleList.mapToDoubleSum                     10000  avgt   10     34.673 ±   0.118  us/op     28840
SumDoubleList.mapToDoubleSumParallel             10000  avgt   10     23.542 ±   0.079  us/op     42477
SumDoubleList.reduceSumDouble                    10000  avgt   10     29.108 ±   0.106  us/op     34354
SumDoubleList.reduceSumDoubleParallel            10000  avgt   10     27.122 ±   0.155  us/op     36870
SumDoubleList.summingCollectorDouble             10000  avgt   10     44.166 ±   0.095  us/op     22641
SumDoubleList.summingCollectorDoubleParallel     10000  avgt   10     24.545 ±   0.070  us/op     40741

SumDoubleList.doubleAdder                     10000000  avgt   10   9100.749 ±  42.132  us/op       109
SumDoubleList.forLoop                         10000000  avgt   10  10917.227 ± 131.071  us/op        91
SumDoubleList.mapToDoubleSum                  10000000  avgt   10  35588.848 ±  49.988  us/op        28
SumDoubleList.mapToDoubleSumParallel          10000000  avgt   10   9088.552 ±  50.270  us/op       110
SumDoubleList.reduceSumDouble                 10000000  avgt   10  29879.475 ± 681.953  us/op        33
SumDoubleList.reduceSumDoubleParallel         10000000  avgt   10  22424.702 ±  50.526  us/op        44
SumDoubleList.summingCollectorDouble          10000000  avgt   10  46174.827 ± 108.088  us/op        21
SumDoubleList.summingCollectorDoubleParallel  10000000  avgt   10   9100.338 ±  56.239  us/op       109
 */
@State(Scope.Benchmark)
public class SumDoubleList {
    @Param({"10", "10000", "10000000"})
//    @Param({"500000000"})
    public int N;

    List<Double> list;

    public static void main(String[] args) throws Exception {
        new Runner(new OptionsBuilder()
                .include(SumDoubleList.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .jvmArgs("-Xmx90G")
                .warmupIterations(3)
                .measurementIterations(10)
                .build()).run();
    }

    @Setup
    public void setup() {
        list = ThreadLocalRandom.current().doubles(N).boxed().collect(Collectors.toList());
    }

    @Benchmark
    public void summingCollectorDouble(Blackhole bh) {
        //noinspection SimplifyStreamApiCallChains
        double sum = list.stream().collect(Collectors.summingDouble(i -> i));
        bh.consume(sum);
    }

    @Benchmark
    public void summingCollectorDoubleParallel(Blackhole bh) {
        //noinspection SimplifyStreamApiCallChains
        double sum = list.parallelStream().collect(Collectors.summingDouble(i -> i));
        bh.consume(sum);
    }

    @Benchmark
    public void mapToDoubleSum(Blackhole bh) {
        double sum = list.stream().mapToDouble(Double::doubleValue).sum();
        bh.consume(sum);
    }

    @Benchmark
    public void mapToDoubleSumParallel(Blackhole bh) {
        double sum = list.parallelStream().mapToDouble(Double::doubleValue).sum();
        bh.consume(sum);
    }

    @Benchmark
    public void doubleAdder(Blackhole bh) {
        DoubleAdder a = new DoubleAdder();
        list.parallelStream().forEach(a::add);
        bh.consume(a.doubleValue());
    }

    @Benchmark
    public void reduceSumDouble(Blackhole bh) {
        double sum = list.stream().reduce(0.0, Double::sum);
        bh.consume(sum);
    }

    @Benchmark
    public void reduceSumDoubleParallel(Blackhole bh) {
        double sum = list.parallelStream().reduce(0.0, Double::sum);
        bh.consume(sum);
    }

    @Benchmark
    public void forLoop(Blackhole bh) {
        double sum = 0;
        for (double num : list) {
            sum += num;
        }
        bh.consume(sum);
    }
}