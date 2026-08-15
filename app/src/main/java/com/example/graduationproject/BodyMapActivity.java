package com.example.graduationproject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.adapters.ExerciseAreaAdapter;
import com.example.graduationproject.data.ExercisePrefs;
import com.example.graduationproject.databinding.ActivityBodyMapBinding;
import com.example.graduationproject.models.ExerciseArea;

import java.util.ArrayList;
import java.util.List;

public class BodyMapActivity extends AppCompatActivity {

    private ActivityBodyMapBinding binding;
    private ExerciseAreaAdapter adapter;
    private ExercisePrefs prefs;
    private List<ExerciseArea> areas;

    private final ActivityResultLauncher<Intent> timerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String key = result.getData().getStringExtra("area_key");
                    if (key != null) {
                        prefs.incrementTryCount(key);
                        int index = adapter.indexOfKey(key);
                        if (index != -1) adapter.refreshTryCount(index);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBodyMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            binding.layoutHeader.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        prefs = new ExercisePrefs(this);
        areas = buildAreas();

        adapter = new ExerciseAreaAdapter(areas, prefs, (area, position) -> {
            Intent intent = new Intent(this, ExerciseTimerActivity.class);
            intent.putExtra("area_key", area.key);
            intent.putExtra("area_title", area.title);
            intent.putExtra("exercise_title", area.exerciseTitle);
            intent.putExtra("exercise_desc", area.exerciseDesc);
            intent.putExtra("duration_minutes", area.durationMinutes);
            intent.putExtra("reps_count", area.repsCount);
            timerLauncher.launch(intent);
        });

        binding.recyclerExercises.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerExercises.setAdapter(adapter);

        // نقاط مجسم الجسد -> توسيع العنصر المطابق بالقائمة
        binding.dotHead.setOnClickListener(v -> expandArea("head"));
        binding.dotChest.setOnClickListener(v -> expandArea("chest"));
        binding.dotShoulders.setOnClickListener(v -> expandArea("shoulders"));
        binding.dotStomach.setOnClickListener(v -> expandArea("stomach"));

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnNotSure.setOnClickListener(v -> {
            com.example.graduationproject.dialogs.BodyMapGuideDialogFragment.newInstance(key -> {
                expandArea(key);
            }).show(getSupportFragmentManager(), "guide");
        });

        startEntranceAnimations();
    }

    private void startEntranceAnimations() {
        // Initial state
        binding.imgBody.setAlpha(0f);
        binding.imgBody.setScaleX(0.8f);
        binding.imgBody.setScaleY(0.8f);

        binding.cardLeftHead.setAlpha(0f);
        binding.cardLeftHead.setTranslationX(-50f);
        binding.cardLeftShoulders.setAlpha(0f);
        binding.cardLeftShoulders.setTranslationX(-50f);
        binding.cardLeftChest.setAlpha(0f);
        binding.cardLeftChest.setTranslationX(-50f);
        binding.cardLeftStomach.setAlpha(0f);
        binding.cardLeftStomach.setTranslationX(-50f);

        binding.tvGuideText.setAlpha(0f);
        binding.tvGuideText.setTranslationY(-20f);

        // Body pop
        binding.imgBody.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(800).setInterpolator(new OvershootInterpolator()).start();

        // Staggered cards
        long delayStart = 300;
        View[] cards = {binding.cardLeftHead, binding.cardLeftShoulders, binding.cardLeftChest, binding.cardLeftStomach};
        for (int i = 0; i < cards.length; i++) {
            cards[i].animate().alpha(1f).translationX(0f).setDuration(600).setStartDelay(delayStart + (i * 150L)).setInterpolator(new DecelerateInterpolator()).start();
        }

        binding.tvGuideText.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(200).start();

        binding.recyclerExercises.setAlpha(0f);
        binding.recyclerExercises.setTranslationY(100f);
        binding.recyclerExercises.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(1000).start();
    }

    private void expandArea(String key) {
        int index = adapter.indexOfKey(key);
        if (index == -1) return;
        adapter.expand(index);
        binding.recyclerExercises.smoothScrollToPosition(index);
        
        // Pulse effect on the clicked dot/area
        final View targetDot;
        switch (key) {
            case "head": targetDot = binding.dotHead; break;
            case "chest": targetDot = binding.dotChest; break;
            case "shoulders": targetDot = binding.dotShoulders; break;
            case "stomach": targetDot = binding.dotStomach; break;
            default: targetDot = null; break;
        }
        
        if (targetDot != null) {
            targetDot.animate().scaleX(1.5f).scaleY(1.5f).setDuration(200).withEndAction(() -> 
                targetDot.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
            ).start();
        }
    }

    private List<ExerciseArea> buildAreas() {
        List<ExerciseArea> list = new ArrayList<>();

        list.add(new ExerciseArea(
                "head", "الرأس والرقبة", "صداع • توتر",
                "#B7950B", "#FFF9E7",
                "تحرير الرقبة - التنفس والتأمل",
                "أدر رأسك ببطء لليمين ثم لليسار، وركز على إخراج التوتر مع كل زفير.",
                3, 3, true));

        list.add(new ExerciseArea(
                "chest", "الصدر", "ضيق • خفقان",
                "#CB4335", "#FDEDEC",
                "تمرين للصدر - فك التوتر",
                "ضع يدك على صدرك، خذ نفساً عميقاً، وتخيّل أن الضيق يخرج مع الزفير.",
                3, 3, true));

        list.add(new ExerciseArea(
                "shoulders", "الأكتاف", "ثقل • تصلب",
                "#2980B9", "#EBF5FB",
                "رفع الكتفين وإرخاؤهما",
                "ارفع كتفيك نحو أذنيك، احبس 3 ثوانٍ، ثم أرخهما فجأة مع الزفير. كرّر 5 مرات.",
                2, 5, true));

        list.add(new ExerciseArea(
                "stomach", "المعدة", "غثيان • قلق",
                "#5D6D7E", "#EAECEE",
                "تنفس البطن العميق",
                "ضع يدك على بطنك، تنفس بعمق حتى ترتفع يدك، ثم أخرج الهواء ببطء شديد.",
                2, 4, true));

        return list;
    }
}