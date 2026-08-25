package com.example.graduationproject.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.graduationproject.R;
import com.example.graduationproject.data.SeedData;
import com.example.graduationproject.models.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter2 extends RecyclerView.Adapter<CategoryAdapter2.CategoryViewHolder> {

    private List<Category> categories;
    private OnCategoryClickListener listener;
    private String selectedCategoryName;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter2(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
        // detect selected if present
        for (Category c : categories) {
            if (c.isSelected()) {
                this.selectedCategoryName = c.getName();
                break;
            }
        }
    }

    public CategoryAdapter2(List<Category> categories, String selected, OnCategoryClickListener listener) {
        this.categories = categories;
        this.selectedCategoryName = selected;
        this.listener = listener;
    }

    public CategoryAdapter2(String[] categoryNames, String selected, OnCategoryClickListener listener) {
        this.categories = new ArrayList<>();
        for (int i = 0; i < categoryNames.length; i++) {
            this.categories.add(new Category(i, categoryNames[i], categoryNames[i].equals(selected)));
        }
        this.selectedCategoryName = selected;
        this.listener = listener;
    }

    public void setSelected(String categoryName) {
        this.selectedCategoryName = categoryName;
        for (Category cat : categories) {
            cat.setSelected(cat.getName().equals(categoryName));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_chip, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvCategoryName.setText(category.getName());

        // emoji from SeedData
        String emoji = SeedData.getCategoryEmoji(category.getName());
        holder.tvCategoryEmoji.setText(emoji);

        // set selected state on the emoji view so selector drawable shows ring
        holder.tvCategoryEmoji.setSelected(category.isSelected());

        if (category.isSelected()) {
            // label uses accent color when selected
            holder.tvCategoryName.setTextColor(holder.itemView.getResources().getColor(R.color.accent));
            holder.tvCategoryName.setAlpha(1f);
        } else {
            holder.tvCategoryName.setTextColor(Color.parseColor("#2D587B"));
            holder.tvCategoryName.setAlpha(0.85f);
        }

        holder.itemView.setOnClickListener(v -> {
            for (Category cat : categories) {
                cat.setSelected(false);
            }
            category.setSelected(true);
            selectedCategoryName = category.getName();
            notifyDataSetChanged();

            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        TextView tvCategoryEmoji;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryEmoji = itemView.findViewById(R.id.tvCategoryEmoji);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}