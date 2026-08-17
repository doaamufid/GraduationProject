package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;

public class MsgEmojiAdapter extends RecyclerView.Adapter<MsgEmojiAdapter.VH> {

    public interface OnEmojiClick {
        void onClick(String emoji);
    }

    private final String[] emojis;
    private String selected;
    private final OnEmojiClick listener;

    public MsgEmojiAdapter(String[] emojis, OnEmojiClick listener) {
        this.emojis = emojis;
        this.listener = listener;
    }

    public void setSelected(String emoji) {
        this.selected = emoji;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emoji, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String e = emojis[position];
        holder.text.setText(e);
        boolean isSelected = e.equals(selected);
        holder.text.setBackgroundResource(isSelected ? R.drawable.bg_emoji_option_selected : R.drawable.bg_emoji_option);
        holder.text.setOnClickListener(v -> {
            holder.text.setScaleX(0.88f);
            holder.text.setScaleY(0.88f);
            holder.text.animate().scaleX(1f).scaleY(1f).setDuration(200)
                    .setInterpolator(new OvershootInterpolator(2f)).start();
            if (listener != null) listener.onClick(e);
        });
    }

    @Override
    public int getItemCount() {
        return emojis.length;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView text;

        VH(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.emojiText);
        }
    }
}
