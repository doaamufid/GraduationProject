package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.SoundItem;

import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundViewHolder> {

    public interface OnSoundClickListener {
        void onSoundClick(String audioFileName);
    }

    private List<SoundItem> soundList;
    private OnSoundClickListener listener;
    private int selectedPosition = -1;

    public SoundAdapter(List<SoundItem> soundList, OnSoundClickListener listener) {
        this.soundList = soundList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_circle_sound, parent, false);
        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        SoundItem item = soundList.get(position);
        holder.label.setText(item.getTitle());

        // تحويل اسم الأيقونة (String) إلى resId فعلي
        int iconResId = holder.itemView.getContext().getResources()
                .getIdentifier(item.getIconName(), "drawable",
                        holder.itemView.getContext().getPackageName());
        if (iconResId != 0) {
            holder.icon.setImageResource(iconResId);
        }

        holder.circleContainer.setSelected(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onSoundClick(item.getAudioFileName());
        });
    }

    @Override
    public int getItemCount() {
        return soundList.size();
    }

    static class SoundViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView label;
        View circleContainer;

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.circleIcon);
            label = itemView.findViewById(R.id.circleLabel);
            circleContainer = itemView.findViewById(R.id.circleContainer);
        }
    }
}