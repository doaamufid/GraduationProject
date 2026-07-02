package com.example.graduationproject.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.ChatItem;

import java.util.List;

public class CbtChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatItem> chatItemList;

    public CbtChatAdapter(List<ChatItem> chatItemList) {
        this.chatItemList = chatItemList;
    }

    @Override
    public int getItemViewType(int position) {
        return chatItemList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatItem.TYPE_AI_SUGGESTION) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cbt_ai_suggestion, parent, false);
            return new SuggestionViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ai_response_with_emojis, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatItem item = chatItemList.get(position);

        if (getItemViewType(position) == ChatItem.TYPE_AI_SUGGESTION) {
            SuggestionViewHolder suggestHolder = (SuggestionViewHolder) holder;
            suggestHolder.tvTitle.setText(item.getTitle());
            suggestHolder.tvDetails.setText(item.getSubTitle());

            // جعل الكارت بالكامل قابل للضغط بدلاً من وجود زر داخلي
            suggestHolder.itemView.setOnClickListener(v -> {
                // هنا يتم برمجة الانتقال لشاشة التمرين عند الضغط على الكارت
            });
        } else {
            MessageViewHolder msgHolder = (MessageViewHolder) holder;
            msgHolder.tvMessage.setText(item.getMessageText());
            msgHolder.tvTime.setText(item.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatItemList.size();
    }

    // الـ ViewHolder المحدث لكروت الاقتراحات (CBT) بناءً على التعديلات الأخيرة
    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetails;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvSuggestTitle);
            tvDetails = itemView.findViewById(R.id.tvSuggestDetails); // الـ ID الصحيح والمحدث هُنا
        }
    }

    // الـ ViewHolder للرسائل العادية والإيموجي
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvAiMessageText);
            tvTime = itemView.findViewById(R.id.tvUserMsgTime);
        }
    }
}
