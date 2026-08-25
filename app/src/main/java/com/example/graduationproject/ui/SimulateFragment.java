package com.example.graduationproject.ui;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.AppHost;
import com.example.graduationproject.R;
import com.example.graduationproject.data.AppRepository;
import com.example.graduationproject.data.Constants;
import com.example.graduationproject.models.CalmDhikrItem;
import com.example.graduationproject.models.CardItem;
import com.example.graduationproject.models.CategoryMeta;
import com.example.graduationproject.view.CalmCardView;
import com.example.graduationproject.view.CircularCountdownView;
import com.example.graduationproject.view.StepDotsView;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Java equivalent of the JS <SimulateTab/>: the full crisis-simulation
 * flow — locked (no active card) -> intro -> breathing (4-7-8, 2 cycles,
 * real phase timers) -> grounding (auto 3s) -> dhikr (real countdown,
 * min 60s) -> card reveal (glow + particles) -> back to intro.
 */
public class SimulateFragment extends Fragment implements AppRepository.Listener {

    private final AppRepository repo = AppRepository.get();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Constants.BreathPhase[] breathSeq = Constants.buildBreathSequence();

    private int simStep = -1; // -1 intro/locked, 0 breathing, 1 grounding, 2 dhikr, 3 card
    private int phaseIdx = 0;
    private float currentScale = 1f;

    private CalmDhikrItem currentDhikrPick;
    private CountDownTimer dhikrTimer;
    private int totalDhikrSeconds;
    private int remainingDhikrSeconds;

    private Runnable pendingRunnable;

    /* views */
    private View lockedContainer, introContainer, stepContainer;
    private TextView btnGoToGallery, btnGoDhikrHint;
    private View btnStartSim;
    private TextView btnCancelSim;
    private StepDotsView stepDots;

    private View breathingContainer, groundingContainer, dhikrContainer, cardRevealContainer;
    private TextView breathCycleLabel, breathSecsText, breathPhaseLabel;
    private View breathCircle;

    private TextView dhikrEmoji, dhikrPickText, dhikrSourceLabel, btnAnotherDhikr;
    private CircularCountdownView dhikrCountdownRing;

    private CalmCardView revealCalmCard;
    private TextView btnImOk;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simulate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lockedContainer = view.findViewById(R.id.lockedContainer);
        introContainer = view.findViewById(R.id.introContainer);
        stepContainer = view.findViewById(R.id.stepContainer);
        btnGoToGallery = view.findViewById(R.id.btnGoToGallery);
        btnGoDhikrHint = view.findViewById(R.id.btnGoDhikrHint);
        btnStartSim = view.findViewById(R.id.btnStartSim);
        btnCancelSim = view.findViewById(R.id.btnCancelSim);
        stepDots = view.findViewById(R.id.stepDots);

        breathingContainer = view.findViewById(R.id.breathingContainer);
        groundingContainer = view.findViewById(R.id.groundingContainer);
        dhikrContainer = view.findViewById(R.id.dhikrContainer);
        cardRevealContainer = view.findViewById(R.id.cardRevealContainer);

        breathCycleLabel = view.findViewById(R.id.breathCycleLabel);
        breathSecsText = view.findViewById(R.id.breathSecsText);
        breathPhaseLabel = view.findViewById(R.id.breathPhaseLabel);
        breathCircle = view.findViewById(R.id.breathCircle);

        dhikrEmoji = view.findViewById(R.id.dhikrEmoji);
        dhikrPickText = view.findViewById(R.id.dhikrPickText);
        dhikrSourceLabel = view.findViewById(R.id.dhikrSourceLabel);
        btnAnotherDhikr = view.findViewById(R.id.btnAnotherDhikr);
        dhikrCountdownRing = view.findViewById(R.id.dhikrCountdownRing);

        revealCalmCard = view.findViewById(R.id.revealCalmCard);
        btnImOk = view.findViewById(R.id.btnImOk);

        btnGoToGallery.setOnClickListener(v -> {
            if (getActivity() instanceof AppHost) ((AppHost) getActivity()).switchTab(0);
        });
        btnGoDhikrHint.setOnClickListener(v -> {
            if (getActivity() instanceof AppHost) ((AppHost) getActivity()).switchTab(1);
        });
        btnStartSim.setOnClickListener(v -> goStep(0));
        btnCancelSim.setOnClickListener(v -> resetToIntro());
        btnAnotherDhikr.setOnClickListener(v -> pickRandomDhikr());
        btnImOk.setOnClickListener(v -> resetToIntro());

