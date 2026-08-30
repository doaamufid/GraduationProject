package com.example.graduationproject.Kids;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.data.LocalStorageHelper;
import com.example.graduationproject.models.ChildProfile;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private long childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // 1. جلب ID الطفل الممرر (من كودك)
        childId = getChildId();

        // 2. عرض الصورة المأخوذة
        ImageView imgDrawingSmall = findViewById(R.id.imgDrawingSmall);
        String uriString = getIntent().getStringExtra("photo_uri");
        Uri photoUri = uriString != null ? Uri.parse(uriString) : null;
        if (photoUri != null) {
            imgDrawingSmall.setImageURI(photoUri);
        }

        // 3. عرض النص القادم من تحليل الذكاء الاصطناعي (من كود صديقتك)
        TextView tvFeedback = findViewById(R.id.tvFeedback);
        String feedbackText = getIntent().getStringExtra("feedback_text");
        if (feedbackText == null || feedbackText.trim().isEmpty()) {
            feedbackText = "رسمتك حلوة كتير يا بطل! 🌟 أنا فخورة فيك ومبسوطة إنك شاركتني إياها 💛";
        }
        if (tvFeedback != null) {
            tvFeedback.setText(feedbackText);
        }

        // 4. حفظ الرسمة والتحليل محلياً كسجل في المعرض (من كود صديقتك)
        LocalStorageHelper.saveResult(this, photoUri, feedbackText);

        // 🌟 عرض الأفاتار الخاص بكِ
        loadChildAvatar();

        // زر الانتقال إلى شاشة معرض الرسومات (GalleryActivity)
        FrameLayout btnPlayAudio = findViewById(R.id.btnPlayAudio);
        btnPlayAudio.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, GalleryActivity.class);
            intent.putExtra("CHILD_ID", childId);
            startActivity(intent);
        });

        // زر العودة للرسم من جديد
        Button btnDrawMore = findViewById(R.id.btnDrawMore);
        btnDrawMore.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, DrawInstructionActivity.class);
            intent.putExtra("CHILD_ID", childId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // زر الإنهاء (إضافة نقاط الشجرة وتسجيل الإنجاز من كودك)
        Button btnFinish = findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(v -> {
            awardPointsAndRecordEvent();
            finish();
        });
    }

    // 🌟 إضافة النقاط وتسجيل الحدث (من كودك)
    private void awardPointsAndRecordEvent() {
        TreeProgressManager progressManager = new TreeProgressManager(this, childId);
        progressManager.addPoints(10);

        ChildProfileStore profileStore = new ChildProfileStore(this);
        try {
            profileStore.recordEvent(childId, "JOURNAL_ENTRY");
        } catch (Exception ignored) {
        } finally {
            profileStore.close();
        }
    }

    // 🌟 جلب رقم الطفل الآمن (من كودك)
    private long getChildId() {
        long id = getIntent().getLongExtra("CHILD_ID", -1L);
        if (id == -1L) {
            id = getSharedPreferences("KidsApp", MODE_PRIVATE).getLong("current_child_id", -1L);
        }
        if (id == -1L) {
            id = getSharedPreferences("KidsAppPrefs", MODE_PRIVATE).getLong("active_child_id", -1L);
        }
        return (id == -1L) ? 1L : id;
    }

    // 🌟 تحميل الأفاتار الخاص بكِ
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
}