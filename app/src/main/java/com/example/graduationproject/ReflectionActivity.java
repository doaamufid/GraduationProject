package com.example.graduationproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
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
    private View btnNext;
    private ImageView imgSceneIcon;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable showNextButtonRunnable;
    private boolean forKids = false;
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

        setContentView(R.layout.activity_reflection);

        if (getIntent() != null) {
            forKids = getIntent().getBooleanExtra("FOR_KIDS", false);
            childId = getIntent().getLongExtra("CHILD_ID", -1);
            childName = getIntent().getStringExtra("CHILD_NAME");
        }

        // تطبيق حواف الشاشة على محتوى الواجهة الأمامية فقط لتستمر الخلفية بالظهور تحت الـ Status Bar
        View contentContainer = findViewById(R.id.content_container);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reflection_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (contentContainer != null) {
                int sidePadding = (int) (48 * getResources().getDisplayMetrics().density);
                contentContainer.setPadding(
                        systemBars.left + sidePadding,
                        systemBars.top + systemBars.bottom, // Use top padding for status bar
                        systemBars.right + sidePadding,
                        systemBars.bottom + (int) (32 * getResources().getDisplayMetrics().density)
                );
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

        // 4. الانتقال إلى MainActivity بعد الضغط
        btnNext.setOnClickListener(v -> {
            cleanupHandler();

            Intent intent;
            if (forKids) {
                intent = new Intent(ReflectionActivity.this, com.example.graduationproject.KidsMoodActivity.class);
                intent.putExtra("CHILD_ID", childId);
                intent.putExtra("CHILD_NAME", childName);
            } else {
                intent = new Intent(ReflectionActivity.this, MainActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityUtils.startActivityAndFinishWithAnimation(ReflectionActivity.this, intent);
        });
    }

    private void buildCards() {
        cards = new ArrayList<>();
        // All cards now follow the Magna City template style as requested
        cards.add(new ReflectionCard(SceneView.SCENE_MOUNTAIN, R.string.c1_title, R.string.c1_tag, R.string.c1_chip, R.string.c1_date, R.string.c1_note, R.drawable.ic_sparkles));
        cards.add(new ReflectionCard(SceneView.SCENE_SEA, R.string.c2_title, R.string.c2_tag, R.string.c2_chip, R.string.c2_date, R.string.c2_note, R.drawable.ic_sparkles));
        cards.add(new ReflectionCard(SceneView.SCENE_FOREST, R.string.c3_title, R.string.c3_tag, R.string.c3_chip, R.string.c3_date, R.string.c3_note, R.drawable.ic_sparkles));
        cards.add(new ReflectionCard(SceneView.SCENE_DESERT, R.string.c4_title, R.string.c4_tag, R.string.c4_chip, R.string.c4_date, R.string.c4_note, R.drawable.ic_sparkles));
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
        imgSceneIcon = findViewById(R.id.img_scene_icon);
    }

    private void renderCardInitial(int idx) {
        ReflectionCard card = cards.get(idx);
        sceneView.setSceneType(card.sceneType);
        imgSceneIcon.setImageResource(card.iconRes);
        
        // Use standard getString to respect the updated English template content
        txtTitle.setText(getString(card.titleRes));
        txtTag.setText(getString(card.tagRes).toUpperCase());
        txtChip.setText(getString(card.chipRes));
        
        // Hide date/prefix to match the clean screenshot look
        txtNoteDate.setVisibility(View.GONE);
        
        txtNote.setText(getString(card.noteRes));
        txtNext.setText(getString(R.string.enter_button));
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