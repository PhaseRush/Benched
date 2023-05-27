package collections;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms6G", "-Xmx60G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)
/*
Benchmark                              (N)  Mode  Cnt    Score   Error  Units
ArrayListClearVsNew.baseline            10  avgt   10    2.214 ± 0.011  ns/op
ArrayListClearVsNew.baseline          1000  avgt   10    2.215 ± 0.013  ns/op

ArrayListClearVsNew.clear_emptyList     10  avgt   10    2.373 ± 0.013  ns/op
ArrayListClearVsNew.clear_filledList    10  avgt   10    2.379 ± 0.016  ns/op

ArrayListClearVsNew.clear_emptyList   1000  avgt   10    2.382 ± 0.027  ns/op
ArrayListClearVsNew.clear_filledList  1000  avgt   10    2.384 ± 0.033  ns/op

ArrayListClearVsNew.new_emptyList       10  avgt   10    8.112 ± 0.226  ns/op
ArrayListClearVsNew.new_filledList      10  avgt   10    8.240 ± 0.083  ns/op

ArrayListClearVsNew.new_emptyList     1000  avgt   10  489.126 ± 2.684  ns/op
ArrayListClearVsNew.new_filledList    1000  avgt   10  487.677 ± 1.371  ns/op
 */
public class ArrayListClearVsNew {

    @Param({"10", "1000"})
    private int N;

    private List<Integer> filledList;
    private List<Integer> emptyList;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(ArrayListClearVsNew.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }


    @Setup
    public void setup() {
        filledList = createList();
        emptyList = new ArrayList<>(N);
    }

    @Benchmark
    public int baseline() {
        int sum = 0;
        for (int i = 0; i < emptyList.size(); i++) {
            sum = sum + emptyList.get(i);
        }
        return sum;
    }

    @Benchmark
    public int clear_filledList() {
        filledList.clear();

        int sum = 0;
        for (int i = 0; i < filledList.size(); i++) {
            sum = sum + filledList.get(i);
        }
        return sum;
    }

    @Benchmark
    public int new_filledList() {
        filledList = new ArrayList<>(N);

        int sum = 0;
        for (int i = 0; i < filledList.size(); i++) {
            sum = sum + filledList.get(i);
        }
        return sum;
    }

    @Benchmark
    public int clear_emptyList() {
        emptyList.clear();

        int sum = 0;
        for (int i = 0; i < emptyList.size(); i++) {
            sum = sum + emptyList.get(i);
        }
        return sum;
    }

    @Benchmark
    public int new_emptyList() {
        emptyList = new ArrayList<>(N);

        int sum = 0;
        for (int i = 0; i < emptyList.size(); i++) {
            sum = sum + emptyList.get(i);
        }
        return sum;
    }


    private List<Integer> createList() {
        return Stream.iterate(1, i -> i + 1).limit(N).collect(Collectors.toCollection(ArrayList::new));
    }

}

