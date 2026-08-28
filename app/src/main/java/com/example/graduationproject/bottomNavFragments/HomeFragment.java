package com.example.graduationproject.bottomNavFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.AdultMoodStatsActivity;
import com.example.graduationproject.ArticlesActivity;
import com.example.graduationproject.BreathingActivity;
import com.example.graduationproject.DailyHabitsActivity;
import com.example.graduationproject.HealingEnvActivity;
import com.example.graduationproject.OneClickCalmActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.SalamCommunityActivity;
import com.example.graduationproject.VideoLibraryActivity;
import com.example.graduationproject.adapters.HomeFeatureAdapter;
import com.example.graduationproject.adapters.WeeklyMoodAdapter;
import com.example.graduationproject.models.HomeFeature;
import com.example.graduationproject.models.MoodDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView rvWeeklyMood, rvFeatures;
    private WeeklyMoodAdapter weeklyMoodAdapter;
    private HomeFeatureAdapter featureAdapter;

    private final List<MoodDay> moodDays = new ArrayList<>();
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

        rvWeeklyMood = view.findViewById(R.id.rvWeeklyMood);
        rvFeatures = view.findViewById(R.id.rvFeatures);

        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        View cardCalm = view.findViewById(R.id.cardCalm);
        View cardBreathing = view.findViewById(R.id.cardBreathing);
        View btnNotifications = view.findViewById(R.id.btnNotifications);
        View btnSwitchSection = view.findViewById(R.id.btnSwitchSection);

        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE);
        String userName = prefs.getString("user_name", "");

        if (userName.trim().isEmpty()) {
            userName = getString(R.string.home_friend);
        }
        tvGreeting.setText(getString(R.string.home_greeting_morning, userName));

        setupWeeklyMood();
        setupFeatures();

        cardCalm.setOnClickListener(v -> startActivity(new Intent(getActivity(), OneClickCalmActivity.class)));
        cardBreathing.setOnClickListener(v -> startActivity(new Intent(getActivity(), BreathingActivity.class)));
        btnNotifications.setOnClickListener(v -> {
            try {
                Class<?> notificationsClass = Class.forName("com.example.graduationproject.NotificationsActivity");
                startActivity(new Intent(getActivity(), notificationsClass));
            } catch (ClassNotFoundException e) {
                // Fallback if not found
            }
        });

        btnSwitchSection.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.graduationproject.SplashSelectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Keep wave animation
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

    private void setupWeeklyMood() {
        moodDays.clear();
        
        SharedPreferences appPrefs = requireContext().getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE);
        String todayMoodId = appPrefs.getString("today_mood_id", "");
        int todayMoodColor = appPrefs.getInt("today_mood_color", 0xFFEAEEF3); // Default neutral

        Calendar cal = Calendar.getInstance();
        int todayDate = cal.get(Calendar.DAY_OF_YEAR);
        
        // Start from Sunday of the current week
        Calendar tempCal = (Calendar) cal.clone();
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        
        // Simulating some past moods to match the design style
        int[] mockMoodColors = {0xFFDEF3EF, 0xFFFCF0C6, 0xFFDEF3E0, 0xFFF8DCDA, 0xFFDEF3EF, 0, 0};
        int[] mockMoodIcons = {R.drawable.ic_smile, R.drawable.ic_smile, R.drawable.ic_smile, R.drawable.ic_smile, R.drawable.ic_smile, 0, 0};

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("ar"));

        for (int i = 0; i < 7; i++) {
            int dateVal = tempCal.get(Calendar.DAY_OF_MONTH);
            int dayOfYear = tempCal.get(Calendar.DAY_OF_YEAR);
            boolean isToday = (dayOfYear == todayDate);
            
            String dayName = dayFormat.format(tempCal.getTime());
            String dateStr = String.valueOf(dateVal);
            
            int icon = 0;
            int color = 0;

            if (isToday) {
                if (!todayMoodId.isEmpty()) {
                    icon = R.drawable.ic_smile;
                    color = todayMoodColor;
                }
            } else if (tempCal.before(cal)) {
                // For past days in this week, show some mock icons/colors if we don't have real data
                icon = mockMoodIcons[i % mockMoodIcons.length];
                color = mockMoodColors[i % mockMoodColors.length];
            }

            moodDays.add(new MoodDay(dayName, dateStr, icon, color, isToday));
            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        weeklyMoodAdapter = new WeeklyMoodAdapter(requireContext(), moodDays);
        rvWeeklyMood.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvWeeklyMood.setAdapter(weeklyMoodAdapter);
        
        // Scroll to current day
        rvWeeklyMood.post(() -> {
            for (int i = 0; i < moodDays.size(); i++) {
                if (moodDays.get(i).isCurrentDay()) {
                    rvWeeklyMood.scrollToPosition(i);
                    break;
                }
            }
        });
    }

    private void setupFeatures() {
        featureList.clear();
        featureList.add(new HomeFeature(R.drawable.video, R.drawable.bg_icon_purple, getString(R.string.home_feature_videos_title), getString(R.string.home_feature_videos_desc)));
        featureList.add(new HomeFeature(R.drawable.audio, R.drawable.bg_icon_green, getString(R.string.home_feature_audio_title), getString(R.string.home_feature_audio_desc)));
        featureList.add(new HomeFeature(R.drawable.ic_heart_filled_red, R.drawable.bg_icon_pink, getString(R.string.home_feature_articles_title), getString(R.string.home_feature_articles_desc)));
        featureList.add(new HomeFeature(R.drawable.habits, R.drawable.bg_icon_orange, getString(R.string.home_feature_habits_title), getString(R.string.home_feature_habits_desc)));
        featureList.add(new HomeFeature(R.drawable.report, R.drawable.bg_icon_blue, getString(R.string.home_feature_reports_title), getString(R.string.home_feature_reports_desc)));
        featureList.add(new HomeFeature(R.drawable.ic_users, R.drawable.bg_icon_purple, getString(R.string.home_feature_community_title), getString(R.string.home_feature_community_desc)));

        featureAdapter = new HomeFeatureAdapter(requireContext(), featureList, position -> {
            HomeFeature feature = featureList.get(position);
            String title = feature.getTitle();
            if (title.equals(getString(R.string.home_feature_videos_title))) {
                startActivity(new Intent(getActivity(), VideoLibraryActivity.class));
            } else if (title.equals(getString(R.string.home_feature_audio_title))) {
                startActivity(new Intent(getActivity(), HealingEnvActivity.class));
            } else if (title.equals(getString(R.string.home_feature_articles_title))) {
                startActivity(new Intent(getActivity(), ArticlesActivity.class));
            } else if (title.equals(getString(R.string.home_feature_habits_title))) {
                startActivity(new Intent(getActivity(), DailyHabitsActivity.class));
            } else if (title.equals(getString(R.string.home_feature_reports_title))) {
                startActivity(new Intent(getActivity(), AdultMoodStatsActivity.class));
            } else if (title.equals(getString(R.string.home_feature_community_title))) {
                startActivity(new Intent(getActivity(), SalamCommunityActivity.class));
            }
        });

        rvFeatures.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvFeatures.setAdapter(featureAdapter);
    }
}