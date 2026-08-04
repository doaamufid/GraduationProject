package com.example.graduationproject.Kids;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.databinding.ItemBotMessageBinding;

import java.util.List;

public class BotMessageAdapter extends RecyclerView.Adapter<BotMessageAdapter.MessageViewHolder> {

    private final List<BotMessage> messages;

    public BotMessageAdapter(List<BotMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBotMessageBinding itemBinding = ItemBotMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MessageViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemBotMessageBinding binding;

        MessageViewHolder(ItemBotMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BotMessage message) {
            binding.textMessage.setText(message.getText());
            // ممكن تلوّني الـ bubble حسب mood لاحقًا هون لو حبيتي
        }
    }
}