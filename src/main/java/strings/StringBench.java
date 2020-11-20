package strings;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms6G", "-Xmx6G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
 * Benchmark                     (N)  Mode  Cnt     Score     Error  Units
 * StringBench.stringBuffer   100000  avgt    5     1.232 ±   0.030  ms/op
 * StringBench.stringBuilder  100000  avgt    5     0.950 ±   0.048  ms/op
 * StringBench.stringConcat   100000  avgt    5  8616.315 ± 235.166  ms/op
 * StringBench.stringRepeat   100000  avgt    5     0.163 ±   0.004  ms/op
 * StringBench.streamReduce   100000  avgt    5  6599.168 ± 249.764  ms/op
 */
public class StringBench {

    @Param({"100000"})
    private int N;

    private String unit;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(StringBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        unit = String.valueOf(Math.random());
    }

    @Benchmark
    public void stringConcat(Blackhole bh) {
        String res = "";
        for (int i = 0; i < N; i++) {
            res += unit;
        }
        bh.consume(res);
    }

    @Benchmark
    public void stringBuilder(Blackhole bh) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) {
            sb.append(unit);
        }
        bh.consume(sb);
    }

    @Benchmark
    public void stringBuffer(Blackhole bh) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < N; i++) {
            sb.append(unit);
        }
        bh.consume(sb);
    }

    @Benchmark
    public void stringRepeat(Blackhole bh) {
        bh.consume(unit.repeat(N));
    }

    @Benchmark
    public void streamReduce(Blackhole bh) {
        bh.consume(IntStream.range(0, N).mapToObj(i -> unit).reduce(String::concat));
    }
}
