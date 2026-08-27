package com.example.graduationproject.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;

/**
 * Mirrors the React <ChoiceCard emoji label selected onClick> component: a full-width
 * rounded row that highlights (border + tint + lift) when selected.
 */
public class KidsAdaptiveChoiceCardView extends LinearLayout {

    private final TextView emojiView;
    private final TextView labelView;
    private boolean selected = false;

    public KidsAdaptiveChoiceCardView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int padH = dp(18), padV = dp(14);
        setPadding(padH, padV, padH, padV);

        emojiView = new TextView(context);
        emojiView.setTextSize(22);
        LayoutParams emojiParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        emojiParams.setMarginEnd(dp(12));
        addView(emojiView, emojiParams);

        labelView = new TextView(context);
        labelView.setTextSize(16);
        labelView.setTypeface(KidsAdaptiveTypefaces.heading(context));
        labelView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        addView(labelView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        applySelectedState(false, false);
    }

    public void setEmoji(String emoji) {
        if (emoji == null || emoji.isEmpty()) {
            emojiView.setVisibility(GONE);
        } else {
            emojiView.setVisibility(VISIBLE);
            emojiView.setText(emoji);
        }
    }

    public void setLabel(String label) {
        labelView.setText(label);
    }

    public void setSelectedState(boolean selected) {
        applySelectedState(selected, true);
    }

    public boolean isSelectedState() {
        return selected;
    }

    private void applySelectedState(boolean sel, boolean animate) {
        this.selected = sel;
        setBackgroundResource(sel ? R.drawable.kids_adaptive_bg_choice_card_selected : R.drawable.kids_adaptive_bg_choice_card_unselected);
        labelView.setTypeface(KidsAdaptiveTypefaces.heading(getContext()), sel ? Typeface.BOLD : Typeface.NORMAL);
        if (animate) {
            animate().translationY(sel ? -dp(2) : 0)
                    .scaleX(sel ? 1.02f : 1f)
                    .scaleY(sel ? 1.02f : 1f)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .setDuration(220)
                    .start();
        }
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
