package com.example.graduationproject.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.graduationproject.R;
import com.example.graduationproject.models.CardPhoto;

/**
 * Java/View equivalent of the JS <CalmCard/> component: shows either an
 * uploaded photo, a gradient preset, or an elegant text-only background,
 * with the reassuring phrase overlaid. Supports the "big" glow animation
 * (scc-anim-glow) and floating particles (scc-anim-float), matching the
 * CSS keyframes in the original design 1:1 in spirit.
 */
public class CalmCardView extends FrameLayout {

    private ImageView photoImage;
    private View gradientBg;
    private View scrim;
    private TextView quoteMark;
    private TextView phraseView;
    private ParticleView particleView;

    private ValueAnimator glowAnimator;
    private GradientDrawable borderDrawable;

    private boolean big = false;

    public CalmCardView(Context context) { super(context); init(context); }
    public CalmCardView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(context); }

    private float dp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    private void init(Context context) {
        setClipToOutline(false);

        // rounded-corner clipping via a foreground/background hack: use outline provider
        setBackground(null);

        photoImage = new ImageView(context);
        photoImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(photoImage, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        gradientBg = new View(context);
        addView(gradientBg, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        scrim = new View(context);
        scrim.setBackgroundResource(R.drawable.gradient_photo_scrim);
        addView(scrim, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        quoteMark = new TextView(context);
        quoteMark.setText("\u275D"); // ❝
        quoteMark.setTextColor(Color.argb(26, 231, 168, 85));
        LayoutParams quoteLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        quoteLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        addView(quoteMark, quoteLp);

        phraseView = new TextView(context);
        phraseView.setTextColor(getResources().getColor(R.color.cream));
        phraseView.setLineSpacing(0f, 1.65f);
        LayoutParams phraseLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(phraseView, phraseLp);

        particleView = new ParticleView(context, null);
        particleView.setVisibility(GONE);
        addView(particleView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Border/glow drawable drawn as the FrameLayout's foreground-like background wrapper
        borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setCornerRadius(dp(22));
        borderDrawable.setColor(Color.TRANSPARENT);
        borderDrawable.setStroke((int) dp(1), getResources().getColor(R.color.border));

        final float radiusPx = dp(22);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radiusPx);
            }
        });
        setClipToOutline(true);
    }

    /** Fills in the card content. Pass photo=null for a text-only elegant card. */
    public void setCard(@Nullable CardPhoto photo, @Nullable String phrase, boolean big) {
        this.big = big;

        // size (use the generic base type — actual runtime type depends on parent container)
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp == null) lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.height = (int) dp(big ? 320 : 168);
        setLayoutParams(lp);

        boolean hasPhoto = photo != null;

        photoImage.setVisibility(hasPhoto && photo.type == CardPhoto.Type.UPLOAD ? VISIBLE : GONE);
        if (hasPhoto && photo.type == CardPhoto.Type.UPLOAD) {
            photoImage.setImageURI(photo.uploadUri);
        }

        if (hasPhoto && photo.type == CardPhoto.Type.PRESET) {
            gradientBg.setVisibility(VISIBLE);
            gradientBg.setBackgroundResource(photo.preset.gradientDrawableRes);
        } else if (!hasPhoto) {
            gradientBg.setVisibility(VISIBLE);
            gradientBg.setBackgroundResource(R.drawable.gradient_card_default);
        } else {
            gradientBg.setVisibility(GONE);
        }

        scrim.setVisibility(hasPhoto ? VISIBLE : GONE);
        quoteMark.setVisibility(hasPhoto ? GONE : VISIBLE);
        quoteMark.setTextSize(TypedValue.COMPLEX_UNIT_SP, big ? 100 : 58);

        String text = (phrase == null || phrase.trim().isEmpty())
                ? getResources().getString(R.string.card_placeholder)
                : phrase;
        phraseView.setText(text);

        LayoutParams phraseLp = (LayoutParams) phraseView.getLayoutParams();
        if (hasPhoto) {
            phraseLp.gravity = Gravity.BOTTOM | Gravity.START;
            int padH = (int) dp(big ? 22 : 14);
            int padV = (int) dp(big ? 26 : 14);
            phraseView.setPadding(padH, padV, padH, padV);
            phraseView.setTextSize(TypedValue.COMPLEX_UNIT_SP, big ? 21 : 14.5f);
            phraseView.setGravity(Gravity.START);
            phraseView.setShadowLayer(14f, 0f, 2f, Color.argb(140, 0, 0, 0));
        } else {
            phraseLp.gravity = Gravity.CENTER;
            int padH = (int) dp(big ? 34 : 20);
            phraseView.setPadding(padH, 0, padH, 0);
            phraseView.setTextSize(TypedValue.COMPLEX_UNIT_SP, big ? 22 : 15);
            phraseView.setGravity(Gravity.CENTER);
            phraseView.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);
        }
        phraseView.setLayoutParams(phraseLp);

        if (big) {
            startGlow();
        } else {
            stopGlow();
            setForeground(borderDrawable);
        }
    }

    public void setShowParticles(boolean show) {
        if (show) particleView.start(); else particleView.stop();
    }

    /** Java equivalent of the `scc-glow` CSS keyframe box-shadow pulse. */
    private void startGlow() {
        stopGlow();
        int from = Color.argb(71, 231, 168, 85);  // ~0.28 alpha
        int to = Color.argb(148, 231, 168, 85);   // ~0.58 alpha
        glowAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
        glowAnimator.setDuration(1700);
        glowAnimator.setRepeatMode(ValueAnimator.REVERSE);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.setInterpolator(new LinearInterpolator());
        glowAnimator.addUpdateListener(a -> {
            int color = (int) a.getAnimatedValue();
            GradientDrawable d = new GradientDrawable();
            d.setCornerRadius(dp(22));
            d.setColor(Color.TRANSPARENT);
            d.setStroke((int) dp(2), color);
            setForeground(d);
        });
        glowAnimator.start();
    }

    private void stopGlow() {
        if (glowAnimator != null) {
            glowAnimator.cancel();
            glowAnimator = null;
        }
        setForeground(null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopGlow();
        particleView.stop();
    }
}
