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
@Fork(value = 2, jvmArgs = {"-Xms50G", "-Xmx50G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                                     (N)  Mode  Cnt        Score       Error  Units
RemoveVowels.loopArrayFilter                   10  avgt    5        0.044 ±     0.004  us/op
RemoveVowels.loopArrayFilter_Amortized         10  avgt    5        0.009 ±     0.001  us/op
RemoveVowels.loopSetFilter                     10  avgt    5        0.103 ±     0.002  us/op
RemoveVowels.loopSetFilter_Amortized           10  avgt    5        0.027 ±     0.001  us/op
RemoveVowels.regex                             10  avgt    5        0.280 ±     0.006  us/op
RemoveVowels.streamLcmFilter                   10  avgt    5        0.052 ±     0.001  us/op
RemoveVowels.streamThiccFilter                 10  avgt    5        0.058 ±     0.001  us/op


RemoveVowels.loopArrayFilter                  100  avgt    5        0.071 ±     0.001  us/op
RemoveVowels.loopArrayFilter_Amortized        100  avgt    5        0.034 ±     0.001  us/op
RemoveVowels.loopSetFilter                    100  avgt    5        0.327 ±     0.145  us/op
RemoveVowels.loopSetFilter_Amortized          100  avgt    5        0.245 ±     0.006  us/op
RemoveVowels.regex                            100  avgt    5        0.993 ±     0.664  us/op
RemoveVowels.streamLcmFilter                  100  avgt    5        0.341 ±     0.136  us/op
RemoveVowels.streamThiccFilter                100  avgt    5        0.280 ±     0.015  us/op


RemoveVowels.loopArrayFilter                10000  avgt    5        3.299 ±     0.102  us/op
RemoveVowels.loopArrayFilter_Amortized      10000  avgt    5        3.414 ±     0.092  us/op
RemoveVowels.loopSetFilter                  10000  avgt    5       29.120 ±     0.619  us/op
RemoveVowels.loopSetFilter_Amortized        10000  avgt    5       36.133 ±     5.149  us/op
RemoveVowels.regex                          10000  avgt    5       63.884 ±     1.876  us/op
RemoveVowels.streamLcmFilter                10000  avgt    5       34.222 ±     0.705  us/op
RemoveVowels.streamThiccFilter              10000  avgt    5       24.301 ±     0.302  us/op


RemoveVowels.loopArrayFilter            100000000  avgt    5    64344.565 ±  2156.860  us/op
RemoveVowels.loopArrayFilter_Amortized  100000000  avgt    5    65623.615 ±  1332.466  us/op
RemoveVowels.loopSetFilter              100000000  avgt    5  1165080.616 ±  9212.730  us/op
RemoveVowels.loopSetFilter_Amortized    100000000  avgt    5   979675.865 ±  6990.508  us/op
RemoveVowels.regex                      100000000  avgt    5   702162.545 ± 30231.671  us/op
RemoveVowels.streamLcmFilter            100000000  avgt    5   608230.428 ±  3287.993  us/op
RemoveVowels.streamThiccFilter          100000000  avgt    5   239484.547 ±  6064.727  us/op


RemoveVowels.loopArrayFilter            1000000000  avgt    5    688661.429 ±   35900.688  us/op
RemoveVowels.loopArrayFilter_Amortized  1000000000  avgt    5    670070.806 ±    9419.759  us/op
RemoveVowels.loopSetFilter              1000000000  avgt    5  10153932.980 ±  161925.882  us/op
RemoveVowels.loopSetFilter_Amortized    1000000000  avgt    5   9753229.980 ±  801770.743  us/op
RemoveVowels.regex                      1000000000  avgt    5   8230948.420 ± 4252443.775  us/op
RemoveVowels.streamLcmFilter            1000000000  avgt    5   6529096.640 ± 2268052.555  us/op
RemoveVowels.streamThiccFilter          1000000000  avgt    5   2228309.496 ±    5077.366  us/op
 */
public class RemoveVowels {

    @Param({"100", "1000000000"})
    private int N;

    private String unit;

    private int[] vowelMask;
    private Set<Character> vowelSet;

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

        vowelMask = new int[128];
        vowelMask['a'] = 1;
        vowelMask['e'] = 1;
        vowelMask['i'] = 1;
        vowelMask['o'] = 1;
        vowelMask['u'] = 1;
        vowelMask['A'] = 1;
        vowelMask['E'] = 1;
        vowelMask['I'] = 1;
        vowelMask['O'] = 1;
        vowelMask['U'] = 1;

        vowelSet = Set.of('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U');
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
        bh.consume(unit
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
    public void loopSetFilter_Amortized(Blackhole bh) {
        int count = 0;
        for (char c : unit.toCharArray()) {
            if (!vowelSet.contains(c)) count++;
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
        bh.consume(unit.length() - count);
    }

    @Benchmark
    public void loopArrayFilter_Amortized(Blackhole bh) {
        int count = 0;
        for (char c : unit.toCharArray()) {
            count += vowelMask[c];
        }
        bh.consume(unit.length() - count);
    }

    @Benchmark
    public void regex(Blackhole bh) {
        bh.consume(unit.replaceAll("[aeiouAEIOU]", "").length());
    }
}
