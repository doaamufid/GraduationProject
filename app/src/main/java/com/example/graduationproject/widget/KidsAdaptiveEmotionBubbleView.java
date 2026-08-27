package com.example.graduationproject.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;

/**
 * Mirrors <EmotionBubble emoji label selected onClick delay>: a circular chip that
 * bobs gently (idle "bob" keyframes) and lifts + glows when selected.
 */
public class KidsAdaptiveEmotionBubbleView extends LinearLayout {

    private final TextView emojiView;
    private final TextView labelView;
    private boolean selected = false;
    private ValueAnimator bobAnimator;

    public KidsAdaptiveEmotionBubbleView(Context context, float bobDelaySeconds) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        int size = dp(96);
        setLayoutParams(new LayoutParams(size, size));

        emojiView = new TextView(context);
        emojiView.setTextSize(30);
        emojiView.setGravity(Gravity.CENTER);
        addView(emojiView);

        labelView = new TextView(context);
        labelView.setTextSize(12);
        labelView.setTypeface(KidsAdaptiveTypefaces.heading(context), Typeface.BOLD);
        labelView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        labelView.setGravity(Gravity.CENTER);
        addView(labelView);

        applySelectedState(false, false);
        startBob(bobDelaySeconds);
    }

    private void startBob(float delaySeconds) {
        bobAnimator = ValueAnimator.ofFloat(0f, 1f);
        bobAnimator.setDuration(4500);
        bobAnimator.setStartDelay((long) (delaySeconds * 1000));
        bobAnimator.setRepeatCount(ValueAnimator.INFINITE);
        bobAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        bobAnimator.addUpdateListener(a -> {
            float t = (Float) a.getAnimatedValue();
            float wave = (float) Math.sin(t * Math.PI); // 0->1->0 mirrors 0%,50%,100%
            setTranslationY(-dp(4) * wave + (selected ? -dp(6) : 0));
        });
        bobAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bobAnimator != null) bobAnimator.cancel();
    }

    public void setEmoji(String emoji) { emojiView.setText(emoji); }
    public void setLabel(String label) { labelView.setText(label); }

    public void setSelectedState(boolean sel) {
        applySelectedState(sel, true);
    }

    public boolean isSelectedState() { return selected; }

    private void applySelectedState(boolean sel, boolean animate) {
        this.selected = sel;
        setBackgroundResource(sel ? R.drawable.kids_adaptive_bg_bubble_selected : R.drawable.kids_adaptive_bg_bubble_unselected);
        if (animate) {
            animate().scaleX(sel ? 1.08f : 1f).scaleY(sel ? 1.08f : 1f)
                    .setInterpolator(new OvershootInterpolator(1.6f))
                    .setDuration(250).start();
        }
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
