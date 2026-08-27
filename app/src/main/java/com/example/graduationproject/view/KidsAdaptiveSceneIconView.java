package com.example.graduationproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Mirrors MorningScene / SunScene / EveningScene / MoonScene: small 64x64-viewBox icons
 * used on the "when are feelings hardest" timeline screen.
 */
public class KidsAdaptiveSceneIconView extends View {

    public enum Scene { MORNING, DAY, EVENING, NIGHT }

    private Scene scene = Scene.DAY;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public KidsAdaptiveSceneIconView(Context context) { super(context); }
    public KidsAdaptiveSceneIconView(Context context, AttributeSet attrs) { super(context, attrs); }
    public KidsAdaptiveSceneIconView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    public void setScene(Scene scene) {
        this.scene = scene;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;
        float s = Math.min(w, h) / 64f;
        paint.reset();
        paint.setAntiAlias(true);

        switch (scene) {
            case MORNING:
                paint.setColor(Color.parseColor("#8FE0C4"));
                paint.setAlpha((int) (0.5f * 255));
                canvas.drawRoundRect(2 * s, 42 * s, 62 * s, 52 * s, 5 * s, 5 * s, paint);
                paint.setColor(Color.parseColor("#FFE29A"));
                paint.setAlpha(255);
                canvas.drawArc(14 * s, 24 * s, 50 * s, 60 * s, 180, 180, true, paint);
                break;
            case DAY:
                paint.setColor(Color.parseColor("#FFC94D"));
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3 * s);
                paint.setStrokeCap(Paint.Cap.ROUND);
                for (int a = 0; a < 360; a += 45) {
                    canvas.save();
                    canvas.rotate(a, 32 * s, 32 * s);
                    canvas.drawLine(32 * s, 32 * s, 32 * s, 10 * s, paint);
                    canvas.restore();
                }
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#FFE29A"));
                canvas.drawCircle(32 * s, 32 * s, 12 * s, paint);
                break;
            case EVENING:
                paint.setColor(Color.parseColor("#FF9B85"));
                canvas.drawCircle(32 * s, 30 * s, 13 * s, paint);
                paint.setColor(Color.parseColor("#C9A6E8"));
                paint.setAlpha((int) (0.8f * 255));
                android.graphics.Path waves = new android.graphics.Path();
                waves.moveTo(4 * s, 46 * s);
                waves.quadTo(16 * s, 34 * s, 28 * s, 46 * s);
                waves.quadTo(40 * s, 34 * s, 52 * s, 46 * s);
                waves.quadTo(58 * s, 40 * s, 60 * s, 46 * s);
                waves.lineTo(60 * s, 54 * s);
                waves.lineTo(4 * s, 54 * s);
                waves.close();
                canvas.drawPath(waves, paint);
                break;
            case NIGHT:
                paint.setShader(new RadialGradient(
                        44 * s * 0.35f + 32 * s, 32 * s * 0.3f, 40 * s,
                        new int[]{Color.WHITE, Color.parseColor("#FFE29A"), Color.parseColor("#FFC94D")},
                        new float[]{0f, 0.6f, 1f}, Shader.TileMode.CLAMP));
                android.graphics.Path moon = new android.graphics.Path();
                moon.moveTo(44 * s, 6 * s);
                moon.cubicTo(44 * s - 14.36f * s, 6 * s, 44 * s - 26 * s, 17.64f * s, 44 * s - 26 * s, 32 * s);
                moon.cubicTo(44 * s - 26 * s, 46.36f * s, 44 * s - 14.36f * s, 58 * s, 44 * s, 58 * s);
                moon.cubicTo(44 * s - 12.15f * s, 58 * s, 44 * s - 22 * s, 48.15f * s, 44 * s - 22 * s, 36 * s);
                moon.cubicTo(44 * s - 22 * s, 23.85f * s, 44 * s - 12.15f * s, 6 * s, 44 * s, 6 * s);
                moon.close();
                canvas.drawPath(moon, paint);
                paint.setShader(null);
                paint.setColor(Color.WHITE);
                canvas.drawCircle(20 * s, 18 * s, 2 * s, paint);
                canvas.drawCircle(14 * s, 34 * s, 1.4f * s, paint);
                break;
        }
    }
}
