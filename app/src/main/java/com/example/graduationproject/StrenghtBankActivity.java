package com.example.graduationproject;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.R;
import com.example.graduationproject.models.StrengthsRepository;
import com.example.graduationproject.models.Trait;
import com.example.graduationproject.models.TraitEvidence;
import com.example.graduationproject.ui.AddTraitDialogFragment;
import com.example.graduationproject.ui.AnalysisResultDialogFragment;
import com.example.graduationproject.ui.StrengthsBankActivity;
import com.example.graduationproject.widget.ChevronRotator;
import com.example.graduationproject.widget.FadeUtils;
import com.example.graduationproject.widget.NewTraitPulseAnimator;
import com.example.graduationproject.widget.SpinAnimator;
import com.example.graduationproject.widget.ToastController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Full Java/Android port of "StrengthsBankScreen": a radial header
 * summarizing discovered traits, an expandable trait-card list, a
 * simulated "analyze conversation" flow, and a manual "add trait" dialog.
 */
public class StrenghtBankActivity extends AppCompatActivity {

    private static final String STATE_READY = "ready";
    private static final String STATE_LOADING = "loading";
    private static final String STATE_EMPTY = "empty";

    private StrengthsRepository repo;

    // ------- state (mirrors the React useState hooks) -------
    private String expandedId = null;
    private String analyzeState = STATE_READY;
    private boolean hasAnalyzedOnce = false;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ToastController toastController;
    private ObjectAnimator radialSpinAnimator;
    private ObjectAnimator loaderSpinAnimator;
    private final Map<String, ValueAnimator> newTraitPulseAnimators = new HashMap<>();

