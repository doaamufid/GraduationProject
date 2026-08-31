package com.example.graduationproject.adapters;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.AdultChatCardData;
import com.example.graduationproject.models.AdultChatChatMessage;
import com.example.graduationproject.util.AdultChatTimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Drives the chat RecyclerView. Ported behaviours from the React version:
 *  - separate bubble styles for bot / user / system rows
 *  - a "typing…" row shown while the bot is composing
 *  - long-press (450ms) opens a small reaction/action bar above the bubble
 *  - bot messages can carry a suggestion "card" (exercise/dhikr/article/video/moment/sos)
 *  - user voice messages render as a mini player with a fake progress fill
 */
public class AdultChatChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BOT = 0;
    private static final int TYPE_USER = 1;
    private static final int TYPE_SYSTEM = 2;
    private static final int TYPE_TYPING = 3;

    private static final long LONG_PRESS_MS = 450;

    public interface Listener {
        void onReply(AdultChatChatMessage msg);
        void onEdit(AdultChatChatMessage msg);
        void onDelete(long id);
        void onRephrase(AdultChatChatMessage msg);
        void onExplainMore(AdultChatChatMessage msg);
        void onRate(long id, String rating);
        void onCardAction(String type);
    }

    private final List<AdultChatChatMessage> messages = new ArrayList<>();
    private boolean typing = false;
    private final Listener listener;
    private String companionName = "الرفيق";
    private String tone = "supportive";

    public AdultChatChatAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setCompanionName(String name) { this.companionName = name; }
    public void setTone(String tone) { this.tone = tone; }

    public void submit(AdultChatChatMessage msg) {
        messages.add(msg);
        notifyItemInserted(messages.size() - 1 + (typing ? 1 : 0));
    }

    public void setTyping(boolean show) {
        if (typing == show) return;
        typing = show;
        if (show) notifyItemInserted(messages.size());
        else notifyItemRemoved(messages.size());
    }

    public void updateMessage(long id) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).id == id) { notifyItemChanged(i); return; }
        }
    }

    /** Marks a message as deleted (mirrors the JS soft-delete: text/card cleared, bubble shows a placeholder). */
    public void deleteMessage(long id) {
        for (int i = 0; i < messages.size(); i++) {
            AdultChatChatMessage m = messages.get(i);
            if (m.id == id) {
                m.deleted = true;
                m.text = null;
                m.card = null;
                notifyItemChanged(i);
                return;
            }
        }
    }

    /** Replaces a message's text in place and flags it as edited. */
    public void editMessage(long id, String newText) {
        for (int i = 0; i < messages.size(); i++) {
            AdultChatChatMessage m = messages.get(i);
            if (m.id == id) {
                m.text = newText;
                m.edited = true;
                notifyItemChanged(i);
                return;
            }
        }
    }

    public AdultChatChatMessage findById(long id) {
        for (AdultChatChatMessage m : messages) if (m.id == id) return m;
        return null;
    }

    public int size() { return messages.size(); }

    @Override
    public int getItemViewType(int position) {
        if (typing && position == messages.size()) return TYPE_TYPING;
        AdultChatChatMessage m = messages.get(position);
        switch (m.kind) {
            case USER: return TYPE_USER;
            case SYSTEM: return TYPE_SYSTEM;
            default: return TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_USER:
                return new UserVH(inflater.inflate(R.layout.adult_chat_item_message_user, parent, false));
            case TYPE_SYSTEM:
                return new SystemVH(inflater.inflate(R.layout.adult_chat_item_message_system, parent, false));
            case TYPE_TYPING:
                return new TypingVH(inflater.inflate(R.layout.adult_chat_item_typing, parent, false));
            default:
                return new BotVH(inflater.inflate(R.layout.adult_chat_item_message_bot, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TypingVH) {
            ((TypingVH) holder).animate();
            return;
        }
        AdultChatChatMessage m = messages.get(position);
        if (holder instanceof UserVH) ((UserVH) holder).bind(m);
        else if (holder instanceof BotVH) ((BotVH) holder).bind(m);
        else if (holder instanceof SystemVH) ((SystemVH) holder).bind(m);
    }

    @Override
    public int getItemCount() {
        return messages.size() + (typing ? 1 : 0);
    }

    private void playEntrance(View v) {
        v.setScaleX(0.8f); v.setScaleY(0.8f); v.setAlpha(0f); v.setTranslationY(10f);
        v.animate().scaleX(1f).scaleY(1f).alpha(1f).translationY(0f)
                .setDuration(300).setInterpolator(new OvershootInterpolator(1.6f)).start();
    }

    // ================= SYSTEM =================
    class SystemVH extends RecyclerView.ViewHolder {
        final TextView txt;
        SystemVH(View v) { super(v); txt = v.findViewById(R.id.txtSystem); }
        void bind(AdultChatChatMessage m) {
            txt.setText(m.text);
            playEntrance(itemView);
        }
    }

    // ================= TYPING =================
    class TypingVH extends RecyclerView.ViewHolder {
        final View d1, d2, d3;
        TypingVH(View v) {
            super(v);
            d1 = v.findViewById(R.id.dot1);
            d2 = v.findViewById(R.id.dot2);
            d3 = v.findViewById(R.id.dot3);
        }
        void animate() {
            bounce(d1, 0);
            bounce(d2, 150);
            bounce(d3, 300);
        }
        void bounce(View dot, long delay) {
            ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f, 0f);
            anim.setDuration(1000);
            anim.setStartDelay(delay);
            anim.setRepeatCount(ValueAnimator.INFINITE);
            anim.addUpdateListener(a -> {
                float t = (float) a.getAnimatedValue();
                dot.setTranslationY(-4f * t);
                dot.setAlpha(0.4f + 0.6f * t);
            });
            anim.start();
        }
    }

    // ================= USER =================
    class UserVH extends RecyclerView.ViewHolder {
        final FrameLayout bubbleTouchWrap;
        final LinearLayout bubbleContainer, reactionBar, replyQuote, voiceRow;
        final TextView txtMessage, txtReplyName, txtReplySnippet, txtTime, txtSeen, txtVoiceTime;
        final TextView actReply, actEdit, actDelete;
        final ImageView imgCheck, btnVoiceToggle;
        final View voiceProgressFill;
        final Handler handler = new Handler(Looper.getMainLooper());
        Runnable pending;
        boolean voicePlaying = false;

        UserVH(View v) {
            super(v);
            bubbleTouchWrap = v.findViewById(R.id.bubbleTouchWrap);
            bubbleContainer = v.findViewById(R.id.bubbleContainer);
            reactionBar = v.findViewById(R.id.reactionBar);
            replyQuote = v.findViewById(R.id.replyQuote);
            voiceRow = v.findViewById(R.id.voiceRow);
            txtMessage = v.findViewById(R.id.txtMessage);
            txtReplyName = v.findViewById(R.id.txtReplyName);
            txtReplySnippet = v.findViewById(R.id.txtReplySnippet);
            txtTime = v.findViewById(R.id.txtTime);
            txtSeen = v.findViewById(R.id.txtSeen);
            txtVoiceTime = v.findViewById(R.id.txtVoiceTime);
            actReply = v.findViewById(R.id.actReply);
            actEdit = v.findViewById(R.id.actEdit);
            actDelete = v.findViewById(R.id.actDelete);
            imgCheck = v.findViewById(R.id.imgCheck);
            btnVoiceToggle = v.findViewById(R.id.btnVoiceToggle);
            voiceProgressFill = v.findViewById(R.id.voiceProgressFill);
        }

        void bind(AdultChatChatMessage m) {
            playEntrance(itemView);
            reactionBar.setVisibility(View.GONE);

            if (m.deleted) {
                txtMessage.setText("🚫 تم حذف هذه الرسالة");
                txtMessage.setAlpha(0.6f);
                voiceRow.setVisibility(View.GONE);
                replyQuote.setVisibility(View.GONE);
            } else {
                txtMessage.setAlpha(1f);
                if (m.hasReplyTo) {
                    replyQuote.setVisibility(View.VISIBLE);
                    txtReplyName.setText(m.replyToFromUser ? "أنتِ" : companionName);
                    txtReplySnippet.setText(m.replyToSnippet);
                } else {
                    replyQuote.setVisibility(View.GONE);
                }

                if (m.voice) {
                    voiceRow.setVisibility(View.VISIBLE);
                    txtMessage.setVisibility(View.GONE);
                    txtVoiceTime.setText(m.voiceDuration);
                    btnVoiceToggle.setImageResource(R.drawable.adult_chat_ic_play);
                    voiceProgressFill.getLayoutParams().width = 0;
                    voiceProgressFill.requestLayout();
                    btnVoiceToggle.setOnClickListener(x -> toggleVoicePlayback(m));
                } else {
                    voiceRow.setVisibility(View.GONE);
                    txtMessage.setVisibility(View.VISIBLE);
                    String text = m.text == null ? "" : m.text;
                    if (m.edited) text = text + "  (معدّلة)";
                    txtMessage.setText(text);
                }
            }

            txtTime.setText(m.time == null ? "" : m.time);
            txtSeen.setVisibility(m.seen ? View.VISIBLE : View.GONE);
            imgCheck.setAlpha(1f);

            bubbleTouchWrap.setOnTouchListener((view, event) -> handleLongPress(event, m));

            actReply.setOnClickListener(x -> { reactionBar.setVisibility(View.GONE); if (listener != null) listener.onReply(m); });
            actEdit.setOnClickListener(x -> { reactionBar.setVisibility(View.GONE); if (listener != null) listener.onEdit(m); });
            actDelete.setOnClickListener(x -> { reactionBar.setVisibility(View.GONE); if (listener != null) listener.onDelete(m.id); });
        }

        void toggleVoicePlayback(AdultChatChatMessage m) {
            voicePlaying = !voicePlaying;
            btnVoiceToggle.setImageResource(voicePlaying ? R.drawable.adult_chat_ic_pause : R.drawable.adult_chat_ic_play);
            if (!voicePlaying) return;
            int totalMs = AdultChatTimeUtil.parseDuration(m.voiceDuration) * 1000;
            int trackWidthPx = ((View) voiceProgressFill.getParent()).getWidth();
            ValueAnimator anim = ValueAnimator.ofInt(0, Math.max(trackWidthPx, 1));
            anim.setDuration(Math.max(totalMs, 400));
            anim.addUpdateListener(a -> {
                voiceProgressFill.getLayoutParams().width = (int) a.getAnimatedValue();
                voiceProgressFill.requestLayout();
            });
            anim.start();
            handler.postDelayed(() -> {
                voicePlaying = false;
                btnVoiceToggle.setImageResource(R.drawable.adult_chat_ic_play);
            }, Math.max(totalMs, 400));
        }

        boolean handleLongPress(MotionEvent event, AdultChatChatMessage m) {
            if (m.deleted) return false;
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    pending = () -> {
                        reactionBar.setVisibility(View.VISIBLE);
                        reactionBar.setAlpha(0f); reactionBar.setScaleX(0.8f); reactionBar.setScaleY(0.8f);
                        reactionBar.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(180).start();
                    };
                    handler.postDelayed(pending, LONG_PRESS_MS);
                    return false;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (pending != null) handler.removeCallbacks(pending);
                    return false;
                default:
                    return false;
            }
        }
    }

    // ================= BOT =================
    class BotVH extends RecyclerView.ViewHolder {
        final FrameLayout bubbleTouchWrap, cardSlot;
        final LinearLayout bubbleContainer, reactionBar;
        final TextView txtMessage, txtTime, txtRatedTag;
        final TextView actRephrase, actExplain, actRateUp, actRateDown;
        final Handler handler = new Handler(Looper.getMainLooper());
        Runnable pending;

        BotVH(View v) {
            super(v);
            bubbleTouchWrap = v.findViewById(R.id.bubbleTouchWrap);
            bubbleContainer = v.findViewById(R.id.bubbleContainer);
            reactionBar = v.findViewById(R.id.reactionBar);
            txtMessage = v.findViewById(R.id.txtMessage);
            txtTime = v.findViewById(R.id.txtTime);
            txtRatedTag = v.findViewById(R.id.txtRatedTag);
            actRephrase = v.findViewById(R.id.actRephrase);
            actExplain = v.findViewById(R.id.actExplain);
            actRateUp = v.findViewById(R.id.actRateUp);
            actRateDown = v.findViewById(R.id.actRateDown);
            cardSlot = v.findViewById(R.id.cardSlot);
        }

        void bind(AdultChatChatMessage m) {
            playEntrance(itemView);
            reactionBar.setVisibility(View.GONE);
            txtMessage.setText(applyTone(m.text));
            txtTime.setText(m.time == null ? "" : m.time);

            if (m.rating != null) {
                txtRatedTag.setVisibility(View.VISIBLE);
                txtRatedTag.setText("up".equals(m.rating) ? "👍" : "👎");
            } else {
                txtRatedTag.setVisibility(View.GONE);
            }

            actRateUp.setAlpha("up".equals(m.rating) ? 1f : 0.55f);
            actRateDown.setAlpha("down".equals(m.rating) ? 1f : 0.55f);

            bubbleTouchWrap.setOnTouchListener((view, event) -> handleLongPress(event));

            actRephrase.setOnClickListener(x -> { reactionBar.setVisibility(View.GONE); if (listener != null) listener.onRephrase(m); });
            actExplain.setOnClickListener(x -> { reactionBar.setVisibility(View.GONE); if (listener != null) listener.onExplainMore(m); });
            actRateUp.setOnClickListener(x -> { reactionBar.setVisibility(View.GONE); if (listener != null) listener.onRate(m.id, "up"); });
            actRateDown.setOnClickListener(x -> { reactionBar.setVisibility(View.GONE); if (listener != null) listener.onRate(m.id, "down"); });

            bindCard(m.card);
        }

        String applyTone(String text) {
            if (text == null) return "";
            if ("direct".equals(tone)) {
                return text.replaceAll("[\\p{So}\\p{Cn}]", "").replaceAll("\\s+", " ").trim();
            }
            if ("optimistic".equals(tone) && !text.matches(".*[\\p{So}].*")) {
                return text + " ✨";
            }
            return text;
        }

        void bindCard(AdultChatCardData card) {
            cardSlot.removeAllViews();
            if (card == null) { cardSlot.setVisibility(View.GONE); return; }
            cardSlot.setVisibility(View.VISIBLE);
            LayoutInflater inflater = LayoutInflater.from(cardSlot.getContext());
            View cardView;
            switch (card.type) {
                case EXERCISE: {
                    cardView = inflater.inflate(R.layout.adult_chat_item_card_exercise, cardSlot, false);
                    ((TextView) cardView.findViewById(R.id.txtTitle)).setText(card.title);
                    ((TextView) cardView.findViewById(R.id.txtDuration)).setText(card.duration);
                    ((TextView) cardView.findViewById(R.id.txtBadge)).setText(card.badge);
                    ((TextView) cardView.findViewById(R.id.txtDesc)).setText(card.desc);
                    cardView.findViewById(R.id.btnCta).setOnClickListener(x -> onCardTap(cardView, "exercise"));
                    pulseIconRing(cardView.findViewById(R.id.iconPulseRing));
                    break;
                }
                case DHIKR: {
                    cardView = inflater.inflate(R.layout.adult_chat_item_card_dhikr, cardSlot, false);
                    ((TextView) cardView.findViewById(R.id.txtTitle)).setText(card.title);
                    ((TextView) cardView.findViewById(R.id.txtQuote)).setText(card.quote);
                    cardView.findViewById(R.id.btnCta).setOnClickListener(x -> onCardTap(cardView, "dhikr"));
                    break;
                }
                case ARTICLE: {
                    cardView = inflater.inflate(R.layout.adult_chat_item_card_article, cardSlot, false);
                    ((TextView) cardView.findViewById(R.id.txtTag)).setText(card.tag);
                    ((TextView) cardView.findViewById(R.id.txtHeadline)).setText(card.headline);
                    ((TextView) cardView.findViewById(R.id.txtSub)).setText(card.sub);
                    TextView preview = cardView.findViewById(R.id.txtPreview);
                    preview.setText(card.preview);
                    View expand = cardView.findViewById(R.id.articleExpand);
                    ImageView arrow = cardView.findViewById(R.id.imgArrow);
                    View header = cardView.findViewById(R.id.articleHeader);
                    final boolean[] expanded = {false};
                    header.setOnClickListener(x -> {
                        expanded[0] = !expanded[0];
                        animateExpand(expand, expanded[0]);
                        arrow.animate().rotation(expanded[0] ? 90f : 0f).setDuration(220).start();
                    });
                    cardView.findViewById(R.id.btnCta).setOnClickListener(x -> onCardTap(cardView, "article"));
                    break;
                }
                case VIDEO: {
                    cardView = inflater.inflate(R.layout.adult_chat_item_card_video, cardSlot, false);
                    ((TextView) cardView.findViewById(R.id.txtTag)).setText(card.tag);
                    ((TextView) cardView.findViewById(R.id.txtTitle)).setText(card.title);
                    ((TextView) cardView.findViewById(R.id.txtDuration)).setText(card.duration);
                    cardView.setOnClickListener(x -> onCardTap(cardView, "video"));
                    pulseRing(cardView.findViewById(R.id.ring1), 0);
                    pulseRing(cardView.findViewById(R.id.ring2), 1200);
                    break;
                }
                case MOMENT: {
                    cardView = inflater.inflate(R.layout.adult_chat_item_card_moment, cardSlot, false);
                    ((TextView) cardView.findViewById(R.id.txtTrail)).setText(card.trail);
                    cardView.setOnClickListener(x -> onCardTap(cardView, "moment"));
                    break;
                }
                default: {
                    cardView = inflater.inflate(R.layout.adult_chat_item_card_sos, cardSlot, false);
                    cardView.findViewById(R.id.btnCta).setOnClickListener(x -> onCardTap(cardView, "sos"));
                    pulseRing(cardView.findViewById(R.id.sosPulse), 0);
                    breatheCta(cardView.findViewById(R.id.btnCta));
                    break;
                }
            }
            cardView.setScaleX(0.94f); cardView.setScaleY(0.94f); cardView.setAlpha(0f);
            cardView.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300)
                    .setInterpolator(new OvershootInterpolator(1.4f)).setStartDelay(80).start();
            cardSlot.addView(cardView);
        }

        void onCardTap(View v, String type) {
            v.animate().scaleX(0.98f).scaleY(0.98f).setDuration(80).withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(120).start()).start();
            if (listener != null) listener.onCardAction(type);
        }

        void animateExpand(View target, boolean expand) {
            target.measure(View.MeasureSpec.makeMeasureSpec(((View) target.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED);
            int targetHeight = expand ? target.getMeasuredHeight() : 0;
            int startHeight = target.getHeight();
            ValueAnimator anim = ValueAnimator.ofInt(startHeight, targetHeight);
            anim.setDuration(300);
            anim.addUpdateListener(a -> {
                target.getLayoutParams().height = (int) a.getAnimatedValue();
                target.requestLayout();
            });
            anim.start();
        }

        void pulseIconRing(View ring) {
            if (ring == null) return;
            ValueAnimator anim = ValueAnimator.ofFloat(0.9f, 1.12f, 0.9f);
            anim.setDuration(2600);
            anim.setRepeatCount(ValueAnimator.INFINITE);
            anim.addUpdateListener(a -> {
                float s = (float) a.getAnimatedValue();
                ring.setScaleX(s); ring.setScaleY(s);
            });
            anim.start();
        }

        void pulseRing(View ring, long delay) {
            if (ring == null) return;
            ring.setScaleX(1f); ring.setScaleY(1f); ring.setAlpha(0.7f);
            ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
            anim.setDuration(2400);
            anim.setStartDelay(delay);
            anim.setRepeatCount(ValueAnimator.INFINITE);
            anim.addUpdateListener(a -> {
                float t = (float) a.getAnimatedValue();
                float s = 1f + t * 0.9f;
                ring.setScaleX(s); ring.setScaleY(s);
                ring.setAlpha(0.7f * (1f - t));
            });
            anim.start();
        }

        void breatheCta(View cta) {
            ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f, 0f);
            anim.setDuration(2400);
            anim.setRepeatCount(ValueAnimator.INFINITE);
            anim.addUpdateListener(a -> cta.setAlpha(0.85f + 0.15f * (float) a.getAnimatedValue()));
            anim.start();
        }

        boolean handleLongPress(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    pending = () -> {
                        reactionBar.setVisibility(View.VISIBLE);
                        reactionBar.setAlpha(0f); reactionBar.setScaleX(0.8f); reactionBar.setScaleY(0.8f);
                        reactionBar.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(180).start();
                    };
                    handler.postDelayed(pending, LONG_PRESS_MS);
                    return false;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (pending != null) handler.removeCallbacks(pending);
                    return false;
                default:
                    return false;
            }
        }
    }
}
