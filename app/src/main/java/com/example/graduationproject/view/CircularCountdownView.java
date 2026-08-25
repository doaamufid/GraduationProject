package com.example.graduationproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.graduationproject.R;

/**
 * Circular countdown progress ring used in the dhikr step — equivalent to
 * the hand-drawn SVG <circle> progress ring + mm:ss label in the JS version.
 * Progress and remaining-seconds text are pushed in from SimulateFragment's
 * real CountDownTimer, exactly mirroring the JS setInterval-driven countdown.
 */
public class CircularCountdownView extends View {

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    private float progress = 0f; // 0..1
    private String centerText = "00:00";

    public CircularCountdownView(Context context) { super(context); init(); }
    public CircularCountdownView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        float strokeW = dp(6);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(strokeW);
        trackPaint.setColor(Color.argb(20, 255, 255, 255));

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeW);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(getResources().getColor(R.color.amber));

        textPaint.setColor(getResources().getColor(R.color.cream));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
    }

    private float dp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    /** progress: 0..1 (fraction elapsed). text: formatted mm:ss remaining. */
    public void setProgress(float progress, String text) {
        this.progress = Math.max(0f, Math.min(1f, progress));
        this.centerText = text;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float strokeW = trackPaint.getStrokeWidth();
        rect.set(strokeW / 2f, strokeW / 2f, getWidth() - strokeW / 2f, getHeight() - strokeW / 2f);

        canvas.drawArc(rect, 0, 360, false, trackPaint);
        canvas.drawArc(rect, -90, 360 * progress, false, progressPaint);

        float textY = getHeight() / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(centerText, getWidth() / 2f, textY, textPaint);
    }
}
