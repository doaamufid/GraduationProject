package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// استيراد كلاس البايندينج المولد تلقائياً
import com.example.graduationproject.Kids.ChildProfilesActivity;
import com.example.graduationproject.databinding.ActivitySplashSelectBinding;

public class SplashSelectActivity extends AppCompatActivity {

    private ActivitySplashSelectBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // ضبط لون شريط الحالة (Status Bar) ليتناسق مع الخلفية
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#D4E8F5"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        binding = ActivitySplashSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // هندلة زر الرجوع الخاص بالنظام للخروج من التطبيق بنظافة
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });

        // تحضير ملفات الـ SharedPreferences
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // 1. عند الضغط على كارد البالغين
        binding.btnAdultsCard.setOnClickListener(v -> {
            String inputName = binding.etName.getText().toString().trim();
            userPrefs.edit().putString("user_name", inputName).apply();

            userPrefs.edit().putString("user_type", "adult").apply();
            appPrefs.edit().putBoolean("isFirstRun", false).apply();

            navigateToQuotes();
        });

        // 2. عند الضغط على كارد الأطفال
        binding.btnKidsCard.setOnClickListener(v -> {
            String inputName = binding.etName.getText().toString().trim();
            userPrefs.edit().putString("user_name", inputName).apply();

            userPrefs.edit().putString("user_type", "kid").apply();
            appPrefs.edit().putBoolean("isFirstRun", false).apply();

            openChildProfilesScreen();
        });
    }

    private void navigateToQuotes() {
        Intent intent = new Intent(SplashSelectActivity.this, MindfulnessQuotesActivity.class);
        startActivity(intent);
        finish();
    }

    private void openChildProfilesScreen() {
        Intent intent = new Intent(this, ChildProfilesActivity.class);
        startActivity(intent);
        finish();
    }
}
