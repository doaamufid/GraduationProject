package com.example.graduationproject.widget;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Equivalent of:
 *   .radial-spin  { animation: spin 40s linear infinite; }      (dashed ring around the radial header)
 *   .animate-spin { animation: spinicon 1s linear infinite; }   (loading spinner icon)
 * Both are a plain continuous 360-degree rotation; only the duration differs.
 */
public final class SpinAnimator {

    private SpinAnimator() {
    }

    public static ObjectAnimator start(View view, long durationMs) {
        view.setRotation(0f);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        animator.setDuration(durationMs);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        return animator;
    }

    public static void stop(ObjectAnimator animator) {
        if (animator != null) animator.cancel();
    }
}
