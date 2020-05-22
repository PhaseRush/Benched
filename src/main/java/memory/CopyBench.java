package memory;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 7)
@Measurement(iterations = 10)

/*
Benchmark                                  (M)     (N)  Mode  Cnt    Score    Error  Units
CopyBench.longArr_ArrayCopy               1000  100000  avgt   10   81.999 ±  4.160  us/op
CopyBench.longArr_manualLoop              1000  100000  avgt   10   98.962 ±  1.935  us/op
CopyBench.longArr_systemCopy              1000  100000  avgt   10   81.502 ±  3.826  us/op
CopyBench.longList_collectionsAddAll      1000  100000  avgt   10   98.968 ± 14.912  us/op
CopyBench.longList_manualLoop_readArr     1000  100000  avgt   10  444.886 ± 11.552  us/op
CopyBench.longList_manualLoop_readList    1000  100000  avgt   10  333.028 ±  4.297  us/op
CopyBench.stringArr_ArrayCopy             1000  100000  avgt   10   47.090 ±  2.448  us/op
CopyBench.stringArr_manualLoop            1000  100000  avgt   10  264.560 ±  2.794  us/op
CopyBench.stringArr_systemCopy            1000  100000  avgt   10   44.271 ±  1.123  us/op
CopyBench.stringList_collectionsAddAll    1000  100000  avgt   10   91.303 ±  3.850  us/op
CopyBench.stringList_manualLoop_readArr   1000  100000  avgt   10  294.379 ±  3.911  us/op
CopyBench.stringList_manualLoop_readList  1000  100000  avgt   10  356.284 ±  9.572  us/op
*/
public class CopyBench {

    @Param({"100000"})
    private int N;

    @Param({"1000"})
    private int M;

    private long[] longArr;
    private List<Long> longList;

    private String[] stringArr;
    private List<String> stringList;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(CopyBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        longArr = new long[N];
        longList = new ArrayList<>(N);
        stringArr = new String[N];
        stringList = new ArrayList<>(N);

        for (int i = 0; i < N; i++) {
            longArr[i] = M * i;
            longList.add((long) M * i);

            stringArr[i] = String.valueOf(M * i);
            stringList.add(String.valueOf(M * i));
        }
    }

    @Benchmark
    public void longArr_systemCopy(Blackhole bh) {
        long[] bait = new long[N];
        System.arraycopy(longArr, 0, bait, 0, N);
        bh.consume(bait);
    }

    @Benchmark
    public void longArr_ArrayCopy(Blackhole bh) {
        bh.consume(Arrays.copyOf(longArr, N));
    }

    // Intellij suggests to use System.arraycopy()
    @Benchmark
    public void longArr_manualLoop(Blackhole bh) {
        long[] bait = new long[N];
        for (int i = 0; i < N; i++) {
            bait[i] = longArr[i];
        }
        bh.consume(bait);
    }

    @Benchmark
    public void longList_manualLoop_readArr(Blackhole bh) {
        List<Long> bait = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            bait.add(longArr[i]);
        }
        bh.consume(bait);
    }

    @Benchmark
    public void longList_manualLoop_readList(Blackhole bh) {
        List<Long> bait = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            bait.add(longList.get(i));
        }
        bh.consume(bait);
    }

    @Benchmark
    public void longList_collectionsAddAll(Blackhole bh) {
        List<Long> bait = new ArrayList<>(N);
        bait.addAll(longList);
        bh.consume(bait);
    }

    @Benchmark
    public void stringArr_systemCopy(Blackhole bh) {
        String[] bait = new String[N];
        System.arraycopy(stringArr, 0, bait, 0, N);
        bh.consume(bait);
    }

    @Benchmark
    public void stringArr_ArrayCopy(Blackhole bh) {
        bh.consume(Arrays.copyOf(stringArr, N));
    }

    // Intellij suggests to use System.arraycopy()
    @Benchmark
    public void stringArr_manualLoop(Blackhole bh) {
        String[] bait = new String[N];
        for (int i = 0; i < N; i++) {
            bait[i] = stringArr[i];
        }
        bh.consume(bait);
    }

    // Intellij suggests Collections.addAll()
    @Benchmark
    public void stringList_manualLoop_readArr(Blackhole bh) {
        List<String> bait = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            bait.add(stringArr[i]);
        }
        bh.consume(bait);
    }

    //    @BenchmarkMode(Mode.SingleShotTime)
    @Benchmark
    public void stringList_manualLoop_readList(Blackhole bh) {
        List<String> bait = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            bait.add(stringList.get(i));
        }
        bh.consume(bait);
    }

    @Benchmark
    public void stringList_collectionsAddAll(Blackhole bh) {
        List<String> bait = new ArrayList<>(N);
        bait.addAll(stringList);
        bh.consume(bait);
    }

}
