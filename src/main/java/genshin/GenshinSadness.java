package genshin;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
/*
Benchmark                           (M)       (N)  Mode  Cnt      Score      Error  Units
GenshinSadness.manualPool         10000  10000000  avgt    5  13955.344 ± 2595.250  ms/op
GenshinSadness.parallelStream     10000  10000000  avgt    5   4557.490 ±  149.048  ms/op
GenshinSadness.singleStream       10000  10000000  avgt    5  64711.876 ±  665.457  ms/op
GenshinSadness.singleThread       10000  10000000  avgt    5  64462.378 ±  795.406  ms/op
GenshinSadness.smarterManualPool  10000  10000000  avgt    5   4574.182 ±   76.399  ms/op
 */
public class GenshinSadness {

    //    @Param({"1", "1000", "1000000"})
    @Param({"10000000"})
    private int N;

    /*
    Pooling factor for now many iterations to run per thread instance
     */
    @Param({"10000"})
    private int M;

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(GenshinSadness.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .jvmArgs("-Xmx64G")
                .warmupIterations(3)
                .measurementIterations(5)
                .build()).run();
    }

    @Benchmark
    public int singleStream() {
        return IntStream.range(0, N).map($ -> RegularWish.regularWish()).sum() +
                IntStream.range(0, N).map($ -> EventWish.nonPromoEventWish()).sum() +
                IntStream.range(0, N).map($ -> PromoWish.promoEventWish()).sum();

    }

    @Benchmark
    public int parallelStream() {
        return IntStream.range(0, N).parallel().map($ -> RegularWish.regularWish()).sum() +
                IntStream.range(0, N).parallel().map($ -> EventWish.nonPromoEventWish()).sum() +
                IntStream.range(0, N).parallel().map($ -> PromoWish.promoEventWish()).sum();
    }

    @Benchmark
    public int singleThread() {
        int a = 0, b = 0, c = 0;
        for (int i = 0; i < N; i++) {
            a += RegularWish.regularWish();
            b += EventWish.nonPromoEventWish();
            c += PromoWish.promoEventWish();
        }
        return a + b + c;
    }

    @Benchmark
    public int manualPool() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Callable<Integer>> regularWishTasks = new ArrayList<>();
        List<Callable<Integer>> eventWishTasks = new ArrayList<>();
        List<Callable<Integer>> promoWishTasks = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            regularWishTasks.add(RegularWish::regularWish);
            eventWishTasks.add(EventWish::nonPromoEventWish);
            promoWishTasks.add(PromoWish::promoEventWish);
        }

        List<Future<Integer>> regularResults = pool.invokeAll(regularWishTasks);
        List<Future<Integer>> eventResults = pool.invokeAll(eventWishTasks);
        List<Future<Integer>> promoResults = pool.invokeAll(promoWishTasks);

        pool.shutdown();


        int regularSum = regularResults.parallelStream().map(integerFuture -> {
                    try {
                        return integerFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .reduce(Integer::sum)
                .orElse(0);

        int eventSum = eventResults.parallelStream().map(integerFuture -> {
                    try {
                        return integerFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .reduce(Integer::sum)
                .orElse(0);
        int promoSum = promoResults.parallelStream().map(integerFuture -> {
                    try {
                        return integerFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .reduce(Integer::sum)
                .orElse(0);
        // check correctness of implementation
//        System.out.println(regularSum/N);
//        System.out.println(eventSum/N);
//        System.out.println(promoSum/N);
//        System.out.println("---");

        return regularSum + eventSum + promoSum;
    }

    @Benchmark
    public int smarterManualPool() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Callable<Integer>> regularWishTasks = new ArrayList<>();
        List<Callable<Integer>> eventWishTasks = new ArrayList<>();
        List<Callable<Integer>> promoWishTasks = new ArrayList<>();
        for (int i = 0; i < N / M; i++) {
            regularWishTasks.add(() -> {
                int acc = 0;
                for (int m = 0; m < M; m++) {
                    acc += RegularWish.regularWish();
                }
                return acc;
            });
            eventWishTasks.add(() -> {
                int acc = 0;
                for (int m = 0; m < M; m++) {
                    acc += EventWish.nonPromoEventWish();
                }
                return acc;
            });
            promoWishTasks.add(() -> {
                int acc = 0;
                for (int m = 0; m < M; m++) {
                    acc += PromoWish.promoEventWish();
                }
                return acc;
            });
        }

        List<Future<Integer>> regularResults = pool.invokeAll(regularWishTasks);
        List<Future<Integer>> eventResults = pool.invokeAll(eventWishTasks);
        List<Future<Integer>> promoResults = pool.invokeAll(promoWishTasks);

        pool.shutdown();


        int regularSum = regularResults.parallelStream().map(integerFuture -> {
                    try {
                        return integerFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .reduce(Integer::sum)
                .orElse(0);

        int eventSum = eventResults.parallelStream().map(integerFuture -> {
                    try {
                        return integerFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .reduce(Integer::sum)
                .orElse(0);
        int promoSum = promoResults.parallelStream().map(integerFuture -> {
                    try {
                        return integerFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .reduce(Integer::sum)
                .orElse(0);
        // check correctness of implementation
//        System.out.println(regularSum/N);
//        System.out.println(eventSum/N);
//        System.out.println(promoSum/N);
//        System.out.println("---");

        return regularSum + eventSum + promoSum;
    }

}
