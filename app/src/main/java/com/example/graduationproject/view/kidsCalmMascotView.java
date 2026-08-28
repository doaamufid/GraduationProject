package com.example.graduationproject.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.kidsCalmAnimUtils;

/** Mirrors the React <Mascot> speech-bubble component with a bouncing star. */
public class kidsCalmMascotView extends LinearLayout {

    private TextView emoji;
    private TextView text;
    private ObjectAnimator bounceAnim;

    public kidsCalmMascotView(Context context) { super(context); init(context); }
    public kidsCalmMascotView(Context context, AttributeSet attrs) { super(context, attrs); init(context); }

    private void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.kids_calm_view_mascot, this, true);
        emoji = v.findViewById(R.id.mascotEmoji);
        text = v.findViewById(R.id.mascotText);
    }

    public void setText(CharSequence s) {
        text.setText(s);
    }

    public void setEmojiSize(float sp) {
        emoji.setTextSize(sp);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        bounceAnim = kidsCalmAnimUtils.bounce(emoji);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bounceAnim != null) bounceAnim.cancel();
    }
}
