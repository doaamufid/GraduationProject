package com.example.graduationproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Port of the chart from MoodStatsScreen.jsx:
 *  - smooth line through the data points (Catmull-Rom -> cubic Bezier,
 *    identical maths to the JS smoothPath() helper)
 *  - soft gradient area fill under the line
 *  - drag/scrub anywhere on the chart to move a dashed crosshair to the
 *    nearest data point (mirrors the SVG pointer-events version exactly)
 *  - releasing the finger reverts to showing the last (most recent) point
 */
public class AdultChartView extends View {

    public interface OnIndexChangeListener {
        void onIndexChanged(int shownIndex, boolean isScrubbing);
    }

    private float[] scores = new float[0];
    private String[] labels = new String[0];
    private PointF[] points = new PointF[0];
    private int activeIndex = -1; // -1 = not scrubbing -> show last point
    private int highlightColor = Color.parseColor("#0EA5E9");
    private int dotHighlightColor = Color.parseColor("#0EA5E9");

    private OnIndexChangeListener listener;

    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint areaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path linePath = new Path();
    private final Path areaPath = new Path();

    private float padTop, padBottom, padX;

    public AdultChartView(Context context) { super(context); init(); }
    public AdultChartView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        padTop = 22 * density;
        padBottom = 22 * density;
        padX = 10 * density;

        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3 * density);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setColor(highlightColor);

        areaPaint.setStyle(Paint.Style.FILL);

        dotPaint.setStyle(Paint.Style.FILL);
        dotStrokePaint.setStyle(Paint.Style.STROKE);
        dotStrokePaint.setColor(Color.WHITE);
        dotStrokePaint.setStrokeWidth(2 * density);

        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeWidth(1.5f * density);
        dashPaint.setColor(Color.parseColor("#8598AC"));
        dashPaint.setPathEffect(new DashPathEffect(new float[]{6, 6}, 0));

        labelPaint.setColor(Color.parseColor("#8598AC"));
        labelPaint.setTextSize(11 * density);
        labelPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setListener(OnIndexChangeListener l) {
        this.listener = l;
    }

    /** Colour of the smooth line + area fill (the app's accent colour). */
    public void setHighlightColor(int color) {
        this.highlightColor = color;
        linePaint.setColor(color);
        recomputePoints();
        invalidate();
    }

    /** Colour of the currently-highlighted dot (changes with the scrubbed mood). */
    public void setDotHighlightColor(int color) {
        this.dotHighlightColor = color;
        invalidate();
    }

    /** Replaces the dataset (called when Day / Week / Month tab changes). */
    public void setData(float[] scores, String[] labels) {
        this.scores = scores;
        this.labels = labels;
        activeIndex = -1;
        recomputePoints();
        invalidate();
        notifyListener();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        recomputePoints();
    }

    private void recomputePoints() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0 || scores.length == 0) {
            points = new PointF[0];
            return;
        }
        points = new PointF[scores.length];
        float usableW = w - padX * 2;
        float usableH = h - padTop - padBottom;
        float step = scores.length > 1 ? usableW / (scores.length - 1) : 0;

        for (int i = 0; i < scores.length; i++) {
            float px = padX + i * step;
            float py = padTop + usableH - ((scores[i] - 1f) / 4f) * usableH;
            points[i] = new PointF(px, py);
        }

        areaPaint.setShader(new LinearGradient(0, padTop, 0, h - padBottom,
                withAlpha(highlightColor, 46), withAlpha(highlightColor, 0), Shader.TileMode.CLAMP));
    }

    private int withAlpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (points.length < 2) return;

        buildSmoothPath();

        canvas.drawPath(areaPath, areaPaint);
        canvas.drawPath(linePath, linePaint);

        int shown = shownIndex();

        if (activeIndex >= 0) {
            PointF p = points[activeIndex];
            canvas.drawLine(p.x, padTop - 8, p.x, getHeight() - padBottom + 6, dashPaint);
        }

        for (int i = 0; i < points.length; i++) {
            PointF p = points[i];
            boolean isShown = (i == shown);
            float r = isShown ? 7f : 4f;
            dotPaint.setColor(isShown ? dotHighlightColor : highlightColor);
            canvas.drawCircle(p.x, p.y, r, dotPaint);
            canvas.drawCircle(p.x, p.y, r, dotStrokePaint);
        }

        for (int i = 0; i < points.length && i < labels.length; i++) {
            canvas.drawText(labels[i], points[i].x, getHeight() - 6, labelPaint);
        }
    }

    /** Catmull-Rom -> cubic Bezier smoothing, identical to the JS smoothPath() helper. */
    private void buildSmoothPath() {
        linePath.reset();
        linePath.moveTo(points[0].x, points[0].y);
        for (int i = 0; i < points.length - 1; i++) {
            PointF p0 = points[i == 0 ? i : i - 1];
            PointF p1 = points[i];
            PointF p2 = points[i + 1];
            PointF p3 = points[i + 2 < points.length ? i + 2 : i + 1];

            float c1x = p1.x + (p2.x - p0.x) / 6f;
            float c1y = p1.y + (p2.y - p0.y) / 6f;
            float c2x = p2.x - (p3.x - p1.x) / 6f;
            float c2y = p2.y - (p3.y - p1.y) / 6f;
            linePath.cubicTo(c1x, c1y, c2x, c2y, p2.x, p2.y);
        }

        areaPath.set(linePath);
        areaPath.lineTo(points[points.length - 1].x, getHeight() - padBottom + 10);
        areaPath.lineTo(points[0].x, getHeight() - padBottom + 10);
        areaPath.close();
    }

    private int shownIndex() {
        return activeIndex >= 0 ? activeIndex : points.length - 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (points.length == 0) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);
                updateNearest(event.getX());
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(false);
                activeIndex = -1;
                invalidate();
                notifyListener();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void updateNearest(float touchX) {
        int nearest = 0;
        float minDist = Float.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            float d = Math.abs(points[i].x - touchX);
            if (d < minDist) { minDist = d; nearest = i; }
        }
        if (nearest != activeIndex) {
            activeIndex = nearest;
            invalidate();
            notifyListener();
        }
    }

    private void notifyListener() {
        if (listener != null && points.length > 0) {
            listener.onIndexChanged(shownIndex(), activeIndex >= 0);
        }
    }
}
