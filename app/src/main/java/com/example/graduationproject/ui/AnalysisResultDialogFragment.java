package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.R;
import com.example.graduationproject.StrenghtBankActivity;
import com.example.graduationproject.models.OldConversation;
import com.example.graduationproject.models.RelatedTrait;
import com.example.graduationproject.models.StrengthsRepository;
import com.example.graduationproject.widget.ChevronRotator;
import com.example.graduationproject.widget.FadeUtils;

/**
 * Equivalent of &lt;AnalysisResultScreen/&gt;: a full-screen reveal for the
 * newly-discovered trait, a secondary-trait bump notice, and a togglable
 * list of related traits the person could develop next.
 */
public class AnalysisResultDialogFragment extends DialogFragment {

    public static AnalysisResultDialogFragment newInstance() {
        return new AnalysisResultDialogFragment();
    }

    private boolean showRelated = false;
    private LinearLayout llRelatedTraits;
    private ImageView ivRelatedChevron;

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public int getTheme() {
        return R.style.Theme_StrengthsBank_FullScreenDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_analysis_result, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(root.findViewById(R.id.analysis_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OldConversation data = StrengthsRepository.getInstance(requireContext()).oldConversation;

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        TextView tvSource = root.findViewById(R.id.tvSource);
        tvSource.setText(getString(R.string.source_format, data.dateLabel, data.timeLabel, data.relativeLabel));

        View revealIconBg = root.findViewById(R.id.revealIconBg);
        revealIconBg.getBackground().mutate().setTint(data.primaryTrait.colorInt);

        ((TextView) root.findViewById(R.id.tvRevealLabel)).setText(data.primaryTrait.label);
        ((TextView) root.findViewById(R.id.tvRevealParaphrase)).setText(data.primaryTrait.paraphrase);

        ((TextView) root.findViewById(R.id.tvSecondaryDelta)).setText("+" + data.secondaryUpdate.delta);
        ((TextView) root.findViewById(R.id.tvSecondaryTitle)).setText(
                getString(R.string.secondary_update_format, data.secondaryUpdate.label));
        ((TextView) root.findViewById(R.id.tvSecondaryNote)).setText(data.secondaryUpdate.note);

        ivRelatedChevron = root.findViewById(R.id.ivRelatedChevron);
        llRelatedTraits = root.findViewById(R.id.llRelatedTraits);
        TextView tvRelatedToggleLabel = root.findViewById(R.id.tvRelatedToggleLabel);
        tvRelatedToggleLabel.setText(getString(R.string.related_toggle_format, data.primaryTrait.label));

        buildRelatedTraits(data);

        View btnToggleRelated = root.findViewById(R.id.btnToggleRelated);
        btnToggleRelated.setOnClickListener(v -> {
            showRelated = !showRelated;
            ChevronRotator.setExpanded(ivRelatedChevron, showRelated, true);
            llRelatedTraits.setVisibility(showRelated ? View.VISIBLE : View.GONE);
            if (showRelated) {
                FadeUtils.relatedFade(llRelatedTraits);
            }
        });

        root.findViewById(R.id.btnViewInBank).setOnClickListener(v -> {
            if (getActivity() instanceof StrenghtBankActivity) {
                ((StrenghtBankActivity) getActivity()).openTraitExpanded(data.primaryTrait.id);
            }
            dismiss();
        });

        root.findViewById(R.id.btnLater).setOnClickListener(v -> dismiss());

        FadeUtils.resultFade(root.findViewById(R.id.scrollContent));
        FadeUtils.slideInUp(root.findViewById(R.id.revealIconCircle), 20, 800, 200);
        FadeUtils.slideInUp(root.findViewById(R.id.tvRevealLabel), 15, 800, 300);
        return root;
    }

    private void buildRelatedTraits(OldConversation data) {
        llRelatedTraits.removeAllViews();
        for (int i = 0; i < data.relatedTraits.size(); i++) {
            RelatedTrait rt = data.relatedTraits.get(i);
            View card = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_related_trait_card, llRelatedTraits, false);

            ((TextView) card.findViewById(R.id.tvRelatedLabel)).setText(rt.label);
            card.findViewById(R.id.dotColor).getBackground().mutate().setTint(rt.colorInt);
            ((TextView) card.findViewById(R.id.tvRelatedReason)).setText(rt.reason);

            View suggestionCard = card.findViewById(R.id.relatedSuggestionCard);
            suggestionCard.getBackground().mutate().setTint(rt.colorInt);

            TextView btnStart = card.findViewById(R.id.btnStartRelated);
            btnStart.getBackground().mutate().setTint(rt.colorInt);
            btnStart.setOnClickListener(v -> {
                if (getActivity() instanceof StrenghtBankActivity) {
                    ((StrenghtBankActivity) getActivity()).handleStartExercise(rt.suggestion);
                }
            });

            TextView tvTag = card.findViewById(R.id.tvRelatedTag);
            tvTag.setTextColor(rt.colorInt);

            ((TextView) card.findViewById(R.id.tvRelatedSuggestion)).setText(rt.suggestion);

            llRelatedTraits.addView(card);
            FadeUtils.slideInUp(card, 10, 400, i * 80L);
        }
    }
}
