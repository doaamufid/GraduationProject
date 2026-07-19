package com.example.graduationproject.widget;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Equivalent of the shared CSS entrance animations:
 *   .step-fade { animation: sfade .3s ease; }   from{opacity:0;translateY(6px)}  to{opacity:1;translateY(0)}
 *   .note-fade { animation: nfade .2s ease; }   from{opacity:0}                  to{opacity:1}
 *   .done-fade { animation: dfade .5s ease; }   from{opacity:0;translateY(10px)} to{opacity:1;translateY(0)}
 */
public final class FadeUtils {

    private FadeUtils() {
    }

    /** .step-fade: 300ms fade + 6dp slide up. Used when moving to a new sense step. */
    public static void stepFade(View view) {
        fadeUp(view, 6, 300);
    }

    /** .done-fade: 500ms fade + 10dp slide up. Used for the final "you're back" screen. */
    public static void doneFade(View view) {
        fadeUp(view, 10, 500);
    }

    /** .note-fade: 200ms plain fade, no movement. Used when the optional note field appears. */
    public static void noteFade(View view) {
        view.setAlpha(0f);
        view.animate()
                .alpha(1f)
                .setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private static void fadeUp(View view, int dp, long durationMs) {
        float density = view.getResources().getDisplayMetrics().density;
        view.setAlpha(0f);
        view.setTranslationY(dp * density);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(durationMs)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }
}
