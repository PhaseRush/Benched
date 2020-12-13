package strings;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
Benchmark                                        (N)  Mode  Cnt        Score         Error  Units
RemoveVowels.loopArrayFilter                      10  avgt    5        0.042 ±       0.001  us/op
RemoveVowels.loopArrayFilter_Amortized            10  avgt    5        0.008 ±       0.001  us/op
RemoveVowels.loopSetFilter                        10  avgt    5        0.126 ±       0.087  us/op
RemoveVowels.loopHashSetFilter                    10  avgt    5        0.148 ±       0.023  us/op
RemoveVowels.loopHashSetFilter_Amortized          10  avgt    5        0.019 ±       0.016  us/op
RemoveVowels.regex                                10  avgt    5        0.284 ±       0.001  us/op
RemoveVowels.regex_Amortized                      10  avgt    5        0.135 ±       0.070  us/op
RemoveVowels.streamLcmFilter                      10  avgt    5        0.051 ±       0.001  us/op
RemoveVowels.streamThiccFilter                    10  avgt    5        0.051 ±       0.001  us/op
RemoveVowels.streamThiccFilter_Parallel           10  avgt    5        7.090 ±       0.112  us/op

RemoveVowels.loopArrayFilter                     100  avgt    5        0.065 ±       0.003  us/op
RemoveVowels.loopArrayFilter_Amortized           100  avgt    5        0.030 ±       0.001  us/op
RemoveVowels.loopSetFilter                       100  avgt    5        0.319 ±       0.159  us/op
RemoveVowels.loopHashSetFilter                   100  avgt    5        0.393 ±       0.210  us/op
RemoveVowels.loopHashSetFilter_Amortized         100  avgt    5        0.184 ±       0.003  us/op
RemoveVowels.regex                               100  avgt    5        0.904 ±       0.832  us/op
RemoveVowels.regex_Amortized                     100  avgt    5        0.833 ±       0.545  us/op
RemoveVowels.streamLcmFilter                     100  avgt    5        0.327 ±       0.138  us/op
RemoveVowels.streamThiccFilter                   100  avgt    5        0.305 ±       0.007  us/op
RemoveVowels.streamThiccFilter_Parallel          100  avgt    5       13.856 ±       4.215  us/op

RemoveVowels.loopArrayFilter                   10000  avgt    5        3.147 ±       0.031  us/op
RemoveVowels.loopArrayFilter_Amortized         10000  avgt    5        3.147 ±       0.038  us/op
RemoveVowels.loopSetFilter                     10000  avgt    5       23.777 ±       1.521  us/op
RemoveVowels.loopHashSetFilter                 10000  avgt    5       19.987 ±       0.273  us/op
RemoveVowels.loopHashSetFilter_Amortized       10000  avgt    5       16.102 ±       2.056  us/op
RemoveVowels.regex                             10000  avgt    5       79.519 ±      42.925  us/op
RemoveVowels.regex_Amortized                   10000  avgt    5       73.491 ±      35.444  us/op
RemoveVowels.streamLcmFilter                   10000  avgt    5       30.275 ±       0.983  us/op
RemoveVowels.streamThiccFilter                 10000  avgt    5       29.670 ±       0.135  us/op
RemoveVowels.streamThiccFilter_Parallel        10000  avgt    5       17.228 ±       1.989  us/op

RemoveVowels.loopArrayFilter              1000000000  avgt    5   610578.502 ±    4550.097  us/op
RemoveVowels.loopArrayFilter_Amortized    1000000000  avgt    5   620377.731 ±   15831.890  us/op
RemoveVowels.loopSetFilter                1000000000  avgt    5  9533203.230 ±  785787.479  us/op
RemoveVowels.loopHashSetFilter            1000000000  avgt    5  4459794.033 ±  672294.458  us/op
RemoveVowels.loopHashSetFilter_Amortized  1000000000  avgt    5  5359775.650 ±  740845.202  us/op
RemoveVowels.regex                        1000000000  avgt    5  7607016.560 ± 3205685.701  us/op
RemoveVowels.regex_Amortized              1000000000  avgt    5  7777850.210 ± 3391998.383  us/op
RemoveVowels.streamLcmFilter              1000000000  avgt    5  6439135.130 ± 2227171.904  us/op
RemoveVowels.streamThiccFilter            1000000000  avgt    5  2103311.436 ±   10844.265  us/op
RemoveVowels.streamThiccFilter_Parallel   1000000000  avgt    5   397270.609 ±   13624.687  us/op
*/
public class RemoveVowels {

    @Param({"10", "100", "10000", "1000000000"})
    private int N;

    private String unit;

    private int[] vowelMask;
    private Set<Character> vowelSet;
    private Set<Character> vowelHashSet;
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
        vowelHashSet = new HashSet<>(vowelSet);
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
    public void loopHashSetFilter(Blackhole bh) {
        final Set<Character> vowels = new HashSet<>(vowelHashSet);
        int count = 0;
        for (char c : unit.toCharArray()) {
            if (!vowels.contains(c)) count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void loopHashSetFilter_Amortized(Blackhole bh) {
        int count = 0;
        for (char c : unit.toCharArray()) {
            if (!vowelHashSet.contains(c)) count++;
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
