package strings;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms50G", "-Xmx50G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                                       (N)  Mode  Cnt         Score         Error  Units
RemoveVowels.loopArrayFilter                     10  avgt    5         0.050 ±       0.002  us/op
RemoveVowels.loopArrayFilter_Amortized           10  avgt    5         0.009 ±       0.001  us/op
RemoveVowels.loopSetFilter                       10  avgt    5         0.128 ±       0.114  us/op
RemoveVowels.loopSetFilter_Amortized             10  avgt    5         0.029 ±       0.019  us/op
RemoveVowels.regex                               10  avgt    5         0.385 ±       0.043  us/op
RemoveVowels.regex_Amortized                     10  avgt    5         0.199 ±       0.144  us/op
RemoveVowels.streamLcmFilter                     10  avgt    5         0.054 ±       0.001  us/op
RemoveVowels.streamThiccFilter                   10  avgt    5         0.043 ±       0.001  us/op
RemoveVowels.streamThiccFilter_Parallel          10  avgt    5         7.388 ±       0.341  us/op


RemoveVowels.loopArrayFilter                    100  avgt    5         0.076 ±       0.004  us/op
RemoveVowels.loopArrayFilter_Amortized          100  avgt    5         0.033 ±       0.001  us/op
RemoveVowels.loopSetFilter                      100  avgt    5         0.345 ±       0.170  us/op
RemoveVowels.loopSetFilter_Amortized            100  avgt    5         0.238 ±       0.068  us/op
RemoveVowels.regex                              100  avgt    5         1.053 ±       0.890  us/op
RemoveVowels.regex_Amortized                    100  avgt    5         0.905 ±       0.796  us/op
RemoveVowels.streamLcmFilter                    100  avgt    5         0.346 ±       0.136  us/op
RemoveVowels.streamThiccFilter                  100  avgt    5         0.275 ±       0.011  us/op
RemoveVowels.streamThiccFilter_Parallel         100  avgt    5        14.048 ±       4.378  us/op


RemoveVowels.loopArrayFilter                  10000  avgt    5         3.388 ±       0.111  us/op
RemoveVowels.loopArrayFilter_Amortized        10000  avgt    5         3.475 ±       0.149  us/op
RemoveVowels.loopSetFilter                    10000  avgt    5        29.955 ±       3.176  us/op
RemoveVowels.loopSetFilter_Amortized          10000  avgt    5        27.272 ±       2.566  us/op
RemoveVowels.regex                            10000  avgt    5        81.807 ±      50.543  us/op
RemoveVowels.regex_Amortized                  10000  avgt    5        85.993 ±      50.730  us/op
RemoveVowels.streamLcmFilter                  10000  avgt    5        21.863 ±       0.431  us/op
RemoveVowels.streamThiccFilter                10000  avgt    5        20.554 ±       0.280  us/op
RemoveVowels.streamThiccFilter_Parallel       10000  avgt    5        17.953 ±       2.041  us/op

RemoveVowels.loopArrayFilter             1000000000  avgt    5    668448.124 ±   16559.729  us/op
RemoveVowels.loopArrayFilter_Amortized   1000000000  avgt    5    666494.337 ±   28720.441  us/op
RemoveVowels.loopSetFilter               1000000000  avgt    5  10069305.450 ±  453083.840  us/op
RemoveVowels.loopSetFilter_Amortized     1000000000  avgt    5  10085519.990 ±  222339.965  us/op
RemoveVowels.regex                       1000000000  avgt    5   8140364.360 ± 3372461.290  us/op
RemoveVowels.regex_Amortized             1000000000  avgt    5   8238729.660 ± 3635157.965  us/op
RemoveVowels.streamLcmFilter             1000000000  avgt    5   6492456.870 ± 2263767.217  us/op
RemoveVowels.streamThiccFilter           1000000000  avgt    5   2158537.660 ±   64251.445  us/op
RemoveVowels.streamThiccFilter_Parallel  1000000000  avgt    5    281348.069 ±    7285.706  us/op
*/
public class RemoveVowels {

    @Param({"10", "100", "10000", "1000000000"})
    private int N;

    private String unit;

    private int[] vowelMask;
    private Set<Character> vowelSet;
    private Pattern vowelPattern = Pattern.compile("[aeiouAEIOU]");

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
        Arrays.fill(vowelMask, 1);
        vowelMask['a'] = 0;
        vowelMask['e'] = 0;
        vowelMask['i'] = 0;
        vowelMask['o'] = 0;
        vowelMask['u'] = 0;
        vowelMask['A'] = 0;
        vowelMask['E'] = 0;
        vowelMask['I'] = 0;
        vowelMask['O'] = 0;
        vowelMask['U'] = 0;

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
    public void streamThiccFilter_Parallel(Blackhole bh) {
        bh.consume(unit
                .chars()
                .parallel()
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
        Arrays.fill(mask, 1);
        mask['a'] = 0;
        mask['e'] = 0;
        mask['i'] = 0;
        mask['o'] = 0;
        mask['u'] = 0;
        mask['A'] = 0;
        mask['E'] = 0;
        mask['I'] = 0;
        mask['O'] = 0;
        mask['U'] = 0;
        int count = 0;
        for (char c : unit.toCharArray()) {
            count += mask[c];
        }
        bh.consume(count);
    }

    @Benchmark
    public void loopArrayFilter_Amortized(Blackhole bh) {
        int count = 0;
        for (char c : unit.toCharArray()) {
            count += vowelMask[c];
        }
        bh.consume(count);
    }

    @Benchmark
    public void regex(Blackhole bh) {
        bh.consume(unit.replaceAll("[aeiouAEIOU]", "").length());
    }

    @Benchmark
    public void regex_Amortized(Blackhole bh) {
        final var m = vowelPattern.matcher(unit);
        bh.consume(m.replaceAll("").length());
    }
}
