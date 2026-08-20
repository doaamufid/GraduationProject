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

/**
 * A smooth, baby-blue liquid gradient background that moves slowly.
 */
public class LiquidGradientView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float offset = 0f;
    private ValueAnimator animator;

    // Baby blue palette
    private static final int COLOR_BASE = Color.parseColor("#E1F1FF");   // @color/bg
    private static final int COLOR_BLOB1 = Color.parseColor("#D1E8FF");  // Light blue
    private static final int COLOR_BLOB2 = Color.parseColor("#B3E5FC");  // Slightly darker baby blue
    private static final int COLOR_BLOB3 = Color.parseColor("#E3F2FD");  // Very light blue

    public LiquidGradientView(Context context) {
        super(context);
        init();
    }

    public LiquidGradientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        startAnimation();
    }

    private void startAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(15000); // Slow movement
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
        if (w == 0 || h == 0) return;

        // Base Layer
        canvas.drawColor(COLOR_BASE);

        // Blob 1: Top Right-ish
        float b1X = w * (0.7f + 0.2f * (float)Math.sin(offset * Math.PI));
        float b1Y = h * (0.2f + 0.2f * (float)Math.cos(offset * Math.PI));
        paint.setShader(new RadialGradient(b1X, b1Y, w * 1.2f,
                COLOR_BLOB1, Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);

        // Blob 2: Bottom Left-ish
        float b2X = w * (0.2f - 0.1f * (float)Math.cos(offset * Math.PI));
        float b2Y = h * (0.8f - 0.2f * (float)Math.sin(offset * Math.PI));
        paint.setShader(new RadialGradient(b2X, b2Y, w * 1.0f,
                COLOR_BLOB2, Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);

        // Blob 3: Center focus
        float b3X = w * (0.5f + 0.1f * (float)Math.cos(offset * 2 * Math.PI));
        float b3Y = h * (0.5f + 0.1f * (float)Math.sin(offset * 2 * Math.PI));
        paint.setShader(new RadialGradient(b3X, b3Y, w * 0.8f,
                COLOR_BLOB3, Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) animator.cancel();
    }
}
