package com.example.graduationproject;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.graduationproject.data.ReframingAppData;
import com.google.android.flexbox.FlexboxLayout;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Single-activity implementation of the "CBT Reframing" flow.
 * <p>
 * This mirrors the original React component (CBTReframingFlow) 1:1:
 *  - stage state machine: entry -> identify -> examine -> reframe -> done
 *  - a write-thought bottom-sheet dialog reachable from the entry screen
 *  - the same copy, colors, ordering and interaction logic
 * <p>
 * Instead of React state + conditional rendering, each "stage" is a
 * separate XML layout that gets inflated into {@code contentContainer}
 * and swapped with a fade/slide-up animation (step_fade_in.xml), the
 * Android equivalent of the original CSS ".step-fade" keyframes.
 */
public class CBTRReframingActivity extends AppCompatActivity {

    // ---- Stage identifiers (mirrors the `stage` useState in React) ----
    private static final String STAGE_ENTRY = "entry";
    private static final String STAGE_IDENTIFY = "identify";
    private static final String STAGE_EXAMINE = "examine";
    private static final String STAGE_REFRAME = "reframe";
    private static final String STAGE_DONE = "done";

    // ---- App-level state (mirrors the useState hooks in CBTReframingFlow) ----
    private String stage = STAGE_ENTRY;
    private String thought = "";
    private String source = "";
    private String detected = "overgen";
    private String pattern = "overgen";
    private String answer = null;      // "نعم" / "لا" / "مو متأكد"
    private String followUp = "";

    // ---- Write-dialog local state (mirrors WriteDialog's own useState) ----
    private String writeMode = "text"; // "text" or "voice"
    private final Set<String> selectedEmotions = new HashSet<>();

    // ---- Reframe-step local state (mirrors ReframeStep's useState idx) ----
    private int reframeIdx = 0;

    // ---- Root chrome views ----
    private FrameLayout contentContainer;
    private View headerBack;
    private com.example.graduationproject.view.ReframingStepIndicator stepIndicator;
    private View layoutDetectedPattern;
    private TextView tvDetectedPatternName;
    private FrameLayout dialogOverlay;


    // ---- Write dialog views (statically included, bound once) ----
    private TextView tabText, tabVoice;
    private EditText etThoughtText, etThoughtVoiceAlt;
    private View voiceModeContainer;
    private FlexboxLayout emotionsContainer;
    private Button btnAnalyze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this,
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }

        setContentView(R.layout.activity_cbtr_reframing);

        contentContainer = findViewById(R.id.contentContainer);
        headerBack = findViewById(R.id.headerBack);
        stepIndicator = findViewById(R.id.stepIndicator);
        layoutDetectedPattern = findViewById(R.id.layoutDetectedPattern);
        tvDetectedPatternName = findViewById(R.id.tvDetectedPatternName);
        dialogOverlay = findViewById(R.id.dialogOverlay);

        findViewById(R.id.btnBack).setOnClickListener(v -> restart());

        setupWriteDialog();
        showStage(STAGE_ENTRY, false);
    }

    // =========================================================================================
    // Stage navigation (equivalent to React re-rendering based on `stage`)
    // =========================================================================================

    private void showStage(String newStage, boolean animate) {
        stage = newStage;

        boolean isEntry = STAGE_ENTRY.equals(stage);
        boolean isDone = STAGE_DONE.equals(stage);
        headerBack.setVisibility(isEntry ? View.GONE : View.VISIBLE);
        
        if (stepIndicator != null) {
            if (isEntry || isDone) {
                stepIndicator.setVisibility(View.GONE);
                if (layoutDetectedPattern != null) layoutDetectedPattern.setVisibility(View.GONE);
            } else {
                stepIndicator.setVisibility(View.VISIBLE);
                if (layoutDetectedPattern != null) {
                    layoutDetectedPattern.setVisibility(STAGE_IDENTIFY.equals(stage) ? View.VISIBLE : View.GONE);
                    if (tvDetectedPatternName != null) {
                        tvDetectedPatternName.setText(ReframingAppData.findPattern(detected).label);
                    }
                }
                int step = 0;
                if (STAGE_EXAMINE.equals(stage)) step = 1;
                else if (STAGE_REFRAME.equals(stage)) step = 2;
                stepIndicator.setStep(step);
            }
        }

        contentContainer.removeAllViews();
        int layoutRes;
        switch (stage) {
            case STAGE_IDENTIFY:
                layoutRes = R.layout.step_identify;
                break;
            case STAGE_EXAMINE:
                layoutRes = R.layout.step_examine;
                break;
            case STAGE_REFRAME:
                layoutRes = R.layout.step_reframe;
                break;
            case STAGE_DONE:
                layoutRes = R.layout.screen_completion;
                break;
            case STAGE_ENTRY:
            default:
                layoutRes = R.layout.screen_entry;
                break;
        }

        View screen = LayoutInflater.from(this).inflate(layoutRes, contentContainer, false);
        contentContainer.addView(screen);

        switch (stage) {
            case STAGE_ENTRY:
                bindEntry(screen);
                break;
            case STAGE_IDENTIFY:
                bindIdentify(screen);
                break;
            case STAGE_EXAMINE:
                bindExamine(screen);
                break;
            case STAGE_REFRAME:
                bindReframe(screen);
                break;
            case STAGE_DONE:
                bindCompletion(screen);
                break;
        }

        if (animate) {
            screen.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        }
    }

    private void restart() {
        answer = null;
        followUp = "";
        reframeIdx = 0;
        showStage(STAGE_ENTRY, false);
    }

    // =========================================================================================
    // Entry screen
    // =========================================================================================

    private void bindEntry(View root) {
        root.findViewById(R.id.tvTitle).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        root.findViewById(R.id.tvSubtitle).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        root.findViewById(R.id.btnFromChat).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        root.findViewById(R.id.btnWriteNew).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));

        root.findViewById(R.id.btnFromChat).setOnClickListener(v -> startFromChat());
        root.findViewById(R.id.btnWriteNew).setOnClickListener(v -> openWriteDialog());
    }

    private void startFromChat() {
        thought = getString(R.string.thought_from_chat);
        source = getString(R.string.source_from_chat);
        detected = "overgen";
        pattern = "overgen";
        showStage(STAGE_IDENTIFY, true);
    }

    // =========================================================================================
    // Write-thought dialog (bottom sheet overlay)
    // =========================================================================================

    private void setupWriteDialog() {
        View dialogRoot = findViewById(R.id.writeDialogInclude);

        findViewById(R.id.btnCloseDialog).setOnClickListener(v -> closeWriteDialog());
        dialogOverlay.setOnClickListener(v -> closeWriteDialog()); // tap outside sheet == onClose

        tabText = dialogRoot.findViewById(R.id.tabText);
        tabVoice = dialogRoot.findViewById(R.id.tabVoice);
        etThoughtText = dialogRoot.findViewById(R.id.etThoughtText);
        etThoughtVoiceAlt = dialogRoot.findViewById(R.id.etThoughtVoiceAlt);
        voiceModeContainer = dialogRoot.findViewById(R.id.voiceModeContainer);
        emotionsContainer = dialogRoot.findViewById(R.id.emotionsContainer);
        btnAnalyze = dialogRoot.findViewById(R.id.btnAnalyze);

        tabText.setOnClickListener(v -> setWriteMode("text"));
        tabVoice.setOnClickListener(v -> setWriteMode("voice"));

        // Enable "حلّل هذه الفكرة" only once there is text, mirrors disabled={!text}
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshAnalyzeButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        etThoughtText.addTextChangedListener(watcher);
        etThoughtVoiceAlt.addTextChangedListener(watcher);

        // Emotion chips, built from AppData.EMOTIONS (mirrors EMOTIONS.map(...))
        for (String emotion : ReframingAppData.EMOTIONS) {
            TextView chip = new TextView(this);
            chip.setText(emotion);
            chip.setTextColor(getColorCompat(R.color.text_main));
            chip.setTextSize(11);
            int padH = dp(12), padV = dp(7);
            chip.setPadding(padH, padV, padH, padV);
            chip.setBackgroundResource(R.drawable.bg_chip_unselected);
            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(dp(4), dp(4), dp(4), dp(4));
            chip.setLayoutParams(lp);
            chip.setOnClickListener(v -> {
                if (selectedEmotions.contains(emotion)) {
                    selectedEmotions.remove(emotion);
                    chip.setBackgroundResource(R.drawable.bg_chip_unselected);
                } else {
                    selectedEmotions.add(emotion);
                    chip.setBackgroundResource(R.drawable.bg_chip_selected);
                }
            });
            emotionsContainer.addView(chip);
        }

        btnAnalyze.setOnClickListener(v -> {
            String text = activeWriteText();
            if (!text.isEmpty()) submitWrite(text);
        });
    }

    private String activeWriteText() {
        if ("voice".equals(writeMode)) {
            String alt = etThoughtVoiceAlt.getText().toString().trim();
            return !alt.isEmpty() ? alt : etThoughtText.getText().toString().trim();
        }
        return etThoughtText.getText().toString().trim();
    }

    private void refreshAnalyzeButtonState() {
        btnAnalyze.setEnabled(!activeWriteText().isEmpty());
    }

    private void setWriteMode(String mode) {
        writeMode = mode;
        boolean isVoice = "voice".equals(mode);
        tabText.setBackgroundResource(isVoice ? R.drawable.bg_tab_unselected : R.drawable.bg_tab_selected);
        tabVoice.setBackgroundResource(isVoice ? R.drawable.bg_tab_selected : R.drawable.bg_tab_unselected);
        etThoughtText.setVisibility(isVoice ? View.GONE : View.VISIBLE);
        voiceModeContainer.setVisibility(isVoice ? View.VISIBLE : View.GONE);
        refreshAnalyzeButtonState();
    }

    private void openWriteDialog() {
        // Reset dialog-local state each time it's opened, mirrors a fresh WriteDialog mount
        writeMode = "text";
        selectedEmotions.clear();
        etThoughtText.setText("");
        etThoughtVoiceAlt.setText("");
        for (int i = 0; i < emotionsContainer.getChildCount(); i++) {
            emotionsContainer.getChildAt(i).setBackgroundResource(R.drawable.bg_chip_unselected);
        }
        setWriteMode("text");
        dialogOverlay.setVisibility(View.VISIBLE);
        dialogOverlay.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
    }

    private void closeWriteDialog() {
        dialogOverlay.setVisibility(View.GONE);
    }

    private void submitWrite(String text) {
        thought = text;
        source = getString(R.string.source_written);
        detected = "overgen";
        pattern = "overgen";
        closeWriteDialog();
        showStage(STAGE_IDENTIFY, true);
    }

    // =========================================================================================
    // Step 1: Identify
    // =========================================================================================

    private void bindIdentify(View root) {
        TextView tvSource = root.findViewById(R.id.tvSource);
        TextView tvThought = root.findViewById(R.id.tvThought);
        TextView tvWhyExplain = root.findViewById(R.id.tvWhyExplain);

        View card = root.findViewById(R.id.cardIdentify);
        if (card != null) card.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        root.findViewById(R.id.patternContainer).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        root.findViewById(R.id.btnIdentifyNext).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));

        tvSource.setText(source);
        tvThought.setText("\"" + thought + "\"");

        // Map pattern id -> (button container, label TextView, badge TextView)
        Map<String, View[]> patternViews = new LinkedHashMap<>();
        patternViews.put("overgen", new View[]{
                root.findViewById(R.id.patternOvergen),
                root.findViewById(R.id.tvPatternOvergen),
                root.findViewById(R.id.badgeOvergen)});
        patternViews.put("allnothing", new View[]{
                root.findViewById(R.id.patternAllnothing),
                root.findViewById(R.id.tvPatternAllnothing),
                root.findViewById(R.id.badgeAllnothing)});
        patternViews.put("catastro", new View[]{
                root.findViewById(R.id.patternCatastro),
                root.findViewById(R.id.tvPatternCatastro),
                root.findViewById(R.id.badgeCatastro)});
        patternViews.put("neglect", new View[]{
                root.findViewById(R.id.patternNeglect),
                root.findViewById(R.id.tvPatternNeglect),
                root.findViewById(R.id.badgeNeglect)});

        for (Map.Entry<String, View[]> entry : patternViews.entrySet()) {
            String id = entry.getKey();
            View container = entry.getValue()[0];
            View badge = entry.getValue()[2];
            badge.setVisibility(id.equals(detected) ? View.VISIBLE : View.GONE);
            container.setOnClickListener(v -> {
                pattern = id;
                refreshPatternSelectionUI(patternViews, tvWhyExplain);
            });
        }

        refreshPatternSelectionUI(patternViews, tvWhyExplain);

        root.findViewById(R.id.btnIdentifyNext).setOnClickListener(v -> {
            answer = null;
            followUp = "";
            showStage(STAGE_EXAMINE, true);
        });
    }

    private void refreshPatternSelectionUI(Map<String, View[]> patternViews, TextView tvWhyExplain) {
        for (Map.Entry<String, View[]> entry : patternViews.entrySet()) {
            boolean selected = entry.getKey().equals(pattern);
            entry.getValue()[0].setBackgroundResource(
                    selected ? R.drawable.bg_pattern_selected_purple : R.drawable.bg_pattern_unselected_gray);
            ((TextView) entry.getValue()[1]).setTextColor(Color.parseColor(selected ? "#7E81BA" : "#1F3A60"));
        }
        tvWhyExplain.setText(ReframingAppData.findPattern(pattern).explain);
    }

    // =========================================================================================
    // Step 2: Examine
    // =========================================================================================

    private void bindExamine(View root) {
        TextView tvQuestion = root.findViewById(R.id.tvExamineQuestion);
        TextView answerYes = root.findViewById(R.id.answerYes);
        TextView answerNo = root.findViewById(R.id.answerNo);
        TextView answerUnsure = root.findViewById(R.id.answerUnsure);
        EditText etFollowUp = root.findViewById(R.id.etFollowUp);
        Button btnNext = root.findViewById(R.id.btnExamineNext);
        TextView btnSkip = root.findViewById(R.id.btnSkip);

        tvQuestion.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        root.findViewById(R.id.answersContainer).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        btnNext.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));

        tvQuestion.setText(ReframingAppData.EXAMINE_QUESTIONS.get(pattern));

        Map<String, TextView> answerViews = new LinkedHashMap<>();
        answerViews.put(getString(R.string.answer_yes), answerYes);
        answerViews.put(getString(R.string.answer_no), answerNo);
        answerViews.put(getString(R.string.answer_unsure), answerUnsure);

        for (Map.Entry<String, TextView> entry : answerViews.entrySet()) {
            String value = entry.getKey();
            entry.getValue().setOnClickListener(v -> {
                answer = value;
                for (Map.Entry<String, TextView> e2 : answerViews.entrySet()) {
                    boolean isSel = e2.getKey().equals(answer);
                    e2.getValue().setBackgroundResource(
                            isSel ? R.drawable.bg_answer_selected : R.drawable.bg_answer_unselected);
                    e2.getValue().setTextColor(getColorCompat(isSel ? R.color.white : R.color.text_main));
                }
                btnNext.setEnabled(true);
                btnNext.setAlpha(1f);

                boolean showFollowUp = getString(R.string.answer_yes).equals(answer);
                if (showFollowUp && etFollowUp.getVisibility() != View.VISIBLE) {
                    etFollowUp.setVisibility(View.VISIBLE);
                    etFollowUp.startAnimation(AnimationUtils.loadAnimation(CBTRReframingActivity.this, R.anim.step_fade_in));
                } else if (!showFollowUp) {
                    etFollowUp.setVisibility(View.GONE);
                }
            });
        }

        etFollowUp.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                followUp = s.toString();
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        btnNext.setOnClickListener(v -> {
            reframeIdx = 0;
            showStage(STAGE_REFRAME, true);
        });

        btnSkip.setOnClickListener(v -> {
            reframeIdx = 0;
            showStage(STAGE_REFRAME, true);
        });
    }

    // =========================================================================================
    // Step 3: Reframe (mirror carousel)
    // =========================================================================================

    private void bindReframe(View root) {
        TextView tvMirror = root.findViewById(R.id.tvMirrorText);
        LinearLayout dotsContainer = root.findViewById(R.id.dotsContainer);
        TextView tvExercise = root.findViewById(R.id.tvExercise);
        View btnReword = root.findViewById(R.id.btnReword);
        View btnSave = root.findViewById(R.id.btnSave);
        View btnBackToChat = root.findViewById(R.id.btnBackToChat);

        root.findViewById(R.id.cardMirror).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        root.findViewById(R.id.cardExercise).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        btnSave.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));

        tvExercise.setText(ReframingAppData.EXERCISES.get(pattern));

        String[] reframes = ReframingAppData.REFRAMES.get(pattern);

        buildDots(dotsContainer, reframes.length);
        for (int i = 0; i < dotsContainer.getChildCount(); i++) {
            final int idx = i;
            dotsContainer.getChildAt(i).setOnClickListener(v -> {
                reframeIdx = idx;
                renderReframeText(tvMirror, reframes);
                updateDots(dotsContainer, reframeIdx);
            });
        }

        renderReframeText(tvMirror, reframes);
        updateDots(dotsContainer, reframeIdx);

        btnReword.setOnClickListener(v -> {
            reframeIdx = (reframeIdx + 1) % reframes.length;
            renderReframeText(tvMirror, reframes);
            updateDots(dotsContainer, reframeIdx);
        });

        btnSave.setOnClickListener(v -> showStage(STAGE_DONE, true));
        btnBackToChat.setOnClickListener(v -> restart());
    }

    private void renderReframeText(TextView tvMirror, String[] reframes) {
        String text = reframes[reframeIdx];
        if (followUp != null && !followUp.isEmpty() && reframeIdx == 0) {
            text = text + " " + getString(R.string.followup_appendix);
        }
        tvMirror.setText(text);
        tvMirror.startAnimation(AnimationUtils.loadAnimation(this, R.anim.mirror_fade_in));
    }

    /** Builds the carousel dot row (one dot per reframe option). */
    private void buildDots(LinearLayout container, int count) {
        container.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(6), dp(6));
            lp.setMarginStart(dp(3));
            lp.setMarginEnd(dp(3));
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(R.drawable.bg_dot_inactive);
            container.addView(dot);
        }
    }

    private void updateDots(LinearLayout container, int activeIdx) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View dot = container.getChildAt(i);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) dot.getLayoutParams();
            if (i == activeIdx) {
                lp.width = dp(16);
                dot.setBackgroundResource(R.drawable.bg_dot_active);
            } else {
                lp.width = dp(6);
                dot.setBackgroundResource(R.drawable.bg_dot_inactive);
            }
            dot.setLayoutParams(lp);
        }
    }

    // =========================================================================================
    // Completion screen
    // =========================================================================================

    private void bindCompletion(View root) {
        TextView tvExercise = root.findViewById(R.id.tvCompletionExercise);
        root.findViewById(R.id.tvDoneTitle).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        root.findViewById(R.id.tvDoneSubtitle).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        root.findViewById(R.id.cardDoneExercise).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));
        root.findViewById(R.id.btnAnotherThought).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));

        tvExercise.setText(ReframingAppData.EXERCISES.get(pattern));
        root.findViewById(R.id.btnAnotherThought).setOnClickListener(v -> restart());
    }

    // =========================================================================================
    // Small helpers
    // =========================================================================================

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }

    private int getColorCompat(int colorRes) {
        return androidx.core.content.ContextCompat.getColor(this, colorRes);
    }

    @Override
    public void onBackPressed() {
        if (dialogOverlay.getVisibility() == View.VISIBLE) {
            closeWriteDialog();
            return;
        }
        if (!STAGE_ENTRY.equals(stage)) {
            restart();
            return;
        }
        super.onBackPressed();
    }
}