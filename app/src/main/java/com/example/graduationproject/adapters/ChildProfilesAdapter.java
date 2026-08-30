package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.databinding.ItemAddChildProfileBinding;
import com.example.graduationproject.databinding.ItemChildProfileBinding;
import com.example.graduationproject.models.ChildProfile;

import java.util.List;

public class ChildProfilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PROFILE = 1;
    private static final int TYPE_ADD = 2;

    private final List<ChildProfile> profiles;
    private final OnChildProfileClickListener listener;

    public interface OnChildProfileClickListener {
        void onProfileClick(ChildProfile profile);
        void onAddProfileClick();
    }

    public ChildProfilesAdapter(List<ChildProfile> profiles, OnChildProfileClickListener listener) {
        this.profiles = profiles;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position < profiles.size() ? TYPE_PROFILE : TYPE_ADD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROFILE) {
            return new ProfileViewHolder(ItemChildProfileBinding.inflate(inflater, parent, false));
        }
        return new AddViewHolder(ItemAddChildProfileBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProfileViewHolder) {
            ((ProfileViewHolder) holder).bind(profiles.get(position), position, listener);
        } else if (holder instanceof AddViewHolder) {
            holder.itemView.setOnClickListener(v -> listener.onAddProfileClick());
        }
    }

    @Override
    public int getItemCount() {
        return profiles.size() + 1;
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        private final ItemChildProfileBinding binding;

        ProfileViewHolder(ItemChildProfileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChildProfile profile, int position, OnChildProfileClickListener listener) {
            Context context = binding.getRoot().getContext();
            binding.tvChildName.setText(profile.getName());

            // 1. تحويل قيمة الجنس المسجلة إلى نص مترجم تلقائياً
            String rawGender = profile.getGender() != null ? profile.getGender().trim() : "";
            String genderText;
            if ("boy".equalsIgnoreCase(rawGender) || "male".equalsIgnoreCase(rawGender) || "ولد".equalsIgnoreCase(rawGender)) {
                genderText = context.getString(R.string.gender_boy);
            } else if ("girl".equalsIgnoreCase(rawGender) || "female".equalsIgnoreCase(rawGender) || "بنت".equalsIgnoreCase(rawGender)) {
                genderText = context.getString(R.string.gender_girl);
            } else {
                genderText = rawGender.isEmpty() ? "غير محدد" : rawGender;
            }

            // 2. جلب صيغة النص المترجمة حسب لغة الجهاز (تستبدل العمر والجنس)
            String ageAndGender = context.getString(R.string.kids_age_gender_format, profile.getAge(), genderText);
            binding.tvChildAge.setText(ageAndGender);

            // 3. 🌟 إظهار شخصية/أفاتار الطفل مع حماية ضد القيمة الفارغة (Fallback)
            String avatar = profile.getAvatar();
            if (avatar == null || avatar.trim().isEmpty()) {
                // شكل افتراضي بحسب الجنس إذا لم يتم اختيار أي شكل
                if ("female".equalsIgnoreCase(rawGender) || "girl".equalsIgnoreCase(rawGender) || "بنت".equalsIgnoreCase(rawGender)) {
                    avatar = "🎀";
                } else if ("male".equalsIgnoreCase(rawGender) || "boy".equalsIgnoreCase(rawGender) || "ولد".equalsIgnoreCase(rawGender)) {
                    avatar = "⭐";
                } else {
                    avatar = "🧸";
                }
            }

            binding.tvAvatar.setText(avatar);
            binding.tvAvatar.setBackgroundResource(position % 2 == 0
                    ? R.drawable.bg_child_avatar_mint
                    : R.drawable.bg_child_avatar_pink);

            binding.cardRoot.setOnClickListener(v -> listener.onProfileClick(profile));
        }
    }

    static class AddViewHolder extends RecyclerView.ViewHolder {
        AddViewHolder(ItemAddChildProfileBinding binding) {
            super(binding.getRoot());
        }
    }
}