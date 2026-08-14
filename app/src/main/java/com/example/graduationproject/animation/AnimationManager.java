package com.example.graduationproject.animation;

import com.airbnb.lottie.LottieAnimationView;
import app.rive.runtime.kotlin.RiveAnimationView;

/**
 * Centralized manager for all animations (Lottie, Lordicon, dotLottie, Rive).
 * Activities and Fragments use this as their single entry point.
 */
public class AnimationManager {

    // --- Lottie & dotLottie ---

    public static void playLottie(LottieAnimationView view, String fileName) {
        LottieHelper.loadAndPlay(view, fileName);
    }

    public static void playLottie(LottieAnimationView view, int rawResId) {
        LottieHelper.loadAndPlay(view, rawResId);
    }

    public static void pauseLottie(LottieAnimationView view) {
        LottieHelper.pause(view);
    }

    public static void restartLottie(LottieAnimationView view) {
        LottieHelper.restart(view);
    }

    public static void stopLottie(LottieAnimationView view) {
        LottieHelper.stop(view);
    }

    public static void setLottieLoop(LottieAnimationView view, boolean loop) {
        LottieHelper.setLoop(view, loop);
    }

    public static void setLottieSpeed(LottieAnimationView view, float speed) {
        LottieHelper.setSpeed(view, speed);
    }

    public static void setLottieCompletionListener(LottieAnimationView view, Runnable onComplete) {
        LottieHelper.setCompletionListener(view, onComplete);
    }

    // --- dotLottie ---

    public static void playDotLottie(LottieAnimationView view, String fileName) {
        LottieHelper.loadAndPlay(view, fileName);
    }

    public static void playDotLottie(LottieAnimationView view, int rawResId) {
        LottieHelper.loadAndPlay(view, rawResId);
    }

    // --- Lordicon ---

    public static void playLordicon(LottieAnimationView view, String jsonPath) {
        LordiconHelper.play(view, jsonPath);
    }

    public static void playLordicon(LottieAnimationView view, int rawResId) {
        LordiconHelper.play(view, rawResId);
    }

    public static void restartLordicon(LottieAnimationView view) {
        LordiconHelper.restart(view);
    }

    public static void stopLordicon(LottieAnimationView view) {
        LordiconHelper.stop(view);
    }

    // --- Rive ---

    public static void playRive(RiveAnimationView view, int rawResId) {
        RiveHelper.loadAndPlay(view, rawResId);
    }

    public static void playRive(RiveAnimationView view) {
        RiveHelper.play(view);
    }

    public static void pauseRive(RiveAnimationView view) {
        RiveHelper.pause(view);
    }

    public static void stopRive(RiveAnimationView view) {
        RiveHelper.stop(view);
    }

    public static void resetRive(RiveAnimationView view) {
        RiveHelper.reset(view);
    }

    public static void setRiveBoolean(RiveAnimationView view, String stateMachine, String input, boolean value) {
        RiveHelper.setBooleanInput(view, stateMachine, input, value);
    }

    public static void setRiveNumber(RiveAnimationView view, String stateMachine, String input, float value) {
        RiveHelper.setNumberInput(view, stateMachine, input, value);
    }

    public static void triggerRive(RiveAnimationView view, String stateMachine, String input) {
        RiveHelper.triggerInput(view, stateMachine, input);
    }
}
