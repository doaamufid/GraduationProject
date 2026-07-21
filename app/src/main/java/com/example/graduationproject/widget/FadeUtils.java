package com.example.graduationproject.widget;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Equivalent of the shared entrance animations:
 *   .dialog-fade  { animation: dfade .3s ease; } from{opacity:0;translateY(20px)} to{opacity:1;translateY(0)}
 *   .reminder-fade{ animation: rfade .2s ease; } from{opacity:0} to{opacity:1}
 *   .screen-fade  { animation: sfade .3s ease; } from{opacity:0;translateX(12px)} to{opacity:1;translateX(0)}
 */
public final class FadeUtils {

    private FadeUtils() {
    }

    /** .dialog-fade: 300ms fade + 20dp slide up. */
    public static void dialogFade(View view) {
        float density = view.getResources().getDisplayMetrics().density;
        view.setAlpha(0f);
        view.setTranslationY(20 * density);
        view.animate().alpha(1f).translationY(0f).setDuration(300)
                .setInterpolator(new DecelerateInterpolator()).start();
    }

    /** .reminder-fade: 200ms plain fade. */
    public static void reminderFade(View view) {
        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(200)
                .setInterpolator(new DecelerateInterpolator()).start();
    }

    /** .screen-fade: 300ms fade + 12dp slide in from the (start) side. */
    public static void screenFade(View view) {
        float density = view.getResources().getDisplayMetrics().density;
        view.setAlpha(0f);
        view.setTranslationX(12 * density);
        view.animate().alpha(1f).translationX(0f).setDuration(300)
                .setInterpolator(new DecelerateInterpolator()).start();
    }
}
