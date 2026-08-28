package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.graduationproject.models.profile.settings.SettingsRepository;
import com.example.graduationproject.models.profile.settings.ThemeOption;
import com.example.graduationproject.Kids.ChildProfilesActivity;
import com.example.graduationproject.ui.profile.settings.DeleteAllDialogFragment;
import com.example.graduationproject.ui.profile.settings.SettingsRowHelper;
import com.example.graduationproject.widget.FadeUtils;
import com.example.graduationproject.widget.ToastController;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private final SettingsRepository repo = SettingsRepository.getInstance();

    private ToastController toastController;
    private TextView tvStatusClock;
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private final Runnable clockRunnable = new Runnable() {
        @Override
        public void run() {
            if (tvStatusClock != null) {
                tvStatusClock.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
            }
            clockHandler.postDelayed(this, 60000L);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Full screen / Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_settings);

        tvStatusClock = findViewById(R.id.tvStatusClock);
        clockRunnable.run();

        View rootLayout = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, top, 0, bottom);
            return insets;
        });

        toastController = new ToastController(findViewById(R.id.toastHost));

        bindHeaderActions();
        bindPrivacySection();
        bindNotificationsSection();
        bindCustomizationSection();
        bindAiSection();
        bindSoundSection();
        bindDataSection();
        bindChildrenSection();
        bindSupportSection();
        bindDestructiveZone();

        getSupportFragmentManager().setFragmentResultListener(
                DeleteAllDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    toastController.show(getString(R.string.toast_deleted_everything));
                });

        applyThemeColors();
        animateElements();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clockHandler.removeCallbacks(clockRunnable);
    }

    private void animateElements() {
        View header = findViewById(R.id.mainHeader);
        if (header != null) {
            FadeUtils.fadeIn(header, 0);
        }

        ViewGroup root = findViewById(R.id.mainScrollContent);
        if (root == null) return;
        
        int delay = 150; // Start after header
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            FadeUtils.fadeIn(child, delay);
            delay += 100; // Slower stagger
        }
    }

    private void bindHeaderActions() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void bindPrivacySection() {
        SwitchMaterial swDisguise = SettingsRowHelper.bindToggleRow(
                findViewById(R.id.rowDisguise), getString(R.string.disguise_title), getString(R.string.disguise_sub));
        swDisguise.setChecked(repo.disguise);
        swDisguise.setOnCheckedChangeListener((b, checked) -> repo.disguise = checked);

        SwitchMaterial swAutoDelete = SettingsRowHelper.bindToggleRow(
                findViewById(R.id.rowAutoDelete), getString(R.string.auto_delete_title), getString(R.string.auto_delete_sub));
        swAutoDelete.setChecked(repo.autoDelete);
        swAutoDelete.setOnCheckedChangeListener((b, checked) -> repo.autoDelete = checked);

        SwitchMaterial swAppLock = SettingsRowHelper.bindToggleRow(
                findViewById(R.id.rowAppLock), getString(R.string.app_lock_title), getString(R.string.app_lock_sub));
        swAppLock.setChecked(repo.appLock);
        swAppLock.setOnCheckedChangeListener((b, checked) -> repo.appLock = checked);
    }

    private void bindNotificationsSection() {
        SettingsRowHelper.bindNavRow(findViewById(R.id.rowNotifications),
                getString(R.string.notifications_title), getString(R.string.notifications_sub),
                R.drawable.ic_bell, () -> {
                });
    }

    private void bindCustomizationSection() {
        SwitchMaterial swAutoDark = SettingsRowHelper.bindToggleRow(
                findViewById(R.id.rowAutoDark), getString(R.string.auto_dark_title), getString(R.string.auto_dark_sub));
        swAutoDark.setChecked(repo.autoDark);
        swAutoDark.setOnCheckedChangeListener((b, checked) -> repo.autoDark = checked);
    }

    private void applyThemeColors() {
        List<ThemeOption> themeOptions = SettingsRepository.themes(this);
        int colorInt = 0;
        for (ThemeOption opt : themeOptions) {
            if (opt.key.equals(repo.theme)) {
                colorInt = opt.colorInt;
                break;
            }
        }
        if (colorInt == 0) return;

        SettingsRowHelper.setThemeColor(findViewById(R.id.rowDisguise), colorInt);
        SettingsRowHelper.setThemeColor(findViewById(R.id.rowAutoDelete), colorInt);
        SettingsRowHelper.setThemeColor(findViewById(R.id.rowAppLock), colorInt);
        SettingsRowHelper.setThemeColor(findViewById(R.id.rowNotifications), colorInt);
        SettingsRowHelper.setThemeColor(findViewById(R.id.rowAutoDark), colorInt);
        SettingsRowHelper.setThemeColor(findViewById(R.id.rowCloudAI), colorInt);
        SettingsRowHelper.setThemeColor(findViewById(R.id.rowBreathHaptic), colorInt);
        SettingsRowHelper.setThemeColor(findViewById(R.id.rowManageChildren), colorInt);
        SettingsRowHelper.setThemeColor(findViewById(R.id.rowFaq), colorInt);

        findViewById(R.id.resetRecsIconBg).getBackground().mutate().setTint(colorInt);
    }

    private void bindAiSection() {
        SwitchMaterial swCloudAI = SettingsRowHelper.bindToggleRow(
                findViewById(R.id.rowCloudAI), getString(R.string.cloud_ai_title), getString(R.string.cloud_ai_sub));
        swCloudAI.setChecked(repo.cloudAI);
        swCloudAI.setOnCheckedChangeListener((b, checked) -> repo.cloudAI = checked);

        findViewById(R.id.btnResetRecs).setOnClickListener(v ->
                startActivity(new Intent(this, AdultOnboardingMainActivity.class)));
    }

    private void bindSoundSection() {
        SwitchMaterial swBreathHaptic = SettingsRowHelper.bindToggleRow(
                findViewById(R.id.rowBreathHaptic), getString(R.string.breath_haptic_title), getString(R.string.breath_haptic_sub));
        swBreathHaptic.setChecked(repo.breathHaptic);
        swBreathHaptic.setOnCheckedChangeListener((b, checked) -> repo.breathHaptic = checked);
    }

    private void bindDataSection() {
    }

    private void bindChildrenSection() {
        int count = repo.children.size();
        SettingsRowHelper.bindNavRow(findViewById(R.id.rowManageChildren),
                getString(R.string.manage_children_title), getString(R.string.children_count_format, count),
                R.drawable.ic_users, () -> startActivity(new Intent(this, ChildProfilesActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindChildrenSection();
    }

    private void bindSupportSection() {
        SettingsRowHelper.bindNavRow(findViewById(R.id.rowFaq), getString(R.string.faq_title), null,
                R.drawable.ic_help_circle, () -> {
                });

        SettingsRowHelper.bindNavRowNoIcon(findViewById(R.id.rowAbout),
                getString(R.string.about_title), getString(R.string.about_sub), () -> {
                });
    }

    private void bindDestructiveZone() {
        findViewById(R.id.btnDeleteAll).setOnClickListener(v ->
                DeleteAllDialogFragment.newInstance().show(getSupportFragmentManager(), "delete_all"));
    }
}
