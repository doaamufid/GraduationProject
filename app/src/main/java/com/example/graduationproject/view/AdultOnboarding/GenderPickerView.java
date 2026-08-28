package com.example.graduationproject.view.AdultOnboarding;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.AdultOnboardingUiUtils;
import com.example.graduationproject.models.AdultOnboarding.Option;

/** Mirrors <GenderPicker/>: three toggle cards in a row. */
public class GenderPickerView extends LinearLayout {

    public interface OnGenderChange { void onChange(String genderId); }

    private String value;
    private OnGenderChange listener;
    private LinearLayout row;

    public GenderPickerView(Context context) { super(context); init(); }
    public GenderPickerView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        row = new LinearLayout(getContext());
        row.setOrientation(HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        addView(row, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        render();
    }

    public void setOnGenderChange(OnGenderChange l) { this.listener = l; }

    public void setValue(String value) {
        this.value = value;
        render();
        if (listener != null) listener.onChange(value);
    }

    public void setValueSilently(String value) {
        this.value = value;
        render();
    }

    private void render() {
        row.removeAllViews();
        for (Option g : AdultOnboardingAppData.GENDER_OPTIONS) {
            boolean selected = g.id.equals(value);
            LinearLayout col = new LinearLayout(getContext());
            col.setOrientation(VERTICAL);
            col.setGravity(Gravity.CENTER);
            
            // Adjust width to fit 3 in a row
            LayoutParams lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
            int m = AdultOnboardingUiUtils.dp(getContext(), 6);
            lp.setMargins(m, 0, m, 0);
            col.setLayoutParams(lp);
            col.setPadding(AdultOnboardingUiUtils.dp(getContext(), 8), AdultOnboardingUiUtils.dp(getContext(), 12), AdultOnboardingUiUtils.dp(getContext(), 8), AdultOnboardingUiUtils.dp(getContext(), 12));

            GradientDrawable gd = new GradientDrawable();
            gd.setCornerRadii(new float[]{
                AdultOnboardingUiUtils.dp(getContext(), 25), AdultOnboardingUiUtils.dp(getContext(), 25),
                AdultOnboardingUiUtils.dp(getContext(), 20), AdultOnboardingUiUtils.dp(getContext(), 20),
                AdultOnboardingUiUtils.dp(getContext(), 25), AdultOnboardingUiUtils.dp(getContext(), 25),
                AdultOnboardingUiUtils.dp(getContext(), 20), AdultOnboardingUiUtils.dp(getContext(), 20)
            });

            if (selected) {
                gd.setColor(Color.argb(60, 255, 255, 255));
            } else {
                gd.setColor(Color.argb(15, 255, 255, 255));
            }
            col.setBackground(gd);

            TextView symbol = new TextView(getContext());
            symbol.setText(g.emoji);
            symbol.setTextSize(22);
            symbol.setGravity(Gravity.CENTER);
            col.addView(symbol);

            TextView label = new TextView(getContext());
            label.setText(g.labelRes);
            label.setTextColor(Color.WHITE);
            label.setTextSize(12f);
            label.setGravity(Gravity.CENTER);
            label.setTypeface(AdultOnboardingUiUtils.tajawal(selected));
            col.addView(label);

            col.setOnClickListener(v -> setValue(g.id));
            row.addView(col);
        }
    }
}
