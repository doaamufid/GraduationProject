package com.example.graduationproject.bottomNavFragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.graduationproject.ArticlesActivity;
import com.example.graduationproject.BreathingActivity;
import com.example.graduationproject.DailyHabitsActivity;
import com.example.graduationproject.HealingEnvironmentActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.SurvivalBoxActivity;
import com.example.graduationproject.VideoLibraryActivity;
import com.example.graduationproject.VisualContentActivity;
import com.example.graduationproject.adapters.HomeActionAdapter;
import com.example.graduationproject.adapters.HomeFeatureAdapter;
import com.example.graduationproject.models.HomeAction;
import com.example.graduationproject.models.HomeFeature;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvActions, rvFeatures;
    private HomeActionAdapter actionAdapter;
    private HomeFeatureAdapter featureAdapter;

    private final List<HomeAction> actionList = new ArrayList<>();
    private final List<HomeFeature> featureList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvActions = view.findViewById(R.id.rvActions);
        rvFeatures = view.findViewById(R.id.rvFeatures);

        TextView tvGreeting = view.findViewById(R.id.tvGreeting);

        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE);
        String userName = prefs.getString("user_name", "");

        if (userName.trim().isEmpty()) {
            userName = "صديقي";
        }

        tvGreeting.setText("صباح الخير " + userName + " كيف تشعر الآن؟\nأنا هنا معك 🌊");

        // --- إضافة التحريكات (Animations) ---
        Animation fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_up);
        tvGreeting.startAnimation(fadeIn);

        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down);
        rvActions.setLayoutAnimation(animationController);
        rvFeatures.setLayoutAnimation(animationController);

        // تحريك واجهة الرموز التعبيرية (Emojis)
        View layoutEmojis = view.findViewById(R.id.layoutEmojis);
        if (layoutEmojis != null) {
            Animation emojiEnter = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_up);
            emojiEnter.setStartOffset(400); // زيادة التأخير ليظهر بوضوح بعد الترحيب
            layoutEmojis.startAnimation(emojiEnter);

            setupEmojiInteractions(view);
        }

        setupActions();
        setupFeatures();

        // تحريك الأمواج في الأسفل بشكل عائم (Floating Waves) - يطابق تصميم شاشة التمارين
        View imgWaveBottom = view.findViewById(R.id.imgWaveBottom);
        if (imgWaveBottom != null) {
            imgWaveBottom.animate()
                    .translationY(30)
                    .setDuration(5000)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> animateFloating(imgWaveBottom, -30))
                    .start();
        }
    }

    private void animateFloating(View view, float targetY) {
        if (view == null || getContext() == null) return;
        view.animate()
                .translationY(targetY)
                .setDuration(5000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> animateFloating(view, -targetY))
                .start();
    }

    private void setupEmojiInteractions(View view) {
        int[] emojiIds = {R.id.emoji1, R.id.emoji2, R.id.emoji3, R.id.emoji4, R.id.emoji5};
        for (int id : emojiIds) {
            View emoji = view.findViewById(id);
            if (emoji != null) {
                emoji.setOnClickListener(v -> {
                    // تأثير نبض أبطأ عند الضغط
                    v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(250).withEndAction(() -> {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250).start();
                    }).start();
                });
            }
        }
    }

    private void setupActions() {
        actionList.clear();
        actionList.add(new HomeAction(
                R.drawable.body_map, // صورة تعبيرية تشبه الموجودة بالصورة
                R.drawable.bg_icon_calm,
                "جلسة استرخاء سريعة لمزاج اليوم",
                "١٠ دقائق من الهدوء"
        ));

        actionAdapter = new HomeActionAdapter(requireContext(), actionList, position -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(getActivity(), BreathingActivity.class));
                    break;
            }
        });

        rvActions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvActions.setAdapter(actionAdapter);
    }

    private void setupFeatures() {
        featureList.clear();
        featureList.add(new HomeFeature(R.drawable.video, R.drawable.bg_icon_purple, "مرئيات", "VIDEOS"));
        featureList.add(new HomeFeature(R.drawable.audio, R.drawable.bg_icon_green, "صوتيات", "AUDIO"));
        featureList.add(new HomeFeature(R.drawable.ic_heart_filled_red, R.drawable.bg_icon_pink, "مقالات", "ARTICLES"));
        featureList.add(new HomeFeature(R.drawable.habits, R.drawable.bg_icon_orange, "عاداتي", "HABITS"));
        featureList.add(new HomeFeature(R.drawable.report, R.drawable.bg_icon_blue, "تقارير", "REPORTS"));
        featureList.add(new HomeFeature(R.drawable.box2, R.drawable.bg_icon_purple, "صندوق النجاة", "SURVIVAL BOX"));

        featureAdapter = new HomeFeatureAdapter(requireContext(), featureList, position -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(getActivity(), VideoLibraryActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getActivity(), HealingEnvironmentActivity.class));
                    break;
                case 2: // مقالات (ARTICLES)
                    startActivity(new Intent(getActivity(), ArticlesActivity.class));
                    break;
                case 3: // عاداتي
                    startActivity(new Intent(getActivity(), DailyHabitsActivity.class));
                    break;
                case 4: // تقارير
                    // TODO: Implement ReportsActivity connection when ready
                    break;
                case 5: // صندوق النجاة
                    startActivity(new Intent(getActivity(), SurvivalBoxActivity.class));
                    break;
            }
        });

        rvFeatures.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 3));
        rvFeatures.setAdapter(featureAdapter);
    }
}