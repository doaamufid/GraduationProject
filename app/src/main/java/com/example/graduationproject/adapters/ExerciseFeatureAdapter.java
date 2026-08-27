package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
        ExerciseFeature item = exerciseList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.tvDuration.setText(item.getDuration());
        holder.ivIcon.setImageResource(item.getIconResId());
        
        // Set colors
        holder.cardContainer.setCardBackgroundColor(item.getCardBgColor());
        holder.flIconBg.getBackground().setTint(item.getCircleColor());
        holder.tvDuration.setTextColor(item.getCircleColor());

        holder.itemView.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(200).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardContainer;
        ImageView ivIcon;
        View flIconBg;
        TextView tvTitle, tvDescription, tvDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            ivIcon = itemView.findViewById(R.id.ivExerciseIcon);
            flIconBg = itemView.findViewById(R.id.flIconBg);
            tvTitle = itemView.findViewById(R.id.tvExerciseTitle);
            tvDescription = itemView.findViewById(R.id.tvExerciseDescription);
            tvDuration = itemView.findViewById(R.id.tvExerciseDuration);
        }
    }
}