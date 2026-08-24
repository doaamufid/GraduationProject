package com.example.graduationproject.Kids;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessingActivity extends AppCompatActivity {

    private static final String TAG = "ProcessingActivity";

    // أقصى بعد للصورة قبل ما نبعتها لـ Gemini (تصغير يخفف حجم الطلب ويسرّع الرد)
    private static final int MAX_IMAGE_DIMENSION = 1024;

    private String photoUriString;
    private final ExecutorService bitmapExecutor = Executors.newSingleThreadExecutor();
    private GeminiService geminiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        geminiService = new GeminiService();

        ImageView imgDrawingThumb = findViewById(R.id.imgDrawingThumb);
        photoUriString = getIntent().getStringExtra("photo_uri");
        if (photoUriString != null) {
            imgDrawingThumb.setImageURI(Uri.parse(photoUriString));
        }

        startAnalysis();
    }

    private void startAnalysis() {
        if (photoUriString == null) {
            // ما في صورة أصلاً، ما فيه داعي نكمل - نرجع فيدباك افتراضي مباشرة
            goToResult(getFallbackFeedback());
            return;
        }

        bitmapExecutor.execute(() -> {
            Bitmap bitmap = loadBitmapFromUri(Uri.parse(photoUriString));

            if (bitmap == null) {
                runOnUiThread(() -> goToResult(getFallbackFeedback()));
                return;
            }

            geminiService.analyzeDrawing(bitmap, new GeminiService.GeminiCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> goToResult(message));
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "فشل تحليل الرسمة: " + errorMessage);
                    runOnUiThread(() -> goToResult(getFallbackFeedback()));
                }
            });
        });
    }

    /**
     * يحمّل الصورة من الـ Uri ويصغّرها حتى ما تكون كبيرة أكتر من اللازم
     * قبل ما نبعتها لـ Gemini
     */
    private Bitmap loadBitmapFromUri(Uri uri) {
        try {
            Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            return resizeIfNeeded(original);
        } catch (IOException e) {
            Log.e(TAG, "تعذّر تحميل الصورة: " + e.getMessage(), e);
            return null;
        }
    }

    private Bitmap resizeIfNeeded(Bitmap original) {
        int width = original.getWidth();
        int height = original.getHeight();

        if (width <= MAX_IMAGE_DIMENSION && height <= MAX_IMAGE_DIMENSION) {
            return original;
        }

        float scale = Math.min(
                (float) MAX_IMAGE_DIMENSION / width,
                (float) MAX_IMAGE_DIMENSION / height
        );

        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    }

    private String getFallbackFeedback() {
        return "رسمتك حلوة كتير يا بطل! 🌟 أنا فخورة فيك ومبسوطة إنك شاركتني إياها 💛";
    }

    private void goToResult(String feedbackText) {
        Intent intent = new Intent(ProcessingActivity.this, ResultActivity.class);
        if (photoUriString != null) {
            intent.putExtra("photo_uri", photoUriString);
        }
        intent.putExtra("feedback_text", feedbackText);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmapExecutor.shutdown();
    }
}