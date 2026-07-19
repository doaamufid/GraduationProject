package com.example.graduationproject.widget;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Equivalent of the shared CSS entrance animation used across the app:
 *   .crisis-fade { animation: cfade .5s ease; }
 *   @keyframes cfade { from { opacity:0; transform: translateY(10px); }
 *                       to   { opacity:1; transform: translateY(0);   } }
 */
public final class FadeUtils {

    private FadeUtils() {
    }

    /** Fades the view in while sliding it up ~10dp, matching the web keyframes. */
    public static void fadeInUp(View view) {
        float density = view.getResources().getDisplayMetrics().density;
        view.setAlpha(0f);
        view.setTranslationY(10 * density);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /**
     * Cross-fades from the currently displayed step to a new one:
     * fades {@code outView} out, then runs {@code onSwap} (where the
     * caller should update content) and fades {@code inView} in with
     * the slide-up entrance.
     */
    public static void crossFadeStep(View outView, View inView, Runnable onSwap) {
        outView.animate().alpha(0f).setDuration(120).withEndAction(() -> {
            if (onSwap != null) onSwap.run();
            outView.setAlpha(1f);
            fadeInUp(inView);
        }).start();
    }
}
