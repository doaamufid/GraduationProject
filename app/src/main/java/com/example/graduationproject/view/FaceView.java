package com.example.graduationproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Draws one of the seven mood faces using a Canvas Path.
 * Every coordinate below is a direct port of the original SVG paths
 * (each SVG "d" attribute used fractions of the icon size, e.g. s*0.24 —
 * the same fractions are reused here so the drawing matches pixel-for-pixel).
 *
 * Usage: faceView.setMoodType("happy");
 */
public class FaceView extends View {

    private String type = "neutral";

    private int lineColor = 0xFF26324A;
    private static final int BLUSH_COLOR = 0xFFFF9FAE;
    private static final int TEAR_COLOR = 0xFF7FCBEF;
    private static final int SPARKLE_COLOR = 0xFFFFFFFF;

    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();

    public FaceView(Context context) {
        super(context);
        init();
    }

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setColor(lineColor);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    /** Sets which of the 7 expressions to render and redraws. */
    public void setMoodType(String type) {
        this.type = type;
        invalidate();
    }

    public void setLineColor(int color) {
        this.lineColor = color;
        strokePaint.setColor(color);
        invalidate();
    }

    public String getMoodType() {
        return type;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float s = Math.min(getWidth(), getHeight());
        if (s <= 0) return;

        canvas.save();
        // center the s x s drawing inside the view if the view isn't square
        canvas.translate((getWidth() - s) / 2f, (getHeight() - s) / 2f);

        strokePaint.setStrokeWidth(s * 0.05f);

        switch (type) {
            case "awful":     drawAwful(canvas, s); break;
            case "sad":       drawSad(canvas, s); break;
            case "low":       drawLow(canvas, s); break;
            case "neutral":   drawNeutral(canvas, s); break;
            case "calm":      drawCalm(canvas, s); break;
            case "happy":     drawHappy(canvas, s); break;
            case "overjoyed": drawOverjoyed(canvas, s); break;
            default:          drawNeutral(canvas, s); break;
        }
        canvas.restore();
    }

    private float x(float s, float f) { return s * f; }
    private float y(float s, float f) { return s * f; }

    // ---- shared decorations -------------------------------------------------

    private void drawTear(Canvas canvas, float s, float cx) {
        fillPaint.setColor(TEAR_COLOR);
        fillPaint.setAlpha((int) (0.9f * 255));
        path.reset();
        path.moveTo(cx, y(s, 0.5f));
        path.cubicTo(cx - s * 0.045f, y(s, 0.58f), cx - s * 0.045f, y(s, 0.66f), cx, y(s, 0.7f));
        path.cubicTo(cx + s * 0.045f, y(s, 0.66f), cx + s * 0.045f, y(s, 0.58f), cx, y(s, 0.5f));
        path.close();
        canvas.drawPath(path, fillPaint);
    }

    private void drawBlush(Canvas canvas, float s, float opacity) {
        fillPaint.setColor(BLUSH_COLOR);
        fillPaint.setAlpha((int) (0.55f * opacity * 255));
        canvas.drawOval(x(s, 0.24f) - s * 0.07f, y(s, 0.58f) - s * 0.045f,
                x(s, 0.24f) + s * 0.07f, y(s, 0.58f) + s * 0.045f, fillPaint);
        canvas.drawOval(x(s, 0.76f) - s * 0.07f, y(s, 0.58f) - s * 0.045f,
                x(s, 0.76f) + s * 0.07f, y(s, 0.58f) + s * 0.045f, fillPaint);
    }

    private void drawSparkle(Canvas canvas, float s, float cx, float cy, float r) {
        fillPaint.setColor(SPARKLE_COLOR);
        fillPaint.setAlpha((int) (0.9f * 255));
        path.reset();
        path.moveTo(cx, cy - r);
        path.lineTo(cx + r * 0.28f, cy - r * 0.28f);
        path.lineTo(cx + r, cy);
        path.lineTo(cx + r * 0.28f, cy + r * 0.28f);
        path.lineTo(cx, cy + r);
        path.lineTo(cx - r * 0.28f, cy + r * 0.28f);
        path.lineTo(cx - r, cy);
        path.lineTo(cx - r * 0.28f, cy - r * 0.28f);
        path.close();
        canvas.drawPath(path, fillPaint);
    }

    // ---- 7 mood expressions ---------------------------------------------

    private void drawAwful(Canvas canvas, float s) {
        strokePaint.setColor(lineColor);
        strokePaint.setAlpha(255);
        // eyes: two X marks
        path.reset();
        path.moveTo(x(s, 0.24f), y(s, 0.34f)); path.lineTo(x(s, 0.36f), y(s, 0.44f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.36f), y(s, 0.34f)); path.lineTo(x(s, 0.24f), y(s, 0.44f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.64f), y(s, 0.34f)); path.lineTo(x(s, 0.76f), y(s, 0.44f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.76f), y(s, 0.34f)); path.lineTo(x(s, 0.64f), y(s, 0.44f));
        canvas.drawPath(path, strokePaint);

        // mouth: filled wide open frown
        fillPaint.setColor(lineColor);
        fillPaint.setAlpha(255);
        path.reset();
        path.moveTo(x(s, 0.28f), y(s, 0.72f));
        path.quadTo(x(s, 0.5f), y(s, 0.55f), x(s, 0.72f), y(s, 0.72f));
        path.quadTo(x(s, 0.5f), y(s, 0.68f), x(s, 0.28f), y(s, 0.72f));
        path.close();
        canvas.drawPath(path, fillPaint);

        drawTear(canvas, s, x(s, 0.3f));
        drawTear(canvas, s, x(s, 0.7f));
    }

    private void drawSad(Canvas canvas, float s) {
        strokePaint.setColor(lineColor);
        strokePaint.setAlpha(255);
        path.reset();
        path.moveTo(x(s, 0.23f), y(s, 0.42f));
        path.quadTo(x(s, 0.3f), y(s, 0.34f), x(s, 0.37f), y(s, 0.4f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.63f), y(s, 0.4f));
        path.quadTo(x(s, 0.7f), y(s, 0.34f), x(s, 0.77f), y(s, 0.42f));
        canvas.drawPath(path, strokePaint);

        path.reset();
        path.moveTo(x(s, 0.3f), y(s, 0.7f));
        path.quadTo(x(s, 0.5f), y(s, 0.58f), x(s, 0.7f), y(s, 0.7f));
        canvas.drawPath(path, strokePaint);

        drawTear(canvas, s, x(s, 0.72f));
    }

    private void drawLow(Canvas canvas, float s) {
        strokePaint.setColor(lineColor);
        strokePaint.setAlpha(255);
        path.reset();
        path.moveTo(x(s, 0.24f), y(s, 0.38f));
        path.quadTo(x(s, 0.3f), y(s, 0.42f), x(s, 0.36f), y(s, 0.4f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.64f), y(s, 0.4f));
        path.quadTo(x(s, 0.7f), y(s, 0.42f), x(s, 0.76f), y(s, 0.38f));
        canvas.drawPath(path, strokePaint);

        path.reset();
        path.moveTo(x(s, 0.32f), y(s, 0.66f));
        path.quadTo(x(s, 0.5f), y(s, 0.6f), x(s, 0.68f), y(s, 0.66f));
        canvas.drawPath(path, strokePaint);
    }

    private void drawNeutral(Canvas canvas, float s) {
        fillPaint.setColor(lineColor);
        fillPaint.setAlpha(255);
        float r = strokePaint.getStrokeWidth() * 0.55f;
        canvas.drawCircle(x(s, 0.3f), y(s, 0.4f), r, fillPaint);
        canvas.drawCircle(x(s, 0.7f), y(s, 0.4f), r, fillPaint);

        strokePaint.setColor(lineColor);
        strokePaint.setAlpha(255);
        canvas.drawLine(x(s, 0.32f), y(s, 0.63f), x(s, 0.68f), y(s, 0.63f), strokePaint);
    }

    private void drawCalm(Canvas canvas, float s) {
        strokePaint.setColor(lineColor);
        strokePaint.setAlpha(255);
        path.reset();
        path.moveTo(x(s, 0.23f), y(s, 0.4f));
        path.quadTo(x(s, 0.3f), y(s, 0.34f), x(s, 0.37f), y(s, 0.4f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.63f), y(s, 0.4f));
        path.quadTo(x(s, 0.7f), y(s, 0.34f), x(s, 0.77f), y(s, 0.4f));
        canvas.drawPath(path, strokePaint);

        path.reset();
        path.moveTo(x(s, 0.3f), y(s, 0.6f));
        path.quadTo(x(s, 0.5f), y(s, 0.7f), x(s, 0.7f), y(s, 0.6f));
        canvas.drawPath(path, strokePaint);

        drawBlush(canvas, s, 0.6f);
    }

    private void drawHappy(Canvas canvas, float s) {
        strokePaint.setColor(lineColor);
        strokePaint.setAlpha(255);
        path.reset();
        path.moveTo(x(s, 0.22f), y(s, 0.42f));
        path.quadTo(x(s, 0.3f), y(s, 0.32f), x(s, 0.38f), y(s, 0.42f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.62f), y(s, 0.42f));
        path.quadTo(x(s, 0.7f), y(s, 0.32f), x(s, 0.78f), y(s, 0.42f));
        canvas.drawPath(path, strokePaint);

        fillPaint.setColor(lineColor);
        fillPaint.setAlpha(255);
        path.reset();
        path.moveTo(x(s, 0.26f), y(s, 0.56f));
        path.quadTo(x(s, 0.5f), y(s, 0.78f), x(s, 0.74f), y(s, 0.56f));
        path.quadTo(x(s, 0.5f), y(s, 0.68f), x(s, 0.26f), y(s, 0.56f));
        path.close();
        canvas.drawPath(path, fillPaint);

        drawBlush(canvas, s, 1f);
    }

    private void drawOverjoyed(Canvas canvas, float s) {
        strokePaint.setColor(lineColor);
        strokePaint.setAlpha(255);
        path.reset();
        path.moveTo(x(s, 0.2f), y(s, 0.44f));
        path.quadTo(x(s, 0.3f), y(s, 0.3f), x(s, 0.4f), y(s, 0.44f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.6f), y(s, 0.44f));
        path.quadTo(x(s, 0.7f), y(s, 0.3f), x(s, 0.8f), y(s, 0.44f));
        canvas.drawPath(path, strokePaint);

        fillPaint.setColor(lineColor);
        fillPaint.setAlpha(255);
        path.reset();
        path.moveTo(x(s, 0.22f), y(s, 0.55f));
        path.quadTo(x(s, 0.5f), y(s, 0.85f), x(s, 0.78f), y(s, 0.55f));
        path.quadTo(x(s, 0.5f), y(s, 0.66f), x(s, 0.22f), y(s, 0.55f));
        path.close();
        canvas.drawPath(path, fillPaint);

        drawBlush(canvas, s, 1f);
        drawSparkle(canvas, s, x(s, 0.86f), y(s, 0.18f), s * 0.05f);
        drawSparkle(canvas, s, x(s, 0.1f), y(s, 0.28f), s * 0.035f);
    }
}
