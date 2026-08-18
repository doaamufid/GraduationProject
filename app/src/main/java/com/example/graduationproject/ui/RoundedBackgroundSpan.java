package com.example.graduationproject.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RoundedBackgroundSpan extends ReplacementSpan {

    private final int backgroundColor;
    private final int textColor;
    private final float cornerRadius;
    private final float paddingVertical;
    private final float paddingHorizontal;

    public RoundedBackgroundSpan(int backgroundColor, int textColor, float cornerRadius, float paddingVertical, float paddingHorizontal) {
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.cornerRadius = cornerRadius;
        this.paddingVertical = paddingVertical;
        this.paddingHorizontal = paddingHorizontal;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end) + 2 * paddingHorizontal);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        float width = paint.measureText(text, start, end);
        
        // Use ascent and descent to get the actual text height instead of the line height (top/bottom)
        float textTop = y + paint.ascent();
        float textBottom = y + paint.descent();
        
        RectF rect = new RectF(
                x, 
                textTop - paddingVertical, 
                x + width + 2 * paddingHorizontal, 
                textBottom + paddingVertical
        );
        
        // Draw background
        int oldColor = paint.getColor();
        paint.setColor(backgroundColor);
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
        
        // Draw text
        paint.setColor(textColor);
        canvas.drawText(text, start, end, x + paddingHorizontal, y, paint);
        
        // Restore paint color
        paint.setColor(oldColor);
    }
}
