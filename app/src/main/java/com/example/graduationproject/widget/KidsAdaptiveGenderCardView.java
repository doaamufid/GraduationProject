package com.example.graduationproject.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;

/** Mirrors a GENDER_OPTIONS button inside <GenderPicker>. */
public class KidsAdaptiveGenderCardView extends LinearLayout {

    private final TextView symbolView;
    private final TextView labelView;
    private boolean selected = false;

    public KidsAdaptiveGenderCardView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        int padH = dp(8), padV = dp(16);
        setPadding(padH, padV, padH, padV);
        setLayoutParams(new LayoutParams(dp(100), LayoutParams.WRAP_CONTENT));

        symbolView = new TextView(context);
        symbolView.setTextSize(26);
        symbolView.setGravity(Gravity.CENTER);
        LayoutParams sp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        sp.bottomMargin = dp(6);
        addView(symbolView, sp);

        labelView = new TextView(context);
        labelView.setTextSize(14);
        labelView.setTypeface(KidsAdaptiveTypefaces.heading(context));
        labelView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        labelView.setGravity(Gravity.CENTER);
        addView(labelView);

        applySelectedState(false, false);
    }

    public void setSymbol(String symbol) { symbolView.setText(symbol); }
    public void setLabel(String label) { labelView.setText(label); }

    public void setSelectedState(boolean sel) { applySelectedState(sel, true); }
    public boolean isSelectedState() { return selected; }

    private void applySelectedState(boolean sel, boolean animate) {
        this.selected = sel;
        setBackgroundResource(sel ? R.drawable.kids_adaptive_bg_gender_selected : R.drawable.kids_adaptive_bg_gender_unselected);
        labelView.setTypeface(KidsAdaptiveTypefaces.heading(getContext()), sel ? Typeface.BOLD : Typeface.NORMAL);
        if (animate) {
            animate().translationY(sel ? -dp(3) : 0).scaleX(sel ? 1.05f : 1f).scaleY(sel ? 1.05f : 1f)
                    .setInterpolator(new OvershootInterpolator(1.5f)).setDuration(220).start();
        }
    }

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density); }
}
