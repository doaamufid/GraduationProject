package com.example.graduationproject.animation;

import android.util.Log;
import app.rive.runtime.kotlin.RiveAnimationView;
import app.rive.runtime.kotlin.core.Alignment;
import app.rive.runtime.kotlin.core.Direction;
import app.rive.runtime.kotlin.core.Fit;
import app.rive.runtime.kotlin.core.Loop;

class RiveHelper {
    static {
        try {
            System.loadLibrary("rive-android");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }
    static void loadAndPlay(RiveAnimationView view, int rawResId) {
        loadAndPlay(view, rawResId, "State Machine 1");
    }

    static void loadAndPlay(RiveAnimationView view, int rawResId, String stateMachineName) {
        if (view == null) return;
        try {
            view.setRiveResource(
                    rawResId,
                    null,
                    null,
                    stateMachineName,
                    true,
                    false,
                    Fit.CONTAIN,
                    Alignment.CENTER,
                    Loop.AUTO
            );
        } catch (Throwable t) {
            // يمنع الكراش في حال وجود مشكلة Native C++ Library
            Log.e("RiveHelper", "فشل تحميل انيميشن Rive: " + t.getMessage());
        }
    }

    static void play(RiveAnimationView view) {
        if (view != null) {
            try { view.play(Loop.AUTO, Direction.AUTO, true); } catch (Throwable ignored) {}
        }
    }

    static void pause(RiveAnimationView view) {
        if (view != null) {
            try { view.pause(); } catch (Throwable ignored) {}
        }
    }

    static void stop(RiveAnimationView view) {
        if (view != null) {
            try { view.stop(); } catch (Throwable ignored) {}
        }
    }

    static void reset(RiveAnimationView view) {
        if (view != null) {
            stop(view);
            play(view);
        }
    }

    static void setBooleanInput(RiveAnimationView view, String stateMachineName, String inputName, boolean value) {
        if (view != null && stateMachineName != null && inputName != null) {
            try { view.setBooleanState(stateMachineName, inputName, value); } catch (Throwable ignored) {}
        }
    }

    static void setNumberInput(RiveAnimationView view, String stateMachineName, String inputName, float value) {
        if (view != null && stateMachineName != null && inputName != null) {
            try { view.setNumberState(stateMachineName, inputName, value); } catch (Throwable ignored) {}
        }
    }

    static void triggerInput(RiveAnimationView view, String stateMachineName, String inputName) {
        if (view != null && stateMachineName != null && inputName != null) {
            try { view.fireState(stateMachineName, inputName); } catch (Throwable ignored) {}
        }
    }
}