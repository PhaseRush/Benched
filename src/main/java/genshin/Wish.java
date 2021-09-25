package genshin;

import java.util.concurrent.Callable;

public abstract class Wish implements Callable<Integer> {
    static final int HARD_PITY = 90;
    static final int SOFT_PITY = 75;
    static final double PRE_SOFT_PROB = 0.006;
    static final double POST_SOFT_PROB = 0.324;

}
