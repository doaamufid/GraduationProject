package com.example.graduationproject.view;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Renders 16 soft golden particles that continuously float upward and fade,
 * matching the CSS `.particle` + `@keyframes floatUp` behaviour from the
 * original web design:
 *
 *   0%   -> y=0,    scale=0.6, opacity=0
 *   12%  -> opacity=0.85
 *   80%  -> opacity=0.5
 *   100% -> y=-680dp, scale=1.1, opacity=0
 */
public class QouteFeatureParticleView extends View {

    private static final int PARTICLE_COUNT = 16;

    private static class Particle {
        float leftFraction;   // 0..1 horizontal position
        long delayMs;
        long durationMs;
        float sizeDp;
    }

    private final List<Particle> particles = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private ValueAnimator driver;
    private long startTime;

    public QouteFeatureParticleView(Context context) {
        super(context);
        init();
    }

    public QouteFeatureParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            Particle p = new Particle();
            p.leftFraction = random.nextFloat();
            p.delayMs = (long) (random.nextFloat() * 10000);
            p.durationMs = (long) (10000 + random.nextFloat() * 10000);
            p.sizeDp = 2f + random.nextFloat() * 3f;
            particles.add(p);
        }
        startTime = android.os.SystemClock.uptimeMillis();

        driver = ValueAnimator.ofFloat(0f, 1f);
        driver.setDuration(16); // ~60fps ticks, infinite
        driver.setRepeatCount(ValueAnimator.INFINITE);
        driver.setInterpolator(new LinearInterpolator());
        driver.addUpdateListener(a -> invalidate());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (driver != null) driver.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (driver != null) driver.cancel();
    }

    private static float opacityForT(float t) {
        if (t <= 0.12f) return lerp(0f, 0.85f, t / 0.12f);
        if (t <= 0.8f) return lerp(0.85f, 0.5f, (t - 0.12f) / (0.8f - 0.12f));
        return lerp(0.5f, 0f, (t - 0.8f) / 0.2f);
    }

    private static float lerp(float a, float b, float f) {
        return a + (b - a) * f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() == 0 || getHeight() == 0) return;

        long now = android.os.SystemClock.uptimeMillis();
        float density = getResources().getDisplayMetrics().density;
        float travelPx = 680f * density;

        for (Particle p : particles) {
            long elapsed = now - startTime - p.delayMs;
            if (elapsed < 0) continue; // still waiting for its delay before first loop
            float t = (elapsed % p.durationMs) / (float) p.durationMs;

            float opacity = opacityForT(t);
            if (opacity <= 0.01f) continue;

            float scale = lerp(0.6f, 1.1f, t);
            float baseRadius = (p.sizeDp * density) / 2f * scale;
            float cx = p.leftFraction * getWidth();
            float cy = getHeight() - (10 * density) - (travelPx * t);

            paint.setShader(new RadialGradient(
                    cx, cy, Math.max(baseRadius, 1f),
                    new int[]{Color.parseColor("#F0CF8F"), Color.parseColor("#00F0CF8F")},
                    new float[]{0f, 1f},
                    Shader.TileMode.CLAMP));
            paint.setAlpha((int) (opacity * 255));
            canvas.drawCircle(cx, cy, Math.max(baseRadius, 1f), paint);
        }
    }
}
