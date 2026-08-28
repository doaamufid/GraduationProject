package com.example.graduationproject.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
    private final GradientDrawable blobBg = new GradientDrawable();

    public KidsAdaptiveEmotionBubbleView(Context context, float bobDelaySeconds) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        int size = dp(84); // Smaller size like adult
        setLayoutParams(new LayoutParams(size, size));

        // Blob-like radius matching Adult version
        blobBg.setCornerRadii(new float[]{
                dp(35), dp(35),
                dp(25), dp(25),
                dp(40), dp(40),
                dp(20), dp(20)
        });
        setBackground(blobBg);

        emojiView = new TextView(context);
        emojiView.setTextSize(28); // Smaller icon like adult
        emojiView.setGravity(Gravity.CENTER);
        addView(emojiView);

        labelView = new TextView(context);
        labelView.setTextSize(11.5f); // Smaller text like adult
        labelView.setTypeface(KidsAdaptiveTypefaces.body(context));
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
        if (sel) {
            blobBg.setColor(Color.argb(80, 255, 255, 255)); // Translucent white selected
        } else {
            blobBg.setColor(Color.TRANSPARENT);
        }
        
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

