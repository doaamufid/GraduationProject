package com.example.graduationproject.animation;

import com.airbnb.lottie.LottieAnimationView;

/**
 * Internal helper for Lordicon animations.
 * Lordicon animations are used as Lottie JSON assets in native Android.
 * Note: Lordicon web triggers (hover, etc.) are NOT supported natively 
 * and must be handled via Java click/touch listeners.
 */
class LordiconHelper {

    static void play(LottieAnimationView view, String jsonPath) {
        LottieHelper.loadAndPlay(view, jsonPath);
    }

    static void play(LottieAnimationView view, int rawResId) {
        LottieHelper.loadAndPlay(view, rawResId);
    }

    static void stop(LottieAnimationView view) {
        LottieHelper.stop(view);
    }

    static void restart(LottieAnimationView view) {
        LottieHelper.restart(view);
    }
}
