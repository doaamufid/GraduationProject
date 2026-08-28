package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;

public class KidsRoutineEmojiGridAdapter extends RecyclerView.Adapter<KidsRoutineEmojiGridAdapter.EmojiViewHolder> {

    public static final String[] EMOJI_CHOICES = {
            "🌞", "🦷", "🍳", "👕", "🎒", "🎨", "🍽️", "🛁",
            "📖", "🌙", "💧", "🧸", "⚽", "📚", "🧹", "🎵"
    };

    public interface OnEmojiSelectedListener {
        void onEmojiSelected(String emoji);
    }

    private String selected = EMOJI_CHOICES[0];
    private final OnEmojiSelectedListener listener;

    public KidsRoutineEmojiGridAdapter(OnEmojiSelectedListener listener) {
        this.listener = listener;
    }

    public String getSelected() {
        return selected;
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kids_routine_item_emoji_pick, parent, false);
        return new EmojiViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int position) {
        String emoji = EMOJI_CHOICES[position];
        holder.tv.setText(emoji);
        boolean isSelected = emoji.equals(selected);
        holder.tv.setBackgroundResource(isSelected
                ? R.drawable.kids_routine_bg_emoji_pick_selected
                : R.drawable.kids_routine_bg_emoji_pick_unselected);

        holder.tv.setOnClickListener(v -> {
            String previous = selected;
            selected = emoji;
            notifyItemChanged(indexOf(previous));
            notifyItemChanged(position);
            if (listener != null) listener.onEmojiSelected(selected);
        });
    }

    private int indexOf(String emoji) {
        for (int i = 0; i < EMOJI_CHOICES.length; i++) {
            if (EMOJI_CHOICES[i].equals(emoji)) return i;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return EMOJI_CHOICES.length;
    }

    static class EmojiViewHolder extends RecyclerView.ViewHolder {
        final TextView tv;
        EmojiViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvEmojiPick);
        }
    }
}
