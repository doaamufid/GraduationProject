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
        // Alternating X to distribute nodes around the road
        points.add(new PointF(CENTER_X_DP, 40f));   // 0: Today (Top)
        points.add(new PointF(250f, 130f));         // 1: Week (Right)
        points.add(new PointF(70f, 225f));          // 2: Month (Left)
        points.add(new PointF(250f, 320f));         // 3: 3 Months (Right)
        points.add(new PointF(70f, 415f));          // 4: Year (Left)
        points.add(new PointF(CENTER_X_DP, 530f));  // 5: Send Button (Bottom)
        return points;
    }

    /** Total height of the road view itself. */
    public static float totalHeightDp(int count) {
        return 600f;
    }
}
