package com.example.graduationproject.widget;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

/**
 * Equivalent of:
 *   <div className="h-full rounded-full transition-all"
 *        style={{ width: `${(overallTapped / overallTotal) * 100}%` }} />
 *
 * Smoothly animates a fill View's width (as a fraction of its parent's
 * width) whenever the tapped/total ratio changes.
 */
public final class ProgressBarAnimator {

    private ProgressBarAnimator() {
    }

    public static void animateTo(View fillView, View trackView, float fraction) {
        int trackWidth = trackView.getWidth();
        if (trackWidth <= 0) {
            // Not laid out yet - set directly and animate on the next frame.
            trackView.post(() -> animateTo(fillView, trackView, fraction));
            return;
        }

        ViewGroup.LayoutParams lp = fillView.getLayoutParams();
        int fromWidth = lp.width > 0 ? lp.width : 0;
        int toWidth = Math.round(trackWidth * Math.max(0f, Math.min(1f, fraction)));

        ValueAnimator animator = ValueAnimator.ofInt(fromWidth, toWidth);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(anim -> {
            ViewGroup.LayoutParams params = fillView.getLayoutParams();
            params.width = (int) anim.getAnimatedValue();
            fillView.setLayoutParams(params);
        });
        animator.start();
    }
}
