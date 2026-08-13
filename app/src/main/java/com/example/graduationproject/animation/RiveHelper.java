package com.example.graduationproject.animation;

import app.rive.runtime.kotlin.RiveAnimationView;
import app.rive.runtime.kotlin.core.Alignment;
import app.rive.runtime.kotlin.core.Direction;
import app.rive.runtime.kotlin.core.Fit;
import app.rive.runtime.kotlin.core.Loop;

/**
 * Internal helper for Rive animations.
 * Uses RiveAnimationView (Android View/XML API).
 * Note: Rive 11.x View API requires explicit parameters in Java 
 * as Kotlin's default arguments are not visible.
 */
class RiveHelper {

    static void loadAndPlay(RiveAnimationView view, int rawResId) {
        if (view == null) return;
        // setRiveResource(resId, artboard, animation, stateMachine, autoplay, autoBind, fit, alignment, loop)
        view.setRiveResource(
            rawResId,
            null,       // artboardName
            null,       // animationName
            null,       // stateMachineName
            true,       // autoplay
            false,      // autoBind
            Fit.CONTAIN,
            Alignment.CENTER,
            Loop.AUTO
        );
    }

    static void play(RiveAnimationView view) {
        if (view != null) {
            view.play(Loop.AUTO, Direction.AUTO, true);
        }
    }

    static void pause(RiveAnimationView view) {
        if (view != null) {
            view.pause(); // pause() usually has no arguments or defaults
        }
    }

    static void stop(RiveAnimationView view) {
        if (view != null) {
            view.stop(); // stop() usually has no arguments or defaults
        }
    }

    static void reset(RiveAnimationView view) {
        if (view != null) {
            view.stop();
            play(view);
        }
    }

    static void setBooleanInput(RiveAnimationView view, String stateMachineName, String inputName, boolean value) {
        if (view != null && stateMachineName != null && inputName != null) {
            view.setBooleanState(stateMachineName, inputName, value);
        }
    }

    static void setNumberInput(RiveAnimationView view, String stateMachineName, String inputName, float value) {
        if (view != null && stateMachineName != null && inputName != null) {
            view.setNumberState(stateMachineName, inputName, value);
        }
    }

    static void triggerInput(RiveAnimationView view, String stateMachineName, String inputName) {
        if (view != null && stateMachineName != null && inputName != null) {
            view.fireState(stateMachineName, inputName);
        }
    }
}
