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
import com.example.graduationproject.OneClickCalmActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.StrenghtBankActivity;
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

        // Colors from the reference image
        int colorGreenCard = 0xFFEBF2E5;
        int colorGreenCircle = 0xFF7BA67E;
        
        int colorBlueCard = 0xFFE3F2F5;
        int colorBlueCircle = 0xFF3D7B85;
        
        int colorYellowCard = 0xFFFDF2D7;
        int colorYellowCircle = 0xFFE5A647;
        
        int colorPinkCard = 0xFFF9E7E7;
        int colorPinkCircle = 0xFFD67676;

        // Add exercises based on the localized strings
        // 1. التأريض
        exerciseList.add(new ExerciseFeature(R.drawable.center, getString(R.string.exercises_grounding_title), getString(R.string.exercises_grounding_desc), getString(R.string.exercises_duration_5min), colorGreenCard, colorGreenCircle));
        
        // 2. التنفس
        exerciseList.add(new ExerciseFeature(R.drawable.air, getString(R.string.exercises_breathing_title_main), getString(R.string.exercises_breathing_desc_main), getString(R.string.exercises_duration_3min), colorBlueCard, colorBlueCircle));
        
        // 3. صندوق النجاة
        exerciseList.add(new ExerciseFeature(R.drawable.box2, getString(R.string.exercises_survival_box_title), getString(R.string.exercises_survival_box_desc), getString(R.string.exercises_duration_1min), colorYellowCard, colorYellowCircle));
        
        // 4. بطاقة التهدئة
        exerciseList.add(new ExerciseFeature(R.drawable.style, getString(R.string.exercises_calm_card_title), getString(R.string.exercises_calm_card_desc), getString(R.string.exercises_duration_1min), colorPinkCard, colorPinkCircle));

        // 5. نقاط قوتي
        exerciseList.add(new ExerciseFeature(R.drawable.license, getString(R.string.exercises_strengths_title), getString(R.string.exercises_strengths_desc), getString(R.string.exercises_duration_5min), colorGreenCard, colorGreenCircle));
        
        // 6. رسالة لنفسي
        exerciseList.add(new ExerciseFeature(R.drawable.mail, getString(R.string.exercises_future_letter_title), getString(R.string.exercises_future_letter_desc), getString(R.string.exercises_duration_10min), colorBlueCard, colorBlueCircle));
        
        // 7. إعادة صياغة الأفكار
        exerciseList.add(new ExerciseFeature(R.drawable.center, getString(R.string.exercises_reframe_title), getString(R.string.exercises_reframe_desc), getString(R.string.exercises_duration_7min), colorYellowCard, colorYellowCircle));
        
        // 8. خريطة الجسد
        exerciseList.add(new ExerciseFeature(R.drawable.pin, getString(R.string.exercises_body_map_title), getString(R.string.exercises_body_map_desc), getString(R.string.exercises_duration_5min), colorPinkCard, colorPinkCircle));

        // 3. تهيئة الـ Adapter وتمرير مستمع النقرات لاحقاً
        adapter = new ExerciseFeatureAdapter(requireContext(), exerciseList, position -> {
            // هنا ستضعين الأكشن لكل تمرين عند الضغط عليه لاحقاً
            switch (position) {
                case 0: // التأريض
                    startActivity(new Intent(requireContext(), GroundingExActivity.class));
                    break;

                case 1: // التنفس (Breathing)
                    Intent breathingIntent = new Intent(requireContext(), BreathingActivity.class);
                    startActivity(breathingIntent);
                    break;

                case 2: // صندوق النجاة (Survival Box)
                    startActivity(new Intent(requireContext(), SurvivalBoxActivity.class));
                    break;

                case 3: // بطاقة التهدئة الشخصية
                    startActivity(new Intent(requireContext(), OneClickCalmActivity.class));
                    break;

                case 4: // نقاط قوتي
                    startActivity(new Intent(requireContext(), StrenghtBankActivity.class));
                    break;

                case 5: // رسالة لنفسي
                    startActivity(new Intent(requireContext(), FutureActivity.class));
                    break;

                case 6: // إعادة صياغة الأفكار (CBT Reframing)
                    startActivity(new Intent(requireContext(), CBTRReframingActivity.class));
                    break;

                case 7: // خريطة الجسد (Body Map)
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