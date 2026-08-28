package com.example.graduationproject.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.Random;

/**
 * Mirrors the CSS keyframes used in the original React component:
 * kid-bounce, kid-wiggle, kid-pop, kid-fadeUp, kid-fadeIn, kid-fall,
 * kid-sparkle, kid-glow.
 */
public final class kidsCalmAnimUtils {

    private kidsCalmAnimUtils() {}

    /** @keyframes kid-bounce: translateY(0) <-> translateY(-8px), 1.6s ease-in-out infinite */
    public static ObjectAnimator bounce(View v) {
        float dy = dp(v, -8);
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, 0f, dy, 0f);
        anim.setDuration(1600);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        anim.start();
        return anim;
    }

    /** @keyframes kid-wiggle: rotate(-3deg) <-> rotate(3deg), 1.4s ease-in-out infinite */
    public static ObjectAnimator wiggle(View v) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, View.ROTATION, -3f, 3f, -3f);
        anim.setDuration(1400);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        anim.start();
        return anim;
    }

    /** @keyframes kid-pop: scale(0.6) opacity 0 -> scale(1) opacity 1, .35s overshoot */
    public static void pop(View v) {
        v.setScaleX(0.6f);
        v.setScaleY(0.6f);
        v.setAlpha(0f);
        v.animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(350)
                .setInterpolator(new OvershootInterpolator(1.6f))
                .start();
    }

    /** @keyframes kid-fadeUp: opacity 0, translateY(14px) -> opacity 1, translateY(0) */
    public static void fadeUp(View v) {
        v.setAlpha(0f);
        v.setTranslationY(dp(v, 14));
        v.animate().alpha(1f).translationY(0f)
                .setDuration(450)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
    }

    /** @keyframes kid-fadeIn: opacity 0 -> opacity 1, .5s ease */
    public static void fadeIn(View v) {
        v.setAlpha(0f);
        v.animate().alpha(1f).setDuration(500).start();
    }

    /** @keyframes kid-sparkle: opacity .3 scale .8 <-> opacity 1 scale 1.15, 1.8s infinite */
    public static ObjectAnimator sparkle(View v) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator a1 = ObjectAnimator.ofFloat(v, View.ALPHA, 0.3f, 1f, 0.3f);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(v, View.SCALE_X, 0.8f, 1.15f, 0.8f);
        ObjectAnimator a3 = ObjectAnimator.ofFloat(v, View.SCALE_Y, 0.8f, 1.15f, 0.8f);
        for (ObjectAnimator a : new ObjectAnimator[]{a1, a2, a3}) {
            a.setDuration(1800);
            a.setRepeatCount(ValueAnimator.INFINITE);
            a.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            a.start();
        }
        return a1;
    }

    /** @keyframes kid-glow: pulsing soft glow via scale + elevation, 1.8s infinite */
    public static AnimatorSet glow(View v) {
        ObjectAnimator scale1 = ObjectAnimator.ofFloat(v, View.SCALE_X, 1f, 1.015f, 1f);
        ObjectAnimator scale2 = ObjectAnimator.ofFloat(v, View.SCALE_Y, 1f, 1.015f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scale1, scale2);
        set.setDuration(1800);
        for (Animator a : set.getChildAnimations()) {
            ((ObjectAnimator) a).setRepeatCount(ValueAnimator.INFINITE);
        }
        set.start();
        return set;
    }

    /** @keyframes kid-fall: falling + rotating confetti piece, used by ConfettiView */
    public static void fall(View v, int fallDistancePx, long duration, long delay) {
        v.setTranslationY(-dp(v, 10));
        v.setRotation(0f);
        v.setAlpha(1f);
        ObjectAnimator ty = ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, -dp(v, 10), fallDistancePx);
        ObjectAnimator rot = ObjectAnimator.ofFloat(v, View.ROTATION, 0f, 360f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(v, View.ALPHA, 1f, 1f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ty, rot, alpha);
        set.setDuration(duration);
        set.setStartDelay(delay);
        set.setInterpolator(new AccelerateInterpolator(0.9f));
        set.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // loop with a fresh random delay, like the CSS `infinite` fall
                v.postDelayed(() -> fall(v, fallDistancePx, duration, 0), new Random().nextInt(400));
            }
        });
        set.start();
    }

    public static void cancel(Animator a) {
        if (a != null) a.cancel();
    }

    private static float dp(View v, float value) {
        return value * v.getResources().getDisplayMetrics().density;
    }
}
