package com.example.graduationproject.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;

/** Mirrors the goal selection chip: emoji + label in unique blob shapes and light colors. */
public class KidsAdaptiveGoalChipView extends LinearLayout {

    private static final int[][] RADII_DP = {
            {35, 35, 20, 20, 40, 40, 25, 25},
            {40, 40, 25, 25, 35, 35, 20, 20},
            {30, 30, 45, 45, 25, 25, 40, 40},
            {45, 45, 30, 30, 20, 20, 35, 35},
            {35, 35, 40, 40, 20, 20, 45, 45}
    };

    private static final String[] LIGHT_COLORS = {
            "#FFF3D6", // Light Sun
            "#E1F5EF", // Light Mint
            "#FCD9EA", // Light Pink
            "#EAF7FF", // Light Sky
            "#F3EAFB", // Light Lilac
            "#FBF0DE", // Light Cream
            "#EAF3E7", // Light Sage
            "#FFE7CE", // Light Orange
            "#FCEBE5"  // Light Rose
    };

    private final TextView emojiView;
    private final TextView labelView;
    private boolean selected = false;
    private int chipIndex = 0;

    public KidsAdaptiveGoalChipView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int padH = dp(20), padV = dp(12);
        setPadding(padH, padV, padH, padV);

        emojiView = new TextView(context);
        emojiView.setTextSize(20);
        LayoutParams ep = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ep.setMarginEnd(dp(10));
        addView(emojiView, ep);

        labelView = new TextView(context);
        labelView.setTextSize(15);
        labelView.setTypeface(KidsAdaptiveTypefaces.heading(context));
        labelView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        addView(labelView);
    }

    public void setEmoji(String emoji) { emojiView.setText(emoji); }
    public void setLabel(String label) { labelView.setText(label); }
    public void setChipIndex(int index) {
        this.chipIndex = index;
        applySelectedState(selected, false);
    }

    public void setSelectedState(boolean sel) { applySelectedState(sel, true); }
    public boolean isSelectedState() { return selected; }

    private void applySelectedState(boolean sel, boolean animate) {
        this.selected = sel;

        GradientDrawable gd = new GradientDrawable();
        int[] radiiDp = RADII_DP[chipIndex % RADII_DP.length];
        float[] radiiPx = new float[8];
        for (int i = 0; i < 8; i++) radiiPx[i] = dp(radiiDp[i]);
        gd.setCornerRadii(radiiPx);

        int baseColor = Color.parseColor(LIGHT_COLORS[chipIndex % LIGHT_COLORS.length]);
        if (sel) {
            gd.setColor(baseColor);
            gd.setStroke(dp(2.5f), getResources().getColor(R.color.kids_adaptive_ink));
        } else {
            // Semi-transparent version of the light color
            int alphaColor = Color.argb(160, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));
            gd.setColor(alphaColor);
            gd.setStroke(dp(1.5f), Color.argb(40, 0, 0, 0));
        }
        setBackground(gd);

        labelView.setTypeface(KidsAdaptiveTypefaces.heading(getContext()), sel ? Typeface.BOLD : Typeface.NORMAL);
        if (animate) {
            animate().translationY(sel ? -dp(3) : 0).scaleX(sel ? 1.04f : 1f).scaleY(sel ? 1.04f : 1f)
                    .setInterpolator(new OvershootInterpolator(1.6f)).setDuration(240).start();
        }
    }

    private int dp(float v) { return (int) (v * getResources().getDisplayMetrics().density); }
}
