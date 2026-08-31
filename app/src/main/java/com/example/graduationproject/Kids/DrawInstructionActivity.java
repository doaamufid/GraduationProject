package com.example.graduationproject.Kids;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.models.ChildProfile;

import java.util.List;

public class DrawInstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_draw_instruction);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FBF6EC"));
        window.setNavigationBarColor(Color.parseColor("#FBF6EC"));

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }

        TextView tvBearAvatar = findViewById(R.id.tvBearAvatar);
        loadChildAvatarFromDatabase(tvBearAvatar);

        Button btnTakePhotoNow = findViewById(R.id.btnTakePhotoNow);
        btnTakePhotoNow.setOnClickListener(v -> {
            Intent intent = new Intent(DrawInstructionActivity.this, UploadPhotoActivity.class);
            // 🌟 تمرير CHILD_ID للشاشة التالية
            intent.putExtra("CHILD_ID", getChildId());
            startActivity(intent);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        View btnStart = findViewById(R.id.btnStart);
        if (btnStart != null) {
            btnStart.setVisibility(View.VISIBLE);
            btnStart.setOnClickListener(v -> {
                // Already here, maybe just scroll down or provide feedback
                android.widget.Toast.makeText(this, "ابدأ الرسم الآن 🎨", android.widget.Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void loadChildAvatarFromDatabase(TextView avatarTextView) {
        long childId = getChildId();

        if (childId != -1L) {
            ChildProfileStore store = new ChildProfileStore(this);
            try {
                List<ChildProfile> profiles = store.getProfiles();
                for (ChildProfile profile : profiles) {
                    if (profile.getId() == childId) {
                        String avatar = profile.getAvatar();
                        if (avatar != null && !avatar.trim().isEmpty()) {
                            avatarTextView.setText(avatar);
                        } else {
                            avatarTextView.setText("🐻");
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("DrawInstruction", "Error loading avatar: " + e.getMessage());
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
        return id;
    }
}