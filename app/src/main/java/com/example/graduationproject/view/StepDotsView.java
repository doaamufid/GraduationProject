package com.example.graduationproject.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.graduationproject.R;

/**
 * Java equivalent of <StepDots current={simStep}/> — four dots representing
 * Breathing / Grounding / Dhikr / Card, the last one styled as the amber
 * "destination" dot exactly like the JS version.
 */
public class StepDotsView extends LinearLayout {

    private static final int STEP_COUNT = 4;

    public StepDotsView(Context context) { super(context); init(); }
    public StepDotsView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    private float dp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    public void setCurrent(int current) {
        removeAllViews();
        for (int i = 0; i < STEP_COUNT; i++) {
            boolean isCardDot = (i == STEP_COUNT - 1);
            boolean active = i == current;
            boolean done = i < current;

            android.widget.ImageView dot = new android.widget.ImageView(getContext());
            android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
            shape.setShape(android.graphics.drawable.GradientDrawable.OVAL);

            int color;
            if (isCardDot) color = getResources().getColor(R.color.primary);
            else if (active) color = getResources().getColor(R.color.text_main);
            else if (done) color = Color.argb(140, 31, 58, 96);
            else color = Color.argb(41, 31, 58, 96);
            shape.setColor(color);
            dot.setImageDrawable(shape);

            int size = (int) dp(isCardDot ? 11 : 8);
            LayoutParams lp = new LayoutParams(size, size);
            lp.setMarginEnd((int) dp(4.5f));
            lp.setMarginStart((int) dp(4.5f));
            dot.setScaleX(active ? 1.3f : 1f);
            dot.setScaleY(active ? 1.3f : 1f);
            if (isCardDot) {
                dot.setElevation(dp(2));
            }
            addView(dot, lp);
        }
    }
}
