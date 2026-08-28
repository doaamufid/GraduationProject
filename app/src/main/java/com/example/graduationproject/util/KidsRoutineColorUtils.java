package com.example.graduationproject.util;

import android.graphics.Color;

import com.example.graduationproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Direct port of the JS helpers:
 *   lerp(), lerpRGB(), rgbStr(), SKY_STOPS, getSky(progress), skyMarker(progress), greeting(progress,total)
 */
public class KidsRoutineColorUtils {

    /** One stop of the 4-point sky gradient: progress (0..1), top color, bottom color. */
    public static class SkyStop {
        final float p;
        final int[] top;
        final int[] bottom;

        SkyStop(float p, int[] top, int[] bottom) {
            this.p = p;
            this.top = top;
            this.bottom = bottom;
        }
    }

    public static final List<SkyStop> SKY_STOPS = new ArrayList<>();
    static {
        SKY_STOPS.add(new SkyStop(0f,    new int[]{255, 227, 179}, new int[]{255, 193, 166}));
        SKY_STOPS.add(new SkyStop(0.34f, new int[]{191, 230, 255}, new int[]{234, 248, 255}));
        SKY_STOPS.add(new SkyStop(0.67f, new int[]{201, 166, 255}, new int[]{139, 123, 184}));
        SKY_STOPS.add(new SkyStop(1f,    new int[]{43,  36,  86},  new int[]{18,  14,  42}));
    }

    public static class SkyColors {
        public final int top;
        public final int bottom;
        SkyColors(int top, int bottom) { this.top = top; this.bottom = bottom; }
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static int[] lerpRgb(int[] a, int[] b, float t) {
        return new int[]{
                Math.round(lerp(a[0], b[0], t)),
                Math.round(lerp(a[1], b[1], t)),
                Math.round(lerp(a[2], b[2], t))
        };
    }

    private static int toColorInt(int[] rgb) {
        return Color.rgb(
                clamp255(rgb[0]),
                clamp255(rgb[1]),
                clamp255(rgb[2])
        );
    }

    private static int clamp255(int v) {
        return Math.max(0, Math.min(255, v));
    }

    /** Direct port of getSky(progress) from the React component. */
    public static SkyColors getSky(float progress) {
        SkyStop lo = SKY_STOPS.get(0);
        SkyStop hi = SKY_STOPS.get(SKY_STOPS.size() - 1);
        for (int i = 0; i < SKY_STOPS.size() - 1; i++) {
            SkyStop a = SKY_STOPS.get(i);
            SkyStop b = SKY_STOPS.get(i + 1);
            if (progress >= a.p && progress <= b.p) {
                lo = a;
                hi = b;
                break;
            }
        }
        float span = (hi.p - lo.p) == 0 ? 1f : (hi.p - lo.p);
        float t = (progress - lo.p) / span;
        int[] top = lerpRgb(lo.top, hi.top, t);
        int[] bottom = lerpRgb(lo.bottom, hi.bottom, t);
        return new SkyColors(toColorInt(top), toColorInt(bottom));
    }

    /** Direct port of skyMarker(progress) — emoji shown as the moving marker on the arc. */
    public static String skyMarker(float progress) {
        if (progress < 0.15f) return "☀️";
        if (progress < 0.4f) return "🌤️";
        if (progress < 0.7f) return "⛅";
        if (progress < 0.95f) return "🌆";
        return "🌙";
    }

    /** Direct port of greeting(progress, total). */
    public static String greeting(float progress, int total, android.content.Context ctx) {
        if (total == 0) return ctx.getString(R.string.kids_routine_greet_empty);
        if (progress == 0f) return ctx.getString(R.string.kids_routine_greet_start);
        if (progress < 0.5f) return ctx.getString(R.string.kids_routine_greet_mid);
        if (progress < 1f) return ctx.getString(R.string.kids_routine_greet_half);
        return ctx.getString(R.string.kids_routine_greet_done);
    }
}
