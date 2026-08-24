package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityNewChildProfileBinding;

public class NewChildProfileActivity extends AppCompatActivity {

    private static final String[] BOY_AVATARS = {"🦁", "🦊", "🐻", "🐼", "🐵", "🐯", "🐨"};
    private static final String[] GIRL_AVATARS = {"🦄", "🐰", "🐱", "🐥", "🦋", "🌸", "👑"};

    private ActivityNewChildProfileBinding binding;
    private ChildProfileStore childProfileStore;
    private int selectedAge = -1;
    private String selectedGender = "";
    private String selectedAvatar = "";

    private TextView lastSelectedAgeView = null;
    private TextView lastSelectedAvatarView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNewChildProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);

        childProfileStore = new ChildProfileStore(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAgePicker();
        setupGenderButtons();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnStart.setOnClickListener(v -> saveProfileAndFinish());

        binding.etChildName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateStartState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupAgePicker() {
        binding.rvAges.removeAllViews();
        for (int i = 3; i <= 12; i++) {
            final int age = i;
            TextView tvAge = new TextView(this);
            tvAge.setText(String.valueOf(age));
            tvAge.setTextSize(16);
            tvAge.setTextColor(ContextCompat.getColor(this, R.color.black));
            tvAge.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, 120);
            params.setMargins(12, 0, 12, 0);
            tvAge.setLayoutParams(params);
            tvAge.setBackgroundResource(R.drawable.bg_child_age_default);

            tvAge.setOnClickListener(v -> {
                if (lastSelectedAgeView != null) {
                    lastSelectedAgeView.setBackgroundResource(R.drawable.bg_child_age_default);
                }
                tvAge.setBackgroundResource(R.drawable.bg_child_age_selected);
                lastSelectedAgeView = tvAge;
                selectedAge = age;
                updateStartState();
            });

            binding.rvAges.addView(tvAge);
        }
    }

    private void setupGenderButtons() {
        binding.btnGenderBoy.setOnClickListener(v -> selectGender("boy"));
        binding.btnGenderGirl.setOnClickListener(v -> selectGender("girl"));
    }

    private void selectGender(String gender) {
        selectedGender = gender;
        selectedAvatar = "";
        lastSelectedAvatarView = null;

        if ("boy".equals(gender)) {
            binding.btnGenderBoy.setBackgroundResource(R.drawable.bg_child_age_selected);
            binding.btnGenderGirl.setBackgroundResource(R.drawable.bg_child_age_default);
            setupAvatarPicker(BOY_AVATARS);
        } else {
            binding.btnGenderGirl.setBackgroundResource(R.drawable.bg_child_age_selected);
            binding.btnGenderBoy.setBackgroundResource(R.drawable.bg_child_age_default);
            setupAvatarPicker(GIRL_AVATARS);
        }

        binding.tvAvatarLabel.setVisibility(View.VISIBLE);
        binding.avatarScroll.setVisibility(View.VISIBLE);

        updateStartState();
    }

    private void setupAvatarPicker(String[] avatars) {
        binding.avatarContainer.removeAllViews();
        for (String avatar : avatars) {
            TextView tvAvatar = new TextView(this);
            tvAvatar.setText(avatar);
            tvAvatar.setTextSize(24);
            tvAvatar.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(130, 130);
            params.setMargins(10, 0, 10, 0);
            tvAvatar.setLayoutParams(params);
            tvAvatar.setBackgroundResource(R.drawable.bg_child_age_default);

            tvAvatar.setOnClickListener(v -> {
                if (lastSelectedAvatarView != null) {
                    lastSelectedAvatarView.setBackgroundResource(R.drawable.bg_child_age_default);
                }
                tvAvatar.setBackgroundResource(R.drawable.bg_child_age_selected);
                lastSelectedAvatarView = tvAvatar;
                selectedAvatar = avatar;
                updateStartState();
            });

            binding.avatarContainer.addView(tvAvatar);
        }
    }

    private void updateStartState() {
        boolean canStart = selectedAge >= 3
                && !selectedGender.isEmpty()
                && !selectedAvatar.isEmpty()
                && !binding.etChildName.getText().toString().trim().isEmpty();

        binding.btnStart.setEnabled(canStart);
        binding.btnStart.setBackgroundResource(canStart ? R.drawable.bg_child_start_enabled : R.drawable.bg_child_start_disabled);

        if (canStart) {
            binding.cardInfoBanner.setVisibility(View.VISIBLE);
            String name = binding.etChildName.getText().toString().trim();
            binding.tvBannerEmoji.setText(selectedAvatar);
            binding.tvBannerTitle.setText("أهلاً بك يا " + name + " في عالم نور!");
        } else {
            binding.cardInfoBanner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        if (childProfileStore != null) {
            childProfileStore.close();
        }
        super.onDestroy();
    }

    private void saveProfileAndFinish() {
        String name = binding.etChildName.getText().toString().trim();
        if (name.isEmpty() || selectedAge < 3 || selectedGender.isEmpty() || selectedAvatar.isEmpty()) {
            Toast.makeText(this, "يرجى إكمال جميع البيانات واختيار شخصيتك", Toast.LENGTH_SHORT).show();
            return;
        }

        childProfileStore.addProfile(name, selectedAge, selectedGender, selectedAvatar);

        Intent intent = new Intent(NewChildProfileActivity.this, KidsAiChatActivity.class);
        intent.putExtra("CHILD_NAME", name);
        intent.putExtra("CHILD_AGE", selectedAge);
        intent.putExtra("CHILD_GENDER", selectedGender);
        intent.putExtra("CHILD_AVATAR", selectedAvatar);
        startActivity(intent);

        finish();
    }
}