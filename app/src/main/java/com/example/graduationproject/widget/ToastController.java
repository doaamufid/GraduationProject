package com.example.graduationproject.widget;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.graduationproject.R;

/**
 * Equivalent of the inline toast rendered at the bottom of the screen:
 *   {toast && <div className="... toast-in" style={{ background: T.primaryDark }}>{toast}</div>}
 *   setTimeout(() => setToast(null), 2600)
 *
 * Builds and inserts a self-dismissing message bubble into a host
 * FrameLayout, matching the 300ms fade-in entrance and the 2600ms
 * auto-dismiss timer from the original.
 */
public final class ToastController {

    private static final long AUTO_DISMISS_MS = 2600;

    private final FrameLayout host;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable dismissRunnable;
    private TextView currentToastView;

    public ToastController(FrameLayout host) {
        this.host = host;
    }

    public void show(String message) {
        clear();

        TextView toast = new TextView(host.getContext());
        toast.setText(message);
        toast.setTextColor(host.getResources().getColor(R.color.white));
        toast.setTextSize(12);
        toast.setTypeface(toast.getTypeface(), android.graphics.Typeface.BOLD);
        toast.setGravity(Gravity.CENTER);
        toast.setBackgroundResource(R.drawable.bg_toast);

        float density = host.getResources().getDisplayMetrics().density;
        int padH = (int) (16 * density);
        int padV = (int) (12 * density);
        toast.setPadding(padH, padV, padH, padV);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        int sideMargin = (int) (24 * density);
        lp.leftMargin = sideMargin;
        lp.rightMargin = sideMargin;
        lp.bottomMargin = (int) (24 * density);
        toast.setLayoutParams(lp);

        host.addView(toast);
        currentToastView = toast;
        FadeUtils.toastIn(toast);

        dismissRunnable = this::clear;
        handler.postDelayed(dismissRunnable, AUTO_DISMISS_MS);
    }

    public void clear() {
        if (dismissRunnable != null) {
            handler.removeCallbacks(dismissRunnable);
        }
        if (currentToastView != null) {
            host.removeView(currentToastView);
            currentToastView = null;
        }
    }
}
