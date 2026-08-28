package com.example.graduationproject.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.graduationproject.R;
import com.example.graduationproject.models.MoodDay;
import java.util.List;

public class WeeklyMoodAdapter extends RecyclerView.Adapter<WeeklyMoodAdapter.ViewHolder> {

    private final Context context;
    private final List<MoodDay> moodList;

    public WeeklyMoodAdapter(Context context, List<MoodDay> moodList) {
        this.context = context;
        this.moodList = moodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weekly_mood, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MoodDay item = moodList.get(position);

        holder.tvDayName.setText(item.getDayName());
        holder.tvDate.setText(item.getDate());
        
        if (item.getMoodIconRes() != 0) {
            holder.ivMoodIcon.setImageResource(item.getMoodIconRes());
            holder.cardMoodIcon.setCardBackgroundColor(item.getMoodColor());
            holder.ivMoodIcon.setAlpha(1.0f);
            holder.ivMoodIcon.setScaleX(1.0f);
            holder.ivMoodIcon.setScaleY(1.0f);
        } else {
            holder.ivMoodIcon.setImageResource(R.drawable.ic_lock);
            holder.ivMoodIcon.setAlpha(0.2f); // Faded lock icon
            holder.cardMoodIcon.setCardBackgroundColor(0x1A000000); // Very light gray transparent
            holder.ivMoodIcon.setScaleX(0.7f); // Make lock a bit smaller
            holder.ivMoodIcon.setScaleY(0.7f);
        }
        holder.ivMoodIcon.setVisibility(View.VISIBLE);

        if (item.isCurrentDay()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_mood_item_selected);
        } else {
            holder.itemView.setBackground(null);
        }
    }

    @Override
    public int getItemCount() {
        return moodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardMoodIcon;
        ImageView ivMoodIcon;
        TextView tvDayName, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMoodIcon = itemView.findViewById(R.id.cardMoodIcon);
            ivMoodIcon = itemView.findViewById(R.id.ivMoodIcon);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}