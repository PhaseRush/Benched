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
import java.util.regex.Pattern;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms50G", "-Xmx50G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                                      (N)  Mode  Cnt         Score         Error  Units
RemoveVowels.loopArrayFilter                    10  avgt    5         0.067 ±       0.007  us/op
RemoveVowels.loopArrayFilter_Amortized          10  avgt    5         0.013 ±       0.011  us/op
RemoveVowels.loopSetFilter                      10  avgt    5         0.191 ±       0.154  us/op
RemoveVowels.loopSetFilter_Amortized            10  avgt    5         0.038 ±       0.021  us/op
RemoveVowels.regex                              10  avgt    5         0.562 ±       0.687  us/op
RemoveVowels.regex_Amortized                    10  avgt    5         0.126 ±       0.100  us/op
RemoveVowels.streamLcmFilter                    10  avgt    5         0.056 ±       0.002  us/op
RemoveVowels.streamThiccFilter                  10  avgt    5         0.045 ±       0.004  us/op

RemoveVowels.loopArrayFilter                   100  avgt    5         0.099 ±       0.003  us/op
RemoveVowels.loopArrayFilter_Amortized         100  avgt    5         0.045 ±       0.005  us/op
RemoveVowels.loopSetFilter                     100  avgt    5         0.443 ±       0.164  us/op
RemoveVowels.loopSetFilter_Amortized           100  avgt    5         0.358 ±       0.013  us/op
RemoveVowels.regex                             100  avgt    5         1.231 ±       1.115  us/op
RemoveVowels.regex_Amortized                   100  avgt    5         0.926 ±       0.780  us/op
RemoveVowels.streamLcmFilter                   100  avgt    5         0.345 ±       0.135  us/op
RemoveVowels.streamThiccFilter                 100  avgt    5         0.277 ±       0.011  us/op

RemoveVowels.loopArrayFilter                 10000  avgt    5         4.603 ±       1.180  us/op
RemoveVowels.loopArrayFilter_Amortized       10000  avgt    5         4.532 ±       0.949  us/op
RemoveVowels.loopSetFilter                   10000  avgt    5        49.093 ±       1.923  us/op
RemoveVowels.loopSetFilter_Amortized         10000  avgt    5        46.765 ±       3.051  us/op
RemoveVowels.regex                           10000  avgt    5        76.605 ±      45.409  us/op
RemoveVowels.regex_Amortized                 10000  avgt    5        86.853 ±      45.574  us/op
RemoveVowels.streamLcmFilter                 10000  avgt    5        34.205 ±       1.180  us/op
RemoveVowels.streamThiccFilter               10000  avgt    5        31.285 ±       0.713  us/op

RemoveVowels.loopArrayFilter            1000000000  avgt    5    885800.393 ±   20505.749  us/op
RemoveVowels.loopArrayFilter_Amortized  1000000000  avgt    5    860979.023 ±   50693.750  us/op
RemoveVowels.loopSetFilter              1000000000  avgt    5  11840644.800 ±  523138.178  us/op
RemoveVowels.loopSetFilter_Amortized    1000000000  avgt    5  11701675.280 ±  332665.032  us/op
RemoveVowels.regex                      1000000000  avgt    5   8504775.590 ± 3820477.462  us/op
RemoveVowels.regex_Amortized            1000000000  avgt    5   8673446.090 ± 3785713.628  us/op
RemoveVowels.streamLcmFilter            1000000000  avgt    5   6626493.830 ± 2261788.918  us/op
RemoveVowels.streamThiccFilter          1000000000  avgt    5   2230030.732 ±   58368.680  us/op
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

    @Benchmark
    public void regex_Amortized(Blackhole bh) {
        final var m = vowelPattern.matcher(unit);
        bh.consume(m.replaceAll("").length());
    }
}
