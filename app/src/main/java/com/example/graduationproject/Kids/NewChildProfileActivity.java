package com.example.graduationproject.Kids;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
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
    private static final String[] AVATARS = {"🦊", "🐻", "🐰", "🐼", "🐨"};
    private static final int[] AGES = {4, 5, 6, 7, 8, 9};

    private ActivityNewChildProfileBinding binding;
    private ChildProfileStore childProfileStore;
    private TextView selectedAgeView;
    private int selectedAge = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNewChildProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        childProfileStore = new ChildProfileStore(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnStart.setOnClickListener(v -> saveProfileAndFinish());
        binding.etChildName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateStartState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setupAgeButtons();
    }

    private void setupAgeButtons() {
        int size = (int) (44 * getResources().getDisplayMetrics().density);
        int margin = (int) (6 * getResources().getDisplayMetrics().density);

        for (int age : AGES) {
            TextView ageView = new TextView(this);
            ageView.setText(String.valueOf(age));
            ageView.setGravity(Gravity.CENTER);
            ageView.setTextSize(15);
            ageView.setTypeface(ageView.getTypeface(), android.graphics.Typeface.BOLD);
            ageView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            ageView.setBackgroundResource(R.drawable.bg_child_age_default);
            ageView.setOnClickListener(v -> selectAge((TextView) v, age));

            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            binding.ageContainer.addView(ageView, params);
        }
    }

    private void selectAge(TextView ageView, int age) {
        if (selectedAgeView != null) {
            selectedAgeView.setBackgroundResource(R.drawable.bg_child_age_default);
            selectedAgeView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
        selectedAge = age;
        selectedAgeView = ageView;
        selectedAgeView.setBackgroundResource(R.drawable.bg_child_age_selected);
        selectedAgeView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        binding.worldCard.setVisibility(View.VISIBLE);
        updateStartState();
    }

    private void updateStartState() {
        boolean canStart = selectedAge > 0 && !binding.etChildName.getText().toString().trim().isEmpty();
        binding.btnStart.setEnabled(canStart);
        binding.btnStart.setBackgroundResource(canStart
                ? R.drawable.bg_child_start_enabled
                : R.drawable.bg_child_start_disabled);
    }

    @Override
    protected void onDestroy() {
        childProfileStore.close();
        super.onDestroy();
    }

    private void saveProfileAndFinish() {
        String name = binding.etChildName.getText().toString().trim();
        if (name.isEmpty() || selectedAge <= 0) {
            Toast.makeText(this, "اكتب الاسم واختر العمر", Toast.LENGTH_SHORT).show();
            return;
        }

        childProfileStore.addProfile(name, selectedAge, AVATARS[selectedAge % AVATARS.length]);
        setResult(RESULT_OK);
        finish();
    }
}