    // ------- views -------
    private TextView tvDiscoveredCount;
    private FrameLayout traitDotsContainer;
    private View dashedRing;
    private LinearLayout llTraitCards, llFooterAnalyzeIcon;
    private View btnAnalyze;
    private TextView tvAnalyzeLabel;
    private ImageView ivAnalyzeSpinner, ivAnalyzeSparkle;
    private FrameLayout toastHost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_strnght_bank);

        View root = findViewById(R.id.bank_root_frame);
        View contentContainer = findViewById(R.id.bank_content_container);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (contentContainer != null) {
                contentContainer.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });

        repo = StrengthsRepository.getInstance(this);
        toastController = new ToastController(findViewById(R.id.toastHost));

        bindViews();
        setListeners();

        renderRadialHeader(true);
        renderTraitCards(true);
        renderAnalyzeButton();

        // Animate individual components ONLY ONCE in onCreate
        FadeUtils.slideInUp(findViewById(R.id.header_container), 10, 1000, 100);
        FadeUtils.scaleIn(findViewById(R.id.includeRadialHeader), 1500, 300);
        FadeUtils.slideInUp(findViewById(R.id.bank_title_container), 15, 1200, 600);
        FadeUtils.slideInUp(findViewById(R.id.highlight_container), 15, 1200, 900);
        FadeUtils.slideInUp(btnAnalyze, 20, 1500, 1200);

        getSupportFragmentManager().setFragmentResultListener(
                AddTraitDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    Trait added = repo.addSelfTrait(this,
                            bundle.getString(AddTraitDialogFragment.KEY_NAME),
                            bundle.getString(AddTraitDialogFragment.KEY_NOTE));
                    renderAll();
                    toastController.show(getString(R.string.toast_trait_added), 2200);
                });

        FadeUtils.fadeIn(findViewById(android.R.id.content), 1200);
    }

    private void bindViews() {
        View radialHeader = findViewById(R.id.includeRadialHeader);
        tvDiscoveredCount = radialHeader.findViewById(R.id.tvDiscoveredCount);
        traitDotsContainer = radialHeader.findViewById(R.id.traitDotsContainer);
        dashedRing = radialHeader.findViewById(R.id.dashedRing);

        llTraitCards = findViewById(R.id.llTraitCards);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        tvAnalyzeLabel = findViewById(R.id.tvAnalyzeLabel);
        ivAnalyzeSpinner = findViewById(R.id.ivAnalyzeSpinner);
        ivAnalyzeSparkle = findViewById(R.id.ivAnalyzeSparkle);
    }

    private void setListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnAddTraitHeader).setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentByTag("add_trait") == null) {
                AddTraitDialogFragment.newInstance().show(getSupportFragmentManager(), "add_trait");
            }
        });

        btnAnalyze.setOnClickListener(v -> handleAnalyze());

        findViewById(R.id.btnBankHeader).setOnClickListener(v -> {
            startActivity(new Intent(this, StrengthsBankActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        radialSpinAnimator = SpinAnimator.start(dashedRing, 60_000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SpinAnimator.stop(radialSpinAnimator);
    }

    // ===================== RENDERING =====================

    private void renderAll() {
        renderRadialHeader(false);
        renderTraitCards(false);
        renderAnalyzeButton();
    }

    private void renderRadialHeader(boolean animate) {
        int discoveredCount = repo.traits.size(); // Total strengths list count
        tvDiscoveredCount.setText(String.valueOf(discoveredCount));

        if (animate) {
            FadeUtils.scaleIn(dashedRing, 1500, 200);
            FadeUtils.scaleIn(findViewById(R.id.center_circle), 1200, 400);
        }

        traitDotsContainer.removeAllViews();
    }

    private void renderTraitCards(boolean animate) {
        // Stop any pulse animators for cards about to be rebuilt.
        for (ValueAnimator a : newTraitPulseAnimators.values()) a.cancel();
        newTraitPulseAnimators.clear();

        llTraitCards.removeAllViews();
        List<Trait> allTraits = repo.traits;
        // Only show a preview of top 3 on main screen
        List<Trait> preview = allTraits.subList(0, Math.min(3, allTraits.size()));
        
        for (int i = 0; i < preview.size(); i++) {
            Trait trait = preview.get(i);
            View card = buildTraitCard(trait);
            // Ensure visible alpha if not animating
            card.setAlpha(1.0f);
            card.setScaleX(1.0f);
            card.setScaleY(1.0f);
            llTraitCards.addView(card);
            if (animate) {
                FadeUtils.scaleIn(card, 1200, 800 + (i * 400L));
            }
        }
    }

    private View buildTraitCard(Trait trait) {
        View item = LayoutInflater.from(this).inflate(R.layout.item_trait_card, llTraitCards, false);

        View newPulseRing = item.findViewById(R.id.newPulseRing);
        LinearLayout cardRoot = item.findViewById(R.id.cardRoot);
        LinearLayout btnHeader = item.findViewById(R.id.btnHeader);
        ImageView ivChevron = item.findViewById(R.id.ivChevron);
        TextView tvSelfAddedBadge = item.findViewById(R.id.tvSelfAddedBadge);
        TextView tvNewBadge = item.findViewById(R.id.tvNewBadge);
        TextView tvTraitLabel = item.findViewById(R.id.tvTraitLabel);
        View dotColor = item.findViewById(R.id.dotColor);
        TextView tvCount = item.findViewById(R.id.tvCount);
        TextView tvQuote = item.findViewById(R.id.tvQuote);
        LinearLayout llProgressDots = item.findViewById(R.id.llProgressDots);
        LinearLayout groupExpanded = item.findViewById(R.id.groupExpanded);
        LinearLayout llEvidence = item.findViewById(R.id.llEvidence);
        TextView btnStartExercise = item.findViewById(R.id.btnStartExercise);
        TextView tvExerciseText = item.findViewById(R.id.tvExerciseText);

        cardRoot.setBackgroundResource(trait.isNew ? R.drawable.bg_card_new : R.drawable.bg_card);
        tvSelfAddedBadge.setVisibility(trait.selfAdded ? View.VISIBLE : View.GONE);
        tvNewBadge.setVisibility(trait.isNew ? View.VISIBLE : View.GONE);
        tvTraitLabel.setText(trait.label);
        dotColor.setVisibility(trait.selfAdded ? View.GONE : View.VISIBLE);
        if (dotColor.getBackground() != null) {
            dotColor.getBackground().mutate().setTint(trait.colorInt);
        }
        tvCount.setText(getString(R.string.count_times_format, trait.count));
        tvQuote.setText("\u201C" + trait.quote + "\u201D");
        tvExerciseText.setText(trait.exercise);

        buildProgressDots(llProgressDots, trait.count, trait.colorInt);

        boolean expanded = trait.id.equals(expandedId);
        ChevronRotator.setExpanded(ivChevron, expanded, false);
        groupExpanded.setVisibility(expanded ? View.VISIBLE : View.GONE);
        if (expanded) {
            buildEvidenceRows(llEvidence, trait.evidence);
        }

        if (trait.isNew) {
            ValueAnimator pulse = NewTraitPulseAnimator.start(newPulseRing);
            newTraitPulseAnimators.put(trait.id, pulse);
        }

        btnHeader.setOnClickListener(v -> {
            boolean willExpand = !trait.id.equals(expandedId);
            expandedId = willExpand ? trait.id : null;
            if (trait.isNew) {
                trait.isNew = false;
            }
            renderTraitCards(false);
        });

        btnStartExercise.setOnClickListener(v -> handleStartExercise(trait.exercise));

        return item;
    }

    private void buildProgressDots(LinearLayout container, int count, int colorInt) {
        container.removeAllViews();
        int filled = Math.min(count, 10);
        float density = getResources().getDisplayMetrics().density;

        for (int i = 0; i < 10; i++) {
            TextView star = new TextView(this);
            star.setText("\u2605");
            star.setTextSize(10);
            star.setTextColor(i < filled ? colorInt : ContextCompat.getColor(this, R.color.overlay_15));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd((int) (2 * density));
            star.setLayoutParams(lp);
            container.addView(star);
        }
    }

    private void buildEvidenceRows(LinearLayout container, List<TraitEvidence> evidence) {
        container.removeAllViews();
        for (TraitEvidence e : evidence) {
            View row = LayoutInflater.from(this).inflate(R.layout.item_evidence_row, container, false);
            ((TextView) row.findViewById(R.id.tvWhen)).setText(e.when);
            ((TextView) row.findViewById(R.id.tvCtx)).setText(e.ctx);
            container.addView(row);
        }
        FadeUtils.evidenceFade(container);
    }

    // ===================== ANALYZE FLOW =====================

    private void handleAnalyze() {
        if (!analyzeState.equals(STATE_READY)) return;

        analyzeState = STATE_LOADING;
        renderAnalyzeButton();

        handler.postDelayed(() -> {
            if (!hasAnalyzedOnce) {
                repo.applyAnalysisResult();
                hasAnalyzedOnce = true;
                analyzeState = STATE_READY;
                renderAll();

                if (getSupportFragmentManager().findFragmentByTag("analysis_result") == null) {
                    AnalysisResultDialogFragment.newInstance()
                            .show(getSupportFragmentManager(), "analysis_result");
                }
            } else {
                analyzeState = STATE_EMPTY;
                renderAnalyzeButton();
                toastController.show(getString(R.string.toast_no_new_conversations), 2800);
            }
        }, 1600);
    }

    private void renderAnalyzeButton() {
        boolean loading = analyzeState.equals(STATE_LOADING);
        boolean empty = analyzeState.equals(STATE_EMPTY);

        btnAnalyze.setEnabled(analyzeState.equals(STATE_READY));
        btnAnalyze.setAlpha(analyzeState.equals(STATE_READY) ? 1f : 0.5f);
        btnAnalyze.setBackgroundResource(empty ? R.drawable.bg_analyze_btn_empty : R.drawable.bg_analyze_btn_ready);

        ivAnalyzeSpinner.setVisibility(loading ? View.VISIBLE : View.GONE);
        ivAnalyzeSparkle.setVisibility((!loading && !empty) ? View.VISIBLE : View.GONE);

        if (loading) {
            tvAnalyzeLabel.setText(R.string.analyze_loading);
            if (loaderSpinAnimator == null) {
                loaderSpinAnimator = SpinAnimator.start(ivAnalyzeSpinner, 1000);
            }
        } else {
            tvAnalyzeLabel.setText(empty ? R.string.analyze_empty : R.string.analyze_ready);
            SpinAnimator.stop(loaderSpinAnimator);
            loaderSpinAnimator = null;
        }
        FadeUtils.fadeIn(btnAnalyze, 300);
    }

    /** Called by both the trait-card "ابدأ" button and (via the dialog) the related-trait cards. */
    public void handleStartExercise(String exerciseText) {
        String preview = exerciseText.length() > 24 ? exerciseText.substring(0, 24) : exerciseText;
        toastController.show(getString(R.string.toast_started_exercise_format, preview), 2400);
    }

    /** Called by the analysis-result dialog's "شوفها في بنكي" action. */
    public void openTraitExpanded(String traitId) {
        Intent intent = new Intent(this, StrengthsBankActivity.class);
        intent.putExtra("EXTRA_TRAIT_ID", traitId);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        SpinAnimator.stop(radialSpinAnimator);
        SpinAnimator.stop(loaderSpinAnimator);
        for (ValueAnimator a : newTraitPulseAnimators.values()) a.cancel();
    }
}
