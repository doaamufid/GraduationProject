package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;

/**
 * شاشة "أحسنت يا بطل!" اللي بتظهر بعد ما يحفظ الطفل تسجيله بنجاح.
 */
public class CelebrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celebration);

        findViewById(R.id.anotherWordButton).setOnClickListener(v -> {
            // نرجع لشاشة كلمة الأسبوع من جديد (ونمسح الأكتفتيز اللي فوقها)
            Intent intent = new Intent(CelebrationActivity.this, WordOfWeekActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.seeMyWordsButton).setOnClickListener(v -> {
            Intent intent = new Intent(CelebrationActivity.this, MyWordsActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
