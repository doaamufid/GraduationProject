package com.example.graduationproject.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Custom view that redraws one of four full-bleed illustrated scenes,
 * mirroring the inline <svg> components (MountainScene / SeaScene /
 * ForestScene / DesertScene) from the JSX. All coordinates below are taken
 * directly from the original 390x800 viewBox and are re-scaled to "cover"
 * (preserveAspectRatio="xMidYMid slice") whatever the view's actual size is.
 *
 * The dark bottom-to-top scrim gradient that sits over every scene is drawn
 * here too (as the last step) so the whole background - illustration + scrim -
 * can be cross-faded together as a single layer, exactly like the JSX's
 * `key={idx}` remount + `.scene-fade` animation.
 */
public class SceneView extends View {

    public static final int SCENE_MOUNTAIN = 0;
    public static final int SCENE_SEA = 1;
    public static final int SCENE_FOREST = 2;
    public static final int SCENE_DESERT = 3;

    private static final float VB_W = 390f;
    private static final float VB_H = 800f;

    private int sceneType = SCENE_MOUNTAIN;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SceneView(Context context) { super(context); }
    public SceneView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }

    public void setSceneType(int sceneType) {
        this.sceneType = sceneType;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        // preserveAspectRatio="xMidYMid slice" -> uniform scale to COVER, then center-crop
        float scale = Math.max(w / VB_W, h / VB_H);
        float dx = (w - VB_W * scale) / 2f;
        float dy = (h - VB_H * scale) / 2f;

        canvas.save();
        canvas.translate(dx, dy);
        canvas.scale(scale, scale);

        switch (sceneType) {
            case SCENE_SEA: drawSea(canvas); break;
            case SCENE_FOREST: drawForest(canvas); break;
            case SCENE_DESERT: drawDesert(canvas); break;
            case SCENE_MOUNTAIN:
            default: drawMountain(canvas); break;
        }

        canvas.restore();

        drawScrim(canvas, w, h);
    }

    // ---------------------------------------------------------------
    // Mountain scene (s1)
    // ---------------------------------------------------------------
    private void drawMountain(Canvas canvas) {
        paint.setShader(new LinearGradient(0, 0, 0, VB_H,
                new int[]{ Color.parseColor("#7A8A99"), Color.parseColor("#5B6E80"), Color.parseColor("#2E3A45") },
                new float[]{ 0f, 0.45f, 1f }, Shader.TileMode.CLAMP));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, VB_W, VB_H, paint);
        paint.setShader(null);

        // 8 mist ellipses: cx=(i*61)%390, cy=90+(i*37)%140, rx=55, ry=18, fill #ffffff22
        paint.setColor(Color.parseColor("#22FFFFFF"));
        for (int i = 0; i < 8; i++) {
            float cx = (i * 61) % 390;
            float cy = 90 + (i * 37) % 140;
            canvas.save();
            canvas.translate(cx, cy);
            canvas.scale(1f, 18f / 55f); // draw as circle r=55 then squash vertically to ry=18
            canvas.drawCircle(0, 0, 55, paint);
            canvas.restore();
        }

        // back ridge: M0 560 L120 380 L190 480 L260 340 L390 540 V800 H0 Z, fill #1E2933, opacity .9
        Path ridgeBack = new Path();
        ridgeBack.moveTo(0, 560); ridgeBack.lineTo(120, 380); ridgeBack.lineTo(190, 480);
        ridgeBack.lineTo(260, 340); ridgeBack.lineTo(390, 540); ridgeBack.lineTo(390, 800);
        ridgeBack.lineTo(0, 800); ridgeBack.close();
        paint.setColor(withAlpha("#1E2933", 0.9f));
        canvas.drawPath(ridgeBack, paint);

        // front ridge: M0 640 L150 500 L230 580 L390 610 V800 H0 Z, fill #141C24, opacity .9
        Path ridgeFront = new Path();
        ridgeFront.moveTo(0, 640); ridgeFront.lineTo(150, 500); ridgeFront.lineTo(230, 580);
        ridgeFront.lineTo(390, 610); ridgeFront.lineTo(390, 800); ridgeFront.lineTo(0, 800); ridgeFront.close();
        paint.setColor(withAlpha("#141C24", 0.9f));
        canvas.drawPath(ridgeFront, paint);
    }

    // ---------------------------------------------------------------
    // Sea scene (s2)
    // ---------------------------------------------------------------
    private void drawSea(Canvas canvas) {
        paint.setShader(new LinearGradient(0, 0, 0, VB_H,
                new int[]{ Color.parseColor("#8FB0C7"), Color.parseColor("#4E7691"), Color.parseColor("#1F3A4E") },
                new float[]{ 0f, 0.55f, 1f }, Shader.TileMode.CLAMP));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, VB_W, VB_H, paint);
        paint.setShader(null);

        // sun: cx300 cy130 r30 fill #FFF3D6 opacity .7
        paint.setColor(withAlpha("#FFF3D6", 0.7f));
        canvas.drawCircle(300, 130, 30, paint);

        // 5 wave lines, approximated from the "Q..T.." smooth-quadratic paths
        paint.setColor(Color.parseColor("#33FFFFFF"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        for (int i = 0; i < 5; i++) {
            float y = 520 + i * 28;
            Path wave = new Path();
            wave.moveTo(0, y);
            wave.quadTo(100, y - 20, 195, y);
            wave.quadTo(290, y + 20, 390, y);
            canvas.drawPath(wave, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    // ---------------------------------------------------------------
    // Forest scene (s3)
    // ---------------------------------------------------------------
    private void drawForest(Canvas canvas) {
        paint.setShader(new LinearGradient(0, 0, 0, VB_H,
                new int[]{ Color.parseColor("#9AB09E"), Color.parseColor("#57705E"), Color.parseColor("#22301F") },
                new float[]{ 0f, 0.5f, 1f }, Shader.TileMode.CLAMP));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, VB_W, VB_H, paint);
        paint.setShader(null);

        // 4 tree silhouettes: [x,y,h] triangle apex at (x,y), base at y+h
        int[][] trees = { {50, 560, 130}, {150, 600, 170}, {250, 580, 150}, {340, 620, 120} };
        paint.setColor(withAlpha("#12180F", 0.85f));
        for (int[] t : trees) {
            int x = t[0], y = t[1], hgt = t[2];
            Path tree = new Path();
            tree.moveTo(x, y);
            tree.lineTo(x - 45, y + hgt);
            tree.lineTo(x + 45, y + hgt);
            tree.close();
            canvas.drawPath(tree, paint);
        }

        // top mist overlay: rect 0,0,390,200 fill #ffffff11
        paint.setColor(Color.parseColor("#11FFFFFF"));
        canvas.drawRect(0, 0, 390, 200, paint);
    }

    // ---------------------------------------------------------------
    // Desert scene (s4)
    // ---------------------------------------------------------------
    private void drawDesert(Canvas canvas) {
        paint.setShader(new LinearGradient(0, 0, 0, VB_H,
                new int[]{ Color.parseColor("#E6B98C"), Color.parseColor("#B37B52"), Color.parseColor("#5C3B28") },
                new float[]{ 0f, 0.5f, 1f }, Shader.TileMode.CLAMP));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, VB_W, VB_H, paint);
        paint.setShader(null);

        // sun: cx195 cy150 r45 fill #FFF3D6 opacity .85
        paint.setColor(withAlpha("#FFF3D6", 0.85f));
        canvas.drawCircle(195, 150, 45, paint);

        // dune: M0 600 Q120 540 195 580 T390 560 V800 H0 Z, fill #3A2418, opacity .85
        Path dune = new Path();
        dune.moveTo(0, 600);
        dune.quadTo(120, 540, 195, 580);
        dune.quadTo(270, 620, 390, 560); // mirrored control point approximating the smooth "T" command
        dune.lineTo(390, 800);
        dune.lineTo(0, 800);
        dune.close();
        paint.setColor(withAlpha("#3A2418", 0.85f));
        canvas.drawPath(dune, paint);
    }

    // ---------------------------------------------------------------
    // Scrim: linear-gradient(180deg, #00000022 0%, transparent 35%, #00000055 65%, #000000cc 100%)
    // ---------------------------------------------------------------
    private void drawScrim(Canvas canvas, int w, int h) {
        Paint scrimPaint = new Paint();
        scrimPaint.setShader(new LinearGradient(0, 0, 0, h,
                new int[]{
                        Color.parseColor("#44000000"),
                        Color.TRANSPARENT,
                        Color.parseColor("#88000000"),
                        Color.BLACK
                },
                new float[]{ 0f, 0.3f, 0.6f, 1f },
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, scrimPaint);
    }

    private static int withAlpha(String hexColor, float alpha) {
        int color = Color.parseColor(hexColor);
        int a = Math.round(alpha * 255);
        return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color));
    }
}
