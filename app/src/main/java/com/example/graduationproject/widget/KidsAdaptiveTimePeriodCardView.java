package com.example.graduationproject.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;
import com.example.graduationproject.view.KidsAdaptiveSceneIconView;

/** Mirrors a TIME_PERIODS grid button: circular scene icon + label, in a selectable card. */
public class KidsAdaptiveTimePeriodCardView extends LinearLayout {

    private final KidsAdaptiveSceneIconView iconView;
    private final TextView labelView;
    private boolean selected = false;

    public KidsAdaptiveTimePeriodCardView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        int padH = dp(6), padV = dp(18);
        setPadding(padH, padV, padH, padV);

        FrameLayout iconWrap = new FrameLayout(context);
        int circleSize = dp(54);
        LayoutParams wrapParams = new LayoutParams(circleSize, circleSize);
        wrapParams.bottomMargin = dp(8);
        iconWrap.setBackgroundResource(R.drawable.kids_adaptive_bg_icon_circle);
        addView(iconWrap, wrapParams);

        iconView = new KidsAdaptiveSceneIconView(context);
        int iconSize = dp(34);
        FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(iconSize, iconSize);
        iconParams.gravity = Gravity.CENTER;
        iconWrap.addView(iconView, iconParams);

        labelView = new TextView(context);
        labelView.setTextSize(14);
        labelView.setTypeface(KidsAdaptiveTypefaces.heading(context), Typeface.BOLD);
        labelView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        labelView.setGravity(Gravity.CENTER);
        addView(labelView);

        applySelectedState(false, false);
    }

    public void setScene(KidsAdaptiveSceneIconView.Scene scene) { iconView.setScene(scene); }
    public void setLabel(String label) { labelView.setText(label); }

    public void setSelectedState(boolean sel) { applySelectedState(sel, true); }
    public boolean isSelectedState() { return selected; }

    private void applySelectedState(boolean sel, boolean animate) {
        this.selected = sel;
        setBackgroundResource(sel ? R.drawable.kids_adaptive_bg_period_selected : R.drawable.kids_adaptive_bg_period_unselected);
        if (animate) {
            animate().translationY(sel ? -dp(3) : 0).scaleX(sel ? 1.03f : 1f).scaleY(sel ? 1.03f : 1f)
                    .setInterpolator(new OvershootInterpolator(1.5f)).setDuration(220).start();
        }
    }

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density); }
}
