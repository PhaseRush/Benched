package misc;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms6G", "-Xmx6G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                          Mode  Cnt  Score   Error  Units
EqualityBench.primitiveBoolean     avgt    5  3.708 ± 0.080  ns/op
EqualityBench.primitiveDouble      avgt    5  3.959 ± 0.106  ns/op
EqualityBench.stringEqualsDif      avgt    5  4.201 ± 0.148  ns/op
EqualityBench.stringEqualsSame     avgt    5  3.904 ± 0.015  ns/op
EqualityBench.stringReferenceDif   avgt    5  3.919 ± 0.082  ns/op
EqualityBench.stringReferenceSame  avgt    5  3.915 ± 0.037  ns/op
EqualityBench.stringEqualsNull     avgt    5  3.929 ± 0.100  ns/op
 */
public class EqualityBench {
    double d;
    boolean b;
    String s;
    String sCopyRef;
    String sCopyVal;
    String sDiffVal;
    String sNull;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(EqualityBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        d = ThreadLocalRandom.current().nextDouble();
        b = ThreadLocalRandom.current().nextBoolean();
        s = "https://stackoverflow.com/questions/7520432/what-is-the-difference-between-and-equals-in-java";
        sCopyRef = s;
        sCopyVal = "https://stackoverflow.com/questions/7520432/what-is-the-difference-between-and-equals-in-java";
        sDiffVal = "x";

        sNull = null;
    }

    @Benchmark
    public void primitiveDouble(Blackhole bh) {
        bh.consume(d == 0.1);
    }

    @Benchmark
    public void primitiveBoolean(Blackhole bh) {
        bh.consume(b);
    }


    @Benchmark
    public void stringReferenceSame(Blackhole bh) {
        bh.consume(s == sCopyRef);
    }

    @Benchmark
    public void stringReferenceDif(Blackhole bh) {
        bh.consume(s == sCopyVal);
    }

    @Benchmark
    public void stringEqualsSame(Blackhole bh) {
        bh.consume(s.equals(sCopyVal));
    }

    @Benchmark
    public void stringEqualsDif(Blackhole bh) {
        bh.consume(s.equals(sDiffVal));
    }

    @Benchmark
    public void stringEqualsNull(Blackhole bh) {
        bh.consume(sNull == null);
    }
}
