package com.example.graduationproject.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.graduationproject.util.KidsAdaptiveStages;

import java.util.ArrayList;
import java.util.List;

/**
 * Mirrors the React <Sky> component: an animated gradient backdrop that eases between
 * the 6 STAGES as the user progresses, plus 3 slow-drifting clouds and a fading rainbow
 * arc that appears from stage 4 onward.
 */
public class KidsAdaptiveSkyBackgroundView extends View {

    private int stage = 0;
    private int curTopColor, curBottomColor;
    private ValueAnimator stageAnimator;

    private float rainbowAlpha = 0f;
    private ValueAnimator rainbowAnimator;

    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint cloudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint rainbowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final List<CloudSprite> clouds = new ArrayList<>();

    private static class CloudSprite {
        float topFrac, scale, phase;
        long durationMs;
        ValueAnimator animator;
        float x = -1;
    }

    public KidsAdaptiveSkyBackgroundView(Context context) { super(context); init(); }
    public KidsAdaptiveSkyBackgroundView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public KidsAdaptiveSkyBackgroundView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); init(); }

    private void init() {
        curTopColor = KidsAdaptiveStages.FROM_COLORS[0];
        curBottomColor = KidsAdaptiveStages.TO_COLORS[0];
        cloudPaint.setColor(0xE6FFFFFF);
        rainbowPaint.setStyle(Paint.Style.STROKE);
        rainbowPaint.setStrokeWidth(dp(10));
        rainbowPaint.setStrokeCap(Paint.Cap.ROUND);

        addCloud(0.08f, 1f, 26000, 0f);
        addCloud(0.18f, 0.7f, 34000, 0.25f);
        addCloud(0.04f, 0.5f, 20000, 0.55f);
    }

    private void addCloud(float topFrac, float scale, long durationMs, float phase) {
        CloudSprite c = new CloudSprite();
        c.topFrac = topFrac;
        c.scale = scale;
        c.durationMs = durationMs;
        c.phase = phase;
        clouds.add(c);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (CloudSprite c : clouds) startCloud(c);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (CloudSprite c : clouds) if (c.animator != null) c.animator.cancel();
        if (stageAnimator != null) stageAnimator.cancel();
        if (rainbowAnimator != null) rainbowAnimator.cancel();
    }

    private void startCloud(final CloudSprite c) {
        c.animator = ValueAnimator.ofFloat(c.phase, c.phase + 1f);
        c.animator.setDuration(c.durationMs);
        c.animator.setInterpolator(new LinearInterpolator());
        c.animator.setRepeatCount(ValueAnimator.INFINITE);
        c.animator.addUpdateListener(a -> {
            float t = ((Float) a.getAnimatedValue()) % 1f;
            c.x = -0.3f + t * 1.7f;
            invalidate();
        });
        c.animator.start();
    }

    /** Call whenever the current onboarding screen index changes. */
    public void setStage(int newStage) {
        if (newStage == stage) return;
        boolean rainbowShouldShow = newStage >= 4;
        boolean rainbowWasShown = stage >= 4;
        stage = newStage;

        int fromTop = curTopColor, fromBottom = curBottomColor;
        int toTop = KidsAdaptiveStages.FROM_COLORS[newStage], toBottom = KidsAdaptiveStages.TO_COLORS[newStage];

        if (stageAnimator != null) stageAnimator.cancel();
        stageAnimator = ValueAnimator.ofFloat(0f, 1f);
        stageAnimator.setDuration(1100);
        final ArgbEvaluator evaluator = new ArgbEvaluator();
        stageAnimator.addUpdateListener(a -> {
            float t = (Float) a.getAnimatedValue();
            curTopColor = (int) evaluator.evaluate(t, fromTop, toTop);
            curBottomColor = (int) evaluator.evaluate(t, fromBottom, toBottom);
            invalidate();
        });
        stageAnimator.start();

        if (rainbowShouldShow != rainbowWasShown) {
            if (rainbowAnimator != null) rainbowAnimator.cancel();
            rainbowAnimator = ValueAnimator.ofFloat(rainbowAlpha, rainbowShouldShow ? 0.55f : 0f);
            rainbowAnimator.setDuration(1000);
            rainbowAnimator.addUpdateListener(a -> {
                rainbowAlpha = (Float) a.getAnimatedValue();
                invalidate();
            });
            rainbowAnimator.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        bgPaint.setShader(new LinearGradient(0, 0, w * 0.35f, h, curTopColor, curBottomColor, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, bgPaint);

        drawRainbow(canvas, w, h);
        for (CloudSprite c : clouds) drawCloud(canvas, c, w, h);
    }

    private void drawRainbow(Canvas canvas, int w, int h) {
        if (rainbowAlpha <= 0.001f) return;
        int[] arcColors = {0xFFFF9B85, 0xFFFFC94D, 0xFF8FE0C4, 0xFF8FD3F4, 0xFFC9A6E8};
        float baseRadius = w * 0.55f;
        float cx = w / 2f;
        float cy = h + dp(30) * 0.4f;
        for (int i = 0; i < arcColors.length; i++) {
            float radius = baseRadius - i * dp(22);
            rainbowPaint.setColor(arcColors[i]);
            rainbowPaint.setAlpha((int) (rainbowAlpha * 255));
            RectF oval = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);
            canvas.drawArc(oval, 180, 180, false, rainbowPaint);
        }
    }

    private void drawCloud(Canvas canvas, CloudSprite c, int w, int h) {
        if (c.x < -1) return;
        float baseWidth = dp(90) * c.scale;
        float baseHeight = dp(45) * c.scale;
        float left = c.x * w;
        float top = c.topFrac * h;

        canvas.save();
        canvas.translate(left, top);
        float scaleX = baseWidth / 100f;
        float scaleY = baseHeight / 50f;
        canvas.scale(scaleX, scaleY);
        cloudPaint.setAlpha((int) (0.85f * 255));
        canvas.drawOval(4, 16, 56, 48, cloudPaint);
        canvas.drawOval(33, 6, 77, 42, cloudPaint);
        canvas.drawOval(57, 21, 93, 47, cloudPaint);
        canvas.restore();
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }
}
