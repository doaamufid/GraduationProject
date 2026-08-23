package com.example.graduationproject.Kids;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.databinding.ActivityKidsTreeLevelUpBinding;

public class KidsTreeLevelUpActivity extends AppCompatActivity {

    private ActivityKidsTreeLevelUpBinding binding;
    private TreeProgressManager progressManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKidsTreeLevelUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressManager = new TreeProgressManager(this);

        setupUI();

        // إغلاق الشاشة والعودة للشجرة عند الضغط على الزر
        binding.btnClaimReward.setOnClickListener(v -> finish());
    }

    private void setupUI() {
        int stageNumber = progressManager.getStageNumber();
        String stageName = progressManager.getStageName();

        // 1. تحديد شكل الإيموجي ديناميكياً بحسب المرحلة التي وصل لها الطفل
        String stageEmoji;
        switch (stageNumber) {
            case 1: stageEmoji = "🌱"; break;
            case 2: stageEmoji = "🌿"; break;
            case 3: stageEmoji = "🌳"; break;
            default: stageEmoji = "🍎🌳"; break;
        }

        // 2. تحديث إيموجي الشجرة الرئيسي
        if (binding.tvTreeLevelEmoji != null) {
            binding.tvTreeLevelEmoji.setText(stageEmoji);
        }

        if (binding.tvLevelUpTitle != null) {
            binding.tvLevelUpTitle.setText("وصلت إلى مرحلة " + stageName + " 🎉");
        }
    }
}