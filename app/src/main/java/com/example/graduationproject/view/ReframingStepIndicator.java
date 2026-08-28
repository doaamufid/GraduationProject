package com.example.graduationproject.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.graduationproject.R;

public class ReframingStepIndicator extends LinearLayout {

    private final String[] arabicLabels = {"تحديد", "فحص", "إعادة صياغة"};
    private final String[] englishLabels = {"IDENTIFY", "EXAMINE", "REFRAME"};
    private int currentStep = 0;

    public ReframingStepIndicator(Context context) { super(context); init(); }
    public ReframingStepIndicator(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        render();
    }

    private int dp(int v) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    public void setStep(int step) {
        this.currentStep = step;
        render();
    }

    private void render() {
        removeAllViews();
        
        for (int i = 0; i < arabicLabels.length; i++) {
            addStepView(i);
            if (i < arabicLabels.length - 1) {
                addDivider();
            }
        }
    }

    private void addStepView(int step) {
        LinearLayout stepLayout = new LinearLayout(getContext());
        stepLayout.setOrientation(VERTICAL);
        stepLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        boolean isActive = step == currentStep;
        
        // Arabic Label (Top)
        TextView tvArabic = new TextView(getContext());
        tvArabic.setText(arabicLabels[step]);
        tvArabic.setTextSize(10);
        tvArabic.setTextColor(isActive ? Color.parseColor("#7E81BA") : Color.parseColor("#8A9CB2"));
        tvArabic.setGravity(Gravity.CENTER);
        tvArabic.setPadding(0, 0, 0, dp(4));
        
        // Circle with number
        TextView tvCircle = new TextView(getContext());
        tvCircle.setText(String.valueOf(step + 1));
        tvCircle.setGravity(Gravity.CENTER);
        tvCircle.setTextSize(14);
        tvCircle.setTypeface(null, Typeface.BOLD);
        tvCircle.setTextColor(isActive ? Color.WHITE : Color.parseColor("#8A9CB2"));
        
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        bg.setColor(isActive ? Color.parseColor("#7E81BA") : Color.parseColor("#F0F0F9"));
        if (!isActive) {
            bg.setStroke(dp(1), Color.parseColor("#E0E0EF"));
        }
        tvCircle.setBackground(bg);
        
        int circleSize = dp(32);
        LayoutParams circleLp = new LayoutParams(circleSize, circleSize);
        circleLp.gravity = Gravity.CENTER_HORIZONTAL;

        // English Label (Bottom)
        TextView tvEnglish = new TextView(getContext());
        tvEnglish.setText(englishLabels[step]);
        tvEnglish.setTextSize(9);
        tvEnglish.setTextColor(isActive ? Color.parseColor("#7E81BA") : Color.parseColor("#8A9CB2"));
        tvEnglish.setGravity(Gravity.CENTER);
        tvEnglish.setPadding(0, dp(4), 0, 0);
        tvEnglish.setLetterSpacing(0.05f);
        
        stepLayout.addView(tvArabic);
        stepLayout.addView(tvCircle, circleLp);
        stepLayout.addView(tvEnglish);
        
        addView(stepLayout);
        
        if (isActive) {
            stepLayout.setAlpha(0.6f);
            stepLayout.animate().alpha(1f).setDuration(400).start();
        }
    }

    private void addDivider() {
        View divider = new View(getContext());
        divider.setBackgroundColor(Color.parseColor("#E0E0EF"));
        LayoutParams lp = new LayoutParams(dp(40), dp(1));
        lp.setMargins(dp(8), dp(8), dp(8), 0); // Align with circles
        lp.topMargin = dp(28); // Roughly center horizontally between circles
        addView(divider, lp);
    }
}
