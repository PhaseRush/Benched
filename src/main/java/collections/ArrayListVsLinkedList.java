package collections;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@State(Scope.Benchmark)
/*
Benchmark                                     (N)  Mode  Cnt        Score         Error  Units
ArrayListVsLinkedList.arrayList_add_head       10  avgt    3        0.572 ±       0.996  us/op
ArrayListVsLinkedList.linkedList_add_head      10  avgt    3        0.217 ±       0.526  us/op
ArrayListVsLinkedList.arrayList_add_head   100000  avgt    3  1482745.549 ± 2095261.961  us/op
ArrayListVsLinkedList.linkedList_add_head  100000  avgt    3     1970.139 ±     713.672  us/op

ArrayListVsLinkedList.arrayList_add_tail       10  avgt    3        0.097 ±       0.228  us/op
ArrayListVsLinkedList.linkedList_add_tail      10  avgt    3        0.135 ±       0.143  us/op
ArrayListVsLinkedList.arrayList_add_tail   100000  avgt    3     1629.111 ±    3832.304  us/op
ArrayListVsLinkedList.linkedList_add_tail  100000  avgt    3     1969.470 ±    5034.609  us/op

ArrayListVsLinkedList.arraylist_get_rand       10  avgt    3        0.091 ±       0.081  us/op
ArrayListVsLinkedList.linkedlist_get_rand      10  avgt    3        0.039 ±       0.009  us/op
ArrayListVsLinkedList.arraylist_get_rand   100000  avgt    3      766.206 ±    2118.611  us/op
ArrayListVsLinkedList.linkedlist_get_rand  100000  avgt    3  2308676.153 ±  162439.800  us/op


ArrayListVsLinkedList.arraylist_iter           10  avgt    3        0.031 ±       0.032  us/op
ArrayListVsLinkedList.linkedlist_iter          10  avgt    3        0.033 ±       0.026  us/op
ArrayListVsLinkedList.arraylist_iter       100000  avgt    3      271.849 ±     649.751  us/op
ArrayListVsLinkedList.linkedlist_iter      100000  avgt    3      332.906 ±     849.021  us/op
 */
public class ArrayListVsLinkedList {
    @Param({"10", "100000"})
    int N;

    List<Integer> arrayList;
    List<Integer> linkedList;
    int[] feeder;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(ArrayListVsLinkedList.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .jvmArgs("-Xmx64G")
                .warmupIterations(1)
                .measurementIterations(3)
                .build()).run();
    }

    @Setup
    public void setup() {
        feeder = IntStream.range(0, N).toArray();
        arrayList = IntStream.range(0, N).boxed().collect(Collectors.toList());
        linkedList = IntStream.range(0, N).boxed().collect(Collectors.toCollection(LinkedList::new));
    }

    @Benchmark
    public void arrayList_add_tail() {
        arrayList = new ArrayList<>();
        for (int i = 0; i < feeder.length; i++) {
            arrayList.add(feeder[i]);
        }
    }
    @Benchmark
    public void arrayList_add_head() {
        arrayList = new ArrayList<>();
        for (int i = 0; i < feeder.length; i++) {
            arrayList.add(0,feeder[i]);
        }
    }

    @Benchmark
    public void linkedList_add_tail() {
        linkedList = new LinkedList<>();
        for (int i = 0; i < feeder.length; i++) {
            linkedList.add(feeder[i]);
        }
    }
    @Benchmark
    public void linkedList_add_head() {
        linkedList = new LinkedList<>();
        for (int i = 0; i < feeder.length; i++) {
            linkedList.add(0,feeder[i]);
        }
    }


    @Benchmark
    public void arraylist_iter(Blackhole bh) {
        for (Integer integer : arrayList) {
            bh.consume(integer);
        }
    }

    @Benchmark
    public void linkedlist_iter(Blackhole bh) {
        for (Integer integer : linkedList) {
            bh.consume(integer);
        }
    }

    @Benchmark
    public void arraylist_get_rand(Blackhole bh) {
        for (int i = 0; i < feeder.length; i++) {
            bh.consume(arrayList.get(feeder[i]));
        }
    }

    @Benchmark
    public void linkedlist_get_rand(Blackhole bh) {
        for (int i = 0; i < feeder.length; i++) {
            bh.consume(linkedList.get(feeder[i]));
        }
    }


}
