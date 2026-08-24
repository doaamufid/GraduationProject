package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;

/**
 * شاشة "أحسنت يا بطل!" اللي بتظهر بعد ما يحفظ الطفل تسجيله بنجاح.
 * النص المعروض هلأ فيدباك حقيقي جاي من تحليل Gemini للتسجيل الصوتي،
 * منستقبله عبر EXTRA_FEEDBACK بدل الجملة الثابتة القديمة.
 */
public class CelebrationActivity extends AppCompatActivity {

    public static final String EXTRA_FEEDBACK = "extra_feedback";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celebration);

        String feedback = getIntent().getStringExtra(EXTRA_FEEDBACK);

        // لازم يكون في TextView بالـ XML بمعرف id="feedbackText" مكان نص "أحسنت يا بطل" الثابت
        TextView feedbackText = findViewById(R.id.feedbackText);
        if (feedbackText != null && feedback != null && !feedback.isEmpty()) {
            feedbackText.setText(feedback);
        }

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