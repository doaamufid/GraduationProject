package com.example.graduationproject;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.graduationproject.models.QouteFeatureQuoteEntry;
import com.example.graduationproject.util.QouteFeatureLangStrings;
import com.example.graduationproject.util.QouteFeatureUtils;
import com.example.graduationproject.view.QouteFeatureParticleView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class QouteFeatureMainActivity extends AppCompatActivity {

    // ---- data ----
    private List<QouteFeatureQuoteEntry> allQuotes;
    private QouteFeatureHistoryManager historyManager;
    private List<String> history;
    private QouteFeatureQuoteEntry currentEntry;

    // ---- state ----
    private boolean isArabic = true;
    private boolean liked = false;
    private boolean saved = false;

    // ---- views ----
    private ImageView bgImage;
    private View overlay;
    private QouteFeatureParticleView particleView;
    private TextView brandMark;
    private LinearLayout langPill;
    private TextView langPillText;
    private FrameLayout shuffleBtn;
    private TextView greetLine, dayLine, dateLine, quoteText;
    private View breathGlow;
    private Button ctaBtn;
    private FrameLayout likeBtn, shareBtn, saveBtn;
    private ImageView likeIcon, saveIcon;
    private TextView toastView;
    private View phoneFrame;

    // ---- animators kept so we can restart / cancel them ----
    private Animator kenBurnsAnimator;
    private Animator breathAnimator;
    private Animator shufflePulseAnimator;
    private Animator ctaPulseAnimator;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Handler toastHandler = new Handler(Looper.getMainLooper());
    private Runnable clockRunnable;
    private Runnable pendingToastHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make status bar transparent and navigation bar match theme
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.qoute_feature_bg_dark_bottom));

        setContentView(R.layout.qoute_feature_activity_main);

        bindViews();
        allQuotes = QouteFeatureQuoteRepository.all();
        historyManager = new QouteFeatureHistoryManager(this);

        List<String> imageUrls = new ArrayList<>();
        for (QouteFeatureQuoteEntry q : allQuotes) imageUrls.add(q.imgUrl);

        com.example.graduationproject.data.SalamGeminiService geminiService = new com.example.graduationproject.data.SalamGeminiService();
        QouteFeatureAiQuoteProvider aiProvider = new QouteFeatureAiQuoteProvider(this, geminiService);
        QouteFeatureQuoteEntry aiQuote = aiProvider.getTodaysCachedQuote(imageUrls);

        if (aiQuote != null) {
            currentEntry = aiQuote;
            history = historyManager.loadHistory();
        } else {
            history = historyManager.loadHistory();
            QouteFeatureHistoryManager.Pick pick = historyManager.pickNextEntry(allQuotes, history);
            currentEntry = pick.entry;
            history = pick.nextHistory;
            historyManager.saveHistory(history);
        }

        aiProvider.generateAndCacheForNextTime();

        applyEntryVisuals();
        updateLangUI();
        startClock();
        scheduleCtaReveal();
        startBreathingGlow();
        startShufflePulse();

        shuffleBtn.setOnClickListener(v -> onShuffleClicked());
        langPill.setOnClickListener(v -> onLanguageToggle());
        likeBtn.setOnClickListener(v -> onLikeClicked());
        shareBtn.setOnClickListener(v -> onShareClicked());
        saveBtn.setOnClickListener(v -> onSaveClicked());

        ctaBtn.setOnClickListener(v -> {
            Intent intent = new Intent(QouteFeatureMainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void bindViews() {
        phoneFrame = findViewById(R.id.phoneFrame);
        bgImage = findViewById(R.id.bgImage);
        overlay = findViewById(R.id.overlay);
        particleView = findViewById(R.id.particleView);
        brandMark = findViewById(R.id.brandMark);
        langPill = findViewById(R.id.langPill);
        langPillText = findViewById(R.id.langPillText);
        shuffleBtn = findViewById(R.id.shuffleBtn);
        greetLine = findViewById(R.id.greetLine);
        dayLine = findViewById(R.id.dayLine);
        dateLine = findViewById(R.id.dateLine);
        quoteText = findViewById(R.id.quoteText);
        breathGlow = findViewById(R.id.breathGlow);
        ctaBtn = findViewById(R.id.ctaBtn);
        likeBtn = findViewById(R.id.likeBtn);
        shareBtn = findViewById(R.id.shareBtn);
        saveBtn = findViewById(R.id.saveBtn);
        likeIcon = findViewById(R.id.likeIcon);
        saveIcon = findViewById(R.id.saveIcon);
        toastView = findViewById(R.id.toastView);
    }

    // =====================================================================
    //  QUOTE / BACKGROUND IMAGE
    // =====================================================================

    private void applyEntryVisuals() {
        quoteText.setText(isArabic ? currentEntry.ar : currentEntry.en);

        Glide.with(this)
                .load(currentEntry.imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(bgImage);

        restartKenBurns();
    }

    /** Ken Burns effect: slow continuous scale + drift, mirrors @keyframes kenburns (22s alternate). */
    private void restartKenBurns() {
        if (kenBurnsAnimator != null) kenBurnsAnimator.cancel();

        bgImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bgImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float w = bgImage.getWidth();
                float h = bgImage.getHeight();
                if (w == 0 || h == 0) return;

                PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.14f);
                PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.14f);
                PropertyValuesHolder transX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, -w * 0.015f);
                PropertyValuesHolder transY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, -h * 0.015f);

                ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(bgImage, scaleX, scaleY, transX, transY);
                anim.setDuration(22000);
                anim.setRepeatMode(ObjectAnimator.REVERSE);
                anim.setRepeatCount(ObjectAnimator.INFINITE);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.start();
                kenBurnsAnimator = anim;
            }
        });
    }

    private void onShuffleClicked() {
        shuffleBtn.animate().scaleX(0.85f).scaleY(0.85f).setDuration(90)
                .withEndAction(() -> shuffleBtn.animate().scaleX(1f).scaleY(1f).setDuration(140).start())
                .start();

        liked = false;
        refreshLikeIcon();

        // fade the quote text out (420ms), matching the JS setTimeout(...,420)
        quoteText.animate()
                .alpha(0f)
                .translationY(dp(8))
                .setDuration(420)
                .withEndAction(() -> {
                    QouteFeatureHistoryManager.Pick pick = historyManager.pickNextEntry(allQuotes, history);
                    currentEntry = pick.entry;
                    history = pick.nextHistory;
                    historyManager.saveHistory(history);

                    applyEntryVisuals();

                    quoteText.setTranslationY(dp(8));
                    quoteText.animate().alpha(1f).translationY(0f).setDuration(420).start();
                })
                .start();
    }

    // =====================================================================
    //  LANGUAGE TOGGLE
    // =====================================================================

    private void onLanguageToggle() {
        isArabic = !isArabic;
        updateLangUI();
    }

    private void updateLangUI() {
        int direction = isArabic ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR;
        phoneFrame.setLayoutDirection(direction);

        Typeface arFont = QouteFeatureUtils.getFont(this, "tajawal_regular.ttf");
        Typeface enFontItalic = QouteFeatureUtils.getFont(this, "cormorant_italic.ttf");
        Typeface enFontBold = QouteFeatureUtils.getFont(this, "cormorant_bold.ttf");

        brandMark.setText(QouteFeatureLangStrings.brand(isArabic));
        langPillText.setText(isArabic ? "EN" : "AR");
        ctaBtn.setText(QouteFeatureLangStrings.more(isArabic));

        quoteText.setText(isArabic ? currentEntry.ar : currentEntry.en);
        quoteText.setTextSize(isArabic ? 16.5f : 18f);
        quoteText.setTypeface(isArabic ? arFont : enFontItalic, isArabic ? Typeface.NORMAL : Typeface.ITALIC);

        dayLine.setTypeface(isArabic ? arFont : enFontBold, Typeface.BOLD);
        greetLine.setTypeface(isArabic ? arFont : enFontItalic);
        dateLine.setTypeface(isArabic ? arFont : enFontItalic);

        refreshClockTexts();
    }

    // =====================================================================
    //  LIVE CLOCK  (greeting / day / date / time)
    // =====================================================================

    private void startClock() {
        clockRunnable = new Runnable() {
            @Override
            public void run() {
                refreshClockTexts();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(clockRunnable);
    }

    private void refreshClockTexts() {
        Calendar now = Calendar.getInstance();
        int hour24 = now.get(Calendar.HOUR_OF_DAY);
        int hour12 = hour24 % 12;
        if (hour12 == 0) hour12 = 12;
        String mm = String.format("%02d", now.get(Calendar.MINUTE));
        String ampm = hour24 < 12 ? QouteFeatureLangStrings.am(isArabic) : QouteFeatureLangStrings.pm(isArabic);

        String dayName = isArabic
                ? QouteFeatureLangStrings.DAYS_AR[now.get(Calendar.DAY_OF_WEEK) - 1]
                : QouteFeatureLangStrings.DAYS_EN[now.get(Calendar.DAY_OF_WEEK) - 1];
        String monthName = isArabic
                ? QouteFeatureLangStrings.MONTHS_AR[now.get(Calendar.MONTH)]
                : QouteFeatureLangStrings.MONTHS_EN[now.get(Calendar.MONTH)];
        int date = now.get(Calendar.DAY_OF_MONTH);

        greetLine.setText(QouteFeatureLangStrings.greet(isArabic, hour24) + " ✦");
        dayLine.setText(dayName);
        dateLine.setText(monthName + " " + date + " — " + String.format("%02d", hour12) + ":" + mm + " " + ampm);
    }

    // =====================================================================
    //  CTA BUTTON REVEAL (appears 4.2s after load, then pulses forever)
    // =====================================================================

    private void scheduleCtaReveal() {
        handler.postDelayed(() -> {
            ctaBtn.setVisibility(View.VISIBLE);
            ctaBtn.setAlpha(0f);
            ctaBtn.setScaleX(0.96f);
            ctaBtn.setScaleY(0.96f);
            ctaBtn.setTranslationY(dp(14));
            ctaBtn.animate()
                    .alpha(1f).scaleX(1f).scaleY(1f).translationY(0f)
                    .setDuration(600)
                    .withEndAction(this::startCtaPulse)
                    .start();
        }, 4200);
    }

    /** Mirrors @keyframes ctaPulse — a soft breathing shadow/scale loop every 2.6s. */
    private void startCtaPulse() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.015f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.015f);
        PropertyValuesHolder elevation = PropertyValuesHolder.ofFloat(View.TRANSLATION_Z, 6f, 14f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(ctaBtn, scaleX, scaleY, elevation);
        anim.setDuration(1300);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        ctaPulseAnimator = anim;
    }

    // =====================================================================
    //  BREATHING GLOW (behind the quote text) + SHUFFLE BUTTON GLOW
    // =====================================================================

    /** Mirrors @keyframes breathe — 4s ease-in-out infinite scale+opacity loop. */
    private void startBreathingGlow() {
        breathGlow.setPivotX(dp(45));
        breathGlow.setPivotY(dp(45));
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.85f, 1.25f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.85f, 1.25f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 0.95f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(breathGlow, scaleX, scaleY, alpha);
        anim.setDuration(2000);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        breathAnimator = anim;
    }

    /** Mirrors @keyframes pulseGlow on the shuffle button — 4s glow intensity loop. */
    private void startShufflePulse() {
        PropertyValuesHolder elevation = PropertyValuesHolder.ofFloat(View.TRANSLATION_Z, 4f, 12f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(shuffleBtn, elevation);
        anim.setDuration(2000);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        shufflePulseAnimator = anim;
    }

    // =====================================================================
    //  LIKE / SHARE / SAVE
    // =====================================================================

    private void onLikeClicked() {
        liked = !liked;
        refreshLikeIcon();
        bounce(likeBtn);
    }

    private void refreshLikeIcon() {
        likeIcon.setImageResource(liked ? R.drawable.qoute_feature_ic_heart_filled : R.drawable.qoute_feature_ic_heart);
        likeBtn.setBackgroundResource(liked ? R.drawable.qoute_feature_bg_icon_btn_active : R.drawable.qoute_feature_bg_icon_btn);
    }

    private void onShareClicked() {
        String text = isArabic ? currentEntry.ar : currentEntry.en;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("quote", text));
        bounce(shareBtn);
        showToast(QouteFeatureLangStrings.copiedMsg(isArabic));
    }

    private void onSaveClicked() {
        saved = !saved;
        saveIcon.setImageResource(saved ? R.drawable.qoute_feature_ic_bookmark_filled : R.drawable.qoute_feature_ic_bookmark);
        saveBtn.setBackgroundResource(saved ? R.drawable.qoute_feature_bg_icon_btn_active : R.drawable.qoute_feature_bg_icon_btn);
        bounce(saveBtn);
        if (saved) showToast(QouteFeatureLangStrings.saveMsg(isArabic));
    }

    private void bounce(View v) {
        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(90)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(160).start())
                .start();
    }

    // =====================================================================
    //  TOAST
    // =====================================================================

    private void showToast(String message) {
        if (pendingToastHide != null) toastHandler.removeCallbacks(pendingToastHide);

        toastView.setText(message);
        toastView.setVisibility(View.VISIBLE);
        toastView.setAlpha(0f);
        toastView.setTranslationY(dp(8));
        toastView.animate().alpha(1f).translationY(0f).setDuration(300).start();

        pendingToastHide = () -> toastView.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> toastView.setVisibility(View.GONE))
                .start();
        toastHandler.postDelayed(pendingToastHide, 2000);
    }

    // =====================================================================
    //  UTIL
    // =====================================================================

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        toastHandler.removeCallbacksAndMessages(null);
        if (kenBurnsAnimator != null) kenBurnsAnimator.cancel();
        if (breathAnimator != null) breathAnimator.cancel();
        if (shufflePulseAnimator != null) shufflePulseAnimator.cancel();
        if (ctaPulseAnimator != null) ctaPulseAnimator.cancel();
    }
}
