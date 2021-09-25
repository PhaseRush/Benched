package genshin;

import java.util.concurrent.ThreadLocalRandom;

public class EventWish extends Wish {
    private final int numIters;

    public EventWish(int numIters) {
        this.numIters = numIters;
    }

    static boolean flipCoin() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    static int nonPromoEventWish() {
        int wishes = 0;
        int pity = 0;
        boolean guarantee = false;
        while (true) {
            wishes++;
            if (pity == HARD_PITY) {
                pity = 0;
                if (!guarantee) {
                    if (flipCoin()) {
                        if (ThreadLocalRandom.current().nextInt(5) == 1) return wishes;
                        guarantee = true;
                    }
                } else {
                    guarantee = false; // sad
                }
            } else if (pity > SOFT_PITY) {
                pity++;
                if (ThreadLocalRandom.current().nextFloat() < POST_SOFT_PROB) {
                    pity = 0;
                    if (!guarantee) {
                        if (flipCoin()) {
                            if (ThreadLocalRandom.current().nextInt(5) == 1) return wishes;
                            guarantee = true;
                        }
                    } else {
                        guarantee = false;
                    }
                }
            } else {
                pity++;
                if (ThreadLocalRandom.current().nextFloat() < PRE_SOFT_PROB) {
                    pity = 0;
                    if (!guarantee) {
                        if (flipCoin()) {
                            if (ThreadLocalRandom.current().nextInt(5) == 1) return wishes;
                            guarantee = true;
                        }
                    } else {
                        guarantee = false;
                    }
                }
            }
        }
    }

    @Override
    public Integer call() {
        int sum = 0;
        for (int i = 0; i < numIters; i++) {
            sum += nonPromoEventWish();
        }
        return sum;
    }
}