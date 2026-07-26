package com.example.graduationproject.widget;

import android.view.MotionEvent;
import android.view.View;

/**
 * Equivalent of the various `:active { transform: scale(x) }` CSS rules
 * used throughout the original (.icon-tap:active, .check-tap:active at
 * 0.85, .habit-row:active at 0.99). Parameterized by press scale so one
 * helper covers all three.
 */
public final class TapBounce {

    private static final long DURATION_MS = 100;

    private TapBounce() {
    }

    public static void attach(View view, float pressedScale) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(pressedScale).scaleY(pressedScale).setDuration(DURATION_MS).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(DURATION_MS).start();
                    break;
                default:
                    break;
            }
            return false; // let the click listener still fire
        });
    }

    /** Default attach with a standard 0.95 scale. */
    public static void attach(View view) {
        attach(view, 0.95f);
    }
}
