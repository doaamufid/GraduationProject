package com.example.graduationproject.bottomNavFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.BodyMapActivity;
import com.example.graduationproject.BreathingActivity;
import com.example.graduationproject.FutureActivity;
import com.example.graduationproject.GroundingExActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.ExerciseFeatureAdapter;
import com.example.graduationproject.models.ExerciseFeature;
import java.util.ArrayList;
import java.util.List;

public class ExercisesFragment extends Fragment {

    private RecyclerView rvExercises;
    private ExerciseFeatureAdapter adapter;
    private final List<ExerciseFeature> exerciseList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercises, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. ربط الـ RecyclerView من الـ XML
        rvExercises = view.findViewById(R.id.rvExercises);

        // 2. ملء القائمة بالبيانات الستة للتمارين
        setupExercisesData();
    }

    private void setupExercisesData() {
        exerciseList.clear();

        // البيانات مأخوذة من الفيجما مباشرة
        exerciseList.add(new ExerciseFeature(R.drawable.license, "نقاط قوتي", "STRENGTHS"));
        exerciseList.add(new ExerciseFeature(R.drawable.mail, "رسالة لنفسي", "FUTURE SELF"));
        exerciseList.add(new ExerciseFeature(R.drawable.center, "التأريض", "GROUNDING"));
        exerciseList.add(new ExerciseFeature(R.drawable.style, "بطاقة التهدئة الشخصية", "CALM CARD"));
        exerciseList.add(new ExerciseFeature(R.drawable.air, "التنفس", "BREATHING"));
        exerciseList.add(new ExerciseFeature(R.drawable.pin, "خريطة الجسد", "BODY MAP"));

        // 3. تهيئة الـ Adapter وتمرير مستمع النقرات لاحقاً
        adapter = new ExerciseFeatureAdapter(requireContext(), exerciseList, position -> {
            // هنا ستضعين الأكشن لكل تمرين عند الضغط عليه لاحقاً
            switch (position) {
                case 0: // نقاط قوتي
                    // مثال للانتقال إلى Activity:
                    // startActivity(new Intent(requireContext(), StrengthsActivity.class));
                    Toast.makeText(requireContext(), "تم الضغط على نقاط قوتي", Toast.LENGTH_SHORT).show();
                    break;

                case 1: // رسالة لنفسي
                    startActivity(new Intent(requireContext(), FutureActivity.class));
                    break;

                case 2: // التأريض
                    startActivity(new Intent(requireContext(), GroundingExActivity.class));
                    break;

                case 3: // بطاقة التهدئة الشخصية
                    Toast.makeText(requireContext(), "تم الضغط على بطاقة التهدئة", Toast.LENGTH_SHORT).show();
                    break;

                case 4: // التنفس (Breathing)
                    Intent breathingIntent = new Intent(requireContext(), BreathingActivity.class);
                    startActivity(breathingIntent);
                    break;

                case 5: // خريطة الجسد (Body Map)
                    Intent bodyMapIntent = new Intent(requireContext(), BodyMapActivity.class);
                    startActivity(bodyMapIntent);
                    break;
            }
        });

        // 4. السطر السحري: تحديد شبكة من عمودين لترتيب كروت الـ 0dp بجانب بعضها
        rvExercises.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvExercises.setAdapter(adapter);
    }
}