package com.example.graduationproject.widget;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Equivalent of the `points` / `pathD` / `svgHeight` calculations in the
 * root component. Shared between {@link CurvedTimelineView} (which draws
 * the connecting path) and the Activity (which positions the node
 * buttons at the exact same coordinates) so both stay perfectly in sync.
 *
 * All values are in dp, matching the original's raw pixel numbers 1:1
 * (165, 45, 60, 130, 120) against the original 320dp-wide mockup frame.
 */
public final class TimelineGeometry {

    public static final float CENTER_X_DP = 165f;
    public static final float OFFSET_X_DP = 45f;
    public static final float START_Y_DP = 60f;
    public static final float STEP_Y_DP = 130f;
    public static final float BASE_HEIGHT_DP = 120f;

    private TimelineGeometry() {
    }

    /** Equivalent of `messages.map((m, i) => ({ x: 165 + (i%2===0?-45:45), y: 60 + i*130 }))`. */
    public static List<PointF> computePoints(int count) {
        List<PointF> points = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            float x = CENTER_X_DP + (i % 2 == 0 ? -OFFSET_X_DP : OFFSET_X_DP);
            float y = START_Y_DP + i * STEP_Y_DP;
            points.add(new PointF(x, y));
        }
        return points;
    }

    /** Equivalent of `svgHeight = 120 + messages.length * 130`. */
    public static float totalHeightDp(int count) {
        return BASE_HEIGHT_DP + count * STEP_Y_DP;
    }
}
