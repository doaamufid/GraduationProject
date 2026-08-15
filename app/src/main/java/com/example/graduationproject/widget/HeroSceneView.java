package com.example.graduationproject.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.example.graduationproject.models.HealingEnvironmentRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom View equivalent of &lt;HeroScene env playing/&gt;: draws a small
 * animated illustration behind the hero card, unique per environment.
 */
public class HeroSceneView extends View {

    private String envKey = HealingEnvironmentRepository.ENV_NIGHT_FOREST;
    private boolean playing = true;

    private final List<ValueAnimator> animators = new ArrayList<>();
    private final float[] waveOffsetPx = new float[3];
    private final float[] treeAngleDeg = new float[4];
    private final float[] rainProgress = new float[18];

    private final Paint wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mountainFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sunPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint rainPaint = new Paint();
    private final Paint treePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Forest tree definitions: {x, y, height} in viewBox units.
    private static final float[][] TREES = {{40, 190, 60}, {120, 210, 90}, {230, 195, 75}, {330, 215, 65}};

    private float density;

    public HeroSceneView(Context context) {
        super(context);
        init();
    }

    public HeroSceneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;

        wavePaint.setStyle(Paint.Style.STROKE);
        wavePaint.setStrokeWidth(2 * density);
        wavePaint.setColor(Color.WHITE);
        wavePaint.setAlpha(Math.round(255 * 0.2f));

        mountainFillPaint.setStyle(Paint.Style.FILL);
        mountainFillPaint.setColor(Color.WHITE);
        mountainFillPaint.setAlpha(Math.round(255 * 0.078f));

        sunPaint.setStyle(Paint.Style.FILL);
        sunPaint.setColor(Color.WHITE);
        sunPaint.setAlpha(Math.round(255 * 0.13f));

        rainPaint.setColor(Color.WHITE);
        rainPaint.setAlpha(Math.round(255 * 0.2f));

        treePaint.setStyle(Paint.Style.FILL);
        treePaint.setColor(Color.WHITE);
        treePaint.setAlpha(Math.round(255 * 0.063f));
    }

    public void setEnvironment(String key) {
        if (key.equals(envKey)) return;
        envKey = key;
        restartAnimators();
        invalidate();
    }

    public void setPlaying(boolean isPlaying) {
        if (isPlaying == playing) return;
        playing = isPlaying;
        restartAnimators();
        invalidate();
    }

    private void restartAnimators() {
        for (ValueAnimator a : animators) a.cancel();
        animators.clear();

        java.util.Arrays.fill(waveOffsetPx, 0f);
        java.util.Arrays.fill(treeAngleDeg, 0f);
        java.util.Arrays.fill(rainProgress, 0f);

        if (!playing) return;

        switch (envKey) {
            case HealingEnvironmentRepository.ENV_SEA_WAVES:
                startWaveAnimators();
                break;
            case HealingEnvironmentRepository.ENV_RAIN:
                startRainAnimators();
                break;
            case HealingEnvironmentRepository.ENV_NIGHT_FOREST:
                startTreeAnimators();
                break;
            default:
                break;
        }
    }

    private void startWaveAnimators() {
        float targetPx = 15 * density;
        for (int i = 0; i < 3; i++) {
            final int index = i;
            ValueAnimator animator = ValueAnimator.ofFloat(0f, targetPx);
            animator.setDuration(2000);
            animator.setStartDelay((long) (i * 600));
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(a -> {
                waveOffsetPx[index] = (float) a.getAnimatedValue();
                invalidate();
            });
            animator.start();
            animators.add(animator);
        }
    }

    private void startRainAnimators() {
        for (int i = 0; i < 18; i++) {
            final int index = i;
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(1000);
            animator.setStartDelay((long) ((i % 6) * 250));
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(a -> {
                rainProgress[index] = (float) a.getAnimatedValue();
                invalidate();
            });
            animator.start();
            animators.add(animator);
        }
    }

    private void startTreeAnimators() {
        for (int i = 0; i < 4; i++) {
            final int index = i;
            ValueAnimator animator = ValueAnimator.ofFloat(-2f, 2f);
            animator.setDuration(1750);
            animator.setStartDelay((long) (i * 400));
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(a -> {
                treeAngleDeg[index] = (float) a.getAnimatedValue();
                invalidate();
            });
            animator.start();
            animators.add(animator);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        float scale = Math.max(w / 400f, h / 200f);
        float offsetX = (w - 400 * scale) / 2f;
        float offsetY = (h - 200 * scale) / 2f;

        switch (envKey) {
            case HealingEnvironmentRepository.ENV_SEA_WAVES:
                drawBeach(canvas, scale, offsetX, offsetY);
                break;
            case HealingEnvironmentRepository.ENV_RAIN:
                drawRain(canvas, w, h);
                break;
            case HealingEnvironmentRepository.ENV_NIGHT_FOREST:
                drawForest(canvas, scale, offsetX, offsetY);
                break;
            case HealingEnvironmentRepository.ENV_FIREPLACE:
            case HealingEnvironmentRepository.ENV_LIBRARY:
                drawMountain(canvas, scale, offsetX, offsetY); // static scene
                break;
            default:
                break;
        }
    }

    private void drawBeach(Canvas canvas, float scale, float offsetX, float offsetY) {
        for (int i = 0; i < 3; i++) {
            float y = 130 + i * 20;
            Path path = new Path();
            path.moveTo(0, y);
            path.quadTo(100, 110 + i * 20, 200, y);
            path.quadTo(300, 150 + i * 20, 400, y);

            canvas.save();
            canvas.translate(waveOffsetPx[i], 0);
            canvas.translate(offsetX, offsetY);
            canvas.scale(scale, scale);
            canvas.drawPath(path, wavePaint);
            canvas.restore();
        }
    }

    private void drawMountain(Canvas canvas, float scale, float offsetX, float offsetY) {
        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale, scale);

        Path mountain = new Path();
        mountain.moveTo(0, 160);
        mountain.lineTo(90, 90);
        mountain.lineTo(150, 140);
        mountain.lineTo(210, 70);
        mountain.lineTo(280, 150);
        mountain.lineTo(340, 100);
        mountain.lineTo(400, 160);
        mountain.lineTo(400, 200);
        mountain.lineTo(0, 200);
        mountain.close();
        canvas.drawPath(mountain, mountainFillPaint);

        canvas.drawCircle(320, 50, 22, sunPaint);
        canvas.restore();
    }

    private void drawRain(Canvas canvas, int w, int h) {
        float dropWidth = 1.5f * density;
        float dropHeight = 16 * density;
        float startY = -10 * density;
        float fallDistance = 220 * density;

        for (int i = 0; i < 18; i++) {
            float leftFraction = (i * 53) % 100 / 100f;
            float x = leftFraction * w;
            float progress = rainProgress[i];
            float y = startY + progress * fallDistance;
            int alpha = Math.round(255 * 0.2f * (1f - progress));
            rainPaint.setAlpha(Math.max(0, alpha));
            canvas.drawRect(x, y, x + dropWidth, y + dropHeight, rainPaint);
        }
    }

    private void drawForest(Canvas canvas, float scale, float offsetX, float offsetY) {
        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale, scale);

        for (int i = 0; i < TREES.length; i++) {
            float x = TREES[i][0];
            float y = TREES[i][1];
            float treeH = TREES[i][2];
            float baseY = y + treeH;

            canvas.save();
            canvas.rotate(treeAngleDeg[i], x, baseY);

            Path tree = new Path();
            tree.moveTo(x, y);
            tree.lineTo(x - 30, baseY);
            tree.lineTo(x + 30, baseY);
            tree.close();
            canvas.drawPath(tree, treePaint);

            canvas.restore();
        }
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (ValueAnimator a : animators) a.cancel();
        animators.clear();
    }
}
