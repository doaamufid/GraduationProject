package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Bundle;
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
            // 🔥 التعديل هنا: قراءة الاسم من حقل الإدخال etName وحفظه
            String inputName = binding.etName.getText().toString().trim();
            userPrefs.edit().putString("user_name", inputName).apply();

            // حفظ نوع المستخدم وتأكيد انتهاء التشغيل الأول للـ Splash
            userPrefs.edit().putString("user_type", "adult").apply();
            appPrefs.edit().putBoolean("isFirstRun", false).apply();

            navigateToQuotes();
        });

        // 2. عند الضغط على كارد الأطفال
        binding.btnKidsCard.setOnClickListener(v -> {
            // 🔥 التعديل هنا أيضاً للأطفال (مستقبلاً) ليحفظ الاسم المدخل
            String inputName = binding.etName.getText().toString().trim();
            userPrefs.edit().putString("user_name", inputName).apply();

            userPrefs.edit().putString("user_type", "kid").apply();
            appPrefs.edit().putBoolean("isFirstRun", false).apply();

            navigateToQuotes();
        });
    }

    private void navigateToQuotes() {
        Intent intent = new Intent(SplashSelectActivity.this, MindfulnessQuotesActivity.class);
        startActivity(intent);
        finish();
        // مثال لكيفية الوصول لباقي العناصر في الكود لاحقاً بدون findViewById:
        // binding.btnAdults.setOnClickListener(v -> { ... });
        // binding.etName.getText().toString();
        binding.btnAdultsCard.setOnClickListener(v ->
                startActivity(new Intent(SplashSelectActivity.this, MainActivity.class)));

        binding.btnKidsCard.setOnClickListener(v -> openChildProfilesScreen());
    }

    private void openChildProfilesScreen() {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), ChildProfilesActivity.class.getName());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "تعذر فتح شاشة ملفات الأطفال", Toast.LENGTH_SHORT).show();
        }
    }
}
