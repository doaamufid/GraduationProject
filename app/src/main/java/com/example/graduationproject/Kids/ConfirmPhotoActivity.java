package com.example.graduationproject.Kids;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.models.ChildProfile;

import java.util.List;

public class ConfirmPhotoActivity extends AppCompatActivity {

    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_photo);

        TextView tvChildAvatar = findViewById(R.id.tv_child_avatar);
        ImageView imgDrawing = findViewById(R.id.imgDrawing);
        Button btnSendToNoor = findViewById(R.id.btnSendToNoor);
        Button btnRetake = findViewById(R.id.btnRetake);

        // 🌟 قراءة أفاتار الطفل مباشرة من قاعدة البيانات
        loadChildAvatarFromDatabase(tvChildAvatar);

        String uriString = getIntent().getStringExtra("photo_uri");
        if (uriString != null) {
            photoUri = Uri.parse(uriString);
            imgDrawing.setImageURI(photoUri);
        }

        btnSendToNoor.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmPhotoActivity.this, ProcessingActivity.class);
            if (photoUri != null) {
                intent.putExtra("photo_uri", photoUri.toString());
            }
            startActivity(intent);
            finish();
        });

        btnRetake.setOnClickListener(v -> finish());
    }

    /**
     * جلب شخصية الطفل (الأفاتار) مباشرة من قاعدة البيانات المشفرة
     */
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
                Log.e("ConfirmPhotoActivity", "Error loading avatar from database: " + e.getMessage());
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