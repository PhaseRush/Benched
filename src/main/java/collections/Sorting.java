package collections;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
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
Benchmark                         (N)  Mode  Cnt      Score       Error  Units
Sorting.bogosort_array             10  avgt    5  96587.607 ± 71011.437  us/op
Sorting.parallel_mergesort_array   10  avgt    5      1.132 ±     0.013  us/op
Sorting.qsort_dualpiv_array        10  avgt    5      1.132 ±     0.009  us/op
Sorting.timsort_list               10  avgt    5      1.281 ±     0.016  us/op

Benchmark                         (N)  Mode  Cnt         Score          Error  Units
Sorting.bogosort_array             12  avgt    5  20804411.238 ± 45508526.397  us/op
Sorting.parallel_mergesort_array   12  avgt    5         1.149 ±        0.002  us/op
Sorting.qsort_dualpiv_array        12  avgt    5         1.149 ±        0.003  us/op
Sorting.timsort_list               12  avgt    5         1.346 ±        0.022  us/op
 */
public class Sorting {
    //    @Param({"1", "100", "10000", "1000000", "1000000000"})
    @Param({"12"})
    private int N;

    private static List<Integer> list;
    private static int[] array;
    private static Integer[] objarr;


    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(Sorting.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }


    @Setup(Level.Invocation)
    public void setup() {
        list = ThreadLocalRandom.current().ints(N).boxed().collect(Collectors.toList());
        array = list.stream().mapToInt(i -> i).toArray();
        objarr = list.toArray(Integer[]::new);
    }

    private static boolean sorted() {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1])
                return false;
        }
        return true;
    }

    private static void randomize() {
        for (int i = 0; i < array.length; i++) {
            int j = ThreadLocalRandom.current().nextInt(i, array.length);

            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    @Benchmark
    public void bogosort_array(Blackhole bh) {
        while (!sorted()) {
            randomize();
        }
        bh.consume(array);
    }

    @Benchmark
    public void qsort_dualpiv_array(Blackhole bh) {
        Arrays.sort(array);
        bh.consume(array);
    }

    //https://www.baeldung.com/java-quicksort
    private int partition(int arr[], int begin, int end) {
        int pivot = arr[end];
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            if (arr[j] <= pivot) {
                i++;

                int swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        int swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;

        return i + 1;
    }

    private void quickSort(int arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex - 1);
            quickSort(arr, partitionIndex + 1, end);
        }
    }

    @Benchmark
    public void qsort_singlepiv_array(Blackhole bh) {
        quickSort(array, 0, array.length);
        bh.consume(array);
    }

    @Benchmark
    public void parallel_mergesort_array(Blackhole bh) {
        Arrays.parallelSort(array);
        bh.consume(array);
    }

    // from Arrays.java
    private static void mergeSort(int low,
                                  int high,
                                  int off,
                                  boolean useTimsort,
                                  int INSERTIONSORT_THRESHOLD) {
        int length = high - low;

        // Insertion sort on smallest arrays
        if (useTimsort && length < INSERTIONSORT_THRESHOLD) {
            for (int i = low; i < high; i++)
                for (int j = i; j > low &&
                        ((Comparable) objarr[j - 1]).compareTo(objarr[j]) > 0; j--)
                    swap(objarr, j, j - 1);
            return;
        }

        // Recursively sort halves of dest into src
        int destLow = low;
        int destHigh = high;
        low += off;
        high += off;
        int mid = (low + high) >>> 1;
        mergeSort(low, mid, -off, useTimsort, INSERTIONSORT_THRESHOLD);
        mergeSort(mid, high, -off, useTimsort, INSERTIONSORT_THRESHOLD);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if (((Comparable) array[mid - 1]).compareTo(array[mid]) <= 0) {
            System.arraycopy(array, low, objarr, destLow, length);
            return;
        }

        // Merge sorted halves (now in src) into dest
        for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
            if (q >= high || p < mid && ((Comparable) array[p]).compareTo(array[q]) <= 0)
                objarr[i] = array[p++];
            else
                objarr[i] = array[q++];
        }
    }

    /**
     * Swaps x[a] with x[b].
     */
    private static void swap(Object[] x, int a, int b) {
        Object t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

//    @Benchmark
//    public void mergesort_array(Blackhole bh) {
//        mergeSort(0, objarr.length, 0, false, 0);
//        bh.consume(objarr);
//    }

//    @Benchmark
//    public void timsort_array(Blackhole bh) {
//        mergeSort(0, objarr.length, 0, true, 7);
//        bh.consume(objarr);
//    }


    @Benchmark
    public void timsort_list(Blackhole bh) {
        Collections.sort(list);
        bh.consume(list);
    }

}
