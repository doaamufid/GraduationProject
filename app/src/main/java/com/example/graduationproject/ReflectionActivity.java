package com.example.graduationproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.models.ReflectionCard;
import com.example.graduationproject.ui.SceneView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ReflectionActivity extends AppCompatActivity {

    private List<ReflectionCard> cards;
    private SceneView sceneView;
    private TextView txtTitle, txtTag, txtChip, txtNoteDate, txtNote, txtNext;
    private LinearLayout btnNext;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable showNextButtonRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reflection);

        // تطبيق حواف الشاشة على محتوى الواجهة الأمامية فقط لتستمر الخلفية بالظهور تحت الـ Status Bar
        View contentContainer = findViewById(R.id.content_container);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reflection_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (contentContainer != null) {
                contentContainer.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });

        buildCards();
        bindViews();

        // 1. اختيار بطاقة عشوائية في كل مرة
        int randomIdx = new Random().nextInt(cards.size());
        renderCardInitial(randomIdx);

        // 2. إخفاء زر التالي في البداية
        btnNext.setVisibility(View.INVISIBLE);
        btnNext.setAlpha(0f);

        // إظهار زر التالي بعد 4 ثوانٍ لضمان القراءة
        showNextButtonRunnable = () -> {
            // ensure the button is on top and visible, then animate its alpha using ViewPropertyAnimator
            btnNext.bringToFront();
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setAlpha(0f);
            btnNext.animate().alpha(1f).setDuration(500).start();
        };
        handler.postDelayed(showNextButtonRunnable, 4000);

        // 4. الانتقال المباشر للـ Home بعد الضغط
        btnNext.setOnClickListener(v -> {
            cleanupHandler();
            Intent intent = new Intent(ReflectionActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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
        btnNext = findViewById(R.id.btn_next);
    }

    private String getCurrentLanguageString(int resId) {
        return getString(resId);
    }

    private void renderCardInitial(int idx) {
        ReflectionCard card = cards.get(idx);
        sceneView.setSceneType(card.sceneType);
        txtTitle.setText(getCurrentLanguageString(card.titleRes));
        txtTag.setText(getCurrentLanguageString(card.tagRes).toUpperCase());
        txtChip.setText(getCurrentLanguageString(card.chipRes));
        String noteDate = getCurrentLanguageString(R.string.note_prefix) + " · " + getCurrentLanguageString(card.dateRes);
        txtNoteDate.setText(noteDate);
        txtNote.setText(getCurrentLanguageString(card.noteRes));
        txtNext.setText(getCurrentLanguageString(R.string.enter_button));
    }

    private void cleanupHandler() {
        if (handler != null && showNextButtonRunnable != null) {
            handler.removeCallbacks(showNextButtonRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupHandler();
    }
}