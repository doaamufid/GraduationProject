package com.example.graduationproject.bottomNavFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.BodyMapActivity;
import com.example.graduationproject.BreathingActivity;
import com.example.graduationproject.CBTRReframingActivity;
import com.example.graduationproject.FutureActivity;
import com.example.graduationproject.GroundingExActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.SurvivalBoxActivity;
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

        // --- إضافة التحريكات (Animations) ---
        TextView tvTitle = view.findViewById(R.id.tvExercisesTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvExercisesSubtitle);

        Animation fadeInUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_up);
        if (tvTitle != null) tvTitle.startAnimation(fadeInUp);
        if (tvSubtitle != null) {
            Animation subtitleFade = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_up);
            subtitleFade.setStartOffset(200);
            tvSubtitle.startAnimation(subtitleFade);
        }

        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down);
        rvExercises.setLayoutAnimation(animationController);

        // تحريك الأمواج في الأسفل بشكل عائم (Floating Waves)
        View imgWaveBottom = view.findViewById(R.id.imgWaveBottom);
        if (imgWaveBottom != null) {
            imgWaveBottom.animate()
                    .translationY(30)
                    .setDuration(5000)
                    .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator())
                    .withEndAction(() -> animateFloating(imgWaveBottom, -30))
                    .start();
        }

        // 2. ملء القائمة بالبيانات الستة للتمارين
        setupExercisesData();
    }

    private void animateFloating(View view, float targetY) {
        view.animate()
                .translationY(targetY)
                .setDuration(5000)
                .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator())
                .withEndAction(() -> animateFloating(view, -targetY))
                .start();
    }

    private void setupExercisesData() {
        exerciseList.clear();

        // البيانات مأخوذة من الفيجما مباشرة مع إضافة خلفيات ملونة للأيقونات لتطابق تصميم الصفحة الرئيسية
        exerciseList.add(new ExerciseFeature(R.drawable.license, R.drawable.bg_icon_blue, "نقاط قوتي", "STRENGTHS"));
        exerciseList.add(new ExerciseFeature(R.drawable.mail, R.drawable.bg_icon_purple, "رسالة لنفسي", "FUTURE SELF"));
        exerciseList.add(new ExerciseFeature(R.drawable.center, R.drawable.bg_icon_orange, "التأريض", "GROUNDING"));
        exerciseList.add(new ExerciseFeature(R.drawable.style, R.drawable.bg_icon_pink, "بطاقة التهدئة الشخصية", "CALM CARD"));
        exerciseList.add(new ExerciseFeature(R.drawable.air, R.drawable.bg_icon_green, "التنفس", "BREATHING"));
        exerciseList.add(new ExerciseFeature(R.drawable.center, R.drawable.bg_icon_purple, "إعادة صياغة الأفكار", "CBT REFRAMING"));
        exerciseList.add(new ExerciseFeature(R.drawable.pin, R.drawable.bg_icon_yellow, "خريطة الجسد", "BODY MAP"));
        exerciseList.add(new ExerciseFeature(R.drawable.box2, R.drawable.bg_icon_purple, "صندوق النجاة", "SURVIVAL BOX"));

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

                case 5: // إعادة صياغة الأفكار (CBT Reframing)
                    startActivity(new Intent(requireContext(), CBTRReframingActivity.class));
                    break;

                case 6: // خريطة الجسد (Body Map)
                    Intent bodyMapIntent = new Intent(requireContext(), BodyMapActivity.class);
                    startActivity(bodyMapIntent);
                    break;

                case 7: // صندوق النجاة (Survival Box)
                    startActivity(new Intent(requireContext(), SurvivalBoxActivity.class));
                    break;
            }
        });

        // 4. السطر السحري: تحديد شبكة من عمودين لترتيب كروت الـ 0dp بجانب بعضها
        rvExercises.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvExercises.setAdapter(adapter);
    }
}