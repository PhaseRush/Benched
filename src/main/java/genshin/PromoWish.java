package genshin;

import java.util.concurrent.ThreadLocalRandom;

public class PromoWish extends Wish{
    private final int numIters;

    public PromoWish(int numIters) {
        this.numIters = numIters;
    }

    static boolean flipCoin() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    static int promoEventWish() {
        int wishes = 0;
        int pity = 0;
        boolean guarantee = false;
        while (true) {
            wishes++;
            if (pity == HARD_PITY) {
                pity = 0;
                if (guarantee) return wishes;
                else if (flipCoin()) guarantee = true;
                else return wishes;
            } else if (pity > SOFT_PITY) {
                pity++;
                if (ThreadLocalRandom.current().nextFloat() < POST_SOFT_PROB) {
                    pity = 0;
                    if (guarantee) return wishes;
                    else if (flipCoin()) guarantee = true;
                    else return wishes;
                }
            } else {
                pity++;
                if (ThreadLocalRandom.current().nextFloat() < PRE_SOFT_PROB) {
                    pity = 0;
                    if (guarantee) return wishes;
                    else if (flipCoin()) guarantee = true;
                    else return wishes;
                }
            }
        }
    }

    @Override
    public Integer call() {
        int sum = 0;
        for (int i = 0; i < numIters; i++) {
            sum += promoEventWish();
        }
        return sum;
    }
}
