package com.example.graduationproject.view.AdultOnboarding;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.graduationproject.models.AdultOnboarding.Stage;

/**
 * Mirrors the React <Sky/> component: an animated top-to-bottom gradient that
 * eases between "stages" as the user progresses, twinkling stars, and a soft
 * glow near the bottom of the screen. All drawn on canvas (no bitmaps) so it
 * stays crisp and cheap at any screen size.
 */
public class SkyView extends View {

    private static final long STAGE_TRANSITION_MS = 1100;

    private int fromColor;
    private int toColor;
    private int animFromColor;
    private int animToColor;
    private float starsOpacity = 1f;
    private float animStarsOpacity = 1f;

    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ValueAnimator stageAnimator;
    private ValueAnimator starClock;
    private long clockStartMs;

    // Precomputed star field: 22 stars, formula matches STAR_POSITIONS in the JS file.
    private static final int STAR_COUNT = 22;
    private final float[] starTopPct = new float[STAR_COUNT];
    private final float[] starLeftPct = new float[STAR_COUNT];
    private final float[] starSizeDp = new float[STAR_COUNT];
    private final float[] starDur = new float[STAR_COUNT];
    private final float[] starDelay = new float[STAR_COUNT];

    public SkyView(Context context) {
        super(context);
        init();
    }

    public SkyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        for (int i = 0; i < STAR_COUNT; i++) {
            starTopPct[i] = ((i * 37) % 70 + 4) / 100f;
            starLeftPct[i] = ((i * 53) % 92 + 3) / 100f;
            starSizeDp[i] = (i % 3) + 1.5f;
            starDur[i] = 3 + (i % 4);
            starDelay[i] = (i % 5) * 0.4f;
        }
        starPaint.setColor(0xFFFFF7E4);

        clockStartMs = System.currentTimeMillis();
        starClock = ValueAnimator.ofFloat(0f, 1f);
        starClock.setDuration(16);
        starClock.setRepeatCount(ValueAnimator.INFINITE);
        starClock.setInterpolator(new LinearInterpolator());
        starClock.addUpdateListener(a -> invalidate());
    }

    public void startAnimations() {
        if (starClock != null && !starClock.isStarted()) starClock.start();
    }

    public void stopAnimations() {
        if (starClock != null) starClock.cancel();
        if (stageAnimator != null) stageAnimator.cancel();
    }

    /** Animate to a new Stage, matching the 1.1s CSS `transition: background 1.1s ease`. */
    public void setStage(Stage stage, boolean animate) {
        if (stageAnimator != null) stageAnimator.cancel();
        final int startFrom = (fromColor == 0 && toColor == 0) ? stage.fromColor : animFromColor;
        final int startTo = (fromColor == 0 && toColor == 0) ? stage.toColor : animToColor;
        final float startStars = animStarsOpacity;
        fromColor = stage.fromColor;
        toColor = stage.toColor;
        starsOpacity = stage.starsOpacity;

        if (!animate) {
            animFromColor = fromColor;
            animToColor = toColor;
            animStarsOpacity = starsOpacity;
            invalidate();
            return;
        }

        stageAnimator = ValueAnimator.ofFloat(0f, 1f);
        stageAnimator.setDuration(STAGE_TRANSITION_MS);
        ArgbEvaluator eval = new ArgbEvaluator();
        stageAnimator.addUpdateListener(a -> {
            float f = (float) a.getAnimatedValue();
            animFromColor = (int) eval.evaluate(f, startFrom, fromColor);
            animToColor = (int) eval.evaluate(f, startTo, toColor);
            animStarsOpacity = startStars + (starsOpacity - startStars) * f;
            invalidate();
        });
        stageAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        // Background gradient: approximates CSS 160deg linear-gradient top-left -> bottom-right.
        LinearGradient bgShader = new LinearGradient(0, 0, w * 0.35f, h, animFromColor, animToColor, Shader.TileMode.CLAMP);
        bgPaint.setShader(bgShader);
        canvas.drawRect(0, 0, w, h, bgPaint);

        // Stars
        float elapsedSec = (System.currentTimeMillis() - clockStartMs) / 1000f;
        int starAlphaCap = Math.round(255 * Math.max(0f, Math.min(1f, animStarsOpacity)));
        if (starAlphaCap > 0) {
            for (int i = 0; i < STAR_COUNT; i++) {
                float phase = (elapsedSec - starDelay[i]) / starDur[i];
                float sine = (float) Math.sin(phase * 2 * Math.PI);
                float twinkle = 0.25f + 0.65f * ((sine + 1f) / 2f); // 0.25 .. 0.9
                int alpha = Math.round(twinkle * starAlphaCap * 0.75f);
                starPaint.setAlpha(Math.max(0, Math.min(255, alpha)));
                float cx = w * starLeftPct[i];
                float cy = h * starTopPct[i];
                float r = starSizeDp[i] * getResources().getDisplayMetrics().density;
                canvas.drawCircle(cx, cy, r, starPaint);
            }
        }

        // Bottom soft glow
        float glowH = h * 0.4f;
        RadialGradient glowShader = new RadialGradient(
                w / 2f, h + glowH * 0.1f, w * 0.75f,
                new int[]{(animToColor & 0x00FFFFFF) | 0x55000000, (animToColor & 0x00FFFFFF) | 0x00000000},
                new float[]{0f, 1f}, Shader.TileMode.CLAMP);
        glowPaint.setShader(glowShader);
        canvas.drawRect(0, h - glowH, w, h, glowPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimations();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimations();
    }
}
