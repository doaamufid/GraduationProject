package com.example.graduationproject.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Custom bottom "pill" toast that mirrors the .toast-in CSS keyframe:
 * fades + slides up into place, holds, then the caller (or a later
 * showToast call) removes it. We auto-dismiss after ~1700ms to match
 * the React setTimeout(() => setToast(null), 1700).
 */
public class ToastUtil {

    private static View activeToast;
    private static Runnable pendingDismiss;

    public static void show(FrameLayout overlayRoot, String message) {
        // remove any currently showing toast immediately
        if (activeToast != null) {
            overlayRoot.removeCallbacks(pendingDismiss);
            overlayRoot.removeView(activeToast);
            activeToast = null;
        }

        View toastView = LayoutInflater.from(overlayRoot.getContext())
                .inflate(com.example.graduationproject.R.layout.toast_view, overlayRoot, false);
        TextView tv = toastView.findViewById(com.example.graduationproject.R.id.toastText);
        tv.setText(message);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = dp(overlayRoot, 96);
        toastView.setLayoutParams(lp);

        toastView.setAlpha(0f);
        toastView.setTranslationY(dp(overlayRoot, 8));
        overlayRoot.addView(toastView);
        activeToast = toastView;

        ObjectAnimator fade = ObjectAnimator.ofFloat(toastView, View.ALPHA, 0f, 1f);
        ObjectAnimator slide = ObjectAnimator.ofFloat(toastView, View.TRANSLATION_Y, dp(overlayRoot, 8), 0f);
        AnimatorSet in = new AnimatorSet();
        in.setDuration(300);
        in.setInterpolator(new OvershootInterpolator(1.0f));
        in.playTogether(fade, slide);
        in.start();

        pendingDismiss = () -> {
            if (toastView.getParent() == null) return;
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(toastView, View.ALPHA, 1f, 0f);
            ObjectAnimator slideOut = ObjectAnimator.ofFloat(toastView, View.TRANSLATION_Y, 0f, dp(overlayRoot, 8));
            AnimatorSet out = new AnimatorSet();
            out.setDuration(200);
            out.playTogether(fadeOut, slideOut);
            out.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (toastView.getParent() != null) overlayRoot.removeView(toastView);
                    if (activeToast == toastView) activeToast = null;
                }
            });
            out.start();
        };
        overlayRoot.postDelayed(pendingDismiss, 1700);
    }

    private static int dp(View v, int value) {
        float density = v.getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}
