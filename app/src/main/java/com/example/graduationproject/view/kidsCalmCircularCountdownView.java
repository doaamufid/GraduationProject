package com.example.graduationproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.graduationproject.R;

/** Mirrors the SVG ring with strokeDasharray/strokeDashoffset used for the word countdown. */
public class kidsCalmCircularCountdownView extends View {

    private float progress = 0f; // 0..1
    private String centerText = "";

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    public kidsCalmCircularCountdownView(Context context) { super(context); init(); }
    public kidsCalmCircularCountdownView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        float strokeW = 8 * density;

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(strokeW);
        trackPaint.setColor(getResources().getColor(R.color.kids_calm_cardBorder));

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeW);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(getResources().getColor(R.color.kids_calm_mint));

        textPaint.setColor(getResources().getColor(R.color.kids_calm_navy));
        textPaint.setTextSize(15 * density);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    /** progress: 0 = just started, 1 = finished (ring fully drained), matches React's `progress`. */
    public void setProgress(float progress) {
        this.progress = Math.max(0f, Math.min(1f, progress));
        invalidate();
    }

    public void setCenterText(String text) {
        this.centerText = text;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float strokeW = trackPaint.getStrokeWidth();
        rect.set(strokeW / 2f, strokeW / 2f, getWidth() - strokeW / 2f, getHeight() - strokeW / 2f);

        canvas.drawArc(rect, 0, 360, false, trackPaint);

        float sweep = 360f * (1f - progress);
        canvas.drawArc(rect, -90, sweep, false, progressPaint);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f - ((textPaint.descent() + textPaint.ascent()) / 2f);
        canvas.drawText(centerText, cx, cy, textPaint);
    }
}
