package others;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx40G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class SpaceDandy {

    @Param({"10", "100000"})
    private int N;

    private int[] arr;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(SpaceDandy.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    // Cannot use Level.Invocation unless benchmark is suitably complex, else will saturate througput
    public void setup() {
        arr = ThreadLocalRandom.current().ints().limit(N).toArray();
    }

    @Benchmark
    public void insertion(Blackhole bh) {
        for (int i = 1; i < arr.length; i++) {
            int temp = arr[i];
            int temp2 = i;

            while (temp2 > 0 && arr[temp2 - 1] > temp) {
                arr[temp2] = arr[temp2 - 1];
                temp2 = temp2 - 1;

            }
            arr[temp2] = temp;
        }
    }
}
