package math.primes;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)

/*
Benchmark                              (N)  Mode  Cnt         Score         Error  Units
MikeBlairPrimes.mike_genSieve        10000  avgt    5        29.411 ±       1.462  us/op
MikeBlairPrimes.genSieve             10000  avgt    5        17.265 ±       0.245  us/op

MikeBlairPrimes.mike_genSieve      1000000  avgt    5      3904.944 ±      30.552  us/op
MikeBlairPrimes.genSieve           1000000  avgt    5      2321.352 ±      76.075  us/op

MikeBlairPrimes.mike_genAndCount     10000  avgt    5      4284.334 ±     516.163  us/op
MikeBlairPrimes.genSieveAndCount     10000  avgt    5        17.709 ±       2.022  us/op

MikeBlairPrimes.mike_genAndCount   1000000  avgt    5  26219530.520 ± 1807184.113  us/op
MikeBlairPrimes.genSieveAndCount   1000000  avgt    5      2994.444 ±     109.792  us/op

MikeBlairPrimes.mike_pregen_sieve    10000  avgt    5      4001.394 ±      60.508  us/op
MikeBlairPrimes.pregen_sieve         10000  avgt    5         2.322 ±       0.040  us/op

MikeBlairPrimes.mike_pregen_sieve  1000000  avgt    5  25898224.700 ± 2661178.531  us/op
MikeBlairPrimes.pregen_sieve       1000000  avgt    5       569.112 ±      38.074  us/op
 */
public class MikeBlairPrimes {
    @Param({"10000", "1000000"})
    int N;

    boolean[] sieve;
    int[] mike_sieve;

    Random rand;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(MikeBlairPrimes.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .jvmArgs("-Xmx64G")
                .warmupIterations(3)
                .measurementIterations(5)
                .build()).run();
    }

    private boolean[] makeSieve(int max) {
        final var sieve = new boolean[max + 1]; // sieve[i] is true iff i is not prime
        sieve[0] = true; // 0 is not prime
        sieve[1] = true; // 1 is not prime
        for (int i = 2; i < max; i++) {
            if (!sieve[i]) {
                for (int j = i * 2; j <= max; j += i) {
                    sieve[j] = true;
                }
            }
        }
        return sieve;
    }

    @Setup()
    public void setup() {
        sieve = makeSieve(N);
        mike_sieve = mike_makeSieve(N);
    }


    @Benchmark
    public int genSieve() {
        var sieve = makeSieve(N);
        return sieve.length;
    }

    @Benchmark
    public int mike_genSieve() {
        var sieve = mike_makeSieve(N);
        return sieve.length;
    }

    @Benchmark
    public int pregen_sieve() {
        int numPrimes = 0;
        for (int i = 0; i < N; i++) {
            if (!sieve[i]) numPrimes++;
        }
        return numPrimes;
    }

    @Benchmark
    public int genSieveAndCount() {
        boolean[] sieve = makeSieve(N);
        int numPrimes = 0;
        for (int i = 0; i < N; i++) {
            if (!sieve[i]) numPrimes++;
        }
        return numPrimes;
    }

    @Benchmark
    public int mike_pregen_sieve() {
        int numPrimes = 0;
        for (int i = 0; i < N; i++) {
            if (isPrime(i, mike_sieve)) numPrimes++;
        }
        return numPrimes;
    }

    @Benchmark
    public int mike_genAndCount() {
        int[] mike_sieve = mike_makeSieve(N);
        int numPrimes = 0;
        for (int i = 0; i < N; i++) {
            if (isPrime(i, mike_sieve)) numPrimes++;
        }
        return numPrimes;
    }

    private static int[] mike_makeSieve(int max) {
        int upperRange = max - 1;
        int[] sieve = new int[max];
        int count = 0;
        //Make a sieve and display the primes.
        boolean[] is_prime = MakeSieve(max);
        for (int i = 2; i < max; i++)
            if (is_prime[i]) sieve[count++] = i;
        int[] nonNullSieve = Arrays.stream(sieve)
                .filter(a -> a != 0) //only keep non-null persons
                .toArray(); //create new array of persons
        return nonNullSieve;
    }

    private static boolean[] MakeSieve(int max) {
        boolean[] is_prime = new boolean[max + 1];
        for (int i = 2; i <= max; i++) is_prime[i] = true; //set all to prime to start.

        //Cross out multiples.
        for (int i = 2; i <= max; i++) {
            //See if i is prime.
            if (is_prime[i]) {
                //Knock out multiples of i.
                for (int j = i * 2; j <= max; j += i)
                    is_prime[j] = false;

            }
        }
        return is_prime; // return result of primality test..
    }

    private static boolean isPrime(int searchValue, int[] sieve) {
        int[] primes = Arrays.copyOf(sieve, sieve.length);
        int result = search(primes, searchValue);
        return result != -1;
    }


    public static int search(int[] array, int value) {
        return binarySearch(array, 0, array.length - 1, value);
    }

    /**
     * The binarySearch method performs a recursive binary
     * search on an integer array.
     *
     * @param array The array to search.
     * @param first The first element in the search range.
     * @param last  The last element in the search range.
     * @param value The value to search for.
     * @return The subscript of the value if found,
     * otherwise -1.
     */
    private static int binarySearch(int[] array, int first,
                                    int last, int value) {
        int middle;     // Mid point of search

        // Test for the base case where the
        // value is not found.
        if (first > last)
            return -1;

        // Calculate the middle position.
        middle = (first + last) / 2;

        // Search for the value.
        if (array[middle] == value)
            return middle;
        else if (array[middle] < value)
            return binarySearch(array, middle + 1,
                    last, value);
        else
            return binarySearch(array, first,
                    middle - 1, value);
    }

}
