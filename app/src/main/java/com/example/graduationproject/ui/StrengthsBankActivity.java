package com.example.graduationproject.ui;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import com.example.graduationproject.R;
import com.example.graduationproject.models.StrengthsRepository;
import com.example.graduationproject.models.Trait;
import com.example.graduationproject.models.TraitEvidence;
import com.example.graduationproject.widget.ChevronRotator;
import com.example.graduationproject.widget.FadeUtils;
import com.example.graduationproject.widget.NewTraitPulseAnimator;
import com.example.graduationproject.widget.ToastController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrengthsBankActivity extends AppCompatActivity {

    private StrengthsRepository repo;
    private String expandedId = null;
    private LinearLayout llBankCards;
    private ToastController toastController;
    private final Map<String, ValueAnimator> newTraitPulseAnimators = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_strengths_bank);

        repo = StrengthsRepository.getInstance(this);
        llBankCards = findViewById(R.id.llBankCards);
        toastController = new ToastController(findViewById(R.id.toastHost));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        if (getIntent().hasExtra("EXTRA_TRAIT_ID")) {
            expandedId = getIntent().getStringExtra("EXTRA_TRAIT_ID");
            Trait t = repo.findById(expandedId);
            if (t != null) t.isNew = false;
        }

        renderCards(true);

        FadeUtils.slideInUp(findViewById(R.id.btnBack), 10, 500, 100);
        FadeUtils.slideInUp(findViewById(R.id.bank_title_text), 15, 600, 200);
    }

    private void renderCards(boolean animate) {
        for (ValueAnimator a : newTraitPulseAnimators.values()) a.cancel();
        newTraitPulseAnimators.clear();

        llBankCards.removeAllViews();
        List<Trait> traits = repo.traits;
        for (int i = 0; i < traits.size(); i++) {
            Trait trait = traits.get(i);
            View card = buildTraitCard(trait);
            card.setAlpha(1.0f);
            card.setScaleX(1.0f);
            card.setScaleY(1.0f);
            llBankCards.addView(card);
            if (animate) {
                FadeUtils.scaleIn(card, 1200, 300 + (i * 250L));
            }
        }
    }

    private View buildTraitCard(Trait trait) {
        View item = LayoutInflater.from(this).inflate(R.layout.item_trait_card, llBankCards, false);

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
        dotColor.getBackground().mutate().setTint(trait.colorInt);
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
            renderCards(false);
        });

        btnStartExercise.setOnClickListener(v -> {
            String preview = trait.exercise.length() > 24 ? trait.exercise.substring(0, 24) : trait.exercise;
            toastController.show(getString(R.string.toast_started_exercise_format, preview), 2400);
        });

        return item;
    }

    private void buildProgressDots(LinearLayout container, int count, int colorInt) {
        container.removeAllViews();
        int filled = Math.min(count, 10);
        float density = getResources().getDisplayMetrics().density;

        for (int i = 0; i < 10; i++) {
            TextView star = new TextView(this);
            star.setText("\u2605");
            star.setTextSize(10); // Back to original size
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (ValueAnimator a : newTraitPulseAnimators.values()) a.cancel();
    }
}
