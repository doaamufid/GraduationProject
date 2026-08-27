package com.example.graduationproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Mirrors <ProgressPath total index>: a row of small dots, the current one wider and
 * glowing coral, done ones sun-colored, remaining ones a faint ink tint.
 */
public class KidsAdaptiveProgressPathView extends View {

    private int total = 13;
    private int index = 0;

    private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final int colorTodo = Color.parseColor("#262B3350"); // ink 15%
    private final int colorDone = Color.parseColor("#FFC94D"); // sun
    private final int colorCurrent = Color.parseColor("#FF9B85"); // coral

    public KidsAdaptiveProgressPathView(Context context) { super(context); }
    public KidsAdaptiveProgressPathView(Context context, AttributeSet attrs) { super(context, attrs); }
    public KidsAdaptiveProgressPathView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    public void setProgress(int total, int index) {
        this.total = total;
        this.index = index;
        setContentDescription("الخطوة " + (index + 1) + " من " + total);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (total <= 0) return;
        float dotH = dp(8);
        float dotSmallW = dp(8);
        float dotCurrentW = dp(18);
        float gap = dp(6);

        float totalWidth = 0;
        for (int i = 0; i < total; i++) totalWidth += (i == index ? dotCurrentW : dotSmallW) + (i > 0 ? gap : 0);

        float cx = (getWidth() - totalWidth) / 2f;
        float cy = getHeight() / 2f;

        for (int i = 0; i < total; i++) {
            float w = (i == index) ? dotCurrentW : dotSmallW;
            int color = i < index ? colorDone : (i == index ? colorCurrent : colorTodo);
            dotPaint.setColor(color);

            float left = cx;
            float right = cx + w;
            float top = cy - dotH / 2f;
            float bottom = cy + dotH / 2f;

            if (i == index) {
                glowPaint.setColor(color);
                glowPaint.setAlpha(120);
                canvas.drawRoundRect(left - dp(2), top - dp(2), right + dp(2), bottom + dp(2), dotH, dotH, glowPaint);
            }
            canvas.drawRoundRect(left, top, right, bottom, dotH, dotH, dotPaint);

            cx = right + gap;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = (int) dp(20);
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }
}
