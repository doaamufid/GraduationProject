package com.example.graduationproject.Kids;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class KidsBubbleView extends View {
    public enum Mode {
        WELCOME,
        READY,
        INFLATING,
        RELEASED,
        CELEBRATION,
        DONE
    }

    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sparklePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Mode mode = Mode.WELCOME;
    private float progress;

    public KidsBubbleView(Context context) {
        super(context);
        init();
    }

    public KidsBubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KidsBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint.setTextAlign(Paint.Align.CENTER);
        sparklePaint.setTextAlign(Paint.Align.CENTER);
        setWillNotDraw(false);
    }

    public void showMode(Mode mode) {
        this.mode = mode;
        invalidate();
    }

    public void setProgress(float progress) {
        this.progress = Math.max(0f, Math.min(1f, progress));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float centerX = width / 2f;
        float centerY = height / 2f;

        if (mode == Mode.WELCOME) {
            drawWelcome(canvas, centerX, centerY);
        } else if (mode == Mode.READY) {
            drawReady(canvas, centerX, centerY);
        } else if (mode == Mode.INFLATING) {
            drawInflating(canvas, centerX, centerY);
        } else if (mode == Mode.RELEASED) {
            drawReleased(canvas, centerX, centerY);
        } else if (mode == Mode.CELEBRATION) {
            drawCelebration(canvas, centerX, centerY);
        } else if (mode == Mode.DONE) {
            drawDone(canvas, centerX, centerY);
        }
    }

    private void drawWelcome(Canvas canvas, float centerX, float centerY) {
        drawText(canvas, "🧸", centerX, centerY - dp(80), 38, "#9B6A3A");
        drawBubble(canvas, centerX - dp(6), centerY - dp(24), dp(20));
        drawBubble(canvas, centerX + dp(28), centerY - dp(46), dp(11));
        drawBubble(canvas, centerX + dp(36), centerY - dp(12), dp(13));
    }

    private void drawReady(Canvas canvas, float centerX, float centerY) {
        drawBubble(canvas, centerX, centerY - dp(26), dp(18));
        drawText(canvas, "🧸", centerX, centerY + dp(48), 34, "#9B6A3A");
        drawText(canvas, "اضغط وانفخ...", centerX, centerY + dp(92), 17, "#F47C2B");
    }

    private void drawInflating(Canvas canvas, float centerX, float centerY) {
        float radius = dp(20) + progress * dp(54);
        drawBubble(canvas, centerX, centerY - dp(28), radius);
        drawText(canvas, progress > 0.55f ? "😤" : "😌", centerX, centerY + radius + dp(34), 34, "#5D4037");
        drawText(canvas, "استمر فقاعتك تكبر! 🫧", centerX, centerY + radius + dp(72), 16, "#F47C2B");
    }

    private void drawReleased(Canvas canvas, float centerX, float centerY) {
        drawBubble(canvas, centerX - dp(70), centerY + dp(70), dp(18));
        drawBubble(canvas, centerX - dp(22), centerY + dp(95), dp(24));
        drawBubble(canvas, centerX + dp(30), centerY + dp(66), dp(22));
        drawBubble(canvas, centerX + dp(82), centerY + dp(92), dp(20));
        drawText(canvas, "✨🧸", centerX, centerY - dp(14), 40, "#FFC94A");
    }

    private void drawCelebration(Canvas canvas, float centerX, float centerY) {
        drawText(canvas, "✨", centerX - dp(44), centerY - dp(40), 42, "#FFC94A");
        drawText(canvas, "🧸", centerX, centerY + dp(6), 42, "#9B6A3A");
        drawText(canvas, "طارت الفقاعة! 🌟", centerX, centerY + dp(62), 17, "#F47C2B");
    }

    private void drawDone(Canvas canvas, float centerX, float centerY) {
        drawText(canvas, "🧸", centerX, centerY - dp(20), 48, "#9B6A3A");
    }

    private void drawBubble(Canvas canvas, float cx, float cy, float radius) {
        bubblePaint.setShader(new RadialGradient(
                cx - radius / 3f,
                cy - radius / 3f,
                radius,
                new int[]{Color.WHITE, Color.rgb(175, 230, 241), Color.rgb(132, 209, 232)},
                new float[]{0f, 0.58f, 1f},
                Shader.TileMode.CLAMP));
        bubblePaint.setAlpha(215);
        canvas.drawCircle(cx, cy, radius, bubblePaint);
        bubblePaint.setShader(null);
        bubblePaint.setStyle(Paint.Style.STROKE);
        bubblePaint.setStrokeWidth(dp(2));
        bubblePaint.setColor(Color.argb(130, 255, 255, 255));
        canvas.drawCircle(cx - radius * 0.22f, cy - radius * 0.25f, radius * 0.32f, bubblePaint);
        bubblePaint.setStyle(Paint.Style.FILL);
    }

    private void drawText(Canvas canvas, String text, float x, float y, int sp, String color) {
        textPaint.setTextSize(sp(sp));
        textPaint.setColor(Color.parseColor(color));
        textPaint.setFakeBoldText(true);
        canvas.drawText(text, x, y, textPaint);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}
