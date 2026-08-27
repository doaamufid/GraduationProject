package com.example.graduationproject.util;

/**
 * Mirrors STAGES + stageForScreen(i) from the React source: the sky drifts through
 * 6 light, cheerful gradient stages as the user progresses through the 13 screens.
 */
public final class KidsAdaptiveStages {

    public static final int TOTAL_SCREENS = 13;

    public static final int[] FROM_COLORS = {
            0xFFCDEBFB, 0xFFEAF7FF, 0xFFFFE7CE, 0xFFFCD9EA, 0xFFFFF3B8, 0xFFFFD9A0
    };
    public static final int[] TO_COLORS = {
            0xFFEAF7FF, 0xFFFFE7CE, 0xFFFCD9EA, 0xFFFFF3B8, 0xFFFFD9A0, 0xFFFFC1A6
    };

    private KidsAdaptiveStages() {}

    public static int stageForScreen(int i) {
        if (i <= 1) return 0;
        if (i <= 3) return 1;
        if (i <= 6) return 2;
        if (i <= 8) return 3;
        if (i <= 10) return 4;
        return 5;
    }
}
