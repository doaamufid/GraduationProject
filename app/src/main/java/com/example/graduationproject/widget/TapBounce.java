package com.example.graduationproject.widget;

import android.view.MotionEvent;
import android.view.View;

/**
 * Equivalent of:
 *   .tap-bounce:active { transform: scale(0.88); }
 *
 * Android has no ":active" pseudo-state, so we reproduce the same
 * "press down, spring back" feel with a plain touch listener that
 * scales the view down on ACTION_DOWN and animates it back to 1.0 on
 * ACTION_UP / ACTION_CANCEL. The view's own OnClickListener still
 * fires normally - this only adds the visual bounce.
 */
public final class TapBounce {

    private static final float PRESSED_SCALE = 0.88f;
    private static final long DURATION_MS = 100;

    private TapBounce() {
    }

    public static void attach(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(PRESSED_SCALE).scaleY(PRESSED_SCALE).setDuration(DURATION_MS).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(DURATION_MS).start();
                    break;
                default:
                    break;
            }
            // Returning false lets the touch event continue to be processed
            // normally, so the existing OnClickListener still fires.
            return false;
        });
    }
}
