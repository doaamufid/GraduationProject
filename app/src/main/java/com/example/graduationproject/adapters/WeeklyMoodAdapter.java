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
            holder.ivMoodIcon.setVisibility(View.VISIBLE);
        } else {
            holder.ivMoodIcon.setVisibility(View.GONE);
            holder.cardMoodIcon.setCardBackgroundColor(0xFFF5F5F5); // Neutral light gray
        }

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