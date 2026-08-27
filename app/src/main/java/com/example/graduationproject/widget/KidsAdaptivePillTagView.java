package com.example.graduationproject.widget;

import android.content.Context;
import android.widget.TextView;

import com.example.graduationproject.R;

/** Mirrors the small rounded feeling tags rendered inside the timeline follow-up panel. */
public class KidsAdaptivePillTagView extends TextView {

    private boolean selected = false;

    public KidsAdaptivePillTagView(Context context) {
        super(context);
        setTextSize(13);
        setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        int padH = dp(15), padV = dp(9);
        setPadding(padH, padV, padH, padV);
        applySelectedState(false);
    }

    public void setSelectedState(boolean sel) {
        applySelectedState(sel);
    }

    public boolean isSelectedState() { return selected; }

    private void applySelectedState(boolean sel) {
        this.selected = sel;
        setBackgroundResource(sel ? R.drawable.kids_adaptive_bg_pill_selected : R.drawable.kids_adaptive_bg_pill_unselected);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