        render();
    }

    @Override
    public void onStart() {
        super.onStart();
        repo.addListener(this);
        render();
    }

    @Override
    public void onStop() {
        super.onStop();
        repo.removeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelTimers();
    }

    @Override
    public void onDataChanged() {
        render();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelTimers();
    }

    private void cancelTimers() {
        if (pendingRunnable != null) handler.removeCallbacks(pendingRunnable);
        if (dhikrTimer != null) dhikrTimer.cancel();
    }

    /* ---------------- top-level render ---------------- */

    private void render() {
        CardItem active = repo.getActiveCard();
        if (active == null) {
            lockedContainer.setVisibility(View.VISIBLE);
            introContainer.setVisibility(View.GONE);
            stepContainer.setVisibility(View.GONE);
            cancelTimers();
            return;
        }

        if (simStep < 0) {
            lockedContainer.setVisibility(View.GONE);
            introContainer.setVisibility(View.VISIBLE);
            stepContainer.setVisibility(View.GONE);
            btnGoDhikrHint.setVisibility(repo.getFavoriteDhikr().isEmpty() ? View.VISIBLE : View.GONE);
            revealCalmCard.setShowParticles(false);
            return;
        }

        lockedContainer.setVisibility(View.GONE);
        introContainer.setVisibility(View.GONE);
        stepContainer.setVisibility(View.VISIBLE);
        stepDots.setCurrent(simStep);

        breathingContainer.setVisibility(simStep == 0 ? View.VISIBLE : View.GONE);
        groundingContainer.setVisibility(simStep == 1 ? View.VISIBLE : View.GONE);
        dhikrContainer.setVisibility(simStep == 2 ? View.VISIBLE : View.GONE);
        cardRevealContainer.setVisibility(simStep == 3 ? View.VISIBLE : View.GONE);
        btnCancelSim.setVisibility(simStep == 3 ? View.GONE : View.VISIBLE);
    }

    private void resetToIntro() {
        cancelTimers();
        revealCalmCard.setShowParticles(false);
        simStep = -1;
        render();
    }

    /* ---------------- step transitions ---------------- */

    private void goStep(int step) {
        cancelTimers();
        simStep = step;
        render();

        if (step == 0) {
            phaseIdx = 0;
            currentScale = 1f;
            runBreathingPhase();
        } else if (step == 1) {
            pendingRunnable = () -> goStep(2);
            handler.postDelayed(pendingRunnable, 3000);
        } else if (step == 2) {
            pickRandomDhikr();
        } else if (step == 3) {
            CardItem active = repo.getActiveCard();
            if (active != null) {
                revealCalmCard.setCard(active.photo, active.phrase, true);
                revealCalmCard.setShowParticles(true);
            }
        }
    }

    /* ---------------- breathing (4-7-8) ---------------- */

    private void runBreathingPhase() {
        if (phaseIdx >= breathSeq.length) {
            goStep(1);
            return;
        }
        Constants.BreathPhase phase = breathSeq[phaseIdx];

        breathCycleLabel.setText(getString(R.string.breathing_cycle_fmt, phase.cycle, Constants.BREATH_CYCLES));
        breathSecsText.setText(String.valueOf(phase.secs));
        breathPhaseLabel.setText(phase.label);

        ValueAnimator anim = ValueAnimator.ofFloat(currentScale, phase.scale);
        anim.setDuration(phase.durMs);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(a -> {
            float s = (float) a.getAnimatedValue();
            breathCircle.setScaleX(s);
            breathCircle.setScaleY(s);
        });
        anim.start();
        currentScale = phase.scale;

        pendingRunnable = () -> {
            phaseIdx++;
            runBreathingPhase();
        };
        handler.postDelayed(pendingRunnable, phase.durMs);
    }

    /* ---------------- dhikr countdown ---------------- */

    private void pickRandomDhikr() {
        if (dhikrTimer != null) dhikrTimer.cancel();

        List<CalmDhikrItem> favorites = repo.getFavoriteDhikr();
        if (!favorites.isEmpty()) {
            currentDhikrPick = favorites.get(new Random().nextInt(favorites.size()));
            dhikrSourceLabel.setText(R.string.dhikr_source_favorite);
        } else {
            // default fallback dhikr, matches the JS pool fallback object
            currentDhikrPick = new CalmDhikrItem(-1, "أستغفر الله العظيم", "عام", true, 1);
            dhikrSourceLabel.setText(R.string.dhikr_source_default);
        }

        CategoryMeta meta = Constants.CATEGORY_META.get(currentDhikrPick.category);
        dhikrEmoji.setText(meta != null ? meta.emoji : "");
        dhikrPickText.setText(currentDhikrPick.text);
        btnAnotherDhikr.setVisibility(favorites.size() > 1 ? View.VISIBLE : View.GONE);

        totalDhikrSeconds = Math.max(60, currentDhikrPick.minutes * 60);
        remainingDhikrSeconds = totalDhikrSeconds;
        updateDhikrRing();

        dhikrTimer = new CountDownTimer((long) totalDhikrSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingDhikrSeconds = (int) Math.ceil(millisUntilFinished / 1000.0);
                updateDhikrRing();
            }

            @Override
            public void onFinish() {
                remainingDhikrSeconds = 0;
                updateDhikrRing();
                goStep(3);
            }
        };
        dhikrTimer.start();
    }

    private void updateDhikrRing() {
        float progress = 1f - (remainingDhikrSeconds / (float) totalDhikrSeconds);
        dhikrCountdownRing.setProgress(progress, formatMMSS(remainingDhikrSeconds));
    }

    private String formatMMSS(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", m, s);
    }
}
