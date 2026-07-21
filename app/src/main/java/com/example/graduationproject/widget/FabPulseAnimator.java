package com.example.graduationproject.widget;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Equivalent of:
 *   .compose-fab { animation: fabpulse 2.4s ease-in-out infinite; }
 *   @keyframes fabpulse {
 *     0%,100% { box-shadow: 0 0 0 0 accent55; }
 *     50%     { box-shadow: 0 0 0 10px accent00; }
 *   }
 *
 * A soft halo ring that grows from the FAB's edge out to +10dp while
 * fading out, then eases back - REVERSE repeat mode gives the same
 * "grow then shrink" breathing feel as the two-keyframe CSS animation.
 * The ring view should be a plain stroked-circle drawable (bg_fab_ring)
 * centered behind the FAB, sized to match it.
 */
public final class FabPulseAnimator {

    private FabPulseAnimator() {
    }

    public static ValueAnimator start(View ringView) {
        ringView.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1200); // half of the 2.4s full cycle (grow, then the REVERSE half shrinks back)
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(anim -> {
            float t = (float) anim.getAnimatedValue();
            float scale = 1f + (0.28f * t);
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
