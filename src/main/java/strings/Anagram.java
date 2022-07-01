package strings;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms50G", "-Xmx50G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)

/*
Benchmark                   (N)  Mode  Cnt      Score      Error  Units
Anagram.JayM1           1000000  avgt    5  85161.395 ± 3892.250  us/op
Anagram.JayM1_nostring  1000000  avgt    5  29203.709 ± 9897.196  us/op
Anagram.Requiem         1000000  avgt    5   1169.408 ±   49.253  us/op
Anagram.Eggy            1000000  avgt    5   2467.314 ±   65.174  us/op

*/
public class Anagram {

    @Param({"1000000"})
    private int N;

    private String a, b;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(Anagram.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        a = RandomStringUtils.randomAlphanumeric(N);
        b = RandomStringUtils.randomAlphanumeric(N);
    }


    @Benchmark
    public boolean Requiem() {
        if (a.length() != b.length()) return false;
        char[] freq = new char[128];
        for (char c : a.toCharArray()) {
            freq[c]++;
        }
        for (char c : b.toCharArray()) {
            freq[c]--;
        }
        for (char c : freq) {
            if (c != 0) return false;
        }
        return true;
    }

    @Benchmark
    public boolean Eggy() {
        if (a.length() != b.length())
            return false;
        final int[] freq = new int[123];
        for (int i = 0; i < a.length(); i++) {
            freq[a.charAt(i)]++;
            freq[b.charAt(i)]--;
        }
        for (int c : freq)
            if (c != 0)
                return false;
        return true;

    }

//    @Benchmark
//    public boolean JayM1() {
//        if (a.length() != b.length()) {
//            return false;
//        }
//
//        Map<String, Integer> smap = new HashMap<String, Integer>();//created two maps
//        Map<String, Integer> tmap = new HashMap<String, Integer>();
//
//
//        for (int i = 0; i < a.length(); i++) {
//            smap.put(a.substring(i, i + 1), 0);
//            tmap.put(b.substring(i, i + 1), 0);
//        } // added all strings and value = 0
//
//
//        for (int i = 0; i < a.length(); i++) {
//
//            smap.put(a.substring(i, i + 1), smap.get(a.substring(i, i + 1)) + 1);
//            tmap.put(b.substring(i, i + 1), tmap.get(b.substring(i, i + 1)) + 1);
//        } //went through every value and added in k, value + 1
////        System.out.println(smap.toString());
////        System.out.println(tmap.toString());
//
//        for (String key : smap.keySet()) {
//            if (tmap.containsKey(key)) {
//                if (!(tmap.get(key).equals(smap.get(key)))) {
//                    return false;
//                }
//            }
//            if (!(tmap.containsKey(key))) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @Benchmark
//    public boolean JayM1_nostring() {
//        if (a.length() != b.length()) {
//            return false;
//        }
//
//        Map<Character, Integer> smap = new HashMap<>();//created two maps
//        Map<Character, Integer> tmap = new HashMap<>();
//
//
//        for (int i = 0; i < a.length(); i++) {
//            smap.put(a.charAt(i), 0);
//            tmap.put(b.charAt(i), 0);
//        } // added all strings and value = 0
//
//
//        for (int i = 0; i < a.length(); i++) {
//
//            smap.put(a.charAt(i), smap.get(a.charAt(i)) + 1);
//            tmap.put(b.charAt(i), tmap.get(b.charAt(i)) + 1);
//        } //went through every value and added in k, value + 1
////        System.out.println(smap.toString());
////        System.out.println(tmap.toString());
//
//        for (Character key : smap.keySet()) {
//            if (tmap.containsKey(key)) {
//                if (!(tmap.get(key).equals(smap.get(key)))) {
//                    return false;
//                }
//            }
//            if (!(tmap.containsKey(key))) {
//                return false;
//            }
//        }
//        return true;
//    }

}
