package com.example.graduationproject.ui.profile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom view that redraws <ChildMoodArc/>'s inline SVG: a smooth cubic-bezier
 * line chart with a gradient area fill underneath and a highlighted peak dot,
 * built exactly like the JSX's buildSmoothPath() helper.
 */
public class MoodArcView extends View {

    private static final float VB_W = 320f;
    private static final float VB_H = 110f;
    private static final float PAD = 14f;
    private static final float MIN_VAL = 1f;
    private static final float MAX_VAL = 8f;

    private int[] mood = new int[0];
    private int color = Color.BLUE;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MoodArcView(Context context) { super(context); }
    public MoodArcView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }

    public void setData(int[] mood, int color) {
        this.mood = mood;
        this.color = color;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width * (VB_H / VB_W)); // preserves the SVG's aspect ratio
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mood.length < 2 || getWidth() == 0) return;

        float scale = getWidth() / VB_W;
        canvas.save();
        canvas.scale(scale, scale);

        boolean isRtl = getLayoutDirection() == LAYOUT_DIRECTION_RTL;

        List<PointF> points = new ArrayList<>();
        float step = (VB_W - PAD * 2) / (mood.length - 1);
        int peakIdx = 0;
        for (int i = 0; i < mood.length; i++) {
            if (mood[i] > mood[peakIdx]) peakIdx = i;
            // In RTL, index 0 (Saturday) should be on the right
            float x;
            if (isRtl) {
                x = VB_W - (PAD + i * step);
            } else {
                x = PAD + i * step;
            }
            float y = PAD + (1 - (mood[i] - MIN_VAL) / (MAX_VAL - MIN_VAL)) * (VB_H - PAD * 2);
            points.add(new PointF(x, y));
        }

        Path linePath = buildSmoothPath(points, isRtl);

        Path areaPath = new Path(linePath);
        if (isRtl) {
            areaPath.lineTo(points.get(points.size() - 1).x, VB_H);
            areaPath.lineTo(points.get(0).x, VB_H);
        } else {
            areaPath.lineTo(points.get(points.size() - 1).x, VB_H);
            areaPath.lineTo(points.get(0).x, VB_H);
        }
        areaPath.close();

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, VB_H,
                withAlpha(color, 0.3f), withAlpha(color, 0f), Shader.TileMode.CLAMP));
        canvas.drawPath(areaPath, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(color);
        canvas.drawPath(linePath, paint);

        int sand = Color.parseColor("#D9A85C");
        for (int i = 0; i < points.size(); i++) {
            PointF p = points.get(i);
            boolean isPeak = i == peakIdx;
            float r = isPeak ? 6f : 4f;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(isPeak ? sand : color);
            canvas.drawCircle(p.x, p.y, r, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2f);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(p.x, p.y, r, paint);
        }

        canvas.restore();
    }

    /** Direct port of buildSmoothPath(points) from the JSX. */
    private Path buildSmoothPath(List<PointF> points, boolean isRtl) {
        Path path = new Path();
        path.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 0; i < points.size() - 1; i++) {
            PointF p0 = points.get(i);
            PointF p1 = points.get(i + 1);
            float midX = (p0.x + p1.x) / 2f;
            path.cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y);
        }
        return path;
    }

    private static int withAlpha(int color, float alpha0to1) {
        int a = Math.round(alpha0to1 * 255);
        return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color));
    }
}
