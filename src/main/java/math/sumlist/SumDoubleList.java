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
 * Credit goes to itsHobbes for inspiring this benchmark https://gist.github.com/itsHobbes/87cbf4ffce197136b6558d5d96ddfc30
 * Credit goes to tp99 for lending me a 5950x test rig
 */
/*

Config 1
5800x @ 4.8 Ghz
96 GB DDR4 @ 3600 - 18 22 22 42 83 1T
L123 cache = 512 kib + 4 mib + 32 mib

Config 2 (Thanks to tp99)
5950x @ 4.9 Ghz
96 GB DDR4 @ 3600 - 18 22 xx xx xx 1T
L123 cache = 1 mib + 8 mib + 64 mib

                                                                          Config 1                Config 2
                                                                       ---------------       ----------------
Benchmark                                          (N)  Mode  Cnt      us/op     Error       us/op      Error
SumDoubleList.doubleAdder                           10  avgt   10      7.707 ±   0.054       5.551  ±   0.034
SumDoubleList.forLoop                               10  avgt   10      0.006 ±   0.001       0.006  ±   0.001
SumDoubleList.mapToDoubleSum                        10  avgt   10      0.058 ±   0.001       0.057  ±   0.001
SumDoubleList.mapToDoubleSum_prim                   10  avgt   10      0.057 ±   0.001       0.057  ±   0.001
SumDoubleList.mapToDoubleSumParallel                10  avgt   10      8.260 ±   0.032       5.642  ±   0.04
SumDoubleList.mapToDoubleSumParallel_prim           10  avgt   10      8.266 ±   0.030       5.637  ±   0.062
SumDoubleList.reduceSumDouble                       10  avgt   10      0.056 ±   0.001       0.054  ±   0.001
SumDoubleList.reduceSumDoubleParallel               10  avgt   10      8.117 ±   0.035       5.590  ±   0.031
SumDoubleList.summingCollectorDouble                10  avgt   10      0.064 ±   0.001       0.063  ±   0.001
SumDoubleList.summingCollectorDoubleParallel        10  avgt   10      8.427 ±   0.037       5.879  ±   0.013

SumDoubleList.doubleAdder                        10000  avgt   10     29.342 ±   0.086      35.012  ±   0.131
SumDoubleList.forLoop                            10000  avgt   10      6.269 ±   0.012       6.217  ±   0.052
SumDoubleList.mapToDoubleSum                     10000  avgt   10     34.673 ±   0.118      33.850  ±   0.104
SumDoubleList.mapToDoubleSum_prim                10000  avgt   10     34.679 ±   0.098      20.081  ±   0.037
SumDoubleList.mapToDoubleSumParallel             10000  avgt   10     23.542 ±   0.079      33.759  ±   0.075
SumDoubleList.mapToDoubleSumParallel_prim        10000  avgt   10     23.367 ±   0.062      19.959  ±   0.031
SumDoubleList.reduceSumDouble                    10000  avgt   10     29.108 ±   0.106      29.152  ±   0.053
SumDoubleList.reduceSumDoubleParallel            10000  avgt   10     27.122 ±   0.155      26.381  ±   0.045
SumDoubleList.summingCollectorDouble             10000  avgt   10     44.166 ±   0.095      43.336  ±   0.095
SumDoubleList.summingCollectorDoubleParallel     10000  avgt   10     24.545 ±   0.070      20.912  ±   0.050

SumDoubleList.doubleAdder                     10000000  avgt   10   9100.749 ±  42.132    7247.295  ±  20.757
SumDoubleList.forLoop                         10000000  avgt   10  10917.227 ± 131.071   10899.721  ± 114.039
SumDoubleList.mapToDoubleSum                  10000000  avgt   10  35588.848 ±  49.988   35498.336  ±  87.104
SumDoubleList.mapToDoubleSum_prim             10000000  avgt   10  35583.247 ± 225.742    7246.922  ±  11.496
SumDoubleList.mapToDoubleSumParallel          10000000  avgt   10   9088.552 ±  50.270    7246.922  ±  11.496
SumDoubleList.mapToDoubleSumParallel_prim     10000000  avgt   10   9015.092 ±  35.103    7202.466  ±   8.949
SumDoubleList.reduceSumDouble                 10000000  avgt   10  29879.475 ± 681.953   29835.707  ± 106.342
SumDoubleList.reduceSumDoubleParallel         10000000  avgt   10  22424.702 ±  50.526   22112.735 ±  18.310
SumDoubleList.summingCollectorDouble          10000000  avgt   10  46174.827 ± 108.088   45315.357 ± 375.390
SumDoubleList.summingCollectorDoubleParallel  10000000  avgt   10   9100.338 ±  56.239    7275.261 ±  93.681
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
    public void mapToDoubleSum_prim(Blackhole bh) {
        double sum = list.stream().mapToDouble(i -> i).sum();
        bh.consume(sum);
    }

    @Benchmark
    public void mapToDoubleSumParallel_prim(Blackhole bh) {
        double sum = list.parallelStream().mapToDouble(i -> i).sum();
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