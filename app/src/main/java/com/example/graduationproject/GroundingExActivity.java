package com.example.graduationproject;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Sense;
import com.example.graduationproject.models.SenseRepository;
import com.example.graduationproject.widget.FadeUtils;
import com.example.graduationproject.widget.ProgressBarAnimator;
import com.example.graduationproject.widget.TapBounce;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Full Java/Android port of the "GroundingStandaloneScreen" React component
 * (the 5-4-3-2-1 grounding exercise). State fields below map 1:1 to the
 * useState hooks in the original file.
 */
public class GroundingExActivity extends AppCompatActivity {

    // ------- data -------
    private List<Sense> senses;

    // ------- state (mirrors the React useState hooks) -------
    private int stepIdx = 0;
    private final Map<String, boolean[]> filled = new HashMap<>();
    private final Map<String, String> notes = new HashMap<>();
    private boolean noteOpen = false;

    // ------- views -------
    private LinearLayout groupInProgress, groupDone, groupStep, llTapCounter, btnNext, btnToggleNote;
    private FrameLayout progressTrack;
    private View progressFill;
    private TextView tvStepCounter, tvEmoji, tvTag, tvTitle, tvQuestion, tvTappedCount,
            tvNextLabel, tvDoneSub, btnAnother;
    private ImageView ivChevron;
    private EditText edtNote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grounding_excersize);

        senses = SenseRepository.getSenses();
        resetState();

        bindViews();
        setListeners();
        renderAll(false);
    }

    private void bindViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        groupInProgress = findViewById(R.id.groupInProgress);
        groupDone = findViewById(R.id.groupDone);
        groupStep = findViewById(R.id.groupStep);
        llTapCounter = findViewById(R.id.llTapCounter);
        btnNext = findViewById(R.id.btnNext);
        btnToggleNote = findViewById(R.id.btnToggleNote);

        progressTrack = findViewById(R.id.progressTrack);
        progressFill = findViewById(R.id.progressFill);

        tvStepCounter = findViewById(R.id.tvStepCounter);
        tvEmoji = findViewById(R.id.tvEmoji);
        tvTag = findViewById(R.id.tvTag);
        tvTitle = findViewById(R.id.tvTitle);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvTappedCount = findViewById(R.id.tvTappedCount);
        tvNextLabel = findViewById(R.id.tvNextLabel);
        tvDoneSub = findViewById(R.id.tvDoneSub);
        btnAnother = findViewById(R.id.btnAnother);
        ivChevron = findViewById(R.id.ivChevron);
        edtNote = findViewById(R.id.edtNote);
    }

    private void setListeners() {
        btnNext.setOnClickListener(v -> goNext());
        TapBounce.attach(btnNext);

        btnToggleNote.setOnClickListener(v -> {
            noteOpen = !noteOpen;
            renderNoteToggle();
        });

        btnAnother.setOnClickListener(v -> {
            resetState();
            renderAll(true);
        });
        TapBounce.attach(btnAnother);

        edtNote.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notes.put(currentSense().key, s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
    }

    // ===================== STATE HELPERS =====================

    private void resetState() {
        stepIdx = 0;
        filled.clear();
        notes.clear();
        for (Sense s : senses) {
            filled.put(s.key, new boolean[s.count]);
            notes.put(s.key, "");
        }
        noteOpen = false;
    }

    private boolean isDone() {
        return stepIdx >= senses.size();
    }

    private Sense currentSense() {
        return senses.get(stepIdx);
    }

    private int overallTapped() {
        int total = 0;
        for (boolean[] arr : filled.values()) {
            for (boolean b : arr) if (b) total++;
        }
        return total;
    }

    private int overallTotal() {
        int total = 0;
        for (boolean[] arr : filled.values()) total += arr.length;
        return total;
    }

    private void toggleTap(int i) {
        boolean[] arr = filled.get(currentSense().key);
        arr[i] = !arr[i];
        renderTapCounter();
        renderProgress(true);
    }

    private void goNext() {
        noteOpen = false;
        stepIdx++;
        renderAll(true);
    }

    // ===================== RENDERING =====================

    private void renderAll(boolean animateStep) {
        boolean done = isDone();
        groupInProgress.setVisibility(done ? View.GONE : View.VISIBLE);
        groupDone.setVisibility(done ? View.VISIBLE : View.GONE);

        if (done) {
            tvDoneSub.setText(getString(R.string.done_sub_format, overallTapped()));
            FadeUtils.doneFade(groupDone);
            return;
        }

        Sense step = currentSense();

        renderProgress(false);
        tvStepCounter.setText(getString(R.string.step_counter_format, stepIdx + 1, senses.size()));

        tvEmoji.setText(step.emoji);
        tvTag.setText(step.tag);
        tvTitle.setText(step.title);
        tvQuestion.setText(step.question);

        buildTapCounter(step);
        renderTapCounter();

        tvNextLabel.setText(stepIdx < senses.size() - 1
                ? getString(R.string.btn_next) : getString(R.string.btn_finish));

        // Note field always collapses back to closed when moving to a new step
        // (matches `setNoteOpen(false)` inside `goNext()`).
        edtNote.setText(notes.get(step.key));
        renderNoteToggle();

        if (animateStep) {
            FadeUtils.stepFade(groupStep);
        }
    }

    private void renderProgress(boolean animate) {
        if (isDone()) return;

        Sense step = currentSense();
        boolean[] arr = filled.get(step.key);
        int tappedInStep = 0;
        if (arr != null) {
            for (boolean b : arr) if (b) tappedInStep++;
        }

        float stepProgress = (float) tappedInStep / step.count;
        float fraction = (stepIdx + stepProgress) / senses.size();
        if (animate) {
            ProgressBarAnimator.animateTo(progressFill, progressTrack, fraction);
        } else {
            progressTrack.post(() -> {
                ViewGroup.LayoutParams lp = progressFill.getLayoutParams();
                lp.width = Math.round(progressTrack.getWidth() * fraction);
                progressFill.setLayoutParams(lp);
            });
        }
    }

    /** (Re)builds the row of tap squares for the current step - count varies per sense. */
    private void buildTapCounter(Sense step) {
        llTapCounter.removeAllViews();
        int sizePx = dp(56);
        int marginPx = dp(6);

        for (int i = 0; i < step.count; i++) {
            final int index = i;
            TextView square = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sizePx, sizePx);
            lp.setMarginStart(marginPx);
            lp.setMarginEnd(marginPx);
            square.setLayoutParams(lp);
            square.setGravity(android.view.Gravity.CENTER);
            square.setTextSize(15);
            square.setTypeface(square.getTypeface(), android.graphics.Typeface.BOLD);
            square.setText(String.valueOf(i + 1));
            square.setTextColor(getResources().getColor(R.color.text_soft));
            square.setBackgroundResource(R.drawable.bg_tap_unfilled);
            square.setTag(index);

            square.setOnClickListener(v -> toggleTap(index));
            TapBounce.attach(square);

            llTapCounter.addView(square);
        }
    }

    /** Refreshes the fill state + numbers of the already-built tap squares. */
    private void renderTapCounter() {
        Sense step = currentSense();
        boolean[] arr = filled.get(step.key);
        int tapped = 0;

        for (int i = 0; i < llTapCounter.getChildCount(); i++) {
            TextView square = (TextView) llTapCounter.getChildAt(i);
            boolean isFilled = arr[i];
            if (isFilled) tapped++;

            if (isFilled) {
                square.setBackgroundResource(R.drawable.bg_tap_filled);
                square.setText("\u2713"); // checkmark glyph, matches the <Check/> icon state
                square.setTextColor(getResources().getColor(R.color.surface));
            } else {
                square.setBackgroundResource(R.drawable.bg_tap_unfilled);
                square.setText(String.valueOf(i + 1));
                square.setTextColor(getResources().getColor(R.color.text_soft));
            }
        }

        tvTappedCount.setText(getString(R.string.tapped_count_format, tapped, step.count));
    }

    private void renderNoteToggle() {
        ivChevron.setRotation(0f);
        ivChevron.animate().rotation(noteOpen ? 180f : 0f).setDuration(200).start();

        if (noteOpen) {
            edtNote.setVisibility(View.VISIBLE);
            FadeUtils.noteFade(edtNote);
        } else {
            edtNote.setVisibility(View.GONE);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
