package com.example.graduationproject.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

/** Fires a short haptic pulse, mirroring the web app's navigator.vibrate() helper. */
public class AdultChatHapticUtil {
    public static void vibrate(Context ctx) {
        vibrate(ctx, 12);
    }

    public static void vibrate(Context ctx, long ms) {
        try {
            Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
            if (v == null || !v.hasVibrator()) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(ms);
            }
        } catch (Exception ignored) { /* no-op, mirrors the JS try/catch */ }
    }
}
