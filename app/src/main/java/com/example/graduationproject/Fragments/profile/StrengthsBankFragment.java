package com.example.graduationproject.Fragments.profile;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.ProfileNavigator;
import com.example.graduationproject.R;
import com.example.graduationproject.data.profile.ArabicDateUtils;
import com.example.graduationproject.data.profile.SeedData;
import com.example.graduationproject.models.profile.Trait;
import com.example.graduationproject.models.profile.TraitEvidence;

import java.util.ArrayList;
import java.util.List;

/**
 * Mirrors <StrengthsBankScreen/> + <TraitCard/> + <AddTraitDialog/>.
 */
public class StrengthsBankFragment extends Fragment {

    private List<Trait> traits;
    private String expandedId = null;
    private String analyzeState = "ready"; // ready | loading | empty
    private String pickedSuggestion = "";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private String[] suggestions;

    private LinearLayout traitsContainer;
    private FrameLayout wheelContainer;
    private TextView wheelCount;
    private Button btnAnalyze;
    private View overlayAddTrait, sheetContent;
    private EditText editName, editNote;
    private Button btnSaveTrait;
    private LinearLayout suggestionsRow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        traits = new ArrayList<>(SeedData.getInitialTraits(requireContext()));
        suggestions = getResources().getStringArray(R.array.trait_suggestions);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_strengths_bank, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileNavigator activity = (ProfileNavigator) requireActivity();

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> activity.showHome());

        wheelContainer = view.findViewById(R.id.wheel_container);
        wheelCount = view.findViewById(R.id.txt_wheel_count);
        traitsContainer = view.findViewById(R.id.traits_container);
        btnAnalyze = view.findViewById(R.id.btn_analyze);
        overlayAddTrait = view.findViewById(R.id.overlay_add_trait);
        sheetContent = overlayAddTrait.findViewById(R.id.sheet_content);
        editName = overlayAddTrait.findViewById(R.id.edit_trait_name);
        editNote = overlayAddTrait.findViewById(R.id.edit_trait_note);
        btnSaveTrait = overlayAddTrait.findViewById(R.id.btn_save_trait);
        suggestionsRow = overlayAddTrait.findViewById(R.id.suggestions_row);

        LinearLayout btnAddTrait = view.findViewById(R.id.btn_add_trait);
        btnAddTrait.setOnClickListener(v -> openAddTraitDialog());

        overlayAddTrait.setOnClickListener(v -> closeAddTraitDialog());
        sheetContent.setOnClickListener(v -> { /* swallow, mirrors stopPropagation */ });
        overlayAddTrait.findViewById(R.id.btn_close_dialog).setOnClickListener(v -> closeAddTraitDialog());

        btnAnalyze.setOnClickListener(v -> handleAnalyze(activity));

        renderWheel();
        renderTraits(activity);
        updateAnalyzeButton();
    }

    // ---------------------------------------------------------------
    // Wheel (mirrors the trigonometry positioning traits.slice(0,3) around a ring)
    // ---------------------------------------------------------------
    private void renderWheel() {
        while (wheelContainer.getChildCount() > 2) {
            wheelContainer.removeViewAt(2);
        }

        float density = getResources().getDisplayMetrics().density;
        float radiusPx = 78 * density;
        float centerPx = 80 * density;

        int count = Math.min(traits.size(), 3);
        for (int i = 0; i < count; i++) {
            Trait t = traits.get(i);
            double angle = (i / 3.0) * 2 * Math.PI - Math.PI / 2;
            float x = (float) (centerPx + radiusPx * Math.cos(angle));
            float y = (float) (centerPx + radiusPx * Math.sin(angle));

            LinearLayout label = new LinearLayout(requireContext());
            label.setOrientation(LinearLayout.HORIZONTAL);
            label.setGravity(android.view.Gravity.CENTER_VERTICAL);

            View dot = new View(requireContext());
            int dotSize = Math.round(10 * density);
            LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(dotSize, dotSize);
            dotLp.setMarginEnd(Math.round(4 * density));
            dot.setLayoutParams(dotLp);
            GradientDrawable dotBg = new GradientDrawable();
            dotBg.setShape(GradientDrawable.OVAL);
            dotBg.setColor(t.color);
            dot.setBackground(dotBg);

            TextView labelText = new TextView(requireContext());
            labelText.setText(t.label);
            labelText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
            labelText.setTypeface(labelText.getTypeface(), android.graphics.Typeface.BOLD);
            labelText.setTextSize(12);

            label.addView(dot);
            label.addView(labelText);

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = Math.round(x - 30 * density);
            lp.topMargin = Math.round(y - 10 * density);
            wheelContainer.addView(label, lp);
        }

        wheelCount.setText(ArabicDateUtils.toAr(traits.size() + 9));
    }

    // ---------------------------------------------------------------
    // Traits list
    // ---------------------------------------------------------------
    private void renderTraits(ProfileNavigator activity) {
        traitsContainer.removeAllViews();
        for (Trait t : traits) {
            View card = LayoutInflater.from(requireContext()).inflate(R.layout.item_trait_card, traitsContainer, false);
            bindTraitCard(card, t, activity);
            traitsContainer.addView(card);
        }
    }

    private void bindTraitCard(View card, Trait trait, ProfileNavigator activity) {
        boolean expanded = trait.id.equals(expandedId);

        LinearLayout header = card.findViewById(R.id.btnHeader);
        ImageView chevron = card.findViewById(R.id.ivChevron);
        TextView selfAdded = card.findViewById(R.id.tvSelfAddedBadge);
        TextView label = card.findViewById(R.id.tvTraitLabel);
        View colorDot = card.findViewById(R.id.dotColor);
        TextView quote = card.findViewById(R.id.tvQuote);
        TextView count = card.findViewById(R.id.tvCount);
        LinearLayout dotsContainer = card.findViewById(R.id.llProgressDots);
        View expandedBox = card.findViewById(R.id.groupExpanded);
        LinearLayout evidenceList = card.findViewById(R.id.llEvidence);
        View exerciseBox = card.findViewById(R.id.exercise_box);
        TextView exerciseLabel = card.findViewById(R.id.txt_exercise_label);
        TextView exerciseText = card.findViewById(R.id.tvExerciseText);
        TextView btnStart = card.findViewById(R.id.btnStartExercise);

        chevron.setRotation(expanded ? 180f : 0f);
        selfAdded.setVisibility(trait.selfAdded ? View.VISIBLE : View.GONE);
        label.setText(trait.label);
        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        dotBg.setColor(trait.color);
        colorDot.setBackground(dotBg);
        quote.setText("\"" + trait.quote + "\"");
        count.setText(getString(R.string.times_count_fmt, trait.count));

        renderProgressDots(dotsContainer, trait);

        expandedBox.setVisibility(expanded ? View.VISIBLE : View.GONE);
        if (expanded) {
            evidenceList.removeAllViews();
            for (TraitEvidence e : trait.evidence) {
                View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_evidence_row, evidenceList, false);
                ((TextView) row.findViewById(R.id.tvCtx)).setText(e.ctx);
                ((TextView) row.findViewById(R.id.tvWhen)).setText(e.when);
                evidenceList.addView(row);
            }

            GradientDrawable exBg = rounded(withAlpha(trait.color, 0x12), 16);
            exBg.setStroke(Math.round(1 * getResources().getDisplayMetrics().density), withAlpha(trait.color, 0x33));
            exerciseBox.setBackground(exBg);
            exerciseLabel.setTextColor(trait.color);
            exerciseText.setText(trait.exercise);
            btnStart.setBackground(rounded(trait.color, 100));
            btnStart.setOnClickListener(v -> {
                String preview = trait.exercise.length() > 24 ? trait.exercise.substring(0, 24) : trait.exercise;
                activity.showToast(getString(R.string.toast_started_exercise_fmt, preview + "..."));
            });
        }

        header.setOnClickListener(v -> {
            expandedId = expanded ? null : trait.id;
            renderTraits(activity);
        });
    }

    /** Mirrors <ProgressDots/>: 10 stars, filled up to min(count,10). */
    private void renderProgressDots(LinearLayout container, Trait trait) {
        container.removeAllViews();
        int filled = Math.min(trait.count, 10);
        int filledColor = trait.color;
        int emptyColor = ContextCompat.getColor(requireContext(), R.color.border);

        for (int i = 0; i < 10; i++) {
            TextView star = new TextView(requireContext());
            star.setText("★");
            star.setTextSize(12);
            star.setTextColor(i < filled ? filledColor : emptyColor);
            container.addView(star);
        }
    }

    // ---------------------------------------------------------------
    // Analyze button (mirrors handleAnalyze / analyzeState)
    // ---------------------------------------------------------------
    private void handleAnalyze(ProfileNavigator activity) {
        if (!"ready".equals(analyzeState)) return;
        analyzeState = "loading";
        updateAnalyzeButton();
        handler.postDelayed(() -> {
            analyzeState = "empty";
            updateAnalyzeButton();
            activity.showToast(getString(R.string.analyze_empty));
        }, 1400);
    }

    private void updateAnalyzeButton() {
        btnAnalyze.setEnabled("ready".equals(analyzeState));
        btnAnalyze.setAlpha("ready".equals(analyzeState) ? 1f : 0.5f);
        switch (analyzeState) {
            case "empty": btnAnalyze.setText(R.string.analyze_empty); btnAnalyze.setCompoundDrawables(null, null, null, null); break;
            case "loading": btnAnalyze.setText(R.string.analyze_loading); btnAnalyze.setCompoundDrawables(null, null, null, null); break;
            default:
                btnAnalyze.setText(R.string.analyze_ready);
                btnAnalyze.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sparkles, 0, 0, 0);
        }
    }

    // ---------------------------------------------------------------
    // Add trait dialog
    // ---------------------------------------------------------------
    private void openAddTraitDialog() {
        pickedSuggestion = "";
        editName.setText("");
        editNote.setText("");
        renderSuggestions();
        updateSaveButton();

        overlayAddTrait.setVisibility(View.VISIBLE);
        overlayAddTrait.setAlpha(0f);
        overlayAddTrait.animate().alpha(1f).setDuration(200).start();
        sheetContent.setTranslationY(400);
        sheetContent.animate().translationY(0f).setDuration(250).start();

        editName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateSaveButton(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSaveTrait.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            if (name.isEmpty()) return;
            String note = editNote.getText().toString().trim();
            addTrait(name, note, (ProfileNavigator) requireActivity());
        });
    }

    private void closeAddTraitDialog() {
        overlayAddTrait.animate().alpha(0f).setDuration(150)
                .withEndAction(() -> overlayAddTrait.setVisibility(View.GONE)).start();
    }

    private void renderSuggestions() {
        suggestionsRow.removeAllViews();
        for (String s : suggestions) {
            TextView chip = (TextView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_suggestion_chip, suggestionsRow, false);
            chip.setText(s);
            boolean selected = s.equals(pickedSuggestion);
            if (selected) {
                chip.setBackground(rounded(ContextCompat.getColor(requireContext(), R.color.primary), 100));
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            } else {
                chip.setBackgroundResource(R.drawable.bg_pill_flat);
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main));
            }
            chip.setOnClickListener(v -> {
                pickedSuggestion = s;
                editName.setText(s);
                editName.setSelection(s.length());
                renderSuggestions();
                updateSaveButton();
            });
            suggestionsRow.addView(chip);
        }
    }

    private void updateSaveButton() {
        boolean hasName = !editName.getText().toString().trim().isEmpty();
        btnSaveTrait.setEnabled(hasName);
        btnSaveTrait.setAlpha(hasName ? 1f : 0.4f);
    }

    /** Mirrors handleAddTrait(name, note). */
    private void addTrait(String name, String note, ProfileNavigator activity) {
        String quote = note.isEmpty() ? getString(R.string.self_added_default_quote) : note;
        String ctx = note.isEmpty() ? getString(R.string.self_added_default_ctx) : note;
        int sageColor = ContextCompat.getColor(requireContext(), R.color.sage);

        Trait newTrait = new Trait(
                "self-" + System.currentTimeMillis(), name, sageColor, 1, quote, true,
                java.util.Collections.singletonList(new TraitEvidence(getString(R.string.today_label), ctx)),
                getString(R.string.self_added_exercise)
        );
        traits.add(newTrait);
        renderWheel();
        renderTraits(activity);
        closeAddTraitDialog();
        activity.showToast(getString(R.string.toast_trait_added));
    }

    private GradientDrawable rounded(int color, float radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radiusDp * getResources().getDisplayMetrics().density);
        return d;
    }

    private int withAlpha(int color, int alpha0to255) {
        return android.graphics.Color.argb(alpha0to255,
                android.graphics.Color.red(color), android.graphics.Color.green(color), android.graphics.Color.blue(color));
    }
}
