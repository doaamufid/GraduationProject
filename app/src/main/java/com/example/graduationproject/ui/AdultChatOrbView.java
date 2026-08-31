package com.example.graduationproject.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Custom "glass sphere" companion avatar — a Canvas port of the React <Orb/> component:
 * rotating conic gradient body + sheen, an orbiting ring of small "bubbles" in 3 depth
 * layers, expanding "thinking pulse" ripples, random light glints, and a soft particle
 * "celebrate" burst. Works at any size (header avatar 30dp, welcome screen 148dp, etc).
 */
public class AdultChatOrbView extends View {

    private static final int[] GRADIENT_COLORS = {
            0xFFB9A4EC, 0xFF8F7EE8, 0xFFA9C8F5, 0xFFC9A8EC, 0xFFB9A4EC
    };

    private boolean ringBubbles = true;
    private boolean listening = false;

    private final Paint orbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sheenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float rotationDeg = 0f;      // orb body spin
    private float smallLayerDeg = 0f;    // bubble ring layers (parallax)
    private float mediumLayerDeg = 0f;
    private float largeLayerDeg = 0f;
    private float pulsePhase = 0f;       // glow pulse 0..1

    private ValueAnimator spinAnimator;
    private ValueAnimator ringAnimator;
    private ValueAnimator glowAnimator;
    private ValueAnimator glintAnimator;

    private final List<Ripple> ripples = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private Glint glint = null;
    private final Random random = new Random();

    private static class Ripple {
        float progress = 0f; // 0..1
    }

    private static class Particle {
        float dx, dy;
        float progress = 0f;
        float delay = 0f;
    }

    private static class Glint {
        float x, y; // fraction 0..1 relative to orb bounds
        float progress = 0f;
    }

    public AdultChatOrbView(Context context) {
        super(context);
        init();
    }

    public AdultChatOrbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        bubblePaint.setStyle(Paint.Style.FILL);
        ripplePaint.setStyle(Paint.Style.STROKE);
        ripplePaint.setStrokeWidth(4f);
        particlePaint.setStyle(Paint.Style.FILL);
        glintPaint.setStyle(Paint.Style.FILL);

        spinAnimator = ValueAnimator.ofFloat(0f, 360f);
        spinAnimator.setDuration(10000);
        spinAnimator.setRepeatCount(ValueAnimator.INFINITE);
        spinAnimator.setInterpolator(new LinearInterpolator());
        spinAnimator.addUpdateListener(a -> {
            rotationDeg = (float) a.getAnimatedValue();
            invalidate();
        });

        ringAnimator = ValueAnimator.ofFloat(0f, 360f);
        ringAnimator.setDuration(70000);
        ringAnimator.setRepeatCount(ValueAnimator.INFINITE);
        ringAnimator.setInterpolator(new LinearInterpolator());
        ringAnimator.addUpdateListener(a -> {
            smallLayerDeg = (float) a.getAnimatedValue();
            mediumLayerDeg = -smallLayerDeg * (70f / 95f);
            largeLayerDeg = smallLayerDeg * (70f / 130f);
            invalidate();
        });

        glowAnimator = ValueAnimator.ofFloat(0f, 1f, 0f);
        glowAnimator.setDuration(4000);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.addUpdateListener(a -> {
            pulsePhase = (float) a.getAnimatedValue();
            invalidate();
        });

