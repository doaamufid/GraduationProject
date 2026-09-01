package com.example.graduationproject.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.graduationproject.R;
import com.example.graduationproject.models.ContentItem;
import com.example.graduationproject.util.YouTubeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter that renders {@link ContentItem} objects as video
 * library cards (thumbnail + gradient + play icon + metadata).
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.VH> {

    private static final String DEBUG_TAG = "YouTubeDebug";

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

        // Define a placeholder/fallback gradient based on item colors
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{item.gradStart, item.gradEnd});
        gradient.setCornerRadius(24 * holder.itemView.getContext().getResources().getDisplayMetrics().density);

        // 1. Background (YouTube Thumbnail)
        if (item.videoId != null && !item.videoId.trim().isEmpty()) {
            
            // Requirements: Use API thumbnail URLs with Glide fallbacks (High -> Medium -> Default)
            String high = item.thumbnailUrl;
            String med = item.mediumThumbnailUrl;
            String def = item.defaultThumbnailUrl;

            Log.d(DEBUG_TAG, "Loading thumbnails for " + item.videoId + " - High: " + high + ", Med: " + med + ", Def: " + def);

            RequestBuilder<android.graphics.drawable.Drawable> defaultRequest = Glide.with(holder.itemView.getContext())
                    .load(def)
                    .centerCrop()
                    .error(gradient);

            RequestBuilder<android.graphics.drawable.Drawable> mediumRequest = Glide.with(holder.itemView.getContext())
                    .load(med)
                    .centerCrop()
                    .thumbnail(defaultRequest);

            Glide.with(holder.itemView.getContext())
                    .load(high)
                    .placeholder(gradient)
                    .thumbnail(mediumRequest)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(holder.ivCardBackground);

        } else {
            com.bumptech.glide.Glide.with(holder.itemView.getContext()).clear(holder.ivCardBackground);
            holder.ivCardBackground.setImageDrawable(gradient);
        }

        // 2. Metadata
        holder.tvBrandLogo.setText(item.type);

        if (item.reason != null && !item.reason.isEmpty()) {
            holder.tvSuggestionReason.setVisibility(View.VISIBLE);
            holder.tvSuggestionReason.setText(item.reason);
        } else {
            holder.tvSuggestionReason.setVisibility(View.GONE);
        }

        holder.tvDate.setText(item.duration);

        // 3. Title & Subtitle (Stats)
        holder.tvMainTitle.setText(item.title);
        
        // Mocking stats like the image: Author • Views • Time
        String stats = item.src + " • " + (10 + (item.id % 90)) + "." + (item.id % 10) + " ألف • " + "قبل يوم";
        holder.tvSubTitle.setText(stats);

        // 4. Author Initial
        if (item.src != null && !item.src.isEmpty()) {
            holder.tvAuthorInitial.setText(String.valueOf(item.src.trim().charAt(0)));
        }

        // 5. Favorite & Bookmark States (NEW)
        com.example.graduationproject.data.AppState state = com.example.graduationproject.data.AppState.get();
        boolean isFav = state.isContentSaved(item.id);
        holder.btnFavorite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        if (isFav) {
            holder.btnFavorite.setColorFilter(android.graphics.Color.RED);
        } else {
            holder.btnFavorite.setColorFilter(android.graphics.Color.WHITE);
        }
        
        boolean isBookmarked = state.isContentBookmarked(item.id);
        holder.btnBookmark.setImageResource(isBookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
        if (isBookmarked) {
            holder.btnBookmark.setColorFilter(android.graphics.Color.parseColor("#3A74B8"));
        } else {
            holder.btnBookmark.setColorFilter(android.graphics.Color.BLACK);
        }

        // 6. Pulse Animation
        startPulseAnimation(holder.vPulse1, 0);
        startPulseAnimation(holder.vPulse2, 1000);

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            Animation press = AnimationUtils.loadAnimation(v.getContext(), R.anim.card_press);
            v.startAnimation(press);
            if (listener != null) {
                v.postDelayed(() -> listener.onOpen(item), 90);
            }
        });

        holder.btnFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleFavorite(item);
            }
        });

        holder.btnBookmark.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleBookmark(item);
            }
        });
    }

    private void startPulseAnimation(View view, long delay) {
        view.clearAnimation();
        
        AnimationSet set = new AnimationSet(true);
        
        ScaleAnimation scale = new ScaleAnimation(1f, 1.8f, 1f, 1.8f, 
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(2000);
        scale.setRepeatCount(Animation.INFINITE);
        
        AlphaAnimation alpha = new AlphaAnimation(0.6f, 0f);
        alpha.setDuration(2000);
        alpha.setRepeatCount(Animation.INFINITE);
        
        set.addAnimation(scale);
        set.addAnimation(alpha);
        set.setStartOffset(delay);
        
        view.startAnimation(set);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivCardBackground;
        TextView tvBrandLogo, tvDate, tvMainTitle, tvSubTitle, tvAuthorInitial, tvSuggestionReason;
        android.widget.ImageButton btnFavorite, btnBookmark;
        View vPulse1, vPulse2;

        VH(@NonNull View itemView) {
            super(itemView);
            ivCardBackground = itemView.findViewById(R.id.ivCardBackground);
            tvBrandLogo = itemView.findViewById(R.id.tvBrandLogo);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMainTitle = itemView.findViewById(R.id.tvMainTitle);
            tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
            tvAuthorInitial = itemView.findViewById(R.id.tvAuthorInitial);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
            vPulse1 = itemView.findViewById(R.id.vPulse1);
            vPulse2 = itemView.findViewById(R.id.vPulse2);
            tvSuggestionReason = itemView.findViewById(R.id.tvSuggestionReason);
        }
    }
}
