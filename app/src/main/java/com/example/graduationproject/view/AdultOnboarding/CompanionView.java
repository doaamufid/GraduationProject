package com.example.graduationproject.view.AdultOnboarding;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * The "companion": a single glowing light, not a character with a face —
 * mirrors the React <Companion/> component (radial gradient orb + soft glow
 * shadow + gentle float animation + a pulse triggered on selection events).
 */
public class CompanionView extends View {

    public static final String MOOD_NEUTRAL = "neutral";
    public static final String MOOD_WARM = "warm";
    public static final String MOOD_CALM = "calm";

    private String mood = MOOD_NEUTRAL;
    private boolean reduced = false;

    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint corePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ValueAnimator floatAnimator;
    private ValueAnimator pulseAnimator;
    private float floatOffsetPx = 0f;
    private float pulseScale = 1f;
    private float pulseGlowBoost = 0f; // 0..1

    public CompanionView(Context context) {
        super(context);
        init();
    }

    public CompanionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public void setReducedMotion(boolean reduced) {
        this.reduced = reduced;
        if (reduced && floatAnimator != null) floatAnimator.cancel();
    }

    public void setMood(String mood) {
        this.mood = mood == null ? MOOD_NEUTRAL : mood;
        invalidate();
    }

    private int moodColor() {
        switch (mood) {
            case MOOD_WARM: return 0xFFFFD79A;
            case MOOD_CALM: return 0xFF8FBFB2;
            default: return 0xFFFFE3B0;
        }
    }

    /** One-shot glow pulse — mirrors the ~500ms `pulse` state toggle in the JS app. */
    public void pulse() {
        if (pulseAnimator != null) pulseAnimator.cancel();
        pulseAnimator = ValueAnimator.ofFloat(0f, 1f, 0f);
        pulseAnimator.setDuration(500);
        pulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnimator.addUpdateListener(a -> {
            float f = (float) a.getAnimatedValue();
            pulseGlowBoost = f;
            pulseScale = 1f + 0.08f * f;
            invalidate();
        });
        pulseAnimator.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startFloat();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (floatAnimator != null) floatAnimator.cancel();
        if (pulseAnimator != null) pulseAnimator.cancel();
    }

    private void startFloat() {
        if (reduced) return;
        if (floatAnimator != null) floatAnimator.cancel();
        floatAnimator = ValueAnimator.ofFloat(0f, 1f);
        floatAnimator.setDuration(2750); // half of the 5.5s float cycle
        floatAnimator.setRepeatCount(ValueAnimator.INFINITE);
        floatAnimator.setRepeatMode(ValueAnimator.REVERSE);
        floatAnimator.setInterpolator(new LinearInterpolator());
        floatAnimator.addUpdateListener(a -> {
            float f = (float) a.getAnimatedValue();
            float eased = (float) (0.5 - 0.5 * Math.cos(f * Math.PI)); // ease-in-out
            floatOffsetPx = -eased * getResources().getDisplayMetrics().density * 6f;
            invalidate();
        });
        floatAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        float cx = w / 2f;
        float cy = h / 2f + floatOffsetPx;
        float baseR = getResources().getDisplayMetrics().density * 22f; // Fixed size for the orb core
        float r = baseR * pulseScale;
        int color = moodColor();

        // Soft outer glow (approximates box-shadow blur)
        float glowR = r * (1.6f + 1.4f * pulseGlowBoost);
        int glowAlpha = (int) (110 + 90 * pulseGlowBoost);
        RadialGradient glowShader = new RadialGradient(
                cx, cy, glowR,
                new int[]{Color.argb(glowAlpha, Color.red(color), Color.green(color), Color.blue(color)), Color.argb(0, Color.red(color), Color.green(color), Color.blue(color))},
                new float[]{0f, 1f}, Shader.TileMode.CLAMP);
        glowPaint.setShader(glowShader);
        canvas.drawCircle(cx, cy, glowR, glowPaint);

        // Core orb
        RadialGradient coreShader = new RadialGradient(
                cx - r * 0.15f, cy - r * 0.2f, r,
                new int[]{0xFFFFFDF6, color, Color.argb(0, Color.red(color), Color.green(color), Color.blue(color))},
                new float[]{0f, 0.55f, 1f}, Shader.TileMode.CLAMP);
        corePaint.setShader(coreShader);
        canvas.drawCircle(cx, cy, r, corePaint);
    }
}
