package collections;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class IterateMap {

    @Param({"10", "1000000"})
    public int N;

    Map<Integer, Integer> map;

    public static void main(String[] args) throws Exception {
        new Runner(new OptionsBuilder()
                .include(IterateMap.class.getSimpleName())
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .jvmArgs("-Xmx90G")
                .warmupIterations(3)
                .measurementIterations(10)
                .build()).run();
    }

    @Setup
    public void setup() {
        map = ThreadLocalRandom.current().ints()
                .boxed()
                .map(i -> new AbstractMap.SimpleEntry<>(i, i * i))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}