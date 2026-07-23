package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityOnBoarding1Binding;

public class OnBoardingActivity1 extends AppCompatActivity {

    private ActivityOnBoarding1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل الـ View Binding
        binding = ActivityOnBoarding1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // عند الضغط على زر "التالي" -> ننتقل إلى الشاشة الثانية
        binding.btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(OnBoardingActivity1.this, OnBoardingActivity2.class);
            startActivity(intent);
        });

        // عند الضغط على "تخطي" -> ننهي الـ OnBoarding وننتقل لصفحة الاختيار مباشرة
        binding.tvSkip.setOnClickListener(v -> skipOnBoarding());
    }

    private void skipOnBoarding() {
        // حفظ أن المستخدم أتم/تخطى واجهات الترحيب لمرة واحدة
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        preferences.edit().putBoolean("isFirstRun", false).apply();

        // الانتقال لواجهة الاختيار
        Intent intent = new Intent(OnBoardingActivity1.this, SplashSelectActivity.class);
        startActivity(intent);
        finish(); // إغلاق هذه الشاشة نهائياً
    }
}