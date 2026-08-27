package com.example.graduationproject.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;

/** Mirrors the goal selection chip: emoji + label pill, gradient fill when selected. */
public class KidsAdaptiveGoalChipView extends LinearLayout {

    private final TextView emojiView;
    private final TextView labelView;
    private boolean selected = false;

    public KidsAdaptiveGoalChipView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int padH = dp(18), padV = dp(11);
        setPadding(padH, padV, padH, padV);

        emojiView = new TextView(context);
        emojiView.setTextSize(17);
        LayoutParams ep = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ep.setMarginEnd(dp(8));
        addView(emojiView, ep);

        labelView = new TextView(context);
        labelView.setTextSize(14);
        labelView.setTypeface(KidsAdaptiveTypefaces.heading(context));
        labelView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        addView(labelView);

        applySelectedState(false, false);
    }

    public void setEmoji(String emoji) { emojiView.setText(emoji); }
    public void setLabel(String label) { labelView.setText(label); }

    public void setSelectedState(boolean sel) { applySelectedState(sel, true); }
    public boolean isSelectedState() { return selected; }

    private void applySelectedState(boolean sel, boolean animate) {
        this.selected = sel;
        setBackgroundResource(sel ? R.drawable.kids_adaptive_bg_goal_selected : R.drawable.kids_adaptive_bg_goal_unselected);
        labelView.setTypeface(KidsAdaptiveTypefaces.heading(getContext()), sel ? Typeface.BOLD : Typeface.NORMAL);
        if (animate) {
            animate().translationY(sel ? -dp(2) : 0).scaleX(sel ? 1.03f : 1f).scaleY(sel ? 1.03f : 1f)
                    .setInterpolator(new OvershootInterpolator(1.5f)).setDuration(220).start();
        }
    }

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density); }
}
