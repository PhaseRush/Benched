package strings;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static net.andreinc.aleph.AlephFormatter.str;


import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms6G", "-Xmx6G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                     (N)  Mode  Cnt    Score    Error  Units
AlephFormatter.alephFormat      1  avgt    5   67.101 ±  2.103  ns/op
AlephFormatter.stringFormat     1  avgt    5  273.048 ±  5.632  ns/op

AlephFormatter.alephFormat     10  avgt    5   76.470 ±  4.423  ns/op
AlephFormatter.stringFormat    10  avgt    5  264.106 ±  3.018  ns/op

AlephFormatter.alephFormat    100  avgt    5  113.705 ±  5.941  ns/op
AlephFormatter.stringFormat   100  avgt    5  328.445 ± 15.986  ns/op

AlephFormatter.alephFormat   1000  avgt    5  257.674 ± 19.363  ns/op
AlephFormatter.stringFormat  1000  avgt    5  437.688 ± 11.813  ns/op
 */
public class AlephFormatter {

    @Param({"1", "10", "100", "1000"})
    private int N;

    private String unit;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(AlephFormatter.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        var temp = String.valueOf(Math.random());
        unit = temp.repeat(1 + (N / temp.length())).substring(0, N);
    }

    @Benchmark
    public void stringFormat(Blackhole bh) {
        bh.consume(String.format("%s", unit));
    }

    @Benchmark
    public void alephFormat(Blackhole bh) {
        bh.consume(str("#{0}", unit).fmt());
    }
}
