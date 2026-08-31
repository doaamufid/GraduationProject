package com.example.graduationproject.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
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
 * Full port of the React "DailyProgressCard" (kids version):
 * - Sky/background color follows the REAL wall-clock time (day/noon/evening/night),
 *   fully independent from habit progress.
 * - A switchable critter (hedgehog / bear / butterfly) walks along a path
 *   whose length reflects completed/total habits.
 * - Sleep -> wake -> walk -> run -> celebrate animation states, matching the JS state machine.
 */
public class KidsHabitProgDailyProgressCardView extends View {

    public enum Species { HEDGEHOG("🦔"), BEAR("🐻"), BUTTERFLY("🦋");
        public final String emoji;
        Species(String e) { this.emoji = e; } }

    private String childName = "الطفل";
    private int completed = 0;
    private int total = 5;
    private int streak = 0;
    private boolean soundEnabled = true;
    private Species species = Species.HEDGEHOG;

    private float animatedPercent = 0f; // 0..100, smoothly animated
    private ValueAnimator progressAnimator;
    private final ChimePlayer chime = new ChimePlayer();

    // continuous animation phase (bob / walk cycle / sparkle twinkle)
    private float loopPhase = 0f;
    private ValueAnimator loopAnimator;

    // transient state: null | "wake" | "wave" | "celebrate"
    private String transientState = null;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable clearTransientRunnable;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    public KidsHabitProgDailyProgressCardView(Context context) { super(context); init(); }
    public KidsHabitProgDailyProgressCardView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        loopAnimator = ValueAnimator.ofFloat(0f, 1f);
        loopAnimator.setDuration(1600);
        loopAnimator.setRepeatCount(ValueAnimator.INFINITE);
        loopAnimator.setInterpolator(null);
        loopAnimator.addUpdateListener(a -> {
            loopPhase = (loopPhase + 0.016f) % 1000f;
            invalidate();
        });
        loopAnimator.start();
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        chime.setSoundEnabled(enabled);
    }

    public void setSpecies(Species s) { this.species = s; invalidate(); }

    public void setChildName(String name) { this.childName = name; invalidate(); }

    public void setTotal(int total) { this.total = Math.max(1, total); invalidate(); }

    public void setStreak(int streak) { this.streak = streak; invalidate(); }

    /** Mirrors the JS useEffect that reacts to `completed` changing. */
    public void setCompleted(int newCompleted) {
        int prev = this.completed;
        int clampedNew = Math.max(0, Math.min(newCompleted, total));
        this.completed = clampedNew;

        float prevPercent = total > 0 ? (prev / (float) total) * 100 : 0;
        float newPercent = total > 0 ? (clampedNew / (float) total) * 100 : 0;

        if (clampedNew > prev) {
            if (clampedNew >= total) {
                if (soundEnabled) chime.playFanfare();
                triggerTransient("celebrate", 2400);
            } else {
                if (soundEnabled) chime.playTick();
                if (prev == 0) {
                    triggerTransient("wake", 550);
                } else if (prevPercent < 50 && newPercent >= 50) {
                    triggerTransient("wave", 900);
                }
            }
        }
        animateProgressTo(newPercent);
    }

    private void triggerTransient(String state, long durationMs) {
        this.transientState = state;
        if (clearTransientRunnable != null) handler.removeCallbacks(clearTransientRunnable);
        clearTransientRunnable = () -> { transientState = null; invalidate(); };
        handler.postDelayed(clearTransientRunnable, durationMs);
        invalidate();
    }

    private void animateProgressTo(float target) {
        if (progressAnimator != null) progressAnimator.cancel();
        progressAnimator = ValueAnimator.ofFloat(animatedPercent, target);
        progressAnimator.setDuration(1100);
        progressAnimator.setInterpolator(new OvershootInterpolator(1.1f));
        progressAnimator.addUpdateListener(a -> {
            animatedPercent = (float) a.getAnimatedValue();
            invalidate();
        });
        progressAnimator.start();
    }

    private String baseMode() {
        float percent = total > 0 ? (completed / (float) total) * 100 : 0;
        if (completed == 0) return "sleep";
        if (completed >= total) return "idle-complete";
        if (percent >= 80) return "run";
        return "walk";
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        TimeOfDayUtils.Snapshot t = TimeOfDayUtils.compute();

        // ---- sky/background color driven purely by real time ----
        int[] skyColors = {0xFF1B2A22, 0xFFDCEFC0, 0xFFEAF6D8, 0xFFE0C87E, 0xFF1B2A22};
        float[] stops = {0f, 0.25f, 0.5f, 0.75f, 1f};
        int skyColor = ColorUtils.multiLerp(skyColors, stops, t.hour / 24f);

        rect.set(0, 0, w, h);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(skyColor);
        canvas.drawRoundRect(rect, dp(30), dp(30), paint);

        float pad = dp(22);
        float decorationDim = 1 - t.darkness * 0.42f;

        drawDecorations(canvas, w, h, decorationDim);

        // ---- sky strip (sun/moon + stars) ----
        float skyStripTop = pad;
        float skyStripHeight = dp(44);
        drawSkyStrip(canvas, w, pad, skyStripTop, skyStripHeight, t);

        float headerTop = skyStripTop + skyStripHeight + dp(12);
        drawHeader(canvas, w, pad, headerTop);

        float trackTop = headerTop + dp(56);
        float trackHeight = dp(90);
        drawTrack(canvas, pad, trackTop, w - pad, trackTop + trackHeight);

        float footerTop = trackTop + trackHeight + dp(14);
        drawFooter(canvas, pad, footerTop, w - pad);
    }

    private void drawDecorations(Canvas canvas, int w, int h, float dim) {
        paint.reset();
        paint.setAntiAlias(true);
        int alpha = (int) (255 * 0.5f * dim);
        // simple tree circles (top-right / top-left), mushroom bottom-left — simplified vector shapes
        paint.setColor(Color.argb(alpha, 0xA9, 0xD1, 0x8E));
        canvas.drawCircle(w - dp(15), dp(10), dp(45), paint);
        paint.setColor(Color.argb((int) (alpha * 0.8f), 0xBF, 0xE0, 0xA0));
        canvas.drawCircle(dp(20), dp(15), dp(32), paint);

        paint.setColor(Color.argb((int) (255 * 0.7f * dim), 0xE4, 0x68, 0x5C));
        canvas.drawCircle(dp(28), h - dp(60), dp(16), paint);
    }

    private void drawSkyStrip(Canvas canvas, int w, float pad, float top, float height, TimeOfDayUtils.Snapshot t) {
        paint.reset();
        paint.setAntiAlias(true);
        // stars (only visible as darkness increases)
        float starAlphaF = ColorUtils.smoothstep(t.darkness, 0.55f, 0.9f);
        paint.setColor(Color.argb((int) (255 * starAlphaF), 255, 255, 255));
        canvas.drawCircle(w - pad - (w - 2 * pad) * 0.2f, top + dp(4), dp(1.6f), paint);
        canvas.drawCircle(w - pad - (w - 2 * pad) * 0.5f, top + dp(16), dp(1.2f), paint);
        canvas.drawCircle(w - pad - (w - 2 * pad) * 0.76f, top, dp(1.2f), paint);

        float orbCx = w - pad - (w - 2 * pad) * (t.orbRightPercent / 100f);
        float orbCy = top + height * (t.orbTopPercent / 100f);
        float orbR = dp(13);
        if (t.isDaytime) {
            paint.setColor(0xFFFFB347);
            canvas.drawCircle(orbCx, orbCy, orbR, paint);
            paint.setColor(0xFFFFC857);
            paint.setStrokeWidth(dp(2));
            for (int i = 0; i < 8; i++) {
                double ang = Math.toRadians(i * 45);
                canvas.drawLine(
                        (float) (orbCx + Math.cos(ang) * (orbR + dp(2))), (float) (orbCy + Math.sin(ang) * (orbR + dp(2))),
                        (float) (orbCx + Math.cos(ang) * (orbR + dp(7))), (float) (orbCy + Math.sin(ang) * (orbR + dp(7))), paint);
            }
        } else {
            paint.setColor(0xFFC9D3EE);
            canvas.drawCircle(orbCx, orbCy, orbR, paint);
            paint.setColor(0xFFF6F8FF);
            canvas.drawCircle(orbCx - dp(3), orbCy - dp(3), orbR * 0.7f, paint);
        }
    }

    private void drawHeader(Canvas canvas, int w, float pad, float top) {
        paint.reset();
        paint.setAntiAlias(true);

        // avatar circle
        float avatarR = dp(23);
        float avatarCx = w - pad - avatarR;
        float avatarCy = top + avatarR;
        paint.setColor(Color.WHITE);
        canvas.drawCircle(avatarCx, avatarCy, avatarR, paint);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(dp(22));
        canvas.drawText(species.emoji, avatarCx, avatarCy + dp(8), paint);

        // eyebrow + title (RTL: text grows to the left of avatar)
        float textRight = avatarCx - avatarR - dp(10);
        paint.setColor(0xFF5FA83A);
        paint.setTextSize(dp(12));
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("رحلة " + childName + " اليوم", textRight, top + dp(12), paint);

        String title;
        if (completed == 0) title = "لسه ما بدأنا... يلا نوقظه!";
        else if (completed >= total) title = "أنجزت كل عاداتك! 🌟";
        else title = "كمّل خطواتك اليوم";
        paint.setColor(0xFF2F4B2A);
        paint.setTextSize(dp(16));
        canvas.drawText(title, textRight, top + dp(32), paint);
        paint.setFakeBoldText(false);

        // streak pill (left side)
        float pillLeft = pad;
        float pillTop = top + dp(2);
        float pillW = dp(58), pillH = dp(30);
        rect.set(pillLeft, pillTop, pillLeft + pillW, pillTop + pillH);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(rect, pillH / 2, pillH / 2, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(dp(14));
        canvas.drawText("🔥", pillLeft + dp(8), pillTop + dp(20), paint);
        paint.setColor(0xFFE4685C);
        paint.setFakeBoldText(true);
        canvas.drawText(String.valueOf(streak), pillLeft + dp(30), pillTop + dp(20), paint);
        paint.setFakeBoldText(false);
    }

    private void drawTrack(Canvas canvas, float left, float top, float right, float bottom) {
        paint.reset();
        paint.setAntiAlias(true);
        boolean isComplete = completed >= total && total > 0;

        rect.set(left, top, right, bottom);
        paint.setColor(isComplete ? 0x8CFFECB0 : 0x59FFFFFF);
        canvas.drawRoundRect(rect, dp(22), dp(22), paint);

        float sceneLeft = left + dp(18);
        float sceneRight = right - dp(18);
        float pathTop = top + dp(40);
        float pathHeight = dp(10);

        // home / gift icons
        paint.setTextSize(dp(20));
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("🏠", sceneRight - dp(14), pathTop + dp(8), paint); // RTL start = right
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("🎁", sceneLeft + dp(14), pathTop + dp(6), paint); // RTL end = left

        float pathLeft = sceneLeft + dp(26);
        float pathRight = sceneRight - dp(26);
        rect.set(pathLeft, pathTop, pathRight, pathTop + pathHeight);
        paint.setColor(0xFFF4E4C1);
        canvas.drawRoundRect(rect, pathHeight / 2, pathHeight / 2, paint);

        // fill grows from the RIGHT (RTL), matches `right: 0` fill in CSS
        float fillWidth = (pathRight - pathLeft) * (animatedPercent / 100f);
        rect.set(pathRight - fillWidth, pathTop, pathRight, pathTop + pathHeight);
        paint.setShader(new LinearGradient(pathRight - fillWidth, 0, pathRight, 0,
                0xFF8FD65C, 0xFF5FA83A, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(rect, pathHeight / 2, pathHeight / 2, paint);
        paint.setShader(null);

        // dots (ticks) — one per habit if <= 8, else 25/50/75 markers
        int tickLimit = 8;
        if (total <= tickLimit) {
            for (int i = 0; i < total; i++) {
                float posT = (i + 1) / (float) total;
                float cx = pathRight - (pathRight - pathLeft) * posT;
                float cy = pathTop + pathHeight / 2f;
                boolean filled = i < completed;
                paint.setColor(filled ? 0xFF5FA83A : Color.WHITE);
                canvas.drawCircle(cx, cy, dp(8), paint);
                paint.setColor(filled ? 0xFF3F7A26 : 0xFFE0C88F);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(1.5f));
                canvas.drawCircle(cx, cy, dp(8), paint);
                paint.setStyle(Paint.Style.FILL);
            }
        }

        // walking critter, follows animatedPercent, bobbing with loopPhase
        float walkerCx = pathRight - (pathRight - pathLeft) * (animatedPercent / 100f);
        float bob = (float) Math.sin(loopPhase * Math.PI * 2 * (baseMode().equals("run") ? 3.5 : 1.8)) * dp(5);
        String mode = transientState != null ? transientState : baseMode();
        float scale = mode.equals("celebrate") ? 1.3f : mode.equals("sleep") ? 0.9f : 1f;

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(dp(30) * scale);
        String emoji = species.emoji;
        float walkerCy = pathTop - dp(6) - Math.abs(bob) - (mode.equals("sleep") ? -dp(4) : 0);
        canvas.drawText(emoji, walkerCx, walkerCy, paint);

        if (mode.equals("sleep")) {
            paint.setTextSize(dp(11));
            paint.setColor(0xFF7A8FA0);
            canvas.drawText("Z z", walkerCx + dp(14), walkerCy - dp(18), paint);
        } else if (mode.equals("wave")) {
            paint.setTextSize(dp(16));
            canvas.drawText("😊", walkerCx + dp(2), walkerCy - dp(22), paint);
        }
    }

    private void drawFooter(Canvas canvas, float left, float top, float right) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(dp(22));
        paint.setFakeBoldText(true);
        paint.setColor(0xFF2F4B2A);
        canvas.drawText(String.valueOf(completed), right, top + dp(22), paint);
        float numW = paint.measureText(String.valueOf(completed));

        paint.setTextSize(dp(16));
        paint.setColor(0xFF5FA83A);
        canvas.drawText("/" + total + "  عادات اليوم", right - numW - dp(4), top + dp(20), paint);
        paint.setFakeBoldText(false);

        if (completed >= total && total > 0) {
            String badge = "🎉 أحسنت!";
            paint.setTextSize(dp(13));
            float badgeW = paint.measureText(badge) + dp(24);
            rect.set(left, top - dp(2), left + badgeW, top + dp(26));
            paint.setColor(0xFFFFD35C);
            canvas.drawRoundRect(rect, dp(16), dp(16), paint);
            paint.setColor(0xFF8A5A00);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(badge, left + dp(12), top + dp(17), paint);
        }
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (loopAnimator != null) loopAnimator.cancel();
        if (progressAnimator != null) progressAnimator.cancel();
        if (clearTransientRunnable != null) handler.removeCallbacks(clearTransientRunnable);
    }
}
