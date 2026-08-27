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

    public static final float CENTER_X_DP = 160f;

    private TimelineGeometry() {
    }

    /** Returns fixed points scaled to fit within a typical screen height. */
    public static List<PointF> computePoints(int count) {
        List<PointF> points = new ArrayList<>();
        // Zigzag matching the organic flow in the image
        points.add(new PointF(240f, 60f));   // 0: Milestone 1 (Top Right)
        points.add(new PointF(100f, 180f));  // 1: Milestone 2 (Middle Left)
        points.add(new PointF(260f, 300f));  // 2: Milestone 3 (Middle Right)
        points.add(new PointF(80f, 420f));   // 3: Milestone 4 (Bottom Left)
        points.add(new PointF(200f, 550f));  // 4: Start Button (Bottom Right-ish)
        return points;
    }

    /** Total height of the road view itself. */
    public static float totalHeightDp(int count) {
        return 650f;
    }
}
