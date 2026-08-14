package com.example.kalamati.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * فيو بسيط يرسم أعمدة (bars) بارتفاعات عشوائية ويحرّكها بشكل دوري
 * عشان نعطي إحساس إن في تسجيل صوت شغال (زي الصورة الثانية بالتصميم).
 * استخدامها: waveformView.start() عند بداية التسجيل، و waveformView.stop() عند الإيقاف.
 */
public class WaveformView extends View {

    private static final int BAR_COUNT = 24;
    private final float[] barHeights = new float[BAR_COUNT];
    private final Random random = new Random();
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = false;

    private final Runnable animateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            for (int i = 0; i < BAR_COUNT; i++) {
                barHeights[i] = 0.15f + random.nextFloat() * 0.85f;
            }
            invalidate();
            handler.postDelayed(this, 150);
        }
    };

    public WaveformView(Context context) {
        super(context);
        init();
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint.setColor(Color.parseColor("#F4914B"));
        for (int i = 0; i < BAR_COUNT; i++) {
            barHeights[i] = 0.2f;
        }
    }

    public void start() {
        running = true;
        handler.post(animateRunnable);
    }

    public void stop() {
        running = false;
        handler.removeCallbacks(animateRunnable);
        for (int i = 0; i < BAR_COUNT; i++) {
            barHeights[i] = 0.2f;
        }
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(animateRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) return;

        float barSpace = (float) width / BAR_COUNT;
        float barWidth = barSpace * 0.5f;

        for (int i = 0; i < BAR_COUNT; i++) {
            float barHeight = height * barHeights[i];
            float left = i * barSpace + (barSpace - barWidth) / 2f;
            float top = (height - barHeight) / 2f;
            canvas.drawRoundRect(left, top, left + barWidth, top + barHeight, barWidth / 2f, barWidth / 2f, barPaint);
        }
    }
}
