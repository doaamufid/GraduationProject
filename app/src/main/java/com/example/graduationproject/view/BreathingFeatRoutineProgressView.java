package com.example.graduationproject.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.example.graduationproject.models.BreathingFeatBreathMode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Port of the routine screen's icon row + connecting "balls" progress track
 * (.rt-icons-row / .rt-balls-row / .rt-labels-row in the JS). Each mode is
 * tappable and animates a pop when marked complete.
 */
public class BreathingFeatRoutineProgressView extends View {

    public interface OnModeClickListener { void onModeClick(String modeKey); }

    private final List<BreathingFeatBreathMode> modes = new ArrayList<>();
    private final Map<String, Boolean> completedMap = new LinkedHashMap<>();
    private final Map<String, Float> popScale = new LinkedHashMap<>();
    private OnModeClickListener listener;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private GestureDetector gestureDetector;

    public BreathingFeatRoutineProgressView(Context context) { super(context); init(); }
    public BreathingFeatRoutineProgressView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                int idx = indexForX(e.getX());
                if (idx >= 0 && listener != null) listener.onModeClick(modes.get(idx).key);
                return true;
            }
        });
    }

    public void setModes(List<BreathingFeatBreathMode> modeList) {
        modes.clear();
        modes.addAll(modeList);
        for (BreathingFeatBreathMode m : modes) popScale.putIfAbsent(m.key, 1f);
        invalidate();
    }

    public void setOnModeClickListener(OnModeClickListener l) { this.listener = l; }

    public void setCompleted(Map<String, Boolean> completed) {
        for (BreathingFeatBreathMode m : modes) {
            boolean was = Boolean.TRUE.equals(completedMap.get(m.key));
            boolean now = Boolean.TRUE.equals(completed.get(m.key));
            completedMap.put(m.key, now);
            if (!was && now) animatePop(m.key);
        }
        invalidate();
    }

    private void animatePop(String key) {
        ValueAnimator anim = ValueAnimator.ofFloat(0.3f, 1f);
        anim.setDuration(380);
        anim.setInterpolator(new OvershootInterpolator(2.2f));
        anim.addUpdateListener(a -> {
            popScale.put(key, (float) a.getAnimatedValue());
            invalidate();
        });
        anim.start();
    }

    private int indexForX(float x) {
        if (modes.isEmpty()) return -1;
        float w = getWidth();
        float pad = dp(24);
        float usable = w - 2 * pad;
        float step = usable / modes.size();
        // RTL: first item is at the right
        for (int i = 0; i < modes.size(); i++) {
            float cx = w - pad - step * i - step / 2f;
            if (Math.abs(x - cx) < step / 2f) return i;
        }
        return -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (modes.isEmpty()) return;
        int w = getWidth(), h = getHeight();
        float pad = dp(24);
        float usable = w - 2 * pad;
        int n = modes.size();
        float step = usable / n;

        float iconRowY = dp(28);
        float ballRowY = h - dp(46);
        float labelRowY = h - dp(10);

        int doneCount = 0;
        for (BreathingFeatBreathMode m : modes) if (Boolean.TRUE.equals(completedMap.get(m.key))) doneCount++;
        float percent = n > 0 ? doneCount / (float) n : 0;

        paint.reset();
        paint.setAntiAlias(true);

        // connecting line (track) + fill, growing from the right (RTL)
        float lineLeft = pad, lineRight = w - pad;
        rect.set(lineLeft, ballRowY - dp(2), lineRight, ballRowY + dp(2));
        paint.setColor(0x1A2F4D43);
        canvas.drawRoundRect(rect, dp(2), dp(2), paint);
        float fillW = (lineRight - lineLeft) * percent;
        rect.set(lineRight - fillW, ballRowY - dp(2), lineRight, ballRowY + dp(2));
        paint.setColor(0xFF6FAE6F);
        canvas.drawRoundRect(rect, dp(2), dp(2), paint);

        for (int i = 0; i < n; i++) {
            BreathingFeatBreathMode m = modes.get(i);
            boolean done = Boolean.TRUE.equals(completedMap.get(m.key));
            float cx = w - pad - step * i - step / 2f;

            // icon circle
            float iconR = dp(22) * (0.94f + 0.06f * safePop(m.key));
            paint.setColor(m.iconColor);
            canvas.drawCircle(cx, iconRowY, iconR, paint);
            if (done) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(2));
                paint.setColor(0x596FAE6F);
                canvas.drawCircle(cx, iconRowY, iconR + dp(3), paint);
                paint.setStyle(Paint.Style.FILL);
            }
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(dp(18));
            paint.setColor(Color.WHITE);
            canvas.drawText(modeGlyph(m.key), cx, iconRowY + dp(6), paint);

            if (done) {
                float badgeR = dp(8.5f) * safePop(m.key);
                paint.setColor(0xFF6FAE6F);
                canvas.drawCircle(cx - iconR * 0.72f, iconRowY - iconR * 0.72f, badgeR, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(dp(10));
                canvas.drawText("✓", cx - iconR * 0.72f, iconRowY - iconR * 0.72f + dp(3.5f), paint);
            }

            // ball on the track
            float ballR = dp(10) * safePop(m.key);
            paint.setColor(done ? m.iconColor : Color.WHITE);
            canvas.drawCircle(cx, ballRowY, ballR, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(done ? m.iconColor : 0x262F4D43);
            canvas.drawCircle(cx, ballRowY, ballR, paint);
            paint.setStyle(Paint.Style.FILL);
            if (done) {
                paint.setColor(Color.WHITE);
                paint.setTextSize(dp(10));
                canvas.drawText("✓", cx, ballRowY + dp(3.5f), paint);
            }

            // label
            paint.setColor(0xFF4F7364);
            paint.setTextSize(dp(10.5f));
            canvas.drawText(m.name, cx, labelRowY, paint);
        }
    }

    private float safePop(String key) {
        Float v = popScale.get(key);
        return v == null ? 1f : v;
    }

    private String modeGlyph(String key) {
        switch (key) {
            case "equal": return "≋";
            case "box": return "▢";
            case "relax478": return "☾";
            case "calm711": return "☁";
            default: return "●";
        }
    }

    private float dp(float v) { return v * getResources().getDisplayMetrics().density; }
}
