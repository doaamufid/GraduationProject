package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());

        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // سطر مؤقت للتجريب: يمسح نوع المستخدم في كل مرة يفتح التطبيق للتأكد من ظهور شاشة الاختيار
        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();

        // بعد 3 ثواني، نتخذ القرار بناءً على اختيار المستخدم المسبق
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

            // قراءة نوع المستخدم الحالي (إذا كان فارغاً يعني أنه لم يحدد فئته بعد)
            String userType = userPrefs.getString("user_type", null);

            Intent intent;
            if (userType == null) {
                // توجيه المستخدم لشاشة اختيار الفئة (بالغ/طفل) فوراً بعد السبلاش
                intent = new Intent(SplashActivity.this, SplashSelectActivity.class);
            } else {
                // مستخدم مسجل سابقاً ومحدد فئته -> توجيهه فوراً للرئيسية
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish(); // إغلاق شاشة الـ Splash نهائياً

        }, 3000);
    }
}