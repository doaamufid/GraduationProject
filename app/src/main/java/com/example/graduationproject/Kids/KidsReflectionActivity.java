package com.example.graduationproject.Kids;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.ActivityUtils;
import com.example.graduationproject.R;
import com.example.graduationproject.models.ReflectionCard;
import com.example.graduationproject.view.KidsAdaptiveSkyBackgroundView;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;
import com.example.graduationproject.ui.SceneView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KidsReflectionActivity extends AppCompatActivity {

    private List<ReflectionCard> cards;
    private KidsAdaptiveSkyBackgroundView skyView;
    private TextView txtTitle, txtNote;
    private MaterialButton btnNext;
    private KidsAdaptiveTeddyBuddyView bearCompanion;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable showNextButtonRunnable;
    private long childId = -1;
    private String childName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, 
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.dark(Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        setContentView(R.layout.activity_kids_reflection);

        if (getIntent() != null) {
            childId = getIntent().getLongExtra("CHILD_ID", -1);
            childName = getIntent().getStringExtra("CHILD_NAME");
        }

        View contentContainer = findViewById(R.id.content_container);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reflection_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (contentContainer != null) {
                int sidePadding = (int) (48 * getResources().getDisplayMetrics().density);
                contentContainer.setPadding(
                        systemBars.left + sidePadding,
                        systemBars.top + systemBars.bottom,
                        systemBars.right + sidePadding,
                        systemBars.bottom + (int) (32 * getResources().getDisplayMetrics().density)
                );
            }
            return insets;
        });

        buildCards();
        bindViews();

        // 1. Pick a random card
        int randomIdx = new Random().nextInt(cards.size());
        renderCardInitial(randomIdx);

        // 2. Button visibility logic
        btnNext.setVisibility(View.INVISIBLE);
        btnNext.setAlpha(0f);

        showNextButtonRunnable = () -> {
            btnNext.bringToFront();
            btnNext.setVisibility(View.VISIBLE);
            btnNext.animate().alpha(1f).setDuration(500).start();
        };
        handler.postDelayed(showNextButtonRunnable, 4000);

        btnNext.setOnClickListener(v -> {
            cleanupHandler();
            navigateToMood();
        });
    }

    private void buildCards() {
        cards = new ArrayList<>();
        cards.add(new ReflectionCard(SceneView.SCENE_MOUNTAIN, R.string.kids_reflection_c1_title, 0, 0, 0, R.string.kids_reflection_c1_note, 0));
        cards.add(new ReflectionCard(SceneView.SCENE_SEA, R.string.kids_reflection_c2_title, 0, 0, 0, R.string.kids_reflection_c2_note, 0));
        cards.add(new ReflectionCard(SceneView.SCENE_FOREST, R.string.kids_reflection_c3_title, 0, 0, 0, R.string.kids_reflection_c3_note, 0));
        cards.add(new ReflectionCard(SceneView.SCENE_DESERT, R.string.kids_reflection_c4_title, 0, 0, 0, R.string.kids_reflection_c4_note, 0));
    }

    private void bindViews() {
        skyView = findViewById(R.id.sky_view);
        txtTitle = findViewById(R.id.txt_title);
        txtNote = findViewById(R.id.txt_note);
        btnNext = findViewById(R.id.btn_next);
        bearCompanion = findViewById(R.id.bear_companion);
    }

    private void renderCardInitial(int idx) {
        ReflectionCard card = cards.get(idx);
        if (skyView != null) {
            skyView.setStage(idx % 6); // Cyclically use onboarding sky stages
        }
        if (bearCompanion != null) {
            bearCompanion.setMood(KidsAdaptiveTeddyBuddyView.MOOD_CALM);
        }
        txtTitle.setText(getString(card.titleRes));
        txtNote.setText(getString(card.noteRes));
        btnNext.setText(getString(R.string.kids_reflection_btn));
    }

    private void navigateToMood() {
        Intent intent = new Intent(this, com.example.graduationproject.KidsMoodActivity.class);
        intent.putExtra("CHILD_ID", childId);
        intent.putExtra("CHILD_NAME", childName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityUtils.startActivityAndFinishWithAnimation(this, intent);
    }

    private void cleanupHandler() {
        if (handler != null && showNextButtonRunnable != null) {
            handler.removeCallbacks(showNextButtonRunnable);
        }
    }

    @Override
    public void onBackPressed() {
        cleanupHandler();
        super.onBackPressed();
        ActivityUtils.applyBackTransition(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupHandler();
    }
}
