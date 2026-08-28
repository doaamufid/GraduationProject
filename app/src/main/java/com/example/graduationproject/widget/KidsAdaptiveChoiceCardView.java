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

/**
 * Mirrors the adult ChoiceCard style: unique blob shapes and light colors.
 */
public class KidsAdaptiveChoiceCardView extends LinearLayout {

    private static final int[][] RADII_DP = {
            {40, 40, 25, 25, 35, 35, 20, 20},
            {35, 35, 45, 45, 25, 25, 40, 40},
            {45, 45, 20, 20, 40, 40, 30, 30},
            {30, 30, 40, 40, 20, 20, 45, 45},
            {40, 40, 30, 30, 45, 45, 25, 25}
    };

    private static final String[] LIGHT_COLORS = {
            "#EAF7FF", // Light Sky
            "#FBF0DE", // Light Cream
            "#FCEBE5", // Light Rose
            "#E1F5EF", // Light Mint
            "#F3EAFB"  // Light Lilac
    };

    private final TextView emojiView;
    private final TextView labelView;
    private boolean selected = false;
    private int cardIndex = 0;

    public KidsAdaptiveChoiceCardView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int padH = dp(20), padV = dp(14);
        setPadding(padH, padV, padH, padV);

        emojiView = new TextView(context);
        emojiView.setTextSize(24);
        LayoutParams ep = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ep.setMarginEnd(dp(12));
        addView(emojiView, ep);

        labelView = new TextView(context);
        labelView.setTextSize(16);
        labelView.setTypeface(KidsAdaptiveTypefaces.heading(context));
        labelView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        addView(labelView);
    }

    public void setEmoji(String emoji) {
        if (emoji == null || emoji.isEmpty()) {
            emojiView.setVisibility(GONE);
        } else {
            emojiView.setVisibility(VISIBLE);
            emojiView.setText(emoji);
        }
    }

    public void setLabel(String label) { labelView.setText(label); }

    public void setCardIndex(int index) {
        this.cardIndex = index;
        applySelectedState(selected, false);
    }

    public void setSelectedState(boolean sel) { applySelectedState(sel, true); }

    private void applySelectedState(boolean sel, boolean animate) {
        this.selected = sel;

        GradientDrawable gd = new GradientDrawable();
        int[] radiiDp = RADII_DP[cardIndex % RADII_DP.length];
        float[] radiiPx = new float[8];
        for (int i = 0; i < 8; i++) radiiPx[i] = dp(radiiDp[i]);
        gd.setCornerRadii(radiiPx);

        int baseColor = Color.parseColor(LIGHT_COLORS[cardIndex % LIGHT_COLORS.length]);
        if (sel) {
            gd.setColor(baseColor);
            gd.setStroke(dp(2.5f), getResources().getColor(R.color.kids_adaptive_ink));
        } else {
            int alphaColor = Color.argb(160, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));
            gd.setColor(alphaColor);
            gd.setStroke(dp(1.2f), Color.argb(40, 0, 0, 0));
        }
        setBackground(gd);

        labelView.setTypeface(KidsAdaptiveTypefaces.heading(getContext()), sel ? Typeface.BOLD : Typeface.NORMAL);
        if (animate) {
            animate().translationY(sel ? -dp(3) : 0).scaleX(sel ? 1.025f : 1f).scaleY(sel ? 1.025f : 1f)
                    .setInterpolator(new OvershootInterpolator(1.4f)).setDuration(240).start();
        }
    }

    private int dp(float v) { return (int) (v * getResources().getDisplayMetrics().density); }
}
