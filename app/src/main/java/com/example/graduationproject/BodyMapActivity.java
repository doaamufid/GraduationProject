package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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
        binding = ActivityBodyMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    }

    private void expandArea(String key) {
        int index = adapter.indexOfKey(key);
        if (index == -1) return;
        adapter.expand(index);
        binding.recyclerExercises.smoothScrollToPosition(index);
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