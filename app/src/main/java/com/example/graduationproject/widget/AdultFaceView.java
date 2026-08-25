package com.example.graduationproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Same face renderer used on the main mood screen — ported 1:1 from the
 * React Face() component's SVG path data. Used here for the chart's scrub
 * readout icon.
 */
public class AdultFaceView extends View {

    private String type = "neutral";

    private static final int LINE_COLOR = 0xFF26324A;

    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();

    public AdultFaceView(Context context) { super(context); init(); }
    public AdultFaceView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setColor(LINE_COLOR);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(LINE_COLOR);
    }

    public void setMoodType(String type) {
        this.type = type;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float s = Math.min(getWidth(), getHeight());
        if (s <= 0) return;
        canvas.save();
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

    private void drawAwful(Canvas canvas, float s) {
        path.reset(); path.moveTo(x(s,0.24f), y(s,0.34f)); path.lineTo(x(s,0.36f), y(s,0.44f)); canvas.drawPath(path, strokePaint);
        path.reset(); path.moveTo(x(s,0.36f), y(s,0.34f)); path.lineTo(x(s,0.24f), y(s,0.44f)); canvas.drawPath(path, strokePaint);
        path.reset(); path.moveTo(x(s,0.64f), y(s,0.34f)); path.lineTo(x(s,0.76f), y(s,0.44f)); canvas.drawPath(path, strokePaint);
        path.reset(); path.moveTo(x(s,0.76f), y(s,0.34f)); path.lineTo(x(s,0.64f), y(s,0.44f)); canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s,0.28f), y(s,0.72f));
        path.quadTo(x(s,0.5f), y(s,0.55f), x(s,0.72f), y(s,0.72f));
        path.quadTo(x(s,0.5f), y(s,0.68f), x(s,0.28f), y(s,0.72f));
        path.close();
        canvas.drawPath(path, fillPaint);
    }

    private void drawSad(Canvas canvas, float s) {
        path.reset();
        path.moveTo(x(s,0.23f), y(s,0.42f)); path.quadTo(x(s,0.3f), y(s,0.34f), x(s,0.37f), y(s,0.4f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s,0.63f), y(s,0.4f)); path.quadTo(x(s,0.7f), y(s,0.34f), x(s,0.77f), y(s,0.42f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s,0.3f), y(s,0.7f)); path.quadTo(x(s,0.5f), y(s,0.58f), x(s,0.7f), y(s,0.7f));
        canvas.drawPath(path, strokePaint);
    }

    private void drawLow(Canvas canvas, float s) {
        path.reset();
        path.moveTo(x(s,0.24f), y(s,0.38f)); path.quadTo(x(s,0.3f), y(s,0.42f), x(s,0.36f), y(s,0.4f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s,0.64f), y(s,0.4f)); path.quadTo(x(s,0.7f), y(s,0.42f), x(s,0.76f), y(s,0.38f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s,0.32f), y(s,0.66f)); path.quadTo(x(s,0.5f), y(s,0.6f), x(s,0.68f), y(s,0.66f));
        canvas.drawPath(path, strokePaint);
    }

    private void drawNeutral(Canvas canvas, float s) {
        float r = strokePaint.getStrokeWidth() * 0.55f;
        canvas.drawCircle(x(s,0.3f), y(s,0.4f), r, fillPaint);
        canvas.drawCircle(x(s,0.7f), y(s,0.4f), r, fillPaint);
        canvas.drawLine(x(s,0.32f), y(s,0.63f), x(s,0.68f), y(s,0.63f), strokePaint);
    }

    private void drawCalm(Canvas canvas, float s) {
        path.reset();
        path.moveTo(x(s,0.23f), y(s,0.4f)); path.quadTo(x(s,0.3f), y(s,0.34f), x(s,0.37f), y(s,0.4f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s,0.63f), y(s,0.4f)); path.quadTo(x(s,0.7f), y(s,0.34f), x(s,0.77f), y(s,0.4f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s,0.3f), y(s,0.6f)); path.quadTo(x(s,0.5f), y(s,0.7f), x(s,0.7f), y(s,0.6f));
        canvas.drawPath(path, strokePaint);
    }

    private void drawHappy(Canvas canvas, float s) {
        path.reset();
        path.moveTo(x(s,0.22f), y(s,0.42f)); path.quadTo(x(s,0.3f), y(s,0.32f), x(s,0.38f), y(s,0.42f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s,0.62f), y(s,0.42f)); path.quadTo(x(s,0.7f), y(s,0.32f), x(s,0.78f), y(s,0.42f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s,0.26f), y(s,0.56f));
        path.quadTo(x(s,0.5f), y(s,0.78f), x(s,0.74f), y(s,0.56f));
        path.quadTo(x(s,0.5f), y(s,0.68f), x(s,0.26f), y(s,0.56f));
        path.close();
        canvas.drawPath(path, fillPaint);
    }

    private void drawOverjoyed(Canvas canvas, float s) {
        path.reset();
        path.moveTo(x(s,0.2f), y(s,0.44f)); path.quadTo(x(s,0.3f), y(s,0.3f), x(s,0.4f), y(s,0.44f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s,0.6f), y(s,0.44f)); path.quadTo(x(s,0.7f), y(s,0.3f), x(s,0.8f), y(s,0.44f));
        canvas.drawPath(path, strokePaint);
        path.reset();
        path.moveTo(x(s,0.22f), y(s,0.55f));
        path.quadTo(x(s,0.5f), y(s,0.85f), x(s,0.78f), y(s,0.55f));
        path.quadTo(x(s,0.5f), y(s,0.66f), x(s,0.22f), y(s,0.55f));
        path.close();
        canvas.drawPath(path, fillPaint);
    }
}
