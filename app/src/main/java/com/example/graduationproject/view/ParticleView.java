package com.example.graduationproject.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Draws floating amber dots that drift up and fade in/out on loop —
 * the Java/Canvas equivalent of the CSS `scc-float` keyframe animation
 * applied to the particles inside a big CalmCard reveal.
 */
public class ParticleView extends View {

    private static class Particle {
        float leftPct, topPct, sizeDp, delaySec, durSec;
    }

    private final List<Particle> particles = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ValueAnimator animator;
    private long startTime;
    private boolean running = false;

    public ParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.parseColor("#E7A855"));
        setWillNotDraw(false);
        regenerate();
    }

    private void regenerate() {
        particles.clear();
        Random r = new Random();
        for (int i = 0; i < 9; i++) {
            Particle p = new Particle();
            p.leftPct = 6 + r.nextFloat() * 88;
            p.topPct = 6 + r.nextFloat() * 78;
            p.sizeDp = 3 + r.nextFloat() * 4;
            p.delaySec = r.nextFloat() * 3.5f;
            p.durSec = 3.5f + r.nextFloat() * 3f;
            particles.add(p);
        }
    }

    public void start() {
        regenerate();
        running = true;
        setVisibility(VISIBLE);
        startTime = System.currentTimeMillis();
        if (animator != null) animator.cancel();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(200000); // long-running driver; per-particle timing computed in draw()
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> invalidate());
        animator.start();
    }

    public void stop() {
        running = false;
        if (animator != null) animator.cancel();
        setVisibility(GONE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!running) return;
        float density = getResources().getDisplayMetrics().density;
        float elapsedSec = (System.currentTimeMillis() - startTime) / 1000f;
        int w = getWidth();
        int h = getHeight();

        for (Particle p : particles) {
            float t = (elapsedSec - p.delaySec) % p.durSec;
            if (t < 0) t += p.durSec;
            float frac = t / p.durSec; // 0..1 over one float cycle

            // mimic keyframes: 0%/100% -> origin, opacity .3 ; 50% -> up 16px right 6px, opacity .9
            float triangle = frac <= 0.5f ? (frac / 0.5f) : (1 - (frac - 0.5f) / 0.5f);
            float dx = 6 * density * triangle;
            float dy = -16 * density * triangle;
            float alpha = 0.3f + (0.9f - 0.3f) * triangle;

            float cx = (p.leftPct / 100f) * w + dx;
            float cy = (p.topPct / 100f) * h + dy;
            float radius = (p.sizeDp / 2f) * density;

            paint.setAlpha((int) (alpha * 255));
            canvas.drawCircle(cx, cy, radius, paint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) animator.cancel();
    }
}
