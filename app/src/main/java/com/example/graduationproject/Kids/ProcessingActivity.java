package com.example.graduationproject.Kids;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ActiveChildManager;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.models.ChildProfile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessingActivity extends AppCompatActivity {

    private static final String TAG = "ProcessingActivity";
    private static final int MAX_IMAGE_DIMENSION = 1024;

    private String photoUriString;
    private long childId;
    private final ExecutorService bitmapExecutor = Executors.newSingleThreadExecutor();
    private GeminiService geminiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        geminiService = new GeminiService();
        childId = getChildId();

        ImageView imgDrawingThumb = findViewById(R.id.imgDrawingThumb);
        photoUriString = getIntent().getStringExtra("photo_uri");
        if (photoUriString != null) {
            imgDrawingThumb.setImageURI(Uri.parse(photoUriString));
        }

        // 🌟 عرض أفاتار الطفل الخاص بكِ
        loadChildAvatar();

        // بدء تحليل الرسمة حقيقياً عبر الذكاء الاصطناعي
        startAnalysis();
    }

    private void startAnalysis() {
        if (photoUriString == null) {
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

    private void goToResult(String feedbackText) {
        // إضافة النجمة للطفل عند إتمام النشاط
        long currentChildId = ActiveChildManager.getActiveChildId(this);
        if (currentChildId != ActiveChildManager.NO_ACTIVE_CHILD) {
            new ChildProfileStore(this).addStar(currentChildId);
        }

        Intent intent = new Intent(ProcessingActivity.this, ResultActivity.class);
        if (photoUriString != null) {
            intent.putExtra("photo_uri", photoUriString);
        }
        intent.putExtra("feedback_text", feedbackText);
        intent.putExtra("CHILD_ID", childId);
        startActivity(intent);
        finish();
    }

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

    // 🌟 دالة جلب الأفاتار الخاصة بكِ
    private void loadChildAvatar() {
        ChildProfileStore store = new ChildProfileStore(this);
        try {
            List<ChildProfile> profiles = store.getProfiles();
            for (ChildProfile profile : profiles) {
                if (profile.getId() == childId) {
                    String avatar = profile.getAvatar();
                    TextView tvAvatar = findViewById(R.id.tvMascotAvatar);
                    if (tvAvatar != null && avatar != null && !avatar.trim().isEmpty()) {
                        tvAvatar.setText(avatar);
                    }
                    break;
                }
            }
        } catch (Exception ignored) {
        } finally {
            store.close();
        }
    }

    private long getChildId() {
        long id = getIntent().getLongExtra("CHILD_ID", -1L);
        return (id == -1L) ? 1L : id;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmapExecutor.shutdown();
    }
}