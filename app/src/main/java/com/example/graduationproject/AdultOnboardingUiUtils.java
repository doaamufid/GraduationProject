package com.example.graduationproject;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;

public final class AdultOnboardingUiUtils {
    private AdultOnboardingUiUtils() {}

    public static int dp(Context ctx, float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, ctx.getResources().getDisplayMetrics());
    }

    public static float sp(Context ctx, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, ctx.getResources().getDisplayMetrics());
    }

    /** Cairo is used for headings/emphasis, Tajawal for body text — falls back to system fonts. */
    public static Typeface cairo(boolean bold) {
        return Typeface.create("sans-serif-condensed", bold ? Typeface.BOLD : Typeface.NORMAL);
    }

    public static Typeface tajawal(boolean medium) {
        return Typeface.create("sans-serif", medium ? Typeface.BOLD : Typeface.NORMAL);
    }

    public static int argb(int alpha0to255, int rgbColor) {
        return (alpha0to255 << 24) | (rgbColor & 0x00FFFFFF);
    }
}
