package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityOnBoarding4Binding;

public class OnBoardingActivity4 extends AppCompatActivity {

    private ActivityOnBoarding4Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoarding4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // في الواجهة الرابعة، زر "التالي" يعني إنهاء الـ OnBoarding والبدء بالتطبيق
//        binding.btnStart.setOnClickListener(v -> {
//            // حفظ الحالة لعدم إظهار الواجهات مجدداً
//            SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
//            preferences.edit().putBoolean("isFirstRun", false).apply();
//
//            // الانتقال لصفحة الاختيار
//            Intent intent = new Intent(OnBoardingActivity4.this, SplashSelectActivity.class);
//            startActivity(intent);
//            finish();
//        });
        // داخل OnBoardingActivity4.java عند الضغط على زر البدء/التالي
        binding.btnStart.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            preferences.edit().putBoolean("isFirstRun", false).apply();

            Intent intent = new Intent(OnBoardingActivity4.this, SplashSelectActivity.class);
            startActivity(intent);
            finish(); // <--- هذا السطر السحري يغلق الشاشة الرابعة ويحذفها من الذاكرة!
        });

        // لو كان هناك زر تخطي في التصميم الرابع أيضاً (اختياري)
        if (binding.tvSkip != null) {
            binding.tvSkip.setVisibility(android.view.View.GONE); // إخفاؤه لأنه لا معنى له في الشاشة الأخيرة
        }
    }
}