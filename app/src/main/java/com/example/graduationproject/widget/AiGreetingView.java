package com.example.graduationproject.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Custom View that renders a morphing AI Orb and a floating bubble cloud.
 * High performance, smooth 60fps animations.
 */
public class AiGreetingView extends View {

    private Paint orbPaint;
    private Paint bubblePaint;
    private final List<Bubble> bubbles = new ArrayList<>();
    private final Random random = new Random();

    private float orbPhase = 0f;
    private float orbScale = 1.0f;
    private ValueAnimator animator;

    // Orb colors from resources
    private int[] orbColors = {
            Color.parseColor("#6D5B9E"), // Indigo
            Color.parseColor("#E4849C"), // Magenta
            Color.parseColor("#7FA8D9"), // Sky Blue
            Color.parseColor("#B0A0E0")  // Lavender
    };

    public AiGreetingView(Context context) {
        super(context);
        init();
    }

    public AiGreetingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        orbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setColor(Color.parseColor("#B0A0E0"));
        bubblePaint.setAlpha(80);

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(8000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            orbPhase = (float) animation.getAnimatedValue();
            updateBubbles();
            invalidate();
        });
    }

    private void initBubbles(int width, int height) {
        bubbles.clear();
        int count = 40;
        float centerX = width / 2f;
        float centerY = height / 2f;
        float radiusBase = width * 0.35f;

        for (int i = 0; i < count; i++) {
            float angle = (float) (random.nextFloat() * 2 * Math.PI);
            float dist = radiusBase + random.nextFloat() * 80f;
            bubbles.add(new Bubble(
                    centerX + (float) Math.cos(angle) * dist,
                    centerY + (float) Math.sin(angle) * dist,
                    4f + random.nextFloat() * 12f,
                    random.nextFloat() * 10000
            ));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initBubbles(w, h);
    }

    private void updateBubbles() {
        for (Bubble b : bubbles) {
            b.update();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = getWidth() * 0.4f;

        // Draw Morphing Orb
        drawOrb(canvas, cx, cy, radius);

        // Draw Bubbles
        for (Bubble b : bubbles) {
            bubblePaint.setAlpha(b.alpha);
            canvas.drawCircle(b.x + b.offsetX, b.y + b.offsetY, b.radius * b.scale, bubblePaint);
        }
    }

    private void drawOrb(Canvas canvas, float cx, float cy, float radius) {
        // Create dynamic radial gradient
        int color1 = ColorUtils.blendARGB(orbColors[0], orbColors[1], (float) Math.sin(orbPhase * 2 * Math.PI) * 0.5f + 0.5f);
        int color2 = ColorUtils.blendARGB(orbColors[2], orbColors[3], (float) Math.cos(orbPhase * 2 * Math.PI) * 0.5f + 0.5f);
        
        RadialGradient gradient = new RadialGradient(cx, cy, radius,
                new int[]{color1, color2, Color.TRANSPARENT},
                new float[]{0f, 0.7f, 1f}, Shader.TileMode.CLAMP);

        // Apply organic morphing transformation
        Matrix matrix = new Matrix();
        float skewX = (float) Math.sin(orbPhase * 2 * Math.PI * 2) * 0.1f;
        float skewY = (float) Math.cos(orbPhase * 2 * Math.PI) * 0.1f;
        float scale = 1.0f + (float) Math.sin(orbPhase * 2 * Math.PI) * 0.05f;
        
        matrix.postScale(scale, scale, cx, cy);
        matrix.postSkew(skewX, skewY, cx, cy);
        gradient.setLocalMatrix(matrix);

        orbPaint.setShader(gradient);
        canvas.drawCircle(cx, cy, radius, orbPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (animator != null && !animator.isRunning()) {
            animator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }

    private class Bubble {
        float x, y;
        float radius;
        float seed;
        float offsetX, offsetY;
        float scale;
        int alpha;

        Bubble(float x, float y, float radius, float seed) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.seed = seed;
        }

        void update() {
            float time = (System.currentTimeMillis() + seed) / 1000f;
            offsetX = (float) Math.sin(time * 0.8f) * 15f;
            offsetY = (float) Math.cos(time * 0.7f) * 15f;
            scale = 0.8f + (float) Math.sin(time * 1.2f) * 0.2f;
            alpha = (int) (60 + Math.sin(time * 0.5f) * 40);
        }
    }
}
