package com.example.graduationproject.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.graduationproject.R;
import com.example.graduationproject.models.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
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

        // تغيير مظهر التبويب بناءً على حالته (محدد أم لا)
        if (category.isSelected()) {
            holder.tvCategoryName.setBackgroundResource(R.drawable.bg_timer_chip_selected);
            holder.tvCategoryName.setTextColor(Color.WHITE);
        } else {
            holder.tvCategoryName.setBackgroundResource(R.drawable.bg_timer_chip);
            holder.tvCategoryName.setTextColor(Color.parseColor("#2D587B"));
        }

        holder.itemView.setOnClickListener(v -> {
            // إلغاء تحديد العنصر القديم وتحديد الجديد
            for (Category cat : categories) {
                cat.setSelected(false);
            }
            category.setSelected(true);
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

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}