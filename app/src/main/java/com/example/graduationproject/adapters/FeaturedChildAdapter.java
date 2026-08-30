package com.example.graduationproject.Kids;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.databinding.ItemFeaturedChildBinding;
import com.example.graduationproject.models.ChildProfile;

import java.util.List;

public class FeaturedChildAdapter extends RecyclerView.Adapter<FeaturedChildAdapter.ChildViewHolder> {

    private final List<ChildProfile> children;
    private int topStars = 1; // نتجنب القسمة على صفر

    public FeaturedChildAdapter(List<ChildProfile> children) {
        this.children = children;
    }

    public void setTopStars(int topStars) {
        this.topStars = Math.max(topStars, 1);
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFeaturedChildBinding binding = ItemFeaturedChildBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ChildViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        // position بالقائمة الفرعية، بس الترتيب الحقيقي يبدأ من 2 (لأنه رقم 1 هو الطفل المميز فوق)
        holder.bind(children.get(position), position + 2, topStars);
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        private final ItemFeaturedChildBinding binding;

        ChildViewHolder(ItemFeaturedChildBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChildProfile child, int rank, int topStars) {
            binding.tvRank.setText(String.valueOf(rank));
            binding.tvAvatar.setText(child.getAvatar());
            binding.tvName.setText(child.getName());
            binding.tvStarsCount.setText(String.valueOf(child.getStars()));

            double ratio = (double) child.getStars() / topStars;
            int filled = (int) Math.round(ratio * 5);

            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                stars.append(i < filled ? "⭐" : "☆");
            }
            binding.tvStarsVisual.setText(stars.toString());
        }
    }
}