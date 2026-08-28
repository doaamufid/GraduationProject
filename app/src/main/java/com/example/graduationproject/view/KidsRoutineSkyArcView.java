package com.example.graduationproject.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.example.graduationproject.util.KidsRoutineColorUtils;

/**
 * Port of the "رحلة السماء" (sky journey) SVG from the React component:
 *
 *   <path d="M 20 92 Q 180 -6 340 92" .../>   (background track, virtual box 360x110)
 *   + a matching progress-colored sub-path whose length grows with `progress`
 *   + a moving emoji marker that slides along the curve
 *   + a house emoji (🏡) fixed at the end, glowing once progress === 1
 *
 * All coordinates are defined in a fixed virtual box (VB_W x VB_H) and scaled
 * uniformly to the view's actual pixel size in onDraw(), exactly like an SVG
 * viewBox would.
 */
public class KidsRoutineSkyArcView extends View {

    private static final float VB_W = 360f;
    private static final float VB_H = 110f;
    private static final float Y_OFFSET = 14f; // keeps the arc peak (y=-6) inside the canvas

    private final Path bgPath = new Path();
    private final Path progressPathVisible = new Path();
    private final PathMeasure pathMeasure = new PathMeasure();

    private final Paint bgArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emojiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint housePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float displayedProgress = 0f;
    private float targetProgress = 0f;
    private ValueAnimator progressAnimator;

    private float markerX, markerY;
    private float pathTotalLength = 0f;

    // house glow pulse (only runs while fully complete)
    private ValueAnimator glowAnimator;
    private float glowStrength = 0f;

    public KidsRoutineSkyArcView(Context context) {
        super(context);
        init();
    }

    public KidsRoutineSkyArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        bgPath.moveTo(20, 92 + Y_OFFSET);
        bgPath.quadTo(180, -6 + Y_OFFSET, 340, 92 + Y_OFFSET);

        pathMeasure.setPath(bgPath, false);
        pathTotalLength = pathMeasure.getLength();
        float[] pos = new float[2];
        pathMeasure.getPosTan(0, pos, null);
        markerX = pos[0];
        markerY = pos[1];

        bgArcPaint.setStyle(Paint.Style.STROKE);
        bgArcPaint.setStrokeWidth(6f);
        bgArcPaint.setStrokeCap(Paint.Cap.ROUND);
        bgArcPaint.setColor(Color.argb(140, 255, 255, 255)); // rgba(255,255,255,0.55)

        progressArcPaint.setStyle(Paint.Style.STROKE);
        progressArcPaint.setStrokeWidth(6f);
        progressArcPaint.setStrokeCap(Paint.Cap.ROUND);
        progressArcPaint.setColor(0xFFFFB627); // accent orange

        emojiPaint.setTextAlign(Paint.Align.CENTER);
        emojiPaint.setTextSize(26f);

        housePaint.setTextAlign(Paint.Align.CENTER);
        housePaint.setTextSize(22f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width * (VB_H / VB_W));
        setMeasuredDimension(width, height);
    }

    /**
     * Sets the fraction of the day completed (0..1). Animates smoothly to the new
     * value, mirroring the CSS `transition: stroke-dasharray 0.6s ease` and the
     * `transition: x 0.6s ease, y 0.6s ease` on the moving marker.
     */
    public void setProgress(float progress, boolean animate) {
        targetProgress = Math.max(0f, Math.min(1f, progress));

        if (progressAnimator != null) {
            progressAnimator.cancel();
        }

        if (!animate) {
            displayedProgress = targetProgress;
            recomputeGeometry();
            invalidate();
            updateGlow();
            return;
        }

        progressAnimator = ValueAnimator.ofFloat(displayedProgress, targetProgress);
        progressAnimator.setDuration(600);
        progressAnimator.setInterpolator(new DecelerateInterpolator(1.2f));
        progressAnimator.addUpdateListener(a -> {
            displayedProgress = (float) a.getAnimatedValue();
            recomputeGeometry();
            invalidate();
        });
        progressAnimator.start();
        updateGlow();
    }

    private void recomputeGeometry() {
        float[] pos = new float[2];
        pathMeasure.getPosTan(pathTotalLength * displayedProgress, pos, null);
        markerX = pos[0];
        markerY = pos[1];

        progressPathVisible.reset();
        pathMeasure.getSegment(0, pathTotalLength * displayedProgress, progressPathVisible, true);
    }

    private void updateGlow() {
        boolean complete = targetProgress >= 1f;
        if (complete && glowAnimator == null) {
            glowAnimator = ValueAnimator.ofFloat(0f, 1f, 0f);
            glowAnimator.setDuration(1600);
            glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
            glowAnimator.setInterpolator(new LinearInterpolator());
            glowAnimator.addUpdateListener(a -> {
                glowStrength = (float) a.getAnimatedValue();
                invalidate();
            });
            glowAnimator.start();
        } else if (!complete && glowAnimator != null) {
            glowAnimator.cancel();
            glowAnimator = null;
            glowStrength = 0f;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() == 0) return;

        float scale = getWidth() / VB_W;
        canvas.save();
        canvas.scale(scale, scale);

        // background track
        canvas.drawPath(bgPath, bgArcPaint);

        // progress track (grows with displayedProgress)
        if (displayedProgress > 0f) {
            canvas.drawPath(progressPathVisible, progressArcPaint);
        }

        // house at the end of the path, with a soft glow once the day is complete
        float houseX = 340, houseY = 92 + Y_OFFSET;
        if (glowStrength > 0.01f) {
            housePaint.setShadowLayer(4f + glowStrength * 10f, 0f, 0f,
                    Color.argb((int) (140 + glowStrength * 115), 255, 217, 142));
        } else {
            housePaint.clearShadowLayer();
        }
        drawEmojiCentered(canvas, "🏡", houseX, houseY, housePaint);

        // moving marker (sun -> partly cloudy -> cloudy -> sunset -> moon)
        drawEmojiCentered(canvas, KidsRoutineColorUtils.skyMarker(displayedProgress), markerX, markerY, emojiPaint);

        canvas.restore();
    }

    private void drawEmojiCentered(Canvas canvas, String text, float x, float y, Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        float textY = y - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(text, x, textY, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (progressAnimator != null) progressAnimator.cancel();
        if (glowAnimator != null) glowAnimator.cancel();
    }
}
