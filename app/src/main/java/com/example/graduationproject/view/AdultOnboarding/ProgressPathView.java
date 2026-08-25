package com.example.graduationproject.view.AdultOnboarding;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/** Mirrors <ProgressPath/>: a row of dots, the current one wider and glowing. */
public class ProgressPathView extends View {

    private int total = 13;
    private int index = 0;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ProgressPathView(Context context) {
        super(context);
    }

    public ProgressPathView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setProgress(int total, int index) {
        this.total = total;
        this.index = index;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float density = getResources().getDisplayMetrics().density;
        int dotH = (int) (6 * density);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), dotH + (int) (12 * density));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (total <= 0) return;
        float density = getResources().getDisplayMetrics().density;
        float gap = 6 * density;
        float doneW = 6 * density;
        float currentW = 16 * density;
        float dotH = 6 * density;

        float totalWidth = 0f;
        for (int i = 0; i < total; i++) totalWidth += (i == index ? currentW : doneW) + (i > 0 ? gap : 0);

        float x = (getWidth() - totalWidth) / 2f;
        float cy = getHeight() / 2f;

        for (int i = 0; i < total; i++) {
            float w = (i == index) ? currentW : doneW;
            paint.setShadowLayer(0, 0, 0, 0);
            if (i < index) {
                paint.setColor(Color.argb(191, 255, 231, 176)); // rgba(255,231,176,0.75)
            } else if (i == index) {
                paint.setColor(0xFFFFE3B0);
                paint.setShadowLayer(8 * density, 0, 0, 0xFFFFE3B0);
            } else {
                paint.setColor(Color.argb(64, 255, 255, 255));
            }
            canvas.drawRoundRect(x, cy - dotH / 2f, x + w, cy + dotH / 2f, dotH, dotH, paint);
            x += w + gap;
        }
    }
}
