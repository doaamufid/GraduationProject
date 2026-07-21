package com.example.graduationproject.widget;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Reusable "recording pulse" animation.
 *
 * Equivalent of the original CSS:
 *   .rec-pulse { animation: recpulse 1.4s ease-in-out infinite; }
 *   @keyframes recpulse {
 *     0%,100% { box-shadow: 0 0 0 0 rgba(201,117,117,0.5); }
 *     50%     { box-shadow: 0 0 0 14px rgba(201,117,117,0); }
 *   }
 *
 * Android has no box-shadow, so we fake the expanding ring with a plain
 * circular View placed *behind* the icon button (same center, larger
 * bounds) and animate its scale (1 -> ~1.6) and alpha (0.5 -> 0) in a
 * continuous loop. Used by both the audio-recording dialog and the
 * "playing" icon inside crisis mode.
 */
public final class PulseAnimator {

    private PulseAnimator() {
    }

    /**
     * Starts an infinite pulse loop on the given ring view.
     * The ring view should be a plain circular drawable (see
     * bg_pulse_ring.xml), centered behind the icon it pulses around.
     */
    public static ValueAnimator start(View ringView) {
        ringView.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1400);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(anim -> {
            float t = (float) anim.getAnimatedValue();
            float scale = 1f + (0.6f * t);
            float alpha = 0.5f * (1f - t);
            ringView.setScaleX(scale);
            ringView.setScaleY(scale);
            ringView.setAlpha(alpha);
        });
        animator.start();
        return animator;
    }

    public static void stop(ValueAnimator animator, View ringView) {
        if (animator != null) {
            animator.cancel();
        }
        if (ringView != null) {
            ringView.setVisibility(View.GONE);
            ringView.setAlpha(0f);
            ringView.setScaleX(1f);
            ringView.setScaleY(1f);
        }
    }
}
