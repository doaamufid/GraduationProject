package com.example.graduationproject.util;

import com.example.graduationproject.R;

public class CardColors {

    public static class Pair {
        public final int bgRes;
        public final int fgRes;

        Pair(int bgRes, int fgRes) {
            this.bgRes = bgRes;
            this.fgRes = fgRes;
        }
    }

    private static final Pair[] PALETTE = new Pair[]{
            new Pair(R.color.card0_bg, R.color.card0_fg),
            new Pair(R.color.card1_bg, R.color.card1_fg),
            new Pair(R.color.card2_bg, R.color.card2_fg),
            new Pair(R.color.card3_bg, R.color.card3_fg),
            new Pair(R.color.card4_bg, R.color.card4_fg),
    };

    public static Pair forIndex(int index) {
        int i = ((index % PALETTE.length) + PALETTE.length) % PALETTE.length;
        return PALETTE[i];
    }
}
