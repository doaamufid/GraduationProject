package com.example.graduationproject.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.example.graduationproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Reusable equalizer-style waveform animation.
 *
 * Equivalent of the original CSS:
 *   .wave-bar { height: 6px; animation: wave 0.9s ease-in-out infinite; }
 *   @keyframes wave { 0%,100% { height:6px; } 50% { height:24px; } }
 *   (with a per-bar `animationDelay: i * 0.07s` stagger)
 *
 * Creates {@code barCount} thin vertical bars inside the given container
 * and animates each bar's height between 6dp and 24dp on an infinite
 * loop, staggering the start of each bar by 70ms to reproduce the
 * "equalizer" ripple effect from the web version.
 */
public final class WaveBarAnimator {

    private static final int BAR_COUNT = 14;
    private static final int MIN_HEIGHT_DP = 6;
    private static final int MAX_HEIGHT_DP = 24;
    private static final int BAR_WIDTH_DP = 3;
    private static final long DURATION_MS = 900;
    private static final long STAGGER_MS = 70;

    private WaveBarAnimator() {
    }

    public static List<ValueAnimator> start(Context context, LinearLayout container) {
        container.removeAllViews();
        container.setGravity(Gravity.BOTTOM);
        float density = context.getResources().getDisplayMetrics().density;
        int minPx = (int) (MIN_HEIGHT_DP * density);
        int maxPx = (int) (MAX_HEIGHT_DP * density);
        int widthPx = (int) (BAR_WIDTH_DP * density);
        int marginPx = (int) (2 * density);

        List<ValueAnimator> animators = new ArrayList<>();

        for (int i = 0; i < BAR_COUNT; i++) {
            View bar = new View(context);
            bar.setBackgroundResource(R.drawable.bg_wave_bar);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(widthPx, minPx);
            lp.setMarginStart(marginPx);
            lp.setMarginEnd(marginPx);
            bar.setLayoutParams(lp);
            container.addView(bar);

            ValueAnimator animator = ValueAnimator.ofInt(minPx, maxPx);
            animator.setDuration(DURATION_MS);
            animator.setStartDelay(i * STAGGER_MS);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(anim -> {
                ViewGroupLayoutParamsHelper.setHeight(bar, (int) anim.getAnimatedValue());
            });
            animator.start();
            animators.add(animator);
        }
        return animators;
    }

    public static void stop(List<ValueAnimator> animators) {
        if (animators == null) return;
        for (ValueAnimator a : animators) {
            a.cancel();
        }
    }

    /** Tiny helper so we don't need a separate util class just for this one line. */
    private static final class ViewGroupLayoutParamsHelper {
        static void setHeight(View view, int height) {
            android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.height = height;
            view.setLayoutParams(lp);
        }
    }
}