        scheduleGlint();
    }

    public void setRingBubbles(boolean v) { this.ringBubbles = v; invalidate(); }

    public void setListening(boolean v) {
        this.listening = v;
        glowAnimator.setDuration(v ? 1100 : 4000);
    }

    /** Fires an expanding "thinking pulse" ring — call whenever a new bot message arrives. */
    public void pulse() {
        Ripple r = new Ripple();
        ripples.add(r);
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(1100);
        anim.addUpdateListener(a -> {
            r.progress = (float) a.getAnimatedValue();
            invalidate();
        });
        anim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(android.animation.Animator animation) {
                ripples.remove(r);
                invalidate();
            }
        });
        anim.start();
    }

    /** Soft particle burst — used when reaching a positive resolution (closing node). */
    public void celebrate() {
        for (int i = 0; i < 10; i++) {
            double angle = (i / 10.0) * Math.PI * 2 + random.nextDouble() * 0.4;
            float dist = 42f + random.nextFloat() * 30f;
            Particle p = new Particle();
            p.dx = (float) (Math.cos(angle) * dist);
            p.dy = (float) (Math.sin(angle) * dist);
            p.delay = random.nextFloat() * 150f;
            particles.add(p);
            ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
            anim.setStartDelay((long) p.delay);
            anim.setDuration(900);
            anim.addUpdateListener(a -> {
                p.progress = (float) a.getAnimatedValue();
                invalidate();
            });
            anim.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(android.animation.Animator animation) {
                    particles.remove(p);
                }
            });
            anim.start();
        }
    }

    private void scheduleGlint() {
        long delay = 3200 + random.nextInt(5500);
        postDelayed(() -> {
            double angle = random.nextDouble() * Math.PI * 2;
            float r = 0.22f + random.nextFloat() * 0.16f;
            Glint g = new Glint();
            g.x = 0.5f + (float) Math.cos(angle) * r;
            g.y = 0.5f + (float) Math.sin(angle) * r;
            glint = g;
            ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
            anim.setDuration(900);
            anim.addUpdateListener(a -> {
                g.progress = (float) a.getAnimatedValue();
                invalidate();
            });
            anim.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(android.animation.Animator animation) {
                    if (glint == g) glint = null;
                }
            });
            anim.start();
            scheduleGlint();
        }, delay);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        spinAnimator.start();
        ringAnimator.start();
        glowAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        spinAnimator.cancel();
        ringAnimator.cancel();
        glowAnimator.cancel();
        removeCallbacks(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        if (w <= 0 || h <= 0) return;

        float cx = w / 2f;
        float cy = h / 2f;
        float orbRadius = Math.min(w, h) * 0.26f; // orb body ~52% of view width, ring fills the rest

        if (ringBubbles) {
            drawBubbleRing(canvas, cx, cy, orbRadius, largeLayerDeg, 0.82f, true);
            drawBubbleRing(canvas, cx, cy, orbRadius, mediumLayerDeg, 0.80f, false);
            drawBubbleRing(canvas, cx, cy, orbRadius, smallLayerDeg, 0.78f, false);
        }

        drawOrbBody(canvas, cx, cy, orbRadius);

        if (orbRadius * 2 >= 28) drawGlint(canvas, cx, cy, orbRadius);

        drawRipples(canvas, cx, cy, orbRadius);
        drawParticles(canvas, cx, cy);
    }

    private void drawOrbBody(Canvas canvas, float cx, float cy, float r) {
        canvas.save();
        canvas.rotate(rotationDeg, cx, cy);
        SweepGradient sweep = new SweepGradient(cx, cy, GRADIENT_COLORS, null);
        orbPaint.setShader(sweep);
        canvas.drawCircle(cx, cy, r, orbPaint);
        canvas.restore();

        // sheen highlight (top-left)
        RadialGradient sheen = new RadialGradient(
                cx - r * 0.35f, cy - r * 0.4f, r * 1.1f,
                new int[]{0xF2FFFFFF, 0x00FFFFFF}, null, Shader.TileMode.CLAMP);
        sheenPaint.setShader(sheen);
        canvas.save();
        canvas.clipPath(circlePath(cx, cy, r));
        canvas.drawCircle(cx, cy, r, sheenPaint);

        // deeper purple undertone (bottom-right), mirrors .cc-orb-sheen radial layer
        RadialGradient under = new RadialGradient(
                cx + r * 0.3f, cy + r * 0.4f, r * 1.05f,
                new int[]{0x59784BDC, 0x00784BDC}, null, Shader.TileMode.CLAMP);
        sheenPaint.setShader(under);
        canvas.drawCircle(cx, cy, r, sheenPaint);
        canvas.restore();

        // pulsing glow ring around the body
        int glowAlpha = (int) (90 + 90 * Math.sin(pulsePhase * Math.PI));
        Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(r * (listening ? 0.5f : 0.35f));
        glowPaint.setColor(Color.argb(Math.min(glowAlpha, 130), 0x96, 0x6A, 0xE6));
        canvas.drawCircle(cx, cy, r + glowPaint.getStrokeWidth() / 2f, glowPaint);
    }

    private android.graphics.Path circlePath(float cx, float cy, float r) {
        android.graphics.Path p = new android.graphics.Path();
        p.addCircle(cx, cy, r, android.graphics.Path.Direction.CW);
        return p;
    }

    private void drawBubbleRing(Canvas canvas, float cx, float cy, float orbRadius, float layerDeg, float radiusFactor, boolean large) {
        int count = 40;
        float baseRadius = orbRadius * 1.9f * radiusFactor;
        for (int i = 0; i < count; i++) {
            boolean isLarge = i % 8 == 0;
            boolean isMedium = !isLarge && i % 3 == 0;
            // route each dot to only the layer currently being drawn (large / medium-or-small)
            if (large && !isLarge) continue;
            if (!large && isLarge) continue;

            double angle = (i / (double) count) * Math.PI * 2 + Math.toRadians(layerDeg);
            double jitter = isLarge ? 0.14 : isMedium ? 0.10 : 0.07;
            float radius = (float) (baseRadius + Math.sin(i * 3.1) * baseRadius * jitter);
            float x = (float) (cx + Math.cos(angle) * radius);
            float y = (float) (cy + Math.sin(angle) * radius);

            float size = isLarge ? (11 + (i * 7) % 6) : isMedium ? (6 + (i * 5) % 4) : (2.5f + (i * 11) % 4 * 0.6f);
            size = size * (getWidth() / 300f); // scale with view size
            int alpha = isLarge ? 217 : isMedium ? 166 : (int) (82 + (i * 13) % 10 * 5.1);

            bubblePaint.setColor(Color.argb(Math.min(alpha, 255), 0xB8, 0xA4, 0xEC));
            canvas.drawCircle(x, y, Math.max(size / 2f, 1.5f), bubblePaint);
        }
    }

    private void drawGlint(Canvas canvas, float cx, float cy, float r) {
        Glint g = glint;
        if (g == null) return;
        float p = g.progress;
        float alpha = p < 0.35f ? p / 0.35f : (1f - (p - 0.35f) / 0.65f);
        float scale = 0.3f + Math.min(p / 0.35f, 1f) * 0.9f;
        float gx = cx - r + g.x * (r * 2);
        float gy = cy - r + g.y * (r * 2);
        RadialGradient rg = new RadialGradient(gx, gy, r * 0.16f * scale,
                new int[]{Color.argb((int) (alpha * 230), 255, 255, 255), 0x00FFFFFF}, null, Shader.TileMode.CLAMP);
        glintPaint.setShader(rg);
        canvas.drawCircle(gx, gy, r * 0.16f * scale, glintPaint);
    }

    private void drawRipples(Canvas canvas, float cx, float cy, float r) {
        for (Ripple ripple : new ArrayList<>(ripples)) {
            float p = ripple.progress;
            float scale = 1f + p * 1.6f;
            int alpha = (int) (190 * (1f - p));
            ripplePaint.setColor(Color.argb(Math.max(alpha, 0), 0xB2, 0x8F, 0xE8));
            canvas.drawCircle(cx, cy, r * 0.4f * scale, ripplePaint);
        }
    }

    private void drawParticles(Canvas canvas, float cx, float cy) {
        for (Particle particle : new ArrayList<>(particles)) {
            float p = particle.progress;
            float x = cx + particle.dx * p;
            float y = cy + particle.dy * p;
            int alpha = (int) (255 * (1f - p));
            float radius = Math.max(2.5f * (1f - p * 0.7f), 0.5f);
            particlePaint.setColor(Color.argb(Math.max(alpha, 0), 255, 255, 255));
            canvas.drawCircle(x, y, radius, particlePaint);
        }
    }
}
