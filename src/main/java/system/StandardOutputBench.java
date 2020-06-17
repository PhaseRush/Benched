package system;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"})
@Warmup(iterations = 0)
@Measurement(iterations = 1)
/**
 * WIP
 */
public class StandardOutputBench {

    @Param({"10"})
    private int N;

    private String unit;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StandardOutputBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        unit = String.valueOf(ThreadLocalRandom.current().nextDouble());
    }

    @Benchmark
    public void printLoop(Blackhole bh) {
        for (int i = 0; i < N; i++) {
            System.out.println(unit);
        }
    }

    @Benchmark
    public void bufferedOutputAppend(Blackhole bh) {
        try (BufferedOutputStream os = new BufferedOutputStream(System.out)) {
            for (int i = 0; i < N; i++) {
                os.write(unit.getBytes());
            }
        } catch (IOException ignored) {
        }
    }
}
