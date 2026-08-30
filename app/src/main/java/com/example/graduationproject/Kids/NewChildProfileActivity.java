package com.example.graduationproject.Kids;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
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

import com.example.graduationproject.KidsAdaptiveMainActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.data.ActiveChildManager;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityNewChildProfileBinding;

import java.util.ArrayList;
import java.util.List;

public class NewChildProfileActivity extends AppCompatActivity {

    // ايموجيات مخصصة للأولاد وأخرى للبنات
    // أشكال مرحة ومناسبة للأطفال
    private static final String[] BOY_AVATARS = {"🦁", "🦊", "🐻", "🐼", "🐵", "🐯", "🐨"};
    private static final String[] GIRL_AVATARS = {"🦄", "🐰", "🐱", "🐥", "🦋", "🌸", "👑"};

    private ActivityNewChildProfileBinding binding;
    private ChildProfileStore childProfileStore;
    private int selectedAge = -1;
    private String selectedGender = "";
    private String selectedAvatar = "";

    private AvatarsAdapter avatarsAdapter;
    private final List<String> currentAvatarsList = new ArrayList<>();

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

        setupAvatarsRecyclerView();
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

    private void setupAvatarsRecyclerView() {
        avatarsAdapter = new AvatarsAdapter(currentAvatarsList, avatar -> {
            selectedAvatar = avatar;
            updateStartState();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvAvatars.setLayoutManager(layoutManager);
        binding.rvAvatars.setAdapter(avatarsAdapter);
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
        selectedAvatar = ""; // إعادة تصفير الاختيار السابق للشكل عند تغيير الجنس

        if ("boy".equals(gender)) {
            binding.btnGenderBoy.setBackgroundResource(R.drawable.bg_gender_selected);
            binding.btnGenderGirl.setBackgroundResource(R.drawable.bg_gender_unselected);
            updateAvatarList(BOY_AVATARS);
        } else {
            binding.btnGenderGirl.setBackgroundResource(R.drawable.bg_gender_selected);
            binding.btnGenderBoy.setBackgroundResource(R.drawable.bg_gender_unselected);
            updateAvatarList(GIRL_AVATARS);
        }

        // إظهار اختيار الأشكال بعد تحديد الجنس
        binding.lblAvatar.setVisibility(View.VISIBLE);
        binding.rvAvatars.setVisibility(View.VISIBLE);

        updateStartState();
    }

    private void updateAvatarList(String[] avatars) {
        currentAvatarsList.clear();
        for (String avatar : avatars) {
            currentAvatarsList.add(avatar);
        }
        avatarsAdapter.resetSelection();
        avatarsAdapter.notifyDataSetChanged();
    }

    private void updateStartState() {
        boolean canStart = selectedAge >= 3
                && !selectedGender.isEmpty()
                && !selectedAvatar.isEmpty()
                && !binding.etChildName.getText().toString().trim().isEmpty();

        binding.btnStart.setEnabled(canStart);
        binding.btnStart.setAlpha(canStart ? 1.0f : 0.5f);

        if (canStart) {
            binding.cardInfoBanner.setVisibility(View.VISIBLE);
            String name = binding.etChildName.getText().toString().trim();

            binding.tvBannerEmoji.setText(selectedAvatar);
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
        if (name.isEmpty() || selectedAge < 3 || selectedGender.isEmpty() || selectedAvatar.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        long newChildId = childProfileStore.addProfile(name, selectedAge, selectedGender, selectedAvatar);

        // البروفايل الجديد يصير هو الطفل النشط حالياً فوراً، عن طريق المصدر
        // الموحّد ActiveChildManager. بدون هالسطر، أي رسمة أو تسجيل يعملها
        // الطفل بعد إنشاء البروفايل رح ينخزن بـ childId = -1 (غير محدد).
        ActiveChildManager.setActiveChildId(this, newChildId);

        setResult(RESULT_OK);

        Intent intent = new Intent(this, KidsAdaptiveMainActivity.class);
        intent.putExtra("CHILD_ID", newChildId);
        intent.putExtra("CHILD_NAME", name);
        intent.putExtra("CHILD_AGE", selectedAge);
        intent.putExtra("CHILD_GENDER", selectedGender);
        intent.putExtra("CHILD_AVATAR", selectedAvatar);
        startActivity(intent);

        finish();
    }

    // --- Adapter الأشكال (Avatars) ---
    private static class AvatarsAdapter extends RecyclerView.Adapter<AvatarsAdapter.AvatarViewHolder> {
        interface OnAvatarClickListener {
            void onAvatarClick(String avatar);
        }

        private final List<String> avatarList;
        private final OnAvatarClickListener listener;
        private int selectedPosition = -1;

        AvatarsAdapter(List<String> avatarList, OnAvatarClickListener listener) {
            this.avatarList = avatarList;
            this.listener = listener;
        }

        public void resetSelection() {
            selectedPosition = -1;
        }

        @NonNull
        @Override
        public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_age_selector, parent, false);
            return new AvatarViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
            String avatar = avatarList.get(position);
            holder.tvAvatar.setText(avatar);

            if (position == selectedPosition) {
                holder.tvAvatar.setBackgroundResource(R.drawable.bg_gender_selected);
            } else {
                holder.tvAvatar.setBackgroundResource(R.drawable.bg_edit_text_rounded);
            }

            holder.itemView.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                listener.onAvatarClick(avatar);
            });
        }

        @Override
        public int getItemCount() {
            return avatarList.size();
        }

        static class AvatarViewHolder extends RecyclerView.ViewHolder {
            TextView tvAvatar;

            AvatarViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAvatar = itemView.findViewById(R.id.tv_age_num);
            }
        }
    }

    // --- Adapter الأعمار (Ages) ---
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