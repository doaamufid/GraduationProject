package com.example.graduationproject.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Mirrors the React <TeddyBuddy> companion: a friendly bear face drawn with the same
 * proportions as the source SVG (ears, head, snout, eyes, nose, mouth, cheeks), with:
 *  - a continuous gentle bounce/rotate idle animation (bounceStar keyframes)
 *  - a quick scale-up "pulse" triggered on selection (matches the `pulse` prop)
 *  - two expressions: "warm"/"calm" (happy eyes + smiling mouth) vs neutral (flat mouth)
 */
public class KidsAdaptiveTeddyBuddyView extends View {

    public static final String MOOD_NEUTRAL = "neutral";
    public static final String MOOD_WARM = "warm";
    public static final String MOOD_CALM = "calm";

    private String mood = MOOD_NEUTRAL;
    private boolean reducedMotion = false;

    private float idleTranslateY = 0f;
    private float idleRotateDeg = -2f;
    private ValueAnimator idleAnimator;

    private float pulseScale = 1f;
    private ValueAnimator pulseAnimator;

    private final Paint furPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint innerEarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint snoutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint featurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mouthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint cheekPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public KidsAdaptiveTeddyBuddyView(Context context) { super(context); init(); }
    public KidsAdaptiveTeddyBuddyView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public KidsAdaptiveTeddyBuddyView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); init(); }

    private void init() {
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.parseColor("#B0702F"));
        strokePaint.setStrokeWidth(1.5f);

        innerEarPaint.setColor(Color.parseColor("#FFDDA8"));
        innerEarPaint.setAlpha((int) (0.85f * 255));

        snoutPaint.setColor(Color.parseColor("#FFF3DE"));

        featurePaint.setColor(Color.parseColor("#2B3350"));

        mouthPaint.setStyle(Paint.Style.STROKE);
        mouthPaint.setColor(Color.parseColor("#2B3350"));
        mouthPaint.setStrokeCap(Paint.Cap.ROUND);

        cheekPaint.setColor(Color.parseColor("#FF9B85"));
        cheekPaint.setAlpha((int) (0.35f * 255));
    }

    public void setReducedMotion(boolean reduced) {
        this.reducedMotion = reduced;
        if (reduced && idleAnimator != null) idleAnimator.cancel();
    }

    public void setMood(String mood) {
        if (mood == null) mood = MOOD_NEUTRAL;
        this.mood = mood;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startIdleAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (idleAnimator != null) idleAnimator.cancel();
        if (pulseAnimator != null) pulseAnimator.cancel();
    }

    private void startIdleAnimation() {
        if (reducedMotion) return;
        idleAnimator = ValueAnimator.ofFloat(0f, 1f);
        idleAnimator.setDuration(3200);
        idleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        idleAnimator.setRepeatMode(ValueAnimator.RESTART);
        idleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        idleAnimator.addUpdateListener(a -> {
            float t = (Float) a.getAnimatedValue();
            // 0%,100% -> y0 rot -2 ; 50% -> y -8dp rot 2
            float wave = (float) Math.sin(t * Math.PI); // 0 -> 1 -> 0
            idleTranslateY = -dp(8) * wave;
            idleRotateDeg = -2f + 4f * wave;
            invalidate();
        });
        idleAnimator.start();
    }

    /** Triggers the quick "pop" pulse used when the user makes a selection. */
    public void pulse() {
        if (pulseAnimator != null) pulseAnimator.cancel();
        pulseAnimator = ValueAnimator.ofFloat(1f, 1.12f, 1f);
        pulseAnimator.setDuration(350);
        pulseAnimator.setInterpolator(new OvershootInterpolator(2f));
        pulseAnimator.addUpdateListener(a -> {
            pulseScale = (Float) a.getAnimatedValue();
            invalidate();
        });
        pulseAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;
        float size = Math.min(w, h);

        canvas.save();
        // Shifted down by dp(4) to avoid ear cropping when idleTranslateY is at its peak (-8dp)
        canvas.translate(w / 2f, h / 2f + idleTranslateY + dp(4));
        canvas.rotate(idleRotateDeg);
        canvas.scale(pulseScale, pulseScale);
        canvas.translate(-size / 2f, -size / 2f);

        float s = size / 100f; // svg viewBox is 0..100

        furPaint.setShader(new RadialGradient(
                35 * s, 30 * s, 75 * s,
                new int[]{Color.parseColor("#FFE9C4"), Color.parseColor("#E3A25E"), Color.parseColor("#C87F3D")},
                new float[]{0f, 0.55f, 1f}, Shader.TileMode.CLAMP));

        // ears
        canvas.drawCircle(24 * s, 22 * s, 15 * s, furPaint);
        canvas.drawCircle(24 * s, 22 * s, 15 * s, strokePaint);
        canvas.drawCircle(76 * s, 22 * s, 15 * s, furPaint);
        canvas.drawCircle(76 * s, 22 * s, 15 * s, strokePaint);
        canvas.drawCircle(24 * s, 22 * s, 6.5f * s, innerEarPaint);
        canvas.drawCircle(76 * s, 22 * s, 6.5f * s, innerEarPaint);

        // head
        canvas.drawCircle(50 * s, 54 * s, 36 * s, furPaint);
        canvas.drawCircle(50 * s, 54 * s, 36 * s, strokePaint);

        // snout
        canvas.save();
        canvas.translate(50 * s, 63 * s);
        canvas.scale(18 * s, 13 * s);
        canvas.drawOval(-1, -1, 1, 1, snoutPaint);
        canvas.restore();

        boolean happy = MOOD_WARM.equals(mood) || MOOD_CALM.equals(mood);

        // eyes
        float eyeR = (happy ? 4.4f : 3.8f) * s;
        canvas.drawCircle(37 * s, 48 * s, eyeR, featurePaint);
        canvas.drawCircle(63 * s, 48 * s, eyeR, featurePaint);

        // nose
        canvas.save();
        canvas.translate(50 * s, 58 * s);
        canvas.scale(5 * s, 3.6f * s);
        canvas.drawOval(-1, -1, 1, 1, featurePaint);
        canvas.restore();

        // mouth
        mouthPaint.setStrokeWidth(2.6f * s);
        Path mouth = new Path();
        if (happy) {
            mouth.moveTo(50 * s, 61.5f * s);
            mouth.quadTo(50 * s, 70 * s, 41 * s, 68 * s);
            mouth.moveTo(50 * s, 61.5f * s);
            mouth.quadTo(50 * s, 70 * s, 59 * s, 68 * s);
        } else {
            mouth.moveTo(43 * s, 65 * s);
            mouth.quadTo(50 * s, 68 * s, 57 * s, 65 * s);
        }
        canvas.drawPath(mouth, mouthPaint);

        // cheeks
        canvas.drawCircle(24 * s, 60 * s, 5.5f * s, cheekPaint);
        canvas.drawCircle(76 * s, 60 * s, 5.5f * s, cheekPaint);

        canvas.restore();
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }
}
