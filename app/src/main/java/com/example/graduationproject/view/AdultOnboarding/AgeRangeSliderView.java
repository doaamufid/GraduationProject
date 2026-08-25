package com.example.graduationproject.view.AdultOnboarding;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.AdultOnboardingUiUtils;

/** Mirrors <AgeRangeSlider/>: a pill label above a SeekBar whose filled portion tracks the value. */
public class AgeRangeSliderView extends LinearLayout {

    public interface OnAgeChange { void onChange(int index); }

    private TextView pill;
    private SeekBar seekBar;
    private OnAgeChange listener;
    private TextView minLabel, maxLabel;

    public AgeRangeSliderView(Context context) { super(context); init(); }
    public AgeRangeSliderView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        setOrientation(VERTICAL);
        int d = AdultOnboardingUiUtils.dp(getContext(), 1);

        FrameLayout pillWrap = new FrameLayout(getContext());
        pillWrap.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        pill = new TextView(getContext());
        pill.setTextColor(Color.WHITE);
        pill.setTypeface(AdultOnboardingUiUtils.cairo(true));
        pill.setTextSize(14.5f);
        pill.setGravity(Gravity.CENTER);
        int padH = AdultOnboardingUiUtils.dp(getContext(), 18), padV = AdultOnboardingUiUtils.dp(getContext(), 6);
        pill.setPadding(padH, padV, padH, padV);
        GradientDrawable pillBg = new GradientDrawable();
        pillBg.setCornerRadius(999);
        pillBg.setColor(Color.argb(31, 255, 255, 255));
        pillBg.setStroke(d, Color.argb(51, 255, 255, 255));
        pill.setBackground(pillBg);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.CENTER;
        flp.bottomMargin = AdultOnboardingUiUtils.dp(getContext(), 10);
        pillWrap.addView(pill, flp);
        addView(pillWrap);

        seekBar = new SeekBar(getContext());
        seekBar.setMax(AdultOnboardingAppData.AGE_BRACKETS.length - 1);
        seekBar.setPadding(0, 0, 0, 0);
        LayoutParams sbLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        seekBar.setLayoutParams(sbLp);
        addView(seekBar);

        LinearLayout labelsRow = new LinearLayout(getContext());
        labelsRow.setOrientation(HORIZONTAL);
        labelsRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        minLabel = new TextView(getContext());
        minLabel.setText(AdultOnboardingAppData.AGE_BRACKETS[0]);
        minLabel.setTextColor(Color.WHITE);
        minLabel.setAlpha(0.55f);
        minLabel.setTextSize(11f);
        maxLabel = new TextView(getContext());
        maxLabel.setText(AdultOnboardingAppData.AGE_BRACKETS[AdultOnboardingAppData.AGE_BRACKETS.length - 1]);
        maxLabel.setTextColor(Color.WHITE);
        maxLabel.setAlpha(0.55f);
        maxLabel.setTextSize(11f);
        LayoutParams left = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        LayoutParams right = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        right.gravity = Gravity.END;
        maxLabel.setGravity(Gravity.END);
        labelsRow.addView(minLabel, left);
        labelsRow.addView(maxLabel, right);
        addView(labelsRow);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                setIndex(progress);
                if (fromUser && listener != null) listener.onChange(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });
    }

    public void setOnAgeChange(OnAgeChange l) { this.listener = l; }

    public void setIndex(int index) {
        if (index < 0) index = AdultOnboardingAppData.AGE_BRACKETS.length / 2;
        pill.setText(AdultOnboardingAppData.AGE_BRACKETS[index]);
        if (seekBar.getProgress() != index) seekBar.setProgress(index);
    }
}
