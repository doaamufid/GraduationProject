package com.example.graduationproject.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class LiquidGradientView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float offset = 0f;
    private ValueAnimator animator;

    private int[] colors = {
            Color.parseColor("#EC407A"), // Pink
            Color.parseColor("#AB47BC"), // Purple
            Color.parseColor("#42A5F5"), // Blue
            Color.parseColor("#26A69A")  // Teal
    };

    public LiquidGradientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        startAnimation();
    }

    private void startAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(8000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            offset = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        // Layer 1: Base Purple
        canvas.drawColor(Color.parseColor("#8E24AA"));

        // Layer 2: Moving Pink Blob
        float pinkX = w * (0.2f + 0.6f * offset);
        float pinkY = h * (0.3f + 0.4f * (1 - offset));
        paint.setShader(new RadialGradient(pinkX, pinkY, w * 0.8f, 
                Color.parseColor("#D81B60"), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);

        // Layer 3: Moving Blue Blob
        float blueX = w * (0.8f - 0.5f * offset);
        float blueY = h * (0.7f - 0.3f * offset);
        paint.setShader(new RadialGradient(blueX, blueY, w * 0.7f, 
                Color.parseColor("#1E88E5"), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        
        // Layer 4: Subtle Teal highlight
        float tealX = w * offset;
        float tealY = h * 0.5f;
        paint.setShader(new RadialGradient(tealX, tealY, w * 0.5f, 
                Color.parseColor("#00897B"), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) animator.cancel();
    }
}
