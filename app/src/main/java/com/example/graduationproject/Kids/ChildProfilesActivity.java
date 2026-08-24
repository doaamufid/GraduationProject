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

        // Match system bars with screen background (#FFF8EE)
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFF8EE"));
        window.setNavigationBarColor(Color.parseColor("#FFF8EE"));

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        childProfileStore = new ChildProfileStore(this);
        childProfileStore.migrateFromSharedPreferencesIfNeeded(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnBack.setOnClickListener(v -> onBackPressed());
        setupProfilesList();
        setupAnimations();
    }

    private void setupAnimations() {
        binding.tvMascot.setAlpha(0f);
        binding.tvMascot.setTranslationY(-30f);
        binding.tvTitle.setAlpha(0f);
        binding.tvTitle.setTranslationY(20f);
        binding.tvSubtitle.setAlpha(0f);
        binding.tvSubtitle.setTranslationY(20f);
        binding.rvChildProfiles.setAlpha(0f);

        binding.tvMascot.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(200).start();
        binding.tvTitle.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(400).start();
        binding.tvSubtitle.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(500).start();
        binding.rvChildProfiles.animate().alpha(1f).setDuration(800).setStartDelay(600).start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, com.example.graduationproject.SplashSelectActivity.class);
        ActivityUtils.startActivityAndFinishWithAnimation(this, intent);
    }

    private void setupProfilesList() {
        adapter = new ChildProfilesAdapter(profiles, new ChildProfilesAdapter.OnChildProfileClickListener() {
            @Override
            public void onProfileClick(ChildProfile profile) {
                getSharedPreferences("KidsApp", MODE_PRIVATE).edit()
                        .putLong("current_child_id", profile.getId())
                        .apply();

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
