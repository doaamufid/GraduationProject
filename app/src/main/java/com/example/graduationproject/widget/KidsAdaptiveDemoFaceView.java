package com.example.graduationproject.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;

/** Mirrors one DEMO_FACES button on the mood-check-in preview screen. */
public class KidsAdaptiveDemoFaceView extends LinearLayout {

    private final TextView emojiView;
    private final TextView labelView;
    private boolean selected = false;

    public KidsAdaptiveDemoFaceView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        int padH = dp(2), padV = dp(10);
        setPadding(padH, padV, padH, padV);

        emojiView = new TextView(context);
        emojiView.setTextSize(26);
        emojiView.setGravity(Gravity.CENTER);
        addView(emojiView);

        labelView = new TextView(context);
        labelView.setTextSize(10.5f);
        labelView.setAlpha(0.7f);
        labelView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        labelView.setGravity(Gravity.CENTER);
        addView(labelView);

        setBackground(null);
    }

    public void setEmoji(String emoji) { emojiView.setText(emoji); }
    public void setLabel(String label) { labelView.setText(label); }

    public void setSelectedState(boolean sel) {
        this.selected = sel;
        setBackgroundResource(sel ? R.drawable.kids_adaptive_bg_demo_face_selected : android.R.color.transparent);
        animate().translationY(sel ? -dp(4) : 0).scaleX(sel ? 1.15f : 1f).scaleY(sel ? 1.15f : 1f)
                .setInterpolator(new OvershootInterpolator(1.6f)).setDuration(260).start();
    }

    public boolean isSelectedState() { return selected; }

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density); }
}
