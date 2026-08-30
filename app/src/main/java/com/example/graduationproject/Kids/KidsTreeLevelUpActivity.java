package com.example.graduationproject.Kids;

import android.os.Bundle;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityKidsTreeLevelUpBinding;

public class KidsTreeLevelUpActivity extends AppCompatActivity {

    private ActivityKidsTreeLevelUpBinding binding;
    private TreeProgressManager progressManager;
    private long childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidx.activity.EdgeToEdge.enable(this);
        binding = ActivityKidsTreeLevelUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // جلب رقم الطفل وحجم إنجازه



        binding.btnClaimReward.setOnClickListener(v -> finish());

        // ضبط ألوان شريط الوضع العلوي والسفلي لتناسب الخلفية الخضراء
        Window window = getWindow();
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.parseColor("#3A5A40"));
        window.setNavigationBarColor(android.graphics.Color.parseColor("#3A5A40"));

        childId = getChildId();
        progressManager = new TreeProgressManager(this, childId);

        setupUI();

        // إغلاق الشاشة والعودة للشجرة عند الضغط على الزر
        binding.btnClaimReward.setOnClickListener(v -> finish());
    }

    private void setupUI() {
        int stageNumber = progressManager.getStageNumber();
        String stageName = progressManager.getStageName();

        // 1. تحديد شكل الإيموجي ديناميكياً بحسب المرحلة الحالية التي وصل لها الطفل
        String stageEmoji;
        switch (stageNumber) {
            case 1: stageEmoji = "🌱"; break;
            case 2: stageEmoji = "🌿"; break;
            case 3: stageEmoji = "🌳"; break;
            default: stageEmoji = "🍎🌳"; break;
        }

        // 2. تحديث النصوص والإيموجي
        if (binding.tvTreeLevelEmoji != null) {
            binding.tvTreeLevelEmoji.setText(stageEmoji);
        }

        if (binding.tvLevelUpTitle != null) {
            binding.tvLevelUpTitle.setText("🎉 مبروك! كَبُرت شجرتك 🎉");
        }

        if (binding.tvLevelUpSubtitle != null) {
            binding.tvLevelUpSubtitle.setText("وصلت الآن إلى " + stageName);
        }
    }

    private long getChildId() {
        long id = getIntent().getLongExtra("CHILD_ID", -1L);
        if (id == -1L) {
            id = getSharedPreferences("KidsApp", MODE_PRIVATE).getLong("current_child_id", -1L);
        }
        if (id == -1L) {
            id = getSharedPreferences("KidsAppPrefs", MODE_PRIVATE).getLong("active_child_id", -1L);
        }
        return (id == -1L) ? 1L : id;
    }
}