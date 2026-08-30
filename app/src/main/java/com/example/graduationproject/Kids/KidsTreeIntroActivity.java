package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityKidsTreeIntroBinding;
import com.example.graduationproject.models.ChildProfile;

import java.util.List;

public class KidsTreeIntroActivity extends AppCompatActivity {

    private ActivityKidsTreeIntroBinding binding;
    private long childId;
    private String childName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidx.activity.EdgeToEdge.enable(this);
        binding = ActivityKidsTreeIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ضبط ألوان شريط النظام
        Window window = getWindow();
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.parseColor("#F4F8F3"));
        window.setNavigationBarColor(android.graphics.Color.parseColor("#F4F8F3"));

        androidx.core.view.WindowInsetsControllerCompat controller = androidx.core.view.WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }

        // 1. جلب معرف الطفل الحالي
        childId = getChildId();

        // 2. عرض الأفاتار الخاص بالطفل فوق النبتة
        loadChildAvatar();

        // 3. زر الانتقال لشاشة الشجرة مع تمرير البيانات
        binding.btnGoToTree.setOnClickListener(v -> {
            Intent intent = new Intent(KidsTreeIntroActivity.this, KidsTreeActivity.class);
            intent.putExtra("CHILD_ID", childId);
            intent.putExtra("CHILD_NAME", childName);
            startActivity(intent);
            finish();
        });
    }

    private void loadChildAvatar() {
        if (childId != -1L) {
            ChildProfileStore store = new ChildProfileStore(this);
            try {
                List<ChildProfile> profiles = store.getProfiles();
                for (ChildProfile profile : profiles) {
                    if (profile.getId() == childId) {
                        this.childName = profile.getName();
                        if (profile.getAvatar() != null && !profile.getAvatar().trim().isEmpty()) {
                            binding.tvFoxMascot.setText(profile.getAvatar() + "\n🌱");
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
}