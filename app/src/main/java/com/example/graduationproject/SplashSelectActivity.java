package com.example.graduationproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// استيراد كلاس البايندينج المولد تلقائياً
import com.example.graduationproject.databinding.ActivitySplashSelectBinding;

public class SplashSelectActivity extends AppCompatActivity {

    // 1. تعريف متغير الـ Binding
    private ActivitySplashSelectBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // 2. تهيئة الـ Binding وربطه بالواجهة
        binding = ActivitySplashSelectBinding.inflate(getLayoutInflater());

        // 3. تمرير الجذر (Root View) إلى setContentView بدلاً من الـ layout التقليدي
        setContentView(binding.getRoot());

        // 4. الآن نستخدم binding.main للوصول للـ ConstraintLayout الأساسي
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // مثال لكيفية الوصول لباقي العناصر في الكود لاحقاً بدون findViewById:
        // binding.btnAdults.setOnClickListener(v -> { ... });
        // binding.etName.getText().toString();
    }
}