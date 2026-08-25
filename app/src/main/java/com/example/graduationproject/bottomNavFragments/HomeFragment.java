package com.example.graduationproject.bottomNavFragments;

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
import android.widget.TextView;

import com.example.graduationproject.AdultMoodStatsActivity;
import com.example.graduationproject.ArticlesActivity;
import com.example.graduationproject.BreathingActivity;
import com.example.graduationproject.DailyHabitsActivity;
import com.example.graduationproject.HealingEnvActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.SalamCommunityActivity;
import com.example.graduationproject.VideoLibraryActivity;
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
            userName = getString(R.string.home_friend);
        }

        tvGreeting.setText(getString(R.string.home_greeting_format, userName));

        // --- إضافة التحريكات (Animations) ---
        Animation fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_up);
        tvGreeting.startAnimation(fadeIn);

        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down);
        rvActions.setLayoutAnimation(animationController);
        rvFeatures.setLayoutAnimation(animationController);


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

    private void setupActions() {
        actionList.clear();
        actionList.add(new HomeAction(
                R.drawable.body_map, // صورة تعبيرية تشبه الموجودة بالصورة
                R.drawable.bg_icon_calm,
                getString(R.string.home_relax_title),
                getString(R.string.home_relax_subtitle)
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
        featureList.add(new HomeFeature(R.drawable.video, R.drawable.bg_icon_purple, getString(R.string.home_feature_videos), getString(R.string.home_feature_videos_sub)));
        featureList.add(new HomeFeature(R.drawable.audio, R.drawable.bg_icon_green, getString(R.string.home_feature_audio), getString(R.string.home_feature_audio_sub)));
        featureList.add(new HomeFeature(R.drawable.ic_heart_filled_red, R.drawable.bg_icon_pink, getString(R.string.home_feature_articles), getString(R.string.home_feature_articles_sub)));
        featureList.add(new HomeFeature(R.drawable.habits, R.drawable.bg_icon_orange, getString(R.string.home_feature_habits), getString(R.string.home_feature_habits_sub)));
        featureList.add(new HomeFeature(R.drawable.report, R.drawable.bg_icon_blue, getString(R.string.home_feature_reports), getString(R.string.home_feature_reports_sub)));
        featureList.add(new HomeFeature(R.drawable.ic_users, R.drawable.bg_icon_purple, getString(R.string.home_feature_community), getString(R.string.home_feature_community_sub)));

        featureAdapter = new HomeFeatureAdapter(requireContext(), featureList, position -> {
            // Resolve by feature title to avoid index mismatches
            HomeFeature feature = featureList.get(position);
            String title = feature.getTitle();
            if (title.equals(getString(R.string.home_feature_videos))) {
                startActivity(new Intent(getActivity(), VideoLibraryActivity.class));
            } else if (title.equals(getString(R.string.home_feature_audio))) {
                startActivity(new Intent(getActivity(), HealingEnvActivity.class));
            } else if (title.equals(getString(R.string.home_feature_articles))) {
                startActivity(new Intent(getActivity(), ArticlesActivity.class));
            } else if (title.equals(getString(R.string.home_feature_habits))) {
                startActivity(new Intent(getActivity(), DailyHabitsActivity.class));
            } else if (title.equals(getString(R.string.home_feature_reports))) {
                startActivity(new Intent(getActivity(), AdultMoodStatsActivity.class));
            } else if (title.equals(getString(R.string.home_feature_community))) {
                startActivity(new Intent(getActivity(), SalamCommunityActivity.class));
            }
        });

        rvFeatures.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 3));
        rvFeatures.setAdapter(featureAdapter);
    }
}