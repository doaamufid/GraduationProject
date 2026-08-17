package com.example.graduationproject.Kids;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityNewChildProfileBinding;

import java.util.ArrayList;
import java.util.List;

public class NewChildProfileActivity extends AppCompatActivity {
    private static final String[] AVATARS = {"🦊", "🐻", "🐰", "🐼", "🐨"};

    private ActivityNewChildProfileBinding binding;
    private ChildProfileStore childProfileStore;
    private int selectedAge = -1;
    private String selectedGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNewChildProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ترك تحديد اتجاه الشاشة تلقائياً بحسب لغة الجهاز
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);

        childProfileStore = new ChildProfileStore(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupAgesRecyclerView();
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

    private void setupAgesRecyclerView() {
        List<Integer> agesList = new ArrayList<>();
        for (int i = 3; i <= 12; i++) {
            agesList.add(i);
        }

        AgesAdapter adapter = new AgesAdapter(agesList, age -> {
            selectedAge = age;
            updateStartState();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvAges.setLayoutManager(layoutManager);
        binding.rvAges.setAdapter(adapter);
    }

    private void setupGenderButtons() {
        binding.btnGenderBoy.setBackgroundResource(R.drawable.bg_gender_unselected);
        binding.btnGenderGirl.setBackgroundResource(R.drawable.bg_gender_unselected);

        binding.btnGenderBoy.setOnClickListener(v -> selectGender("boy"));
        binding.btnGenderGirl.setOnClickListener(v -> selectGender("girl"));
    }

    private void selectGender(String gender) {
        selectedGender = gender;

        if ("boy".equals(gender)) {
            binding.btnGenderBoy.setBackgroundResource(R.drawable.bg_gender_selected);
            binding.btnGenderGirl.setBackgroundResource(R.drawable.bg_gender_unselected);
        } else {
            binding.btnGenderGirl.setBackgroundResource(R.drawable.bg_gender_selected);
            binding.btnGenderBoy.setBackgroundResource(R.drawable.bg_gender_unselected);
        }

        updateStartState();
    }

    private void updateStartState() {
        boolean canStart = selectedAge >= 3
                && !selectedGender.isEmpty()
                && !binding.etChildName.getText().toString().trim().isEmpty();

        binding.btnStart.setEnabled(canStart);
        binding.btnStart.setAlpha(canStart ? 1.0f : 0.5f);

        if (canStart) {
            binding.cardInfoBanner.setVisibility(View.VISIBLE);
            String name = binding.etChildName.getText().toString().trim();

            // تركيب عنوان الكارد مع مراعاة اللغة الحالية
            String bannerTitle = getString(R.string.new_child_banner_title, name);
            binding.tvBannerTitle.setText(bannerTitle);
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
        if (name.isEmpty() || selectedAge < 3 || selectedGender.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        childProfileStore.addProfile(name, selectedAge, selectedGender, AVATARS[selectedAge % AVATARS.length]);
        setResult(RESULT_OK);
        finish();
    }

    // --- Inner Adapter Class ---
    private static class AgesAdapter extends RecyclerView.Adapter<AgesAdapter.AgeViewHolder> {
        interface OnAgeClickListener {
            void onAgeClick(int age);
        }

        private final List<Integer> agesList;
        private final OnAgeClickListener listener;
        private int selectedPosition = -1;

        AgesAdapter(List<Integer> agesList, OnAgeClickListener listener) {
            this.agesList = agesList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public AgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_age_selector, parent, false);
            return new AgeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AgeViewHolder holder, int position) {
            int age = agesList.get(position);
            holder.tvAge.setText(String.valueOf(age));

            if (position == selectedPosition) {
                holder.tvAge.setBackgroundResource(R.drawable.bg_gender_selected);
                holder.tvAge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
            } else {
                holder.tvAge.setBackgroundResource(R.drawable.bg_edit_text_rounded);
                holder.tvAge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
            }

            holder.itemView.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                listener.onAgeClick(age);
            });
        }

        @Override
        public int getItemCount() {
            return agesList.size();
        }

        static class AgeViewHolder extends RecyclerView.ViewHolder {
            TextView tvAge;

            AgeViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAge = itemView.findViewById(R.id.tv_age_num);
            }
        }
    }
}