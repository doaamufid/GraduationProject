package com.example.graduationproject.widget;

import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * Equivalent of:
 *   .check-pop { animation: cpop .3s cubic-bezier(.34,1.56,.64,1); }
 *   @keyframes cpop { from { transform:scale(0.3); opacity:0; } to { transform:scale(1); opacity:1; } }
 *
 * OvershootInterpolator reproduces the same "pop past 100% then settle"
 * bounce feel as that cubic-bezier curve.
 */
public final class CheckPopAnimator {

    private CheckPopAnimator() {
    }

    public static void pop(View view) {
        view.setScaleX(0.3f);
        view.setScaleY(0.3f);
        view.setAlpha(0f);
        view.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator(2.2f))
                .start();
    }
}
