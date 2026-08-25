package com.example.graduationproject.Kids;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        holder.tvMessageBody.setText(message.getMessage());

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(32f);

        if (message.isUser()) {
            holder.container.setGravity(Gravity.END);
            shape.setColor(Color.parseColor("#F47C2B")); // لون الطفل
            holder.tvMessageBody.setTextColor(Color.WHITE);
        } else {
            holder.container.setGravity(Gravity.START);
            shape.setColor(Color.WHITE); // لون الـ AI
            holder.tvMessageBody.setTextColor(Color.parseColor("#4E342E"));
        }

        holder.tvMessageBody.setBackground(shape);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageBody;
        LinearLayout container;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageBody = itemView.findViewById(R.id.tvMessageBody);
            container = itemView.findViewById(R.id.chatBubbleContainer);
        }
    }
}