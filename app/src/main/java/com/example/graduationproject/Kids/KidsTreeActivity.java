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
import android.widget.Toast;

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

        // 2. ربط ومدير التقدم بالطفل الموحد بالمعرف وليس الاسم لضمان الخصوصية والتفرد
        progressManager = new TreeProgressManager(this, String.valueOf(childId));

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

        // جلب الـ ID بضمان عدم كونه -1
        this.childId = getCurrentChildId();

        // إعادة بناء مدير النقاط بالمعرف الصحيح وتحديث الواجهة
        this.progressManager = new TreeProgressManager(this, String.valueOf(this.childId));

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
        // 1. ضمان الربط بنفس معرف الطفل الحالي (childId)
        if (progressManager == null) {
            progressManager = new TreeProgressManager(this, String.valueOf(childId));
        }

        int points = progressManager.getPoints();
        int stageNumber = progressManager.getStageNumber();
        String stageName = progressManager.getStageName();
        int progressPercent = progressManager.getProgressPercentage();

        // 2. تحديث النصوص والعدادات
        if (binding.tvPointsCount != null) {
            binding.tvPointsCount.setText("نقاط اليوم: " + points);
        }
        if (binding.tvStageTitle != null) {
            binding.tvStageTitle.setText(stageName);
        }
        if (binding.tvStageSubtitle != null) {
            binding.tvStageSubtitle.setText("المرحلة " + stageNumber + " من 4");
        }
        if (binding.tvProgressPercent != null) {
            binding.tvProgressPercent.setText("🌱 " + progressPercent + "% نمو " + stageName);
        }

        // 3. تحديث أيقونة الشجرة الرئيسية حسب المرحلة
        if (binding.tvTreeEmoji != null) {
            switch (stageNumber) {
                case 1: binding.tvTreeEmoji.setText("🌱"); break;
                case 2: binding.tvTreeEmoji.setText("🌿"); break;
                case 3: binding.tvTreeEmoji.setText("🌳"); break;
                default: binding.tvTreeEmoji.setText("🍎"); break;
            }
        }

        // 4. تحديث مؤشر المراحل والأوسمة
        highlightCurrentStage(stageNumber);
        updateBadgesVisualState();
    }
    private void updateBadgesVisualState() {
        // 1. وسام المُتأمل (تمرين التنفس)
        boolean hasBreathing = profileStore.hasCompletedEventToday(childId, "BREATHING_EXERCISE");
        applyBadgeStyle(binding.badgeMotamel, hasBreathing);

        // 2. وسام اليومي (تسجيل الدخول اليومي)
        SharedPreferences streakPrefs = getSharedPreferences("KidsAppStreak_" + childId, Context.MODE_PRIVATE);
        boolean loggedToday = streakPrefs.getBoolean("logged_today", false);
        applyBadgeStyle(binding.badgeYawmi, loggedToday);

        // 3. وسام المستمر (استمرار 3 أيام)
        boolean isConsistent = getStreakDays() >= 3;
        applyBadgeStyle(binding.badgeMostamer, isConsistent);

        // 4. وسام صديق نور (محادثة الذكاء الاصطناعي اليومية)
        boolean hasChat = profileStore.hasCompletedEventToday(childId, "CHAT_SESSION");
        applyBadgeStyle(binding.badgeFriend, hasChat);
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

        // 1. وسام مُتأمل (تمرين التنفس)
        binding.badgeMotamel.setOnClickListener(v -> {
            boolean isUnlocked = profileStore.hasCompletedEventToday(childId, "BREATHING_EXERCISE");
            String desc = isUnlocked
                    ? "رائع جداً! لقد أتممت تمرين التنفس بنجاح اليوم وهدأت عقلك 🌱"
                    : "أكمل تمرين التنفس اليوم للحصول على هذا الوسام وتهدئة أعصابك! 🧘";
            showBadgeStatusDialog("🧠", "وسام المُتأمل", desc, isUnlocked);
        });

        // 2. وسام يومي (تسجيل الدخول اليومي)
        binding.badgeYawmi.setOnClickListener(v -> {
            SharedPreferences streakPrefs = getSharedPreferences("KidsAppStreak_" + childId, MODE_PRIVATE);
            boolean isUnlocked = streakPrefs.getBoolean("logged_today", false);
            String desc = isUnlocked
                    ? "أحسنت! لقد سجلت دخولك اليوم واعتنيت بشجرتك ⭐"
                    : "سجل دخولك إلى التطبيق يومياً للحصول على هذا الوسام! ⭐";
            showBadgeStatusDialog("⭐", "وسام اليومي", desc, isUnlocked);
        });

        // 3. وسام المستمر (استمرار 3 أيام)
        binding.badgeMostamer.setOnClickListener(v -> {
            int streakDays = getStreakDays();
            boolean isUnlocked = streakDays >= 3;
            String desc = isUnlocked
                    ? "بطل! لقد استمررت في فتح التطبيق لمدة 3 أيام متتالية أو أكثر 🔥"
                    : "تحصل عليه عند الاستمرار بدخول التطبيق لمدة 3 أيام متتالية! (أيامك الحالية: " + streakDays + "/3)";
            showBadgeStatusDialog("🔥", "وسام المستمر", desc, isUnlocked);
        });

        // 4. وسام صديق نور (المحادثة مع الذكاء الاصطناعي)
        binding.badgeFriend.setOnClickListener(v -> {
            boolean isUnlocked = profileStore.hasCompletedEventToday(childId, "CHAT_SESSION");
            String desc = isUnlocked
                    ? "مذهل! لقد تحدثت مع صديقك نور اليوم وشاركته مشاعرك 🐻"
                    : "تحدث مع صديقك الكرتوني نور اليوم للحصول على هذا الوسام! 🐻";
            showBadgeStatusDialog("🐻", "وسام صديق نور", desc, isUnlocked);
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
            Intent intent = new Intent(this, KidsAiCompanionActivity.class);
            intent.putExtra("CHILD_ID", childId);
            intent.putExtra("CHILD_NAME", childName);
            startActivity(intent);
        });

        sheetBinding.btnActionJournal.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private long getCurrentChildId() {
        // 1. القراءة من الـ Intent إذا كان موجوداً
        long id = getIntent().getLongExtra("CHILD_ID", -1L);

        // 2. إذا لم يوجد في الـ Intent، نفحص الملفات الاحتياطية
        if (id == -1L) {
            id = getSharedPreferences("KidsApp", MODE_PRIVATE).getLong("current_child_id", -1L);
        }
        if (id == -1L) {
            id = getSharedPreferences("KidsAppPrefs", MODE_PRIVATE).getLong("active_child_id", -1L);
        }
        return (id == -1L) ? 1L : id; // إذا كان فارغاً تماماً يعتمد 1 كمعرف افتراضي
    }
    private void applyBadgeStyle(View badgeLayout, boolean isUnlocked) {
        if (badgeLayout == null) return;

        if (isUnlocked) {
            badgeLayout.setAlpha(1.0f); // وضوح كامل
            badgeLayout.setBackgroundResource(R.drawable.bg_badge_unlocked); // خلفية خضراء محددة
        } else {
            badgeLayout.setAlpha(0.4f); // شفافية (رمادي/خافت)
            badgeLayout.setBackgroundResource(R.drawable.bg_badge_locked);   // خلفية مغلقة
        }
    }
    private void saveBreathingAchievement() {
        long currentChildId = getCurrentChildId();

        // إذا كان المعرف سالب 1 (أي لم يُمرر عبر Intent)، نجلب الطفل النشط حالياً
        if (currentChildId == -1L) {
            currentChildId = getSharedPreferences("KidsAppPrefs", MODE_PRIVATE).getLong("active_child_id", 1L);
        }

        // 1. حفظ حدث إتمام التنفس في قاعدة البيانات
        ChildProfileStore store = new ChildProfileStore(this);
        store.addCompletedEvent(currentChildId, "BREATHING_EXERCISE");

        // 2. إضافة النقاط لشجرة الطفل
        TreeProgressManager progressManager = new TreeProgressManager(this, String.valueOf(currentChildId));
        progressManager.addPoints(15);
    }

}