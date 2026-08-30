package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.data.SeedData;

import java.util.Arrays;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    private List<String> categories;
    private OnCategoryClickListener listener;
    private String selectedCategory;

    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
        if (!categories.isEmpty()) {
            this.selectedCategory = categories.get(0);
        }
    }

    public CategoryAdapter(String[] categories, String selectedCategory, OnCategoryClickListener listener) {
        this.categories = Arrays.asList(categories);
        this.selectedCategory = selectedCategory;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        boolean isSelected = category.equals(selectedCategory);

        holder.label.setText(category);
        holder.icon.setText(SeedData.getCategoryEmoji(category));

        holder.ring.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        holder.container.setBackgroundResource(isSelected ? 0 : R.drawable.bg_category_border);
        holder.label.setAlpha(isSelected ? 1.0f : 0.6f);
        holder.icon.setAlpha(isSelected ? 1.0f : 0.8f);

        holder.itemView.setOnClickListener(v -> {
            selectedCategory = category;
            notifyDataSetChanged();
            listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setSelected(String category) {
        this.selectedCategory = category;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        TextView icon;
        View ring;
        View container;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.txt_chip);
            icon = itemView.findViewById(R.id.txt_category_icon);
            ring = itemView.findViewById(R.id.category_ring);
            container = itemView.findViewById(R.id.category_icon_container);
        }
    }
}