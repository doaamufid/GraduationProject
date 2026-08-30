package com.example.graduationproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Draws the teddy bear character in one of 7 mood expressions.
 * Direct port of the Bear() function from KidsMoodScreen.jsx — every
 * coordinate reuses the same s*fraction formulas as the original SVG.
 */
public class KidsMoodBearView extends View {

    private String type = "neutral";

    private static final int LINE_COLOR = 0xFF3B2A1E;
    private static final int EAR_FILL = 0xFFB8875A;
    private static final int SNOUT_FILL = 0xFFF3E1C8;
    private static final int HEAD_FILL = 0xFFC99A6B;
    private static final int TEAR_COLOR = 0xFF8FC6F0;
    private static final int BLUSH_COLOR = 0xFFFF9FAE;

    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();

    public KidsMoodBearView(Context context) {
        super(context);
        init();
    }

    public KidsMoodBearView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    public void setMoodType(String type) {
        this.type = type;
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
        canvas.translate((getWidth() - s) / 2f, (getHeight() - s) / 2f);
        strokePaint.setStrokeWidth(s * 0.05f);
        strokePaint.setColor(LINE_COLOR);

        // ears
        fillPaint.setColor(EAR_FILL);
        fillPaint.setAlpha(255);
        canvas.drawCircle(x(s, 0.2f), y(s, 0.2f), s * 0.15f, fillPaint);
        canvas.drawCircle(x(s, 0.8f), y(s, 0.2f), s * 0.15f, fillPaint);
        fillPaint.setColor(SNOUT_FILL);
        canvas.drawCircle(x(s, 0.2f), y(s, 0.2f), s * 0.075f, fillPaint);
        canvas.drawCircle(x(s, 0.8f), y(s, 0.2f), s * 0.075f, fillPaint);

        // head
        fillPaint.setColor(HEAD_FILL);
        canvas.drawCircle(x(s, 0.5f), y(s, 0.52f), s * 0.36f, fillPaint);

        // mood-specific extras drawn BEHIND the eyes (tears/blush), same order as source
        drawExtrasBehind(canvas, s);

        // eyes + mouth
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

        // snout + nose (drawn above eyes area, matching source order) + mouth already drawn per-case
        fillPaint.setColor(SNOUT_FILL);
        canvas.drawOval(x(s, 0.5f) - s * 0.17f, y(s, 0.62f) - s * 0.13f,
                x(s, 0.5f) + s * 0.17f, y(s, 0.62f) + s * 0.13f, fillPaint);
        fillPaint.setColor(LINE_COLOR);
        canvas.drawOval(x(s, 0.5f) - s * 0.045f, y(s, 0.53f) - s * 0.035f,
                x(s, 0.5f) + s * 0.045f, y(s, 0.53f) + s * 0.035f, fillPaint);

        drawMouth(canvas, s);
        canvas.restore();
    }

    private float x(float s, float f) { return s * f; }
    private float y(float s, float f) { return s * f; }

    private String mouthType; // set by the eyes-drawing methods, consumed by drawMouth()
    private void drawAwful(Canvas canvas, float s) {
        path.reset();
        path.moveTo(x(s, 0.32f), y(s, 0.38f)); path.lineTo(x(s, 0.42f), y(s, 0.46f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.42f), y(s, 0.38f)); path.lineTo(x(s, 0.32f), y(s, 0.46f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.58f), y(s, 0.38f)); path.lineTo(x(s, 0.68f), y(s, 0.46f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.68f), y(s, 0.38f)); path.lineTo(x(s, 0.58f), y(s, 0.46f));
        canvas.drawPath(path, strokePaint);
        mouthType = "awful";
    }

    private void drawSad(Canvas canvas, float s) {
        path.reset();
        path.moveTo(x(s, 0.34f), y(s, 0.44f));
        path.quadTo(x(s, 0.4f), y(s, 0.38f), x(s, 0.46f), y(s, 0.44f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.54f), y(s, 0.44f));
        path.quadTo(x(s, 0.6f), y(s, 0.38f), x(s, 0.66f), y(s, 0.44f));
        canvas.drawPath(path, strokePaint);
        mouthType = "sad";
    }

    private void drawLow(Canvas canvas, float s) {
        fillPaint.setColor(LINE_COLOR);
        canvas.drawCircle(x(s, 0.4f), y(s, 0.42f), strokePaint.getStrokeWidth() * 0.6f, fillPaint);
        canvas.drawCircle(x(s, 0.6f), y(s, 0.42f), strokePaint.getStrokeWidth() * 0.6f, fillPaint);
        mouthType = "low";
    }

    private void drawNeutral(Canvas canvas, float s) {
        fillPaint.setColor(LINE_COLOR);
        canvas.drawCircle(x(s, 0.4f), y(s, 0.42f), strokePaint.getStrokeWidth() * 0.62f, fillPaint);
        canvas.drawCircle(x(s, 0.6f), y(s, 0.42f), strokePaint.getStrokeWidth() * 0.62f, fillPaint);
        mouthType = "neutral";
    }

    private void drawCalm(Canvas canvas, float s) {
        fillPaint.setColor(LINE_COLOR);
        canvas.drawCircle(x(s, 0.4f), y(s, 0.42f), strokePaint.getStrokeWidth() * 0.65f, fillPaint);
        canvas.drawCircle(x(s, 0.6f), y(s, 0.42f), strokePaint.getStrokeWidth() * 0.65f, fillPaint);
        mouthType = "calm";
    }

    private void drawHappy(Canvas canvas, float s) {
        path.reset();
        path.moveTo(x(s, 0.33f), y(s, 0.44f));
        path.quadTo(x(s, 0.4f), y(s, 0.34f), x(s, 0.47f), y(s, 0.44f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.53f), y(s, 0.44f));
        path.quadTo(x(s, 0.6f), y(s, 0.34f), x(s, 0.67f), y(s, 0.44f));
        canvas.drawPath(path, strokePaint);
        mouthType = "happy";
    }

    private void drawOverjoyed(Canvas canvas, float s) {
        path.reset();
        path.moveTo(x(s, 0.3f), y(s, 0.46f));
        path.quadTo(x(s, 0.4f), y(s, 0.3f), x(s, 0.5f), y(s, 0.46f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s, 0.5f), y(s, 0.46f));
        path.quadTo(x(s, 0.6f), y(s, 0.3f), x(s, 0.7f), y(s, 0.46f));
        canvas.drawPath(path, strokePaint);
        mouthType = "overjoyed";
    }

    private void drawMouth(Canvas canvas, float s) {
        strokePaint.setColor(LINE_COLOR);
        fillPaint.setColor(LINE_COLOR);
        fillPaint.setAlpha(255);
        switch (mouthType) {
            case "awful":
                path.reset();
                path.moveTo(x(s, 0.4f), y(s, 0.74f));
                path.quadTo(x(s, 0.5f), y(s, 0.63f), x(s, 0.6f), y(s, 0.74f));
                path.quadTo(x(s, 0.5f), y(s, 0.7f), x(s, 0.4f), y(s, 0.74f));
                path.close();
                canvas.drawPath(path, fillPaint);
                break;
            case "sad":
                path.reset();
                path.moveTo(x(s, 0.42f), y(s, 0.72f));
                path.quadTo(x(s, 0.5f), y(s, 0.65f), x(s, 0.58f), y(s, 0.72f));
                canvas.drawPath(path, strokePaint);
                break;
            case "low":
                path.reset();
                path.moveTo(x(s, 0.4f), y(s, 0.68f));
                path.quadTo(x(s, 0.5f), y(s, 0.64f), x(s, 0.6f), y(s, 0.68f));
                canvas.drawPath(path, strokePaint);
                break;
            case "neutral":
                canvas.drawLine(x(s, 0.41f), y(s, 0.66f), x(s, 0.59f), y(s, 0.66f), strokePaint);
                break;
            case "calm":
                path.reset();
                path.moveTo(x(s, 0.38f), y(s, 0.64f));
                path.quadTo(x(s, 0.5f), y(s, 0.72f), x(s, 0.62f), y(s, 0.64f));
                canvas.drawPath(path, strokePaint);
                break;
            case "happy":
                path.reset();
                path.moveTo(x(s, 0.33f), y(s, 0.6f));
                path.quadTo(x(s, 0.5f), y(s, 0.8f), x(s, 0.67f), y(s, 0.6f));
                path.quadTo(x(s, 0.5f), y(s, 0.7f), x(s, 0.33f), y(s, 0.6f));
                path.close();
                canvas.drawPath(path, fillPaint);
                break;
            case "overjoyed":
                path.reset();
                path.moveTo(x(s, 0.28f), y(s, 0.58f));
                path.quadTo(x(s, 0.5f), y(s, 0.88f), x(s, 0.72f), y(s, 0.58f));
                path.quadTo(x(s, 0.5f), y(s, 0.68f), x(s, 0.28f), y(s, 0.58f));
                path.close();
                canvas.drawPath(path, fillPaint);
                break;
        }
    }

    /** Tears (sad/awful) and blush (calm/happy/overjoyed) — drawn before the eyes, same as source. */
    private void drawExtrasBehind(Canvas canvas, float s) {
        switch (type) {
            case "awful":
                drawTearBear(canvas, s, x(s, 0.32f));
                drawTearBear(canvas, s, x(s, 0.68f));
                break;
            case "sad":
                drawTearBear(canvas, s, x(s, 0.66f));
                break;
            case "calm":
                drawBlushBear(canvas, s, 0.6f);
                break;
            case "happy":
                drawBlushBear(canvas, s, 0.75f);
                break;
            case "overjoyed":
                drawBlushBear(canvas, s, 0.8f);
                break;
        }
    }

    private void drawTearBear(Canvas canvas, float s, float cx) {
        fillPaint.setColor(TEAR_COLOR);
        fillPaint.setAlpha(255);
        path.reset();
        path.moveTo(cx, y(s, 0.5f));
        path.cubicTo(cx - s * 0.03f, y(s, 0.57f), cx - s * 0.03f, y(s, 0.63f), cx, y(s, 0.67f));
        path.cubicTo(cx + s * 0.03f, y(s, 0.63f), cx + s * 0.03f, y(s, 0.57f), cx, y(s, 0.5f));
        path.close();
        canvas.drawPath(path, fillPaint);
    }

    private void drawBlushBear(Canvas canvas, float s, float opacity) {
        fillPaint.setColor(BLUSH_COLOR);
        fillPaint.setAlpha((int) (opacity * 255));
        canvas.drawOval(x(s, 0.26f) - s * 0.055f, y(s, 0.58f) - s * 0.036f,
                x(s, 0.26f) + s * 0.055f, y(s, 0.58f) + s * 0.036f, fillPaint);
        canvas.drawOval(x(s, 0.74f) - s * 0.055f, y(s, 0.58f) - s * 0.036f,
                x(s, 0.74f) + s * 0.055f, y(s, 0.58f) + s * 0.036f, fillPaint);
    }
}
