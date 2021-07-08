package system;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/*
Benchmark                      (N)  Mode  Cnt    Score    Error  Units
LoopUnrollBench.normal     1000000  avgt    5  207.966 ±  0.813  us/op
LoopUnrollBench.unroll2    1000000  avgt    5  208.223 ±  1.036  us/op
LoopUnrollBench.unroll4    1000000  avgt    5  208.597 ±  1.652  us/op
LoopUnrollBench.unroll10   1000000  avgt    5  214.095 ± 21.075  us/op
LoopUnrollBench.unroll100  1000000  avgt    5  279.260 ± 20.508  us/op
 */

@State(Scope.Benchmark)
public class LoopUnrollBench {
    @Param({"1000000"})
    public int N;
    int[] arr;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(LoopUnrollBench.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .jvmArgs("-Xmx64G")
                .warmupIterations(3)
                .measurementIterations(5)
                .build()).run();
    }

    @Setup
    public void setup() {
        arr = ThreadLocalRandom.current().ints(N).toArray();
    }

    @Benchmark
    public void normal(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        bh.consume(sum);
    }

    @Benchmark
    public void unroll2(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < arr.length; i += 2) {
            sum += arr[i];
            sum += arr[i + 1];
        }
        bh.consume(sum);
    }

    @Benchmark
    public void unroll4(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < arr.length; i += 4) {
            sum += arr[i];
            sum += arr[i + 1];
            sum += arr[i + 2];
            sum += arr[i + 3];
        }
        bh.consume(sum);
    }


    @Benchmark
    public void unroll10(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < arr.length; i += 10) {
            sum += arr[i];
            sum += arr[i + 1];
            sum += arr[i + 2];
            sum += arr[i + 3];
            sum += arr[i + 4];
            sum += arr[i + 5];
            sum += arr[i + 6];
            sum += arr[i + 7];
            sum += arr[i + 8];
            sum += arr[i + 9];
        }
        bh.consume(sum);
    }

    @Benchmark
    public void unroll100(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < arr.length; i += 100) {
            sum += arr[i + 0];
            sum += arr[i + 1];
            sum += arr[i + 2];
            sum += arr[i + 3];
            sum += arr[i + 4];
            sum += arr[i + 5];
            sum += arr[i + 6];
            sum += arr[i + 7];
            sum += arr[i + 8];
            sum += arr[i + 9];

            sum += arr[i + 10];
            sum += arr[i + 11];
            sum += arr[i + 12];
            sum += arr[i + 13];
            sum += arr[i + 14];
            sum += arr[i + 15];
            sum += arr[i + 16];
            sum += arr[i + 17];
            sum += arr[i + 18];
            sum += arr[i + 19];

            sum += arr[i + 20];
            sum += arr[i + 21];
            sum += arr[i + 22];
            sum += arr[i + 23];
            sum += arr[i + 24];
            sum += arr[i + 25];
            sum += arr[i + 26];
            sum += arr[i + 27];
            sum += arr[i + 28];
            sum += arr[i + 29];

            sum += arr[i + 30];
            sum += arr[i + 31];
            sum += arr[i + 32];
            sum += arr[i + 33];
            sum += arr[i + 34];
            sum += arr[i + 35];
            sum += arr[i + 36];
            sum += arr[i + 37];
            sum += arr[i + 38];
            sum += arr[i + 39];

            sum += arr[i + 40];
            sum += arr[i + 41];
            sum += arr[i + 42];
            sum += arr[i + 43];
            sum += arr[i + 44];
            sum += arr[i + 45];
            sum += arr[i + 46];
            sum += arr[i + 47];
            sum += arr[i + 48];
            sum += arr[i + 49];

            sum += arr[i + 50];
            sum += arr[i + 51];
            sum += arr[i + 52];
            sum += arr[i + 53];
            sum += arr[i + 54];
            sum += arr[i + 55];
            sum += arr[i + 56];
            sum += arr[i + 57];
            sum += arr[i + 58];
            sum += arr[i + 59];

            sum += arr[i + 60];
            sum += arr[i + 61];
            sum += arr[i + 62];
            sum += arr[i + 63];
            sum += arr[i + 64];
            sum += arr[i + 65];
            sum += arr[i + 66];
            sum += arr[i + 67];
            sum += arr[i + 68];
            sum += arr[i + 69];

            sum += arr[i + 70];
            sum += arr[i + 71];
            sum += arr[i + 72];
            sum += arr[i + 73];
            sum += arr[i + 74];
            sum += arr[i + 75];
            sum += arr[i + 76];
            sum += arr[i + 77];
            sum += arr[i + 78];
            sum += arr[i + 79];

            sum += arr[i + 80];
            sum += arr[i + 81];
            sum += arr[i + 82];
            sum += arr[i + 83];
            sum += arr[i + 84];
            sum += arr[i + 85];
            sum += arr[i + 86];
            sum += arr[i + 87];
            sum += arr[i + 88];
            sum += arr[i + 89];

            sum += arr[i + 90];
            sum += arr[i + 91];
            sum += arr[i + 92];
            sum += arr[i + 93];
            sum += arr[i + 94];
            sum += arr[i + 95];
            sum += arr[i + 96];
            sum += arr[i + 97];
            sum += arr[i + 98];
            sum += arr[i + 99];
        }
        bh.consume(sum);
    }
}
