package math;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@State(Scope.Benchmark)

/*
Benchmark                            (N)  Mode  Cnt      Score      Error  Units
SumBench.loop                  100000000  avgt    5  81762.164 ± 7245.730  us/op
SumBench.math                  100000000  avgt    5      0.004 ±    0.001  us/op
SumBench.parallelStreamReduce  100000000  avgt    5  67860.255 ± 2188.650  us/op
SumBench.streamReduce          100000000  avgt    5  44787.744 ± 2993.240  us/op
SumBench.loop_bigint           100000000  avgt    5     21.089 ±    0.716   s/op
 */
public class SumBench {
    @Param({"1000000000"})
    long N;

    @Param("1000000000")
    String M;

    BigInteger m_val;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(SumBench.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .jvmArgs("-Xmx64G")
                .warmupIterations(3)
                .measurementIterations(5)
                .build()).run();
    }

    @Setup()
    public void setup() {
        m_val = new BigInteger(M);
    }

    @Benchmark
    public void math_primitive(Blackhole bh) {
        bh.consume(N * (N + 1) / 2);
    }

    @Benchmark
    public void loop_primitive(Blackhole bh) {
        long count = 0;
        for (int i = 0; i < N; i++) {
            count += i;
        }
        bh.consume(count);
    }

    @Benchmark
    public void streamReduce_primitive(Blackhole bh) {
        bh.consume(LongStream.range(0, N)
                .reduce(0, Long::sum));
    }

    @Benchmark
    public void parallelStreamReduce_primitive(Blackhole bh) {
        bh.consume(LongStream.range(0, N)
                .parallel()
                .reduce(0, Long::sum));
    }

    @Benchmark
    public void math_bigint(Blackhole bh) {
        bh.consume(m_val.multiply(m_val.add(BigInteger.ONE)).divide(BigInteger.TWO));
    }

    @Benchmark
    public void loop_bigint(Blackhole bh) {
        BigInteger count = BigInteger.ZERO;
        for (BigInteger i = BigInteger.ZERO; i.compareTo(m_val) != 0; i = i.add(BigInteger.ONE)) {
            count = count.add(i);
        }
        bh.consume(count);
    }

    @Benchmark
    public void streamReduce_bigint(Blackhole bh) {
        BigInteger indexer = BigInteger.ZERO;
        bh.consume(
                Stream.generate(() -> indexer.add(BigInteger.ONE))
                        .takeWhile(i -> i.compareTo(m_val) < 0)
                        .reduce(BigInteger::add)
        );
    }

    @Benchmark
    public void parallelStreamReduce_bigint(Blackhole bh) {
        BigInteger indexer = BigInteger.ZERO;
        bh.consume(Stream.generate(() -> indexer.add(BigInteger.ONE))
                .parallel()
                .takeWhile(i -> i.compareTo(m_val) < 0)
                .reduce(BigInteger::add));
    }
}
