package com.example.graduationproject.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    private List<String> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0; // أول تصنيف محدد افتراضياً

    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
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
        boolean isSelected = position == selectedPosition;

        holder.label.setText(category);
        holder.icon.setText(getEmojiFor(category));
        holder.iconCard.setCardBackgroundColor(Color.parseColor(getColorFor(category)));

        // خط التحديد يظهر بس تحت التصنيف المحدد
        holder.underline.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);

        // شفافية خفيفة للمربعات الغير محددة (اختياري لإبراز المحدد)
        holder.iconCard.setAlpha(isSelected ? 1f : 0.6f);

        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);
            listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    // إيموجي مناسب لكل تصنيف
    private String getEmojiFor(String category) {
        switch (category) {
            case "لعبة":
                return "🎮";
            case "صداقة":
                return "🤝";
            case "نوم":
                return "😴";
            case "مشاعر":
                return "😊";
            default:
                return "⭐";
        }
    }

    // لون مربع مناسب لكل تصنيف
    private String getColorFor(String category) {
        switch (category) {
            case "لعبة":
                return "#FFA352";
            case "صداقة":
                return "#6C63FF";
            case "نوم":
                return "#2EC4B6";
            case "مشاعر":
                return "#FFD166";
            default:
                return "#B0B0B0";
        }
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        TextView icon;
        CardView iconCard;
        View underline;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.categoryLabel);
            icon = itemView.findViewById(R.id.categoryIcon);
            iconCard = itemView.findViewById(R.id.categoryIconCard);
            underline = itemView.findViewById(R.id.categoryUnderline);
        }
    }
}