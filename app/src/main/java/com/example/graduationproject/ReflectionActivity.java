package com.example.graduationproject;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.models.ReflectionCard;
import com.example.graduationproject.ui.SceneView;

import java.util.ArrayList;
import java.util.List;

/**
 * Java/XML port of PreHomeReflectionScreen (JSX).
 *
 * State mirrored from the original useState hook:
 *   idx -> the currently shown card index
 *
 * isLast = idx === CARDS.length - 1   -> swaps the button label/behavior
 * Clicking "next" on the last card is a no-op, exactly like the JSX's
 * `onClick={() => (isLast ? null : setIdx(i => i + 1))}`.
 */
public class ReflectionActivity extends AppCompatActivity {

    private List<ReflectionCard> cards;
    private int idx = 0;

    private SceneView sceneView;
    private TextView txtTitle, txtTag, txtChip, txtNoteDate, txtNote, txtNext;

    private static final int SCENE_FADE_MS = 1000; // matches @keyframes sfade (1s)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflection);

        buildCards();
        bindViews();
        buildDots();

        // Initial render (no fade-in animation needed on first paint)
        renderCardInitial();

        LinearLayout btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            boolean isLast = idx == cards.size() - 1;
            if (isLast) return; // mirrors: isLast ? null : setIdx(i => i + 1)
            int oldIdx = idx;
            idx++;
            renderCardAnimated(idx, oldIdx);
        });
    }

    private void buildCards() {
        cards = new ArrayList<>();
        cards.add(new ReflectionCard(SceneView.SCENE_MOUNTAIN, R.string.c1_title, R.string.c1_tag, R.string.c1_chip, R.string.c1_date, R.string.c1_note));
        cards.add(new ReflectionCard(SceneView.SCENE_SEA, R.string.c2_title, R.string.c2_tag, R.string.c2_chip, R.string.c2_date, R.string.c2_note));
        cards.add(new ReflectionCard(SceneView.SCENE_FOREST, R.string.c3_title, R.string.c3_tag, R.string.c3_chip, R.string.c3_date, R.string.c3_note));
        cards.add(new ReflectionCard(SceneView.SCENE_DESERT, R.string.c4_title, R.string.c4_tag, R.string.c4_chip, R.string.c4_date, R.string.c4_note));
    }

    private void bindViews() {
        sceneView = findViewById(R.id.scene_view);
        txtTitle = findViewById(R.id.txt_title);
        txtTag = findViewById(R.id.txt_tag);
        txtChip = findViewById(R.id.txt_chip);
        txtNoteDate = findViewById(R.id.txt_note_date);
        txtNote = findViewById(R.id.txt_note);
        txtNext = findViewById(R.id.txt_next);
    }

    // ---------------------------------------------------------------
    // Dots (mirrors CARDS.map(...) progress indicator row)
    // ---------------------------------------------------------------
    private void buildDots() {
        // Progress dots removed as per request
    }

    private void updateDots(int newIdx, int oldIdx) {
        // Progress dots removed as per request
    }

    // ---------------------------------------------------------------
    // Card rendering + animations
    // ---------------------------------------------------------------
    private void renderCardInitial() {
        ReflectionCard card = cards.get(idx);
        sceneView.setSceneType(card.sceneType);
        bindTextContent(card);
        txtNote.setText(card.noteRes);
        updateNextLabel();
    }

    private void renderCardAnimated(int newIdx, int oldIdx) {
        ReflectionCard card = cards.get(newIdx);
        updateNextLabel();
        crossFadeScene(card.sceneType);
        animateContentChange(card);
        updateDots(newIdx, oldIdx);
    }

    private void bindTextContent(ReflectionCard card) {
        txtTitle.setText(card.titleRes);
        txtTag.setText(getString(card.tagRes).toUpperCase());
        txtChip.setText(card.chipRes);
        txtNoteDate.setText(getString(R.string.note_prefix) + " · " + getString(card.dateRes));
    }

    private void updateNextLabel() {
        boolean isLast = idx == cards.size() - 1;
        txtNext.setText(isLast ? getString(R.string.enter_button) : getString(R.string.next_button));
    }

    /** Enhanced content animation for title, tag, chip and note */
    private void animateContentChange(ReflectionCard card) {
        float density = getResources().getDisplayMetrics().density;
        float shiftY = 12 * density;

        // Animate title, tag, chip with a slight slide and fade
        View[] topContent = {txtTitle, txtTag, txtChip, txtNoteDate};
        for (View v : topContent) {
            v.animate().alpha(0f).translationY(-shiftY).setDuration(200).withEndAction(() -> {
                if (v == txtTitle) txtTitle.setText(card.titleRes);
                if (v == txtTag) txtTag.setText(getString(card.tagRes).toUpperCase());
                if (v == txtChip) txtChip.setText(card.chipRes);
                if (v == txtNoteDate) txtNoteDate.setText(getString(R.string.note_prefix) + " · " + getString(card.dateRes));
                
                v.setTranslationY(shiftY);
                v.animate().alpha(1f).translationY(0f).setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            }).start();
        }

        // Animate note card
        txtNote.animate().alpha(0f).translationY(shiftY).setDuration(300).withEndAction(() -> {
            txtNote.setText(card.noteRes);
            txtNote.animate().alpha(1f).translationY(0f).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        }).start();
    }

    /** Mirrors .scene-fade { animation: sfade 1s ease }, triggered by the `key={idx}` remount.
     * Enhanced with a subtle zoom effect. */
    private void crossFadeScene(int newSceneType) {
        sceneView.animate()
                .alpha(0f)
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(SCENE_FADE_MS / 2)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    sceneView.setSceneType(newSceneType);
                    sceneView.setScaleX(1.1f);
                    sceneView.setScaleY(1.1f);
                    sceneView.animate()
                            .alpha(1f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(SCENE_FADE_MS / 2)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                })
                .start();
    }
}
