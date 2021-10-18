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
Benchmark                                          (N)  Mode  Cnt      Score     Error  Units
SumDoubleList.doubleAdder                           10  avgt   10      7.707 ±   0.054  us/op
SumDoubleList.doubleAdder                        10000  avgt   10     29.342 ±   0.086  us/op
SumDoubleList.doubleAdder                     10000000  avgt   10   9100.749 ±  42.132  us/op
SumDoubleList.forLoop                               10  avgt   10      0.006 ±   0.001  us/op
SumDoubleList.forLoop                            10000  avgt   10      6.269 ±   0.012  us/op
SumDoubleList.forLoop                         10000000  avgt   10  10917.227 ± 131.071  us/op
SumDoubleList.mapToDoubleSum                        10  avgt   10      0.058 ±   0.001  us/op
SumDoubleList.mapToDoubleSum                     10000  avgt   10     34.673 ±   0.118  us/op
SumDoubleList.mapToDoubleSum                  10000000  avgt   10  35588.848 ±  49.988  us/op
SumDoubleList.mapToDoubleSumParallel                10  avgt   10      8.260 ±   0.032  us/op
SumDoubleList.mapToDoubleSumParallel             10000  avgt   10     23.542 ±   0.079  us/op
SumDoubleList.mapToDoubleSumParallel          10000000  avgt   10   9088.552 ±  50.270  us/op
SumDoubleList.reduceSumDouble                       10  avgt   10      0.056 ±   0.001  us/op
SumDoubleList.reduceSumDouble                    10000  avgt   10     29.108 ±   0.106  us/op
SumDoubleList.reduceSumDouble                 10000000  avgt   10  29879.475 ± 681.953  us/op
SumDoubleList.reduceSumDoubleParallel               10  avgt   10      8.117 ±   0.035  us/op
SumDoubleList.reduceSumDoubleParallel            10000  avgt   10     27.122 ±   0.155  us/op
SumDoubleList.reduceSumDoubleParallel         10000000  avgt   10  22424.702 ±  50.526  us/op
SumDoubleList.summingCollectorDouble                10  avgt   10      0.064 ±   0.001  us/op
SumDoubleList.summingCollectorDouble             10000  avgt   10     44.166 ±   0.095  us/op
SumDoubleList.summingCollectorDouble          10000000  avgt   10  46174.827 ± 108.088  us/op
SumDoubleList.summingCollectorDoubleParallel        10  avgt   10      8.427 ±   0.037  us/op
SumDoubleList.summingCollectorDoubleParallel     10000  avgt   10     24.545 ±   0.070  us/op
SumDoubleList.summingCollectorDoubleParallel  10000000  avgt   10   9100.338 ±  56.239  us/op
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