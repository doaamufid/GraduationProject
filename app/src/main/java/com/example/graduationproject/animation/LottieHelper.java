package com.example.graduationproject.animation;

import com.airbnb.lottie.LottieAnimationView;
import android.animation.Animator;

/**
 * Internal helper for Airbnb Lottie and dotLottie animations.
 * Supports .json and .lottie formats natively using Lottie 6.0+.
 */
class LottieHelper {

    static void loadAndPlay(LottieAnimationView view, String fileName) {
        if (view == null || fileName == null) return;
        view.setAnimation(fileName);
        view.playAnimation();
    }

    static void loadAndPlay(LottieAnimationView view, int rawResId) {
        if (view == null) return;
        view.setAnimation(rawResId);
        view.playAnimation();
    }

    static void play(LottieAnimationView view) {
        if (view != null) view.playAnimation();
    }

    static void pause(LottieAnimationView view) {
        if (view != null) view.pauseAnimation();
    }

    static void stop(LottieAnimationView view) {
        if (view != null) view.cancelAnimation();
    }

    static void restart(LottieAnimationView view) {
        if (view != null) {
            view.setProgress(0f);
            view.playAnimation();
        }
    }

    static void setLoop(LottieAnimationView view, boolean loop) {
        if (view != null) {
            view.setRepeatCount(loop ? -1 : 0);
        }
    }

    static void setSpeed(LottieAnimationView view, float speed) {
        if (view != null) view.setSpeed(speed);
    }

    static void setCompletionListener(LottieAnimationView view, final Runnable onComplete) {
        if (view == null || onComplete == null) return;
        view.addAnimatorListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationEnd(Animator animation) { onComplete.run(); }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
    }
}
