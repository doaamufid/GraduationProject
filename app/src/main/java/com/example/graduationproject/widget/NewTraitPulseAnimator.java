package com.example.graduationproject.widget;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Equivalent of:
 *   .new-trait-pulse { animation: newpulse 1.8s ease-in-out infinite; }
 *   @keyframes newpulse {
 *     0%,100% { box-shadow: 0 0 0 0 primaryLight55; }
 *     50%     { box-shadow: 0 0 0 6px primaryLight00; }
 *   }
 *
 * A translucent rounded-rect ring, sized to match the card and placed
 * directly behind it, that grows slightly outward while fading -
 * reproducing the pulsing glow border of a newly-discovered trait card.
 */
public final class NewTraitPulseAnimator {

    private NewTraitPulseAnimator() {
    }

    public static ValueAnimator start(View ringView) {
        ringView.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1500); // Slower pulsing
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            float t = (float) a.getAnimatedValue();
            float scale = 1f + (0.02f * t);
            float alpha = 0.55f * (1f - t);
            ringView.setScaleX(scale);
            ringView.setScaleY(scale);
            ringView.setAlpha(alpha);
        });
        animator.start();
        return animator;
    }

    public static void stop(ValueAnimator animator, View ringView) {
        if (animator != null) animator.cancel();
        if (ringView != null) {
            ringView.setVisibility(View.GONE);
            ringView.setAlpha(0f);
            ringView.setScaleX(1f);
            ringView.setScaleY(1f);
        }
    }
}
