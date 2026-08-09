package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.graduationproject.R;
import com.example.graduationproject.models.ExerciseFeature;
import java.util.List;

public class ExerciseFeatureAdapter extends RecyclerView.Adapter<ExerciseFeatureAdapter.ViewHolder> {

    private final Context context;
    private final List<ExerciseFeature> exerciseList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ExerciseFeatureAdapter(Context context, List<ExerciseFeature> exerciseList, OnItemClickListener listener) {
        this.context = context;
        this.exerciseList = exerciseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise_feature, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // التعديل هنا: استخدام .get(position) بدلاً من الأقواس المربعة [position]
        ExerciseFeature item = exerciseList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
        holder.ivIcon.setImageResource(item.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivExerciseIcon);
            tvTitle = itemView.findViewById(R.id.tvExerciseTitle);
            tvSubtitle = itemView.findViewById(R.id.tvExerciseSubtitle);
        }
    }
}