package com.example.graduationproject.util;

import android.graphics.Color;

/**
 * Port of the JS color-blend helpers (dpcLerpColor / dpcMultiLerp / smoothstep)
 * used to animate the sky / background colors based on time of day.
 */
public class ColorUtils {

    public static int lerpColor(int colorA, int colorB, float t) {
        t = clamp01(t);
        int a = Color.alpha(colorA) + Math.round((Color.alpha(colorB) - Color.alpha(colorA)) * t);
        int r = Color.red(colorA) + Math.round((Color.red(colorB) - Color.red(colorA)) * t);
        int g = Color.green(colorA) + Math.round((Color.green(colorB) - Color.green(colorA)) * t);
        int b = Color.blue(colorA) + Math.round((Color.blue(colorB) - Color.blue(colorA)) * t);
        return Color.argb(a, r, g, b);
    }

    /** Multi-stop gradient lerp, mirrors dpcMultiLerp/apcMultiLerp. */
    public static int multiLerp(int[] colors, float[] stops, float t) {
        t = clamp01(t);
        for (int i = 0; i < stops.length - 1; i++) {
            if (t >= stops[i] && t <= stops[i + 1]) {
                float local = (t - stops[i]) / (stops[i + 1] - stops[i]);
                return lerpColor(colors[i], colors[i + 1], local);
            }
        }
        return colors[colors.length - 1];
    }

    public static float smoothstep(float t, float edge0, float edge1) {
        float x = clamp01((t - edge0) / (edge1 - edge0));
        return x * x * (3f - 2f * x);
    }

    public static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    public static int withAlpha(int color, float alpha) {
        int a = Math.round(clamp01(alpha) * 255);
        return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color));
    }
}
