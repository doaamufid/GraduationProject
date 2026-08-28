package com.example.graduationproject.dialogs;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.R;

/**
 * Mirrors the "خلصتي روتين اليوم!" celebration overlay.
 * Entrance animation ports the CSS keyframes:
 *   celebrateScale: scale(0.5) rotate(-6deg) opacity:0  -> scale(1.08) rotate(2deg) opacity:1 (60%) -> scale(1) rotate(0) (100%)
 */
public class KidsRoutineCelebrationDialog extends Dialog {

    public interface OnDismissTapped {
        void onDismissTapped();
    }

    private final String stickerEmoji;
    private final int streak;
    private OnDismissTapped listener;

    public KidsRoutineCelebrationDialog(@NonNull Context context, String stickerEmoji, int streak) {
        super(context, R.style.kids_routine_Theme_SalamRoutine_FullscreenDialog);
        this.stickerEmoji = stickerEmoji;
        this.streak = streak;
    }

    public void setOnDismissTapped(OnDismissTapped listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.kids_routine_dialog_celebration);
        setCancelable(true);

        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        TextView tvSticker = findViewById(R.id.tvCelebrateSticker);
        TextView tvStreak = findViewById(R.id.tvCelebrateStreak);
        TextView btnOk = findViewById(R.id.btnCelebrateOk);
        View card = findViewById(R.id.celebrateCard);

        tvSticker.setText(stickerEmoji);
        tvStreak.setText(getContext().getString(R.string.kids_routine_celebrate_streak_fmt, streak));

        btnOk.setOnClickListener(v -> {
            if (listener != null) listener.onDismissTapped();
            dismiss();
        });

        card.post(() -> playEntranceAnimation(card));
    }

    private void playEntranceAnimation(View card) {
        card.setPivotX(card.getWidth() / 2f);
        card.setPivotY(card.getHeight() / 2f);
        card.setScaleX(0.5f);
        card.setScaleY(0.5f);
        card.setRotation(-6f);
        card.setAlpha(0f);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, View.SCALE_X, 0.5f, 1.08f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, View.SCALE_Y, 0.5f, 1.08f, 1f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(card, View.ROTATION, -6f, 2f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(card, View.ALPHA, 0f, 1f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, rotate, alpha);
        set.setDuration(500);
        set.setInterpolator(new OvershootInterpolator(0.9f));
        set.start();
    }
}
