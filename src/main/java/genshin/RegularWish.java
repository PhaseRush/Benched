package genshin;

import java.util.concurrent.ThreadLocalRandom;

public class RegularWish extends Wish {

    private final int numIters;

    public RegularWish(int numIters) {
        this.numIters = numIters;
    }

    static boolean selected(double prob) {
        return ThreadLocalRandom.current().nextFloat() < prob &&
                ThreadLocalRandom.current().nextInt(5) == 1; // correct 5 star (1 out of 5)
    }

    static int regularWish() {
        int wishes = 0;
        int pity = 0;
        while (true) {
            wishes++;
            if (pity == HARD_PITY) { // hard pity, 50/50
                pity = 0;
                if (selected(0.5)) return wishes;
            } else if (pity > SOFT_PITY) {
                pity++;
                if (ThreadLocalRandom.current().nextFloat() < POST_SOFT_PROB) {
                    pity = 0;
                    if (selected(0.5)) return wishes;
                }
            } else {
                pity++;
                if (ThreadLocalRandom.current().nextFloat() < PRE_SOFT_PROB) {
                    pity = 0;
                    if (selected(0.5)) return wishes;
                }
            }
        }
    }

    @Override
    public Integer call() {
        int sum = 0;
        for (int i = 0; i < numIters; i++) {
            sum += regularWish();
        }
        return sum;
    }
}
