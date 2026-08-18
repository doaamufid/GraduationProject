package com.example.graduationproject.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Java/Canvas port of the <WaveArt colors={...}/> SVG component from the React source:
 * a top-to-bottom gradient rectangle with 3 overlapping translucent white wave paths
 * near the bottom, drawn at a fixed 400x160 viewBox scaled to the view bounds.
 */
public class WaveArtView extends View {

    private static final float VB_W = 400f;
    private static final float VB_H = 160f;

    private int[] colors = new int[]{0xFF3A74B8, 0xFF1F3A60};
    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WaveArtView(Context context) {
        super(context);
    }

    public WaveArtView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Accepts 2 or 3 ARGB colors, same rule as the JS: colors.length===3 ? colors : [c0, c0, c1] */
    public void setColors(int[] newColors) {
        if (newColors.length == 3) {
            this.colors = newColors;
        } else {
            this.colors = new int[]{newColors[0], newColors[0], newColors[1]};
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bgPaint.setShader(new LinearGradient(0, 0, 0, h, colors[0], colors[2], Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        if (bgPaint.getShader() == null) {
            bgPaint.setShader(new LinearGradient(0, 0, 0, h, colors[0], colors[2], Shader.TileMode.CLAMP));
        }
        canvas.drawRect(0, 0, w, h, bgPaint);

        float sx = w / VB_W;
        float sy = h / VB_H;
        canvas.save();
        canvas.scale(sx, sy);

        drawWave(canvas, 0.16f,
                new float[]{0, 70, 60, 100, 120, 40, 200, 65, 280, 90, 340, 45, 400, 70});
        drawWave(canvas, 0.22f,
                new float[]{0, 95, 70, 60, 130, 120, 210, 95, 290, 70, 330, 110, 400, 90});
        drawWave(canvas, 0.30f,
                new float[]{0, 120, 80, 100, 150, 140, 220, 118, 300, 95, 350, 130, 400, 112});

        canvas.restore();
    }

    /** pts = [x0,y0, cx1,cy1, x1,y1, cx2,cy2, x2,y2, cx3,cy3, x3,y3] describing 3 cubic-ish bezier hops,
     *  matching the "M.. C.. C.. C.." path data used in the SVG, closed down to the bottom edge. */
    private void drawWave(Canvas canvas, float alpha, float[] pts) {
        Path path = new Path();
        path.moveTo(pts[0], pts[1]);
        path.cubicTo(pts[2], pts[3], pts[4], pts[5], pts[6], pts[7]);
        path.cubicTo(pts[8], pts[9], pts[10], pts[11], pts[12], pts[13]);
        path.lineTo(VB_W, VB_H);
        path.lineTo(0, VB_H);
        path.close();

        wavePaint.setColor(0xFFFFFFFF);
        wavePaint.setAlpha(Math.round(255 * alpha));
        canvas.drawPath(path, wavePaint);
    }
}
