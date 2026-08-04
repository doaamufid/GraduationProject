package com.example.graduationproject.widget;

import android.os.Handler;
import android.os.Looper;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.graduationproject.R;

/**
 * Equivalent of the original's inline bottom toast:
 *   {toast && <div className="fixed bottom-6 ... toast-in">{toast}</div>}
 *   setTimeout(() => setToast(null), 2600)
 *
 * Manages a single reusable toast TextView anchored to the bottom of a
 * host FrameLayout, fading it in, auto-dismissing it after 2600ms
 * (matching the original's setTimeout), and fading it out.
 */
public final class ToastController {

    private static final long AUTO_DISMISS_MS = 2600;

    private final FrameLayout hostContainer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView currentToast;
    private Runnable dismissRunnable;

    public ToastController(FrameLayout hostContainer) {
        this.hostContainer = hostContainer;
    }

    public void show(String message) {
        // Cancel any pending dismissal from a previous toast.
        if (dismissRunnable != null) {
            handler.removeCallbacks(dismissRunnable);
        }
        if (currentToast != null) {
            hostContainer.removeView(currentToast);
        }

        TextView toast = new TextView(hostContainer.getContext());
        toast.setText(message);
        toast.setTextColor(hostContainer.getResources().getColor(R.color.white));
        toast.setTextSize(12);
        toast.setTypeface(toast.getTypeface(), android.graphics.Typeface.BOLD);
        toast.setGravity(android.view.Gravity.CENTER);
        toast.setBackgroundResource(R.drawable.bg_toast);
        int padH = dp(16), padV = dp(12);
        toast.setPadding(padH, padV, padH, padV);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL;
        lp.setMargins(dp(24), 0, dp(24), dp(24));
        toast.setLayoutParams(lp);

        hostContainer.addView(toast);
        currentToast = toast;

        FadeUtils.toastIn(toast);

        dismissRunnable = () -> {
            if (currentToast != null) {
                FadeUtils.toastOut(currentToast, () -> {
                    hostContainer.removeView(currentToast);
                    currentToast = null;
                });
            }
        };
        handler.postDelayed(dismissRunnable, AUTO_DISMISS_MS);
    }

    public void cancel() {
        if (dismissRunnable != null) {
            handler.removeCallbacks(dismissRunnable);
        }
        if (currentToast != null) {
            hostContainer.removeView(currentToast);
            currentToast = null;
        }
    }

    private int dp(int value) {
        return (int) (value * hostContainer.getResources().getDisplayMetrics().density);
    }
}
