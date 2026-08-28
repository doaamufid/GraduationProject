package com.example.graduationproject.view;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.kidsCalmSticker;
import com.example.graduationproject.util.kidsCalmAnimUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.Random;

/**
 * Mirrors the React <KidCard phrase sticker photo big celebrate /> component:
 * shows either a photo or a gradient+sticker-emoji background, the phrase at
 * the bottom, an optional bigger "glow" size, and optional falling confetti.
 */
public class kidsCalmKidCardView extends FrameLayout {

    private ImageView photoView;
    private View gradientBg;
    private View photoScrim;
    private TextView stickerEmoji;
    private FrameLayout confettiLayer;
    private TextView phraseText;

    private ObjectAnimatorHolder wiggleAnim;
    private AnimatorSet glowAnim;
    private boolean confettiRunning = false;

    private static class ObjectAnimatorHolder {
        android.animation.ObjectAnimator anim;
    }

    public kidsCalmKidCardView(Context context) { super(context); init(context); }
    public kidsCalmKidCardView(Context context, AttributeSet attrs) { super(context, attrs); init(context); }
    public kidsCalmKidCardView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); init(context); }

    private void init(Context context) {
        setClipChildren(false);
        setClipToPadding(false);
        View v = LayoutInflater.from(context).inflate(R.layout.kids_calm_view_kid_card, this, true);
        photoView = v.findViewById(R.id.cardPhoto);
        gradientBg = v.findViewById(R.id.cardGradientBg);
        photoScrim = v.findViewById(R.id.cardPhotoScrim);
        stickerEmoji = v.findViewById(R.id.cardStickerEmoji);
        confettiLayer = v.findViewById(R.id.cardConfettiLayer);
        phraseText = v.findViewById(R.id.cardPhraseText);
    }

    /** Configure the card content. Pass photoUri OR sticker (photo wins, like the React version). */
    public void setContent(String phrase, kidsCalmSticker sticker, Uri photoUri) {
        phraseText.setText(phrase == null || phrase.trim().isEmpty() ? getResources().getString(R.string.kids_calm_dialog_phrase_hint) : phrase);

        boolean hasPhoto = photoUri != null;
        photoView.setVisibility(hasPhoto ? VISIBLE : GONE);
        photoScrim.setVisibility(hasPhoto ? VISIBLE : GONE);
        gradientBg.setVisibility(hasPhoto ? GONE : VISIBLE);
        stickerEmoji.setVisibility(hasPhoto ? GONE : VISIBLE);

        if (hasPhoto) {
            photoView.setImageURI(photoUri);
            phraseText.setTextColor(Color.WHITE);
        } else {
            int start = sticker != null ? sticker.colorStart : Color.parseColor("#FFF7E0");
            int end = sticker != null ? sticker.colorEnd : Color.parseColor("#FFE3A3");
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{start, end});
            gradientBg.setBackground(gd);
            stickerEmoji.setText(sticker != null ? sticker.emoji : "⭐");
            phraseText.setTextColor(getResources().getColor(com.example.graduationproject.R.color.kids_calm_navy));
        }
    }

    /** big=true -> 300dp height + a subtle glow pulse (celebration display). */
    public void setBig(boolean big) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        int heightDp = big ? 300 : 160;
        MaterialCardView card = (MaterialCardView) getChildAt(0);
        ViewGroup.LayoutParams clp = card.getLayoutParams();
        clp.height = (int) (heightDp * getResources().getDisplayMetrics().density);
        card.setLayoutParams(clp);
        stickerEmoji.setTextSize(big ? 76 : 40);
        phraseText.setTextSize(big ? 20 : 15);

        if (glowAnim != null) { glowAnim.cancel(); glowAnim = null; }
        if (big) {
            glowAnim = kidsCalmAnimUtils.glow(card);
        }
    }

    /** Starts/stops the falling-confetti celebration overlay (kid-fall keyframes). */
    public void setCelebrate(boolean celebrate) {
        confettiLayer.removeAllViews();
        confettiRunning = celebrate;
        if (!celebrate) return;

        post(() -> {
            int width = confettiLayer.getWidth();
            if (width <= 0) return;
            int[] colors = new int[]{
                    getResources().getColor(com.example.graduationproject.R.color.kids_calm_coral),
                    getResources().getColor(com.example.graduationproject.R.color.kids_calm_sun),
                    getResources().getColor(com.example.graduationproject.R.color.kids_calm_mint),
                    getResources().getColor(com.example.graduationproject.R.color.kids_calm_purple),
                    getResources().getColor(com.example.graduationproject.R.color.kids_calm_pink)
            };
            Random r = new Random();
            float density = getResources().getDisplayMetrics().density;
            int fallDistance = (int) (340 * density);

            for (int i = 0; i < 14; i++) {
                View piece = new View(getContext());
                int size = (int) ((6 + r.nextInt(6)) * density);
                LayoutParams lp = new LayoutParams(size, size);
                lp.leftMargin = (int) (r.nextFloat() * Math.max(0, width - size));
                lp.topMargin = -((int) (10 * density));
                piece.setLayoutParams(lp);
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(colors[i % colors.length]);
                gd.setCornerRadius(3 * density);
                piece.setBackground(gd);
                confettiLayer.addView(piece);

                long duration = (long) ((2 + r.nextFloat() * 1.4f) * 1000);
                long delay = (long) (r.nextFloat() * 1200);
                kidsCalmAnimUtils.fall(piece, fallDistance, duration, delay);
            }
        });
    }

    /** Starts the gentle wiggle idle animation on the sticker emoji (kid-wiggle). */
    public void startStickerWiggle() {
        stopStickerWiggle();
        if (stickerEmoji.getVisibility() == VISIBLE) {
            wiggleAnim = new ObjectAnimatorHolder();
            wiggleAnim.anim = kidsCalmAnimUtils.wiggle(stickerEmoji);
        }
    }

    public void stopStickerWiggle() {
        if (wiggleAnim != null && wiggleAnim.anim != null) {
            wiggleAnim.anim.cancel();
            stickerEmoji.setRotation(0f);
        }
        wiggleAnim = null;
    }
}
