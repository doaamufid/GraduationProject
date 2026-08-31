package com.example.graduationproject.Kids;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.ActivityUtils;
import com.example.graduationproject.adapters.ChildProfilesAdapter;
import com.example.graduationproject.data.ActiveChildManager;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityChildProfilesBinding;
import com.example.graduationproject.models.ChildProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ChildProfilesActivity extends AppCompatActivity {
    private ActivityChildProfilesBinding binding;
    private ChildProfilesAdapter adapter;
    private ChildProfileStore childProfileStore;
    private final List<ChildProfile> profiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        EdgeToEdge.enable(this);
        binding = ActivityChildProfilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Match system bars with screen background (#FFF8EE)
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        childProfileStore = ChildProfileStore.getInstance(this);
        childProfileStore.migrateFromSharedPreferencesIfNeeded(this);

        binding.btnBack.setOnClickListener(v -> onBackPressed());
        binding.btnSwitchMode.setOnClickListener(v -> onBackPressed());
        binding.btnSwitchProfile.setOnClickListener(v -> loadProfiles());

        setupProfilesList();
        setupAnimations();
    }

    private void setupAnimations() {
        // Initial state
        binding.ivBearFace.setAlpha(0f);
        binding.ivBearFace.setTranslationY(-50f);
        binding.tvTitle.setAlpha(0f);
        binding.tvTitle.setTranslationY(50f);
        binding.tvSubtitle.setAlpha(0f);
        binding.tvSubtitle.setTranslationY(30f);
        binding.rvChildProfiles.setAlpha(0f);
        binding.btnBack.setAlpha(0f);
        binding.btnSwitchMode.setAlpha(0f);
        binding.btnSwitchProfile.setAlpha(0f);

        // Bear Animation
        binding.ivBearFace.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(200)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        // Top bar icons animation
        binding.btnBack.animate().alpha(1f).setDuration(500).setStartDelay(100).start();
        binding.btnSwitchMode.animate().alpha(1f).setDuration(500).setStartDelay(100).start();
        binding.btnSwitchProfile.animate().alpha(1f).setDuration(500).setStartDelay(100).start();

        // Title and Subtitle Animation
        binding.tvTitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(400)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        binding.tvSubtitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(500)
                .start();

        // List Animation
        binding.rvChildProfiles.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(600)
                .start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, com.example.graduationproject.SplashSelectActivity.class);
        ActivityUtils.startActivityAndFinishWithAnimation(this, intent);
        super.onBackPressed();
    }

    private void setupProfilesList() {
        adapter = new ChildProfilesAdapter(profiles, new ChildProfilesAdapter.OnChildProfileClickListener() {
            @Override
            public void onProfileClick(ChildProfile profile) {
                getSharedPreferences("KidsApp", MODE_PRIVATE).edit()
                        .putLong("current_child_id", profile.getId())
                        .putString("current_child_name", profile.getName())
                        .apply();
                ActiveChildManager.setActiveChildId(ChildProfilesActivity.this, profile.getId());

                Intent intent = new Intent(ChildProfilesActivity.this, com.example.graduationproject.Kids.KidsReflectionActivity.class);
                intent.putExtra("FOR_KIDS", true);
                intent.putExtra("CHILD_ID", profile.getId());
                intent.putExtra("CHILD_NAME", profile.getName());
                ActivityUtils.startActivityWithAnimation(ChildProfilesActivity.this, intent);
            }

            @Override
            public void onAddProfileClick() {
                openNewProfileScreen();
            }
        });
        binding.rvChildProfiles.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChildProfiles.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfiles();
    }

    @Override
    protected void onDestroy() {
        if (childProfileStore != null) {
            childProfileStore.close();
        }
        super.onDestroy();
    }

    private void openNewProfileScreen() {
        ActivityUtils.startActivityWithAnimation(this, new Intent(this, com.example.graduationproject.KidsAdaptiveMainActivity.class));
    }

    private void loadProfiles() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<ChildProfile> newProfiles = childProfileStore.getProfiles();
            runOnUiThread(() -> {
                profiles.clear();
                profiles.addAll(newProfiles);
                adapter.notifyDataSetChanged();
            });
        });
    }
}
