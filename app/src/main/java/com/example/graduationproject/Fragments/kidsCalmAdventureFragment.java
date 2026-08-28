package com.example.graduationproject.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.R;
import com.example.graduationproject.models.kidsCalmDurationOption;
import com.example.graduationproject.models.kidsCalmKidCardModel;
import com.example.graduationproject.models.kidsCalmWordModel;
import com.example.graduationproject.util.kidsCalmAnimUtils;
import com.example.graduationproject.util.kidsCalmAppState;
import com.example.graduationproject.view.kidsCalmCircularCountdownView;
import com.example.graduationproject.view.kidsCalmKidCardView;
import com.example.graduationproject.view.kidsCalmMascotView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/** Mirrors the React <AdventureTab> (the "جربيها" simulate tab). */
public class kidsCalmAdventureFragment extends Fragment {

    public interface Host {
        void goToGalleryTab();
        void goToWordsTab();
        void onAdventureFinished();
    }

    private static final int STEP_BALLOON = 0;
    private static final int STEP_HUNT = 1;
    private static final int STEP_WORD = 2;
    private static final int STEP_CARD = 3;
    private static final int MAX_PUFFS = 6;
    private static final int HUNT_TARGET = 5;

    private Host host;
    public void setHost(Host host) { this.host = host; }

    private FrameLayout content;
    private LinearLayout stepDotsRow;
    private View backButton;

    private int step = -1; // -1 = intro
    private final Handler handler = new Handler(Looper.getMainLooper());

    // balloon state
    private int breathCount = 0;
    private final Runnable balloonAutoPuff = new Runnable() {
        @Override public void run() {
            if (step != STEP_BALLOON) return;
            if (breathCount >= MAX_PUFFS) { goToStep(STEP_HUNT); return; }
            breathCount++;
            updateBalloonUi();
            handler.postDelayed(this, 2600);
        }
    };

    // hunt state
    private int found = 0;
    private final Runnable huntTimeout = () -> { if (step == STEP_HUNT) goToStep(STEP_WORD); };

    // word countdown state
    private kidsCalmWordModel wordPick;
    private int remainingSeconds;
    private int totalSeconds;
    private kidsCalmCircularCountdownView countdownView;
    private TextView wordFooter;
    private final Runnable wordTick = new Runnable() {
        @Override public void run() {
            if (step != STEP_WORD) return;
            if (remainingSeconds <= 0) {
                goToStep(STEP_CARD);
                if (host != null) host.onAdventureFinished();
                return;
            }
            remainingSeconds--;
            updateWordUi();
            handler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kids_calm_fragment_adventure, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        content = v.findViewById(R.id.advContent);
        stepDotsRow = v.findViewById(R.id.stepDotsRow);
        backButton = v.findViewById(R.id.advBackButton);
        backButton.setOnClickListener(x -> goToStep(-1));
        render();
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }

    /** Call when the user switches back into this tab, to re-check active card / re-render. */
    public void render() {
        if (getView() == null) return;
        kidsCalmKidCardModel active = kidsCalmAppState.get().getActiveCard();
        if (active == null) {
            showNoCard();
            return;
        }
        if (step < 0) {
            showIntro();
        } else {
            showStep(step);
        }
    }

    private void clearTimers() {
        handler.removeCallbacksAndMessages(null);
    }

    // ---------------- NO CARD ----------------
    private void showNoCard() {
        clearTimers();
        stepDotsRow.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        content.removeAllViews();
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.kids_calm_view_adv_no_card, content, true);
        v.findViewById(R.id.goGalleryButton).setOnClickListener(x -> { if (host != null) host.goToGalleryTab(); });
    }

    // ---------------- INTRO ----------------
    private void showIntro() {
        clearTimers();
        step = -1;
        stepDotsRow.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        content.removeAllViews();
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.kids_calm_view_adv_intro, content, true);

        kidsCalmMascotView mascot = v.findViewById(R.id.introMascot);
        mascot.setText(getString(R.string.kids_calm_adv_intro));
        mascot.setEmojiSize(26);

        v.findViewById(R.id.startButton).setOnClickListener(x -> goToStep(STEP_BALLOON));

        TextView noWordsHint = v.findViewById(R.id.noWordsHint);
        boolean noFavorites = kidsCalmAppState.get().getFavoriteWords().isEmpty();
        noWordsHint.setVisibility(noFavorites ? View.VISIBLE : View.GONE);
        noWordsHint.setOnClickListener(x -> { if (host != null) host.goToWordsTab(); });
    }

    // ---------------- STEP ROUTER ----------------
    private void goToStep(int newStep) {
        clearTimers();
        step = newStep;
        if (newStep == -1) {
            showIntro();
            return;
        }
        showStep(newStep);
    }

    private void showStep(int s) {
        content.removeAllViews();
        renderStepDots(s);
        backButton.setVisibility(s == STEP_CARD ? View.GONE : View.VISIBLE);
        stepDotsRow.setVisibility(View.VISIBLE);

        switch (s) {
            case STEP_BALLOON: showBalloon(); break;
            case STEP_HUNT: showHunt(); break;
            case STEP_WORD: showWord(); break;
            case STEP_CARD: showFinal(); break;
        }
    }

    private void renderStepDots(int current) {
        stepDotsRow.removeAllViews();
        String[] keys = {"balloon", "hunt", "word", "card"};
        for (int i = 0; i < keys.length; i++) {
            View dot = new View(requireContext());
            boolean active = i == current;
            boolean done = i < current;
            boolean isCard = keys[i].equals("card");
            int size = active ? dp(14) : dp(10);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMarginEnd(dp(10));
            dot.setLayoutParams(lp);

            int colorRes;
            if (isCard) colorRes = R.color.kids_calm_coral;
            else if (active) colorRes = R.color.kids_calm_sun;
            else if (done) colorRes = R.color.kids_calm_mint;
            else colorRes = R.color.kids_calm_cardBorder;

            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            gd.setColor(getResources().getColor(colorRes));
            dot.setBackground(gd);
            stepDotsRow.addView(dot);
        }
    }

    // ---------------- BALLOON ----------------
    private View balloonEmojiView;
    private TextView balloonCounter;

    private void showBalloon() {
        breathCount = 0;
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.kids_calm_view_adv_balloon, content, true);
        kidsCalmMascotView mascot = v.findViewById(R.id.balloonMascot);
        mascot.setText(getString(R.string.kids_calm_adv_balloon_hint));
        mascot.setEmojiSize(26);

        balloonEmojiView = v.findViewById(R.id.balloonEmoji);
        balloonCounter = v.findViewById(R.id.balloonCounter);
        balloonEmojiView.setOnClickListener(x -> puffBalloon());

        updateBalloonUi();
        handler.postDelayed(balloonAutoPuff, 2600);
    }

    private void puffBalloon() {
        if (step != STEP_BALLOON) return;
        if (breathCount < MAX_PUFFS) {
            breathCount++;
            updateBalloonUi();
        }
    }

    private void updateBalloonUi() {
        if (balloonEmojiView == null) return;
        float scale = 0.6f + breathCount * ((1.5f - 0.6f) / MAX_PUFFS);
        balloonEmojiView.animate().scaleX(scale).scaleY(scale).setDuration(1100).start();
        balloonCounter.setText(getString(R.string.kids_calm_adv_balloon_progress) + " " + breathCount + "/" + MAX_PUFFS);
        if (breathCount >= MAX_PUFFS) {
            handler.postDelayed(() -> { if (step == STEP_BALLOON) goToStep(STEP_HUNT); }, 500);
        }
    }

    // ---------------- HUNT ----------------
    private LinearLayout huntStarsRow;

    private void showHunt() {
        found = 0;
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.kids_calm_view_adv_hunt, content, true);
        kidsCalmMascotView mascot = v.findViewById(R.id.huntMascot);
        mascot.setText(getString(R.string.kids_calm_adv_hunt_hint));
        mascot.setEmojiSize(26);

        huntStarsRow = v.findViewById(R.id.huntStarsRow);
        renderHuntStars();

        v.findViewById(R.id.huntFoundButton).setOnClickListener(x -> {
            if (found < HUNT_TARGET) {
                found++;
                renderHuntStars();
                if (found >= HUNT_TARGET) goToStep(STEP_WORD);
            }
        });

        handler.postDelayed(huntTimeout, 12000);
    }

    private void renderHuntStars() {
        huntStarsRow.removeAllViews();
        for (int i = 0; i < HUNT_TARGET; i++) {
            TextView star = new TextView(requireContext());
            star.setText(getString(R.string.kids_calm_icon_star));
            star.setTextSize(22);
            star.setAlpha(i < found ? 1f : 0.25f);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(8));
            star.setLayoutParams(lp);
            huntStarsRow.addView(star);
        }
    }

    // ---------------- WORD ----------------
    private void showWord() {
        kidsCalmAppState state = kidsCalmAppState.get();
        List<kidsCalmWordModel> pool = state.getFavoriteWords();
        boolean hasFavorites = !pool.isEmpty();
        if (!hasFavorites) {
            pool = new ArrayList<>();
            pool.add(new kidsCalmWordModel(-1, "الله معي دايماً", "💙", false, "short"));
        }
        wordPick = pool.get(new Random().nextInt(pool.size()));
        kidsCalmDurationOption dur = state.durByKey(wordPick.durKey);
        totalSeconds = Math.max(60, dur.minutes * 60);
        remainingSeconds = totalSeconds;

        View v = LayoutInflater.from(requireContext()).inflate(R.layout.kids_calm_view_adv_word, content, true);
        TextView emoji = v.findViewById(R.id.wordStepEmoji);
        TextView text = v.findViewById(R.id.wordStepText);
        countdownView = v.findViewById(R.id.wordCountdown);
        wordFooter = v.findViewById(R.id.wordStepFooter);

        emoji.setText(wordPick.emoji);
        text.setText(wordPick.text);
        wordFooter.setText(hasFavorites ? getString(R.string.kids_calm_adv_word_fav) : getString(R.string.kids_calm_adv_word_generic));

        updateWordUi();
        handler.postDelayed(wordTick, 1000);
    }

    private void updateWordUi() {
        if (countdownView == null) return;
        float progress = 1f - (float) remainingSeconds / (float) totalSeconds;
        countdownView.setProgress(progress);
        int m = remainingSeconds / 60;
        int s = remainingSeconds % 60;
        countdownView.setCenterText(String.format(Locale.US, "%02d:%02d", m, s));
    }

    // ---------------- FINAL ----------------
    private void showFinal() {
        kidsCalmAppState.get().stars++;
        kidsCalmAppState.get().notifyChanged();
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.kids_calm_view_adv_final, content, true);
        kidsCalmKidCardView finalCard = v.findViewById(R.id.finalKidCard);
        kidsCalmKidCardModel active = kidsCalmAppState.get().getActiveCard();
        if (active != null) {
            finalCard.setContent(active.phrase, active.sticker, active.photoUri);
        }
        finalCard.setBig(true);
        finalCard.setCelebrate(true);
        kidsCalmAnimUtils.pop(v);

        v.findViewById(R.id.adventureDoneButton).setOnClickListener(x -> {
            finalCard.setCelebrate(false);
            goToStep(-1);
        });
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
