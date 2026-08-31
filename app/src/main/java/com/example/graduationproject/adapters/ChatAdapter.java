package com.example.graduationproject.adapters;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.ChatMessage;

import java.util.List;

/**
 * Mirrors the `messages.map(...)` render loop in the JSX, including the
 * conditionally-attached suggestion cards and the typing bubble.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MESSAGE = 0;
    private static final int TYPE_TYPING = 1;
    private static final int TYPE_VOICE = 2;

    public interface CardActionListener {
        void onCardAction(String cardType);
    }

    public interface VoiceActionListener {
        void onVoiceClick(ChatMessage msg);
    }

    private final Context context;
    private final List<ChatMessage> items;
    private final CardActionListener cardActionListener;
    private VoiceActionListener voiceActionListener;
    private long playingMessageId = -1; // id of the voice message currently playing

    public ChatAdapter(Context context, List<ChatMessage> items, CardActionListener listener) {
        this.context = context;
        this.items = items;
        this.cardActionListener = listener;
    }

    public void setVoiceActionListener(VoiceActionListener listener) {
        this.voiceActionListener = listener;
    }

    /** Marks which voice message is playing so its icon can switch to pause. */
    public void setPlayingMessageId(long id) {
        playingMessageId = id;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage msg = items.get(position);
        if (msg.isVoice()) return TYPE_VOICE;
        return msg.typingIndicator ? TYPE_TYPING : TYPE_MESSAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_TYPING) {
            View v = inflater.inflate(R.layout.chat_item_typing_indicator, parent, false);
            return new TypingVH(v);
        }
        if (viewType == TYPE_VOICE) {
            View v = inflater.inflate(R.layout.chat_item_voice_message, parent, false);
            return new VoiceVH(v);
        }
        View v = inflater.inflate(R.layout.chat_item_chat_message, parent, false);
        return new MessageVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = items.get(position);

        // Mirrors .msg-in { animation: min .3s ease } — fade + translateY entrance on every row.
        playEntranceAnimation(holder.itemView);

        if (holder instanceof TypingVH) {
            ((TypingVH) holder).startBounce();
            return;
        }

        if (holder instanceof VoiceVH) {
            bindVoice((VoiceVH) holder, msg);
            return;
        }

        MessageVH vh = (MessageVH) holder;
        vh.bubble.setText(msg.text);
        vh.time.setText(msg.time);

        int gravity = msg.fromUser ? android.view.Gravity.END : android.view.Gravity.START;
        setRowGravity(vh, gravity);

        if (msg.fromUser) {
            vh.bubble.setBackgroundResource(R.drawable.bg_bubble_user);
            vh.bubble.setTextColor(ContextCompat.getColor(context, R.color.white));
            vh.check.setVisibility(View.VISIBLE);
        } else {
            vh.bubble.setBackgroundResource(R.drawable.bg_bubble_bot);
            vh.bubble.setTextColor(ContextCompat.getColor(context, R.color.text_main));
            vh.check.setVisibility(View.GONE);
        }

        bindCard(vh, msg.cardType);
    }

    /** Binding for user voice-message bubbles (always aligned to the end). */
    private void bindVoice(VoiceVH vh, ChatMessage msg) {
        int durationMin = msg.audioDurationSec / 60;
        int durationSec = msg.audioDurationSec % 60;
        vh.duration.setText(String.format(java.util.Locale.US, "%02d:%02d", durationMin, durationSec));
        vh.time.setText(msg.time);

        // Voice messages are always from the user -> align end, primary bubble already in layout.
        setLayoutGravity(vh.voiceBubble, android.view.Gravity.END);
        setLayoutGravity(vh.metaRow, android.view.Gravity.END);

        boolean playing = msg.id == playingMessageId;
        vh.playButton.setImageResource(playing ? R.drawable.ic_pause : R.drawable.ic_play);

        vh.playButton.setOnClickListener(v -> {
            if (voiceActionListener != null) voiceActionListener.onVoiceClick(msg);
        });
    }

    private void setRowGravity(MessageVH vh, int gravity) {
        setLayoutGravity(vh.bubble, gravity);
        setLayoutGravity(vh.metaRow, gravity);
        setLayoutGravity(vh.cardContainer, gravity);
    }

    private void setLayoutGravity(View view, int gravity) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) lp).gravity = gravity;
            view.setLayoutParams(lp);
        }
    }

    /** Inflates the correct suggestion card layout (or hides the container if none). */
    private void bindCard(MessageVH vh, String cardType) {
        vh.cardContainer.removeAllViews();
        if (cardType == null) {
            vh.cardContainer.setVisibility(View.GONE);
            return;
        }
        vh.cardContainer.setVisibility(View.VISIBLE);

        int layoutRes;
        switch (cardType) {
            case "breathing": layoutRes = R.layout.chat_card_breathing; break;
            case "dhikr": layoutRes = R.layout.chat_card_dhikr; break;
            case "article": layoutRes = R.layout.chat_card_article; break;
            case "grounding": layoutRes = R.layout.chat_card_grounding; break;
            case "cbt_reframe": layoutRes = R.layout.chat_card_cbt_reframe; break;
            case "body_map": layoutRes = R.layout.chat_card_body_map; break;
            case "future_letter": layoutRes = R.layout.chat_card_future_letter; break;
            default: vh.cardContainer.setVisibility(View.GONE); return;
        }

        View cardView = LayoutInflater.from(context).inflate(layoutRes, vh.cardContainer, false);
        vh.cardContainer.addView(cardView);

        View actionView = cardView.findViewById(R.id.btn_card_action);
        if (actionView != null) {
            actionView.setOnClickListener(v -> {
                if (cardActionListener != null) cardActionListener.onCardAction(cardType);
            });
        }
    }

    private void playEntranceAnimation(View itemView) {
        itemView.setAlpha(0f);
        itemView.setTranslationY(8 * context.getResources().getDisplayMetrics().density);
        itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VoiceVH extends RecyclerView.ViewHolder {
        LinearLayout voiceBubble, metaRow;
        ImageButton playButton;
        TextView duration, time;

        VoiceVH(@NonNull View itemView) {
            super(itemView);
            voiceBubble = itemView.findViewById(R.id.voice_bubble);
            playButton = itemView.findViewById(R.id.btn_voice_play);
            duration = itemView.findViewById(R.id.txt_voice_duration);
            metaRow = itemView.findViewById(R.id.row_voice_meta);
            time = itemView.findViewById(R.id.txt_voice_time);
        }
    }

    static class MessageVH extends RecyclerView.ViewHolder {
        TextView bubble, time;
        LinearLayout metaRow;
        ImageView check;
        FrameLayout cardContainer;

        MessageVH(@NonNull View itemView) {
            super(itemView);
            bubble = itemView.findViewById(R.id.txt_bubble);
            metaRow = itemView.findViewById(R.id.row_meta);
            time = itemView.findViewById(R.id.txt_time);
            check = itemView.findViewById(R.id.img_check);
            cardContainer = itemView.findViewById(R.id.card_container);
        }
    }

    static class TypingVH extends RecyclerView.ViewHolder {
        View dot1, dot2, dot3;

        TypingVH(@NonNull View itemView) {
            super(itemView);
            dot1 = itemView.findViewById(R.id.dot1);
            dot2 = itemView.findViewById(R.id.dot2);
            dot3 = itemView.findViewById(R.id.dot3);
        }

        /** Mirrors .dot-bounce { animation: db 1s ease-in-out infinite } with staggered 0.15s delays. */
        void startBounce() {
            bounce(dot1, 0);
            bounce(dot2, 150);
            bounce(dot3, 300);
        }

        private void bounce(View dot, long delayMs) {
            float density = dot.getResources().getDisplayMetrics().density;
            PropertyValuesHolder ty = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, -4 * density, 0f);
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.4f, 1f, 0.4f);
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(dot, ty, alpha);
            anim.setDuration(1000);
            anim.setStartDelay(delayMs);
            anim.setRepeatCount(ValueAnimator.INFINITE);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();
        }
    }
}
