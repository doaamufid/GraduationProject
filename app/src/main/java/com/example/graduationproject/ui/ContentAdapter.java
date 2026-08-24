package com.example.graduationproject.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.data.AppState;
import com.example.graduationproject.models.ContentItem;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter that renders {@link ContentItem} objects as video
 * library cards (thumbnail + gradient + play/headphones icon + metadata).
 *
 * <p>Each card is clickable and triggers {@link Listener#onOpen(ContentItem)}
 * which the host Activity forwards to the player screen.</p>
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.VH> {

    /** Callbacks consumed by the host Activity. */
    public interface Listener {
        void onOpen(ContentItem item);
        void onToggleFavorite(ContentItem item);
        void onToggleBookmark(ContentItem item);
    }

    private final List<ContentItem> items = new ArrayList<>();
    private final Listener listener;

    public ContentAdapter(Listener listener) {
        this.listener = listener;
    }

    /** Replace the current list (same semantics as ArticleAdapter.submitList). */
    public void submitList(List<ContentItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ContentItem item = items.get(position);

        // 1. Background (mapped to the new ivCardBackground)
        // Note: Using the gradient logic for now. If you have an image URL, use Glide here.
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{item.gradStart, item.gradEnd});
        holder.ivCardBackground.setBackground(gradient);

        // 2. Top Right: Date (mapped from item.duration)
        holder.tvDate.setText(item.duration);

        // 3. Bottom Left: Title & Subtitle (mapped from title & source)
        holder.tvMainTitle.setText(item.title);
        holder.tvSubTitle.setText(item.src);

        // 4. Top Left: Brand Logo (mapped from item.type)
        holder.tvBrandLogo.setText(item.type);

        // Card click → open player
        holder.itemView.setOnClickListener(v -> {
            Animation press = AnimationUtils.loadAnimation(v.getContext(), R.anim.card_press);
            v.startAnimation(press);
            v.postDelayed(() -> listener.onOpen(item), 90);
        });

        // Pill button click ("Learn more" / Watch now)
        holder.btnLearnMore.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(70).withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(70).start()).start();
            listener.onOpen(item);
        });

        // Staggered entrance animation
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(50f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(100 + position * 80L)
                .start();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int dp(VH holder, int value) {
        return (int) (value * holder.itemView.getResources().getDisplayMetrics().density);
    }


    static class VH extends RecyclerView.ViewHolder {
        ImageView ivCardBackground;
        TextView tvBrandLogo, tvDate, tvMainTitle, tvSubTitle, btnLearnMore;

        VH(@NonNull View itemView) {
            super(itemView);
            ivCardBackground = itemView.findViewById(R.id.ivCardBackground);
            tvBrandLogo = itemView.findViewById(R.id.tvBrandLogo);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMainTitle = itemView.findViewById(R.id.tvMainTitle);
            tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
            btnLearnMore = itemView.findViewById(R.id.btnLearnMore);
        }
    }
}
