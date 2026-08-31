package com.example.graduationproject.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.example.graduationproject.util.ChimePlayer;
import com.example.graduationproject.util.ColorUtils;
import com.example.graduationproject.util.TimeOfDayUtils;

/**
 * Port of the React "AdultProgressCard": a hiker climbing a mountain ridge,
 * with the sky/mountain colors tied to the real time of day and the trail
 * progress tied only to completed/total habits.
 */
public class AdultHabitProgAdultProgressCardView extends View {

    private String label = "مسار اليوم";
    private int completed = 0;
    private int total = 5;
    private int streakDays = 0;
    private boolean soundEnabled = true;

    private float animatedPercent = 0f;
    private ValueAnimator progressAnimator;
    private final ChimePlayer chime = new ChimePlayer();

    private boolean justArrived = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final Path path = new Path();

    public AdultHabitProgAdultProgressCardView(Context context) { super(context); }
    public AdultHabitProgAdultProgressCardView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }

    public void setSoundEnabled(boolean enabled) { this.soundEnabled = enabled; chime.setSoundEnabled(enabled); }
    public void setLabel(String label) { this.label = label; invalidate(); }
    public void setTotal(int total) { this.total = Math.max(1, total); invalidate(); }
    public void setStreakDays(int s) { this.streakDays = s; invalidate(); }

    public void setCompleted(int newCompleted) {
        int prev = this.completed;
        int clamped = Math.max(0, Math.min(newCompleted, total));
        this.completed = clamped;
        float newPercent = total > 0 ? (clamped / (float) total) * 100 : 0;

        if (clamped > prev) {
            if (clamped >= total) {
                if (soundEnabled) chime.playSummit();
                justArrived = true;
                handler.postDelayed(() -> { justArrived = false; invalidate(); }, 2200);
            } else if (soundEnabled) {
                chime.playStep();
            }
        }
        animateProgressTo(newPercent);
    }

    private void animateProgressTo(float target) {
        if (progressAnimator != null) progressAnimator.cancel();
        progressAnimator = ValueAnimator.ofFloat(animatedPercent, target);
        progressAnimator.setDuration(1100);
        progressAnimator.setInterpolator(new OvershootInterpolator(1f));
        progressAnimator.addUpdateListener(a -> { animatedPercent = (float) a.getAnimatedValue(); invalidate(); });
        progressAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        TimeOfDayUtils.Snapshot t = TimeOfDayUtils.compute();
        boolean isComplete = completed >= total && total > 0;

        int[] skyColors = {0xFF161A2C, 0xFFF4DCC9, 0xFFEAF2FB, 0xFFE3A97D, 0xFF161A2C};
        float[] stops = {0f, 0.25f, 0.5f, 0.75f, 1f};
        int skyColor = ColorUtils.multiLerp(skyColors, stops, t.hour / 24f);
        int cardBase = ColorUtils.lerpColor(0xFFF6F1E7, 0xFF14151F, t.darkness);
        float textT = ColorUtils.smoothstep(t.darkness, 0.55f, 0.8f);
        int textColor = ColorUtils.lerpColor(0xFF20263B, 0xFFF4E9DA, textT);
        int mutedColor = ColorUtils.lerpColor(0xFF5B6280, 0xFF9AA1BE, textT);
        int goldAccent = ColorUtils.lerpColor(0xFFB8823B, 0xFFE0A85C, t.darkness);
        int mountainBack = ColorUtils.lerpColor(0xFFC7D5EA, 0xFF333B5E, t.darkness);
        int mountainFront = ColorUtils.lerpColor(0xFF5C6690, 0xFF454F7A, t.darkness);
        int figureColor = ColorUtils.lerpColor(0xFF2B3350, 0xFFF4E3C4, ColorUtils.smoothstep(t.darkness, 0.4f, 0.7f));

        rect.set(0, 0, w, h);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setShader(new android.graphics.LinearGradient(0, 0, 0, h,
                new int[]{skyColor, skyColor, cardBase}, new float[]{0f, 0.30f, 0.85f}, android.graphics.Shader.TileMode.CLAMP));
        canvas.drawRoundRect(rect, dp(26), dp(26), paint);
        paint.setShader(null);

        float pad = dp(22);

        // sky strip: stars + orb
        float stripTop = pad, stripH = dp(46);
        float starAlpha = ColorUtils.smoothstep(t.darkness, 0.5f, 0.85f);
        paint.setColor(Color.argb((int) (255 * starAlpha), 0xDC, 0xE3, 0xF5));
        canvas.drawCircle(w - pad - (w - 2 * pad) * 0.22f, stripTop + dp(4), dp(1.6f), paint);
        canvas.drawCircle(w - pad - (w - 2 * pad) * 0.55f, stripTop + dp(18), dp(1.2f), paint);
        canvas.drawCircle(w - pad - (w - 2 * pad) * 0.80f, stripTop + dp(2), dp(1.6f), paint);

        float orbCx = w - pad - (w - 2 * pad) * (t.orbRightPercent / 100f);
        float orbCy = stripTop + stripH * (t.orbTopPercent / 100f);
        float orbR = dp(15);
        if (t.isDaytime) {
            paint.setColor(0xFFFFB347);
            canvas.drawCircle(orbCx, orbCy, orbR, paint);
            paint.setColor(0xFFFFC857);
            paint.setStrokeWidth(dp(2));
            for (int i = 0; i < 8; i++) {
                double ang = Math.toRadians(i * 45);
                canvas.drawLine((float) (orbCx + Math.cos(ang) * (orbR + dp(2))), (float) (orbCy + Math.sin(ang) * (orbR + dp(2))),
                        (float) (orbCx + Math.cos(ang) * (orbR + dp(8))), (float) (orbCy + Math.sin(ang) * (orbR + dp(8))), paint);
            }
        } else {
            paint.setColor(0xFFC9D3EE);
            canvas.drawCircle(orbCx, orbCy, orbR, paint);
        }

        // header
        float headerTop = stripTop + stripH + dp(6);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(goldAccent);
        paint.setTextSize(dp(11));
        paint.setFakeBoldText(true);
        canvas.drawText(label, w - pad, headerTop + dp(10), paint);

        String stage;
        if (completed == 0) stage = "ابدأ يومك";
        else if (isComplete) stage = "أنجزت يومك، ليلة هنيئة";
        else {
            float pct = (completed / (float) total) * 100;
            if (pct < 40) stage = "بداية الطريق";
            else if (pct < 75) stage = "في المسار الصحيح";
            else stage = "قريب من القمة";
        }
        paint.setColor(textColor);
        paint.setTextSize(dp(15));
        canvas.drawText(stage, w - pad, headerTop + dp(30), paint);
        paint.setFakeBoldText(false);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(mutedColor);
        paint.setTextSize(dp(11));
        canvas.drawText(streakDays + " أيام متتالية 🔥", pad, headerTop + dp(14), paint);

        // big number
        float figTop = headerTop + dp(48);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(textColor);
        paint.setTextSize(dp(44));
        paint.setFakeBoldText(true);
        canvas.drawText(String.valueOf(completed), w - pad, figTop + dp(40), paint);
        float numW = paint.measureText(String.valueOf(completed));
        paint.setFakeBoldText(false);
        paint.setTextSize(dp(22));
        paint.setColor(mutedColor);
        canvas.drawText("/" + total, w - pad - numW - dp(6), figTop + dp(34), paint);
        paint.setTextSize(dp(12));
        canvas.drawText("عادات مكتملة", w - pad - numW - dp(60), figTop + dp(40), paint);

        if (isComplete) {
            String badge = "🏔 أنجزت يومك";
            paint.setTextSize(dp(12.5f));
            float badgeW = paint.measureText(badge) + dp(24);
            rect.set(pad, figTop + dp(52), pad + badgeW, figTop + dp(80));
            paint.setColor(goldAccent);
            canvas.drawRoundRect(rect, dp(16), dp(16), paint);
            paint.setColor(0xFF171B30);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(badge, pad + dp(12), figTop + dp(70), paint);
        }

        // mountain ridge + trail
        float ridgeTop = figTop + (isComplete ? dp(96) : dp(70));
        float ridgeBottom = h - dp(16);
        drawMountains(canvas, pad, ridgeTop, w - pad, ridgeBottom, mountainBack, mountainFront);
        drawTrail(canvas, pad + dp(6), ridgeBottom - dp(4), w - pad - dp(6), goldAccent, figureColor, textColor);
    }

    private void drawMountains(Canvas canvas, float left, float top, float right, float bottom, int back, int front) {
        paint.reset();
        paint.setAntiAlias(true);
        float h = bottom - top;
        float wSpan = right - left;

        path.reset();
        path.moveTo(left, bottom);
        path.lineTo(left + wSpan * 0.15f, top + h * 0.15f);
        path.lineTo(left + wSpan * 0.27f, top + h * 0.55f);
        path.lineTo(left + wSpan * 0.42f, top);
        path.lineTo(left + wSpan * 0.57f, top + h * 0.45f);
        path.lineTo(left + wSpan * 0.72f, top + h * 0.1f);
        path.lineTo(left + wSpan * 0.85f, top + h * 0.4f);
        path.lineTo(right, top + h * 0.2f);
        path.lineTo(right, bottom);
        path.close();
        paint.setColor(ColorUtils.withAlpha(back, 0.7f));
        canvas.drawPath(path, paint);

        float frontTop = top + h * 0.35f;
        path.reset();
        path.moveTo(left, bottom);
        path.lineTo(left + wSpan * 0.12f, frontTop + h * 0.25f);
        path.lineTo(left + wSpan * 0.25f, bottom - h * 0.05f);
        path.lineTo(left + wSpan * 0.40f, frontTop);
        path.lineTo(left + wSpan * 0.55f, bottom - h * 0.05f);
        path.lineTo(left + wSpan * 0.70f, frontTop + h * 0.1f);
        path.lineTo(left + wSpan * 0.82f, bottom - h * 0.07f);
        path.lineTo(right, frontTop + h * 0.15f);
        path.lineTo(right, bottom);
        path.close();
        paint.setColor(front);
        canvas.drawPath(path, paint);
    }

    private void drawTrail(Canvas canvas, float left, float y, float right, int gold, int hikerColor, int tickColor) {
        paint.reset();
        paint.setAntiAlias(true);

        rect.set(left, y - dp(1.5f), right, y + dp(1.5f));
        paint.setColor(ColorUtils.withAlpha(tickColor, 0.22f));
        canvas.drawRoundRect(rect, dp(2), dp(2), paint);

        float fillWidth = (right - left) * (animatedPercent / 100f);
        rect.set(right - fillWidth, y - dp(1.5f), right, y + dp(1.5f));
        paint.setColor(gold);
        canvas.drawRoundRect(rect, dp(2), dp(2), paint);

        int tickLimit = 8;
        if (total <= tickLimit) {
            for (int i = 0; i < total; i++) {
                float posT = (i + 1) / (float) total;
                float cx = right - (right - left) * posT;
                boolean filled = i < completed;
                rect.set(cx - dp(1.5f), y - dp(5), cx + dp(1.5f), y + dp(5));
                paint.setColor(filled ? 0xFFFFDA8A : ColorUtils.withAlpha(tickColor, 0.3f));
                canvas.drawRoundRect(rect, dp(2), dp(2), paint);
            }
        }

        // flag at the summit (left end, RTL "end")
        paint.setColor(gold);
        paint.setStrokeWidth(dp(1.6f));
        canvas.drawLine(left - dp(2), y + dp(10), left - dp(2), y - dp(20), paint);
        path.reset();
        path.moveTo(left - dp(2), y - dp(19));
        path.lineTo(left + dp(10), y - dp(15));
        path.lineTo(left - dp(2), y - dp(11));
        path.close();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(completed >= total && total > 0 ? 0xFFFFDA8A : hikerColor);
        canvas.drawPath(path, paint);

        // hiker figure walking along the trail
        float hikerCx = right - (right - left) * (animatedPercent / 100f);
        paint.setColor(hikerColor);
        paint.setStrokeWidth(dp(2.2f));
        float hy = y - dp(18);
        canvas.drawCircle(hikerCx, hy - dp(6), dp(4.5f), paint);
        canvas.drawLine(hikerCx, hy - dp(1), hikerCx, hy + dp(10), paint);
        canvas.drawLine(hikerCx, hy + dp(2), hikerCx - dp(6), hy + dp(6), paint);
        canvas.drawLine(hikerCx, hy + dp(2), hikerCx + dp(6), hy - dp(2), paint);
        canvas.drawLine(hikerCx, hy + dp(10), hikerCx - dp(4), hy + dp(18), paint);
        canvas.drawLine(hikerCx, hy + dp(10), hikerCx + dp(4), hy + dp(18), paint);

        if (justArrived) {
            paint.setColor(ColorUtils.withAlpha(0xFFFFDA8A, 0.5f));
            canvas.drawCircle(hikerCx, hy - dp(4), dp(16), paint);
        }
    }

    private float dp(float v) { return v * getResources().getDisplayMetrics().density; }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (progressAnimator != null) progressAnimator.cancel();
    }
}
