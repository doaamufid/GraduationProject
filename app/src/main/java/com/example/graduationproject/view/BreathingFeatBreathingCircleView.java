package com.example.graduationproject.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Port of the JS <GlassCircle/> + the SVG progress ring inside <SessionPanel/>:
 * an outer stroked circle showing phase progress (0..1), a soft pulsing glow ring,
 * and an inner gradient "glass" disc that grows/shrinks with breathing (growth 0..1),
 * with the seconds-left number and phase label centered inside.
 */
public class BreathingFeatBreathingCircleView extends View {

    private float progress = 0f;     // 0..1 phase progress, drives the stroke arc
    private float growth = 0f;       // 0..1 inhale/exhale scale factor (0=small,1=full)
    private boolean idle = true;     // idle breathing pulse vs active session
    private int ringColor = 0xFF6FAE6F;
    private String numberText = "";
    private String labelText = "";
    private int textColor = 0xFF2F4D43;
    private int subTextColor = 0xFF4F7364;
    private int glassBase1 = 0xFFFFFFFF;
    private int glassBase2 = 0xFFEEF8F0;

    private float idlePhase = 0f;
    private ValueAnimator idleAnimator;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF ringRect = new RectF();

    public BreathingFeatBreathingCircleView(Context context) { super(context); init(); }
    public BreathingFeatBreathingCircleView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        idleAnimator = ValueAnimator.ofFloat(0f, (float) (Math.PI * 2));
        idleAnimator.setDuration(4500);
        idleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        idleAnimator.addUpdateListener(a -> {
            idlePhase = (float) a.getAnimatedValue();
            if (idle) invalidate();
        });
        idleAnimator.start();
    }

    public void setColors(int textColor, int subTextColor, int glassBase1, int glassBase2) {
        this.textColor = textColor;
        this.subTextColor = subTextColor;
        this.glassBase1 = glassBase1;
        this.glassBase2 = glassBase2;
        invalidate();
    }

    public void setIdle(boolean idle) { this.idle = idle; invalidate(); }

    public void setRingColor(int color) { this.ringColor = color; invalidate(); }

    /** progress: 0..1 phase completion (drives the stroked ring). */
    public void setProgress(float progress) { this.progress = progress; invalidate(); }

    /** growth: 0..1, mirrors {in: progress, hold1: 1, out: 1-progress, hold2: 0}. */
    public void setGrowth(float growth) { this.growth = growth; invalidate(); }

    public void setCenterText(String number, String label) {
        this.numberText = number;
        this.labelText = label;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;
        float cx = w / 2f, cy = h / 2f;
        float outerR = Math.min(w, h) / 2f - dp(6);

        // idle gentle breathing pulse, or active growth-driven scale
        float scale = idle ? 1f + 0.045f * (float) Math.sin(idlePhase) : 1f + 0.15f * growth;
        float glowOpacity = idle ? 0.28f : 0.12f + 0.12f * growth;

        paint.reset();
        paint.setAntiAlias(true);

        // outer glow
        paint.setShader(new RadialGradient(cx, cy, outerR * 1.25f,
                withAlpha(ringColor, glowOpacity), withAlpha(ringColor, 0f), Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, outerR * 1.25f, paint);
        paint.setShader(null);

        // pulse ring (only while active, mirrors CSS ringPulse keyframe)
        if (!idle) {
            float pulseR = outerR * (0.9f + 0.35f * ((idlePhase / (float) (Math.PI * 2)) % 1f));
            float pulseAlpha = 0.3f * (1f - ((idlePhase / (float) (Math.PI * 2)) % 1f));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(withAlpha(ringColor, pulseAlpha));
            canvas.drawCircle(cx, cy, pulseR, paint);
        }

        // progress ring track
        ringRect.set(cx - outerR, cy - outerR, cx + outerR, cy + outerR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(4));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(withAlpha(subTextColor, 0.2f));
        canvas.drawOval(ringRect, paint);

        // progress ring fill (starts at top, like the CSS rotate(-90deg))
        paint.setColor(ringColor);
        canvas.drawArc(ringRect, -90, 360f * (idle ? 0f : progress), false, paint);

        // inner glass disc
        float discR = outerR - dp(10);
        canvas.save();
        canvas.scale(scale, scale, cx, cy);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(cx - discR * 0.32f, cy - discR * 0.35f, discR * 1.5f,
                new int[]{glassBase1, glassBase2, withAlpha(ringColor, idle ? 0.2f : 0.25f + 0.15f * growth), glassBase1},
                new float[]{0f, 0.35f, 0.65f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, discR, paint);
        paint.setShader(null);

        paint.setColor(withAlpha(ringColor, 0.3f));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        canvas.drawCircle(cx, cy, discR, paint);
        canvas.restore();

        // center text
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(textColor);
        paint.setFakeBoldText(true);
        paint.setTextSize(dp(40));
        canvas.drawText(numberText, cx, cy - dp(2), paint);
        paint.setFakeBoldText(false);
        paint.setTextSize(dp(15));
        paint.setColor(subTextColor);
        canvas.drawText(labelText, cx, cy + dp(24), paint);
    }

    private int withAlpha(int color, float a) {
        int alpha = Math.round(Math.max(0f, Math.min(1f, a)) * 255);
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private float dp(float v) { return v * getResources().getDisplayMetrics().density; }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (idleAnimator != null) idleAnimator.cancel();
    }
}
