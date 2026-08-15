package com.example.graduationproject.widget;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Equivalent of:
 *   .hero-pulse { animation: hpulse 2.4s ease-in-out infinite; }
 *   @keyframes hpulse {
 *     0%,100% { box-shadow: 0 0 0 0 #ffffff33; }
 *     50%     { box-shadow: 0 0 0 14px #ffffff00; }
 *   }
 *
 * A translucent ring behind the play/pause button that grows outward
 * while fading, then eases back - only active while audio is "playing",
 * exactly like the original's conditional `hero-pulse` class.
 */
public final class HeroPulseAnimator {

    private HeroPulseAnimator() {
    }

    public static ValueAnimator start(View ringView) {
        ringView.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1200); // half of the 2.4s full cycle
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(anim -> {
            float t = (float) anim.getAnimatedValue();
            float scale = 1f + (0.3f * t);
            float alpha = 0.4f * (1f - t);
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
