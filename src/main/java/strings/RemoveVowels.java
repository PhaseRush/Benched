package strings;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms6G", "-Xmx6G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                        (N)  Mode  Cnt    Score    Error  Units
RemoveVowels.loopArrayFilter    100000  avgt    5   39.570 ±  0.574  us/op
RemoveVowels.loopSetFilter      100000  avgt    5  912.286 ± 12.202  us/op
RemoveVowels.streamLcmFilter    100000  avgt    5  553.343 ± 18.543  us/op
RemoveVowels.streamThiccFilter  100000  avgt    5  494.789 ± 4.130   us/op
 */
public class RemoveVowels {

    @Param({"100000"})
    private int N;

    private String unit;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(RemoveVowels.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        unit = RandomStringUtils.randomAlphanumeric(N);
    }

    @Benchmark
    public void streamLcmFilter(Blackhole bh) {
        bh.consume(unit.toUpperCase()
                .chars()
                .filter(n -> 439704915 % n == 0)
                .count());
    }

    @Benchmark
    public void streamThiccFilter(Blackhole bh) {
        bh.consume(unit.toUpperCase()
                .chars()
                .filter(n -> (n == 'a' || n == 'e' || n == 'i' || n == 'o' || n == 'u' ||
                        n == 'A' || n == 'E' || n == 'I' || n == 'O' || n == 'U'))
                .count());
    }

    @Benchmark
    public void loopSetFilter(Blackhole bh) {
        final Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U');
        int count = 0;
        for (char c : unit.toCharArray()) {
            if (!vowels.contains(c)) count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void loopArrayFilter(Blackhole bh) {
        final int[] mask = new int[128];
        mask['a'] = 1;
        mask['e'] = 1;
        mask['i'] = 1;
        mask['o'] = 1;
        mask['u'] = 1;
        mask['A'] = 1;
        mask['E'] = 1;
        mask['I'] = 1;
        mask['O'] = 1;
        mask['U'] = 1;
        int count = 0;
        for (char c : unit.toCharArray()) {
            count += mask[c];
        }
        bh.consume(count);
    }
}
