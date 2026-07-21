package com.example.graduationproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.graduationproject.R;

import java.util.Collections;
import java.util.List;

/**
 * Custom View equivalent of the original inline &lt;svg&gt;:
 *
 *   <path d={pathD} stroke={T.path} strokeWidth="34" opacity="0.55" strokeLinecap="round" />
 *   <path d={pathD} stroke={T.pathDark} strokeWidth="2" strokeDasharray="1 10" strokeLinecap="round" />
 *
 * Draws a smooth cubic-bezier "river" connecting each message node,
 * using the exact same control-point construction as the original
 * `pathD` reducer (control points at the midpoint Y between consecutive
 * points, which produces the same S-curve zigzag).
 */
public class CurvedTimelineView extends View {

    private final Paint thickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dashedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<PointF> pointsDp = Collections.emptyList();
    private float density;

    public CurvedTimelineView(Context context) {
        super(context);
        init(context);
    }

    public CurvedTimelineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        density = context.getResources().getDisplayMetrics().density;

        thickPaint.setStyle(Paint.Style.STROKE);
        thickPaint.setStrokeWidth(34 * density);
        thickPaint.setStrokeCap(Paint.Cap.ROUND);
        thickPaint.setColor(context.getResources().getColor(R.color.path_color));
        thickPaint.setAlpha(255); // Full opacity for the river color, as it's already light

        dashedPaint.setStyle(Paint.Style.STROKE);
        dashedPaint.setStrokeWidth(2 * density);
        dashedPaint.setStrokeCap(Paint.Cap.ROUND);
        dashedPaint.setColor(context.getResources().getColor(R.color.path_dark));
        dashedPaint.setPathEffect(new DashPathEffect(new float[]{1 * density, 10 * density}, 0));
    }

    /** Points in dp, matching {@link TimelineGeometry#computePoints(int)}. */
    public void setPoints(List<PointF> pointsDp) {
        this.pointsDp = pointsDp;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pointsDp.size() < 2) return;

        Path path = buildPath();
        canvas.drawPath(path, thickPaint);
        canvas.drawPath(path, dashedPaint);
    }

    /** Same construction as the original `pathD` array-reduce. */
    private Path buildPath() {
        Path path = new Path();
        PointF first = toPx(pointsDp.get(0));
        path.moveTo(first.x, first.y);

        for (int i = 1; i < pointsDp.size(); i++) {
            PointF prev = toPx(pointsDp.get(i - 1));
            PointF curr = toPx(pointsDp.get(i));
            float midY = (prev.y + curr.y) / 2f;
            path.cubicTo(prev.x, midY, curr.x, midY, curr.x, curr.y);
        }
        return path;
    }

    private PointF toPx(PointF dp) {
        return new PointF(dp.x * density, dp.y * density);
    }
}
