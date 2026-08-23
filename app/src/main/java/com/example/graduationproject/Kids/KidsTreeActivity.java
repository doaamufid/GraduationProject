package com.example.graduationproject.Kids;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityKidsTreeBinding;
import com.example.graduationproject.databinding.BottomSheetKidsActionsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class KidsTreeActivity extends AppCompatActivity {

    private ActivityKidsTreeBinding binding;
    private TreeProgressManager progressManager;
    private ChildProfileStore profileStore;
    private String childName;
    private long childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKidsTreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profileStore = new ChildProfileStore(this);

        // 1. جلب بيانات الطفل بدقة لتجنب قيم null والحسابات المشتركة
        childId = getCurrentChildId();
        childName = getIntent().getStringExtra("CHILD_NAME");

        if (childName == null || childName.trim().isEmpty()) {
            childName = "Child_" + childId; // اسم افتراضي محمي بالـ ID لمنع تداخل الحسابات
        }

        // 2. ربط ومدير التقدم بالطفل الموحد
        progressManager = new TreeProgressManager(this, childName);

        // تسجيل الدخول اليومي
        trackDailyLogin();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnOpenDailyActions.setOnClickListener(v -> showActionsBottomSheet());

        setupBadgeClickListeners();
        setupStageClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTreeDisplay();
    }

    private void trackDailyLogin() {
        SharedPreferences streakPrefs = getSharedPreferences("KidsAppStreak_" + childId, Context.MODE_PRIVATE);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastLogin = streakPrefs.getString("last_login_date", "");

        if (!today.equals(lastLogin)) {
            int streak = streakPrefs.getInt("consecutive_days", 0);
            streak++;

            streakPrefs.edit()
                    .putString("last_login_date", today)
                    .putInt("consecutive_days", streak)
                    .putBoolean("logged_today", true)
                    .apply();

            // إضافة 10 نقاط دخول يومي مرة واحدة فقط
            progressManager.addPoints(10);
        }
    }

    private int getStreakDays() {
        SharedPreferences streakPrefs = getSharedPreferences("KidsAppStreak_" + childId, Context.MODE_PRIVATE);
        return streakPrefs.getInt("consecutive_days", 1);
    }

    private void updateTreeDisplay() {
        int points = progressManager.getPoints();
        int stageNumber = progressManager.getStageNumber();
        String stageName = progressManager.getStageName();
        int progressPercent = progressManager.getProgressPercentage();

        if (binding.tvPointsCount != null) {
            binding.tvPointsCount.setText(getString(R.string.tree_today_points, points));
        }
        if (binding.tvStageTitle != null) {
            binding.tvStageTitle.setText(stageName);
        }
        if (binding.tvStageSubtitle != null) {
            binding.tvStageSubtitle.setText(getString(R.string.tree_stage_level, stageNumber));
        }
        if (binding.tvProgressPercent != null) {
            binding.tvProgressPercent.setText(progressPercent + "%");
        }

        if (binding.tvTreeEmoji != null) {
            switch (stageNumber) {
                case 1: binding.tvTreeEmoji.setText("🌱"); break;
                case 2: binding.tvTreeEmoji.setText("🌿"); break;
                case 3: binding.tvTreeEmoji.setText("🌳"); break;
                default: binding.tvTreeEmoji.setText("🍎🌳"); break;
            }
        }

        highlightCurrentStage(stageNumber);
        updateBadgesVisualState();
    }

    private void updateBadgesVisualState() {
        boolean hasBreathing = profileStore.hasCompletedEventToday(childId, "BREATHING_EXERCISE");
        binding.badgeMotamel.setAlpha(hasBreathing ? 1.0f : 0.35f);

        SharedPreferences streakPrefs = getSharedPreferences("KidsAppStreak_" + childId, Context.MODE_PRIVATE);
        boolean loggedToday = streakPrefs.getBoolean("logged_today", false);
        binding.badgeYawmi.setAlpha(loggedToday ? 1.0f : 0.35f);

        boolean isConsistent = getStreakDays() >= 3;
        binding.badgeMostamer.setAlpha(isConsistent ? 1.0f : 0.35f);
    }

    private void highlightCurrentStage(int currentStage) {
        binding.stage1Icon.setBackgroundColor(Color.parseColor(currentStage >= 1 ? "#E8F5E9" : "#F5F5F5"));
        binding.stage2Icon.setBackgroundColor(Color.parseColor(currentStage >= 2 ? "#E8F5E9" : "#F5F5F5"));
        binding.stage3Icon.setBackgroundColor(Color.parseColor(currentStage >= 3 ? "#E8F5E9" : "#F5F5F5"));
        binding.stage4Icon.setBackgroundColor(Color.parseColor(currentStage >= 4 ? "#E8F5E9" : "#F5F5F5"));

        switch (currentStage) {
            case 1: binding.stage1Icon.setBackgroundColor(Color.parseColor("#3A5A40")); break;
            case 2: binding.stage2Icon.setBackgroundColor(Color.parseColor("#3A5A40")); break;
            case 3: binding.stage3Icon.setBackgroundColor(Color.parseColor("#3A5A40")); break;
            case 4: binding.stage4Icon.setBackgroundColor(Color.parseColor("#3A5A40")); break;
        }
    }

    private void setupStageClickListeners() {
        binding.stage1Icon.setOnClickListener(v -> showStageStatusDialog(1));
        binding.stage2Icon.setOnClickListener(v -> showStageStatusDialog(2));
        binding.stage3Icon.setOnClickListener(v -> showStageStatusDialog(3));
        binding.stage4Icon.setOnClickListener(v -> showStageStatusDialog(4));
    }

    private void showStageStatusDialog(int targetStage) {
        int currentStage = progressManager.getStageNumber();
        String icon, title, desc, statusText, bgColorHex, textColorHex;

        switch (targetStage) {
            case 1: icon = "🌱"; title = getString(R.string.stage_seed_title); desc = getString(R.string.stage_seed_desc); break;
            case 2: icon = "🌿"; title = getString(R.string.stage_sprout_title); desc = getString(R.string.stage_sprout_desc); break;
            case 3: icon = "🌳"; title = getString(R.string.stage_tree_title); desc = getString(R.string.stage_tree_desc); break;
            default: icon = "🍎"; title = getString(R.string.stage_fruit_tree_title); desc = getString(R.string.stage_fruit_tree_desc); break;
        }

        if (targetStage < currentStage) {
            statusText = "تم تحقيق هذه المرحلة ➔"; bgColorHex = "#E8F5E9"; textColorHex = "#2E7D32";
        } else if (targetStage == currentStage) {
            statusText = "أنت في هذه المرحلة حالياً 🎯"; bgColorHex = "#FFF9C4"; textColorHex = "#F57F17";
        } else {
            statusText = "مرحلة قادمة (مغلقة) 🔒"; bgColorHex = "#E0E0E0"; textColorHex = "#616161";
        }

        showDetailDialog(icon, title, desc, statusText, bgColorHex, textColorHex);
    }

    private void setupBadgeClickListeners() {
        binding.badgeMotamel.setOnClickListener(v -> {
            boolean isUnlocked = profileStore.hasCompletedEventToday(childId, "BREATHING_EXERCISE");
            showBadgeStatusDialog("🧠", "وسام المتأمل", "تحصل عليه عند إتمام تمرين التنفس اليومي.", isUnlocked);
        });

        binding.badgeYawmi.setOnClickListener(v -> {
            SharedPreferences streakPrefs = getSharedPreferences("KidsAppStreak_" + childId, Context.MODE_PRIVATE);
            boolean isUnlocked = streakPrefs.getBoolean("logged_today", false);
            showBadgeStatusDialog("⭐", "وسام اليومي", "تحصل عليه بمجرد زيارة شجرة التعافي يومياً.", isUnlocked);
        });

        binding.badgeMostamer.setOnClickListener(v -> {
            boolean isUnlocked = getStreakDays() >= 3;
            String desc = "تحصل عليه عند الاستمرار بدخول التطبيق لمدة 3 أيام متتالية! (أيامك الحالية: " + getStreakDays() + "/3)";
            showBadgeStatusDialog("🔥", "وسام المستمر", desc, isUnlocked);
        });
    }

    private void showBadgeStatusDialog(String icon, String title, String desc, boolean isUnlocked) {
        String statusText = isUnlocked ? "⭐ " + getString(R.string.badge_unlocked_message) : "🔒 لم يتم تحقيق هذا الإنجاز بعد";
        String bgColorHex = isUnlocked ? "#E8F5E9" : "#E0E0E0";
        String textColorHex = isUnlocked ? "#2E7D32" : "#616161";

        showDetailDialog(icon, title, desc, statusText, bgColorHex, textColorHex);
    }

    private void showDetailDialog(String icon, String title, String desc, String statusText, String bgColorHex, String textColorHex) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_badge_detail);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvIcon = dialog.findViewById(R.id.tvDialogIcon);
        TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvDesc = dialog.findViewById(R.id.tvDialogDescription);
        TextView tvStatus = dialog.findViewById(R.id.tvDialogStatusText);
        View statusBox = dialog.findViewById(R.id.layoutStatusBox);
        Button btnClose = dialog.findViewById(R.id.btnDialogClose);

        tvIcon.setText(icon);
        tvTitle.setText(title);
        tvDesc.setText(desc);
        tvStatus.setText(statusText);
        statusBox.setBackgroundColor(Color.parseColor(bgColorHex));
        tvStatus.setTextColor(Color.parseColor(textColorHex));

        btnClose.setText(R.string.close);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showActionsBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        BottomSheetKidsActionsBinding sheetBinding = BottomSheetKidsActionsBinding.inflate(getLayoutInflater());
        dialog.setContentView(sheetBinding.getRoot());

        boolean hasBreathing = profileStore.hasCompletedEventToday(childId, "BREATHING_EXERCISE");
        boolean hasChat = profileStore.hasCompletedEventToday(childId, "CHAT_SESSION");
        boolean hasJournal = profileStore.hasCompletedEventToday(childId, "JOURNAL_ENTRY");

        sheetBinding.btnActionBreath.setVisibility(hasBreathing ? View.GONE : View.VISIBLE);
        sheetBinding.btnActionChat.setVisibility(hasChat ? View.GONE : View.VISIBLE);
        sheetBinding.btnActionJournal.setVisibility(hasJournal ? View.GONE : View.VISIBLE);

        if (hasBreathing && hasChat && hasJournal) {
            sheetBinding.tvTitle.setText("بطل اليوم! 🌟🎉");
            sheetBinding.tvSubTitle.setText("لقد أكملت جميع تحديات اليوم وكسبت كل النقاط!");
        }

        sheetBinding.btnActionBreath.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, KidsBubbleBreathingActivity.class);
            intent.putExtra("CHILD_ID", childId);
            intent.putExtra("CHILD_NAME", childName);
            startActivity(intent);
        });

        sheetBinding.btnActionChat.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, KidsAiChatActivity.class);
            intent.putExtra("CHILD_ID", childId);
            intent.putExtra("CHILD_NAME", childName);
            startActivity(intent);
        });

        sheetBinding.btnActionJournal.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private long getCurrentChildId() {
        long id = getIntent().getLongExtra("CHILD_ID", -1L);
        if (id == -1L) {
            id = getSharedPreferences("KidsApp", MODE_PRIVATE).getLong("current_child_id", System.currentTimeMillis());
        }
        return id;
    }
}