package com.example.graduationproject.Kids;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.example.graduationproject.data.LocalStorageHelper;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ImageView imgDrawingSmall = findViewById(R.id.imgDrawingSmall);
        String uriString = getIntent().getStringExtra("photo_uri");
        Uri photoUri = uriString != null ? Uri.parse(uriString) : null;
        if (photoUri != null) {
            imgDrawingSmall.setImageURI(photoUri);
        }

        TextView tvFeedback = findViewById(R.id.tvFeedback);
        String feedbackText = getIntent().getStringExtra("feedback_text");
        if (feedbackText == null || feedbackText.trim().isEmpty()) {
            feedbackText = "رسمتك حلوة كتير يا بطل! 🌟 أنا فخورة فيك ومبسوطة إنك شاركتني إياها 💛";
        }
        tvFeedback.setText(feedbackText);

        // خزّن الصورة + التحليل محليًا كسجل جديد بالقائمة (حتى تنعرض لاحقًا بشاشة المعرض)
        LocalStorageHelper.saveResult(this, photoUri, feedbackText);

        // زر Play: ينقل المستخدم لشاشة المعرض اللي فيها كل الرسومات مع تحليلاتها
        FrameLayout btnPlayAudio = findViewById(R.id.btnPlayAudio);
        btnPlayAudio.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, GalleryActivity.class);
            startActivity(intent);
        });

        Button btnDrawMore = findViewById(R.id.btnDrawMore);
        btnDrawMore.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, DrawInstructionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        Button btnFinish = findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(v -> finish());
    }
}