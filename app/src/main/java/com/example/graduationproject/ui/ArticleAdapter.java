package com.example.graduationproject.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.data.AppState;
import com.example.graduationproject.models.Article;
import com.example.graduationproject.models.ArticleCategory;
import com.example.graduationproject.ui.WaveArtView;

import java.util.ArrayList;
import java.util.List;

/**
 * Java port of the ArticleCard component + its list-rendering loop in Library/ArticleListScreen.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.VH> {

    public interface Listener {
        void onOpen(Article article);
        void onToggleFavorite(Article article);
        void onToggleBookmark(Article article);
    }

    private final List<Article> items = new ArrayList<>();
    private final Listener listener;

    public ArticleAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Article> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.articles_item_article_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Article a = items.get(position);
        AppState state = AppState.get();
        boolean saved = state.isSaved(a.id);
        boolean bookmarked = state.isBookmarked(a.id);

        holder.waveArt.setColors(ArticleCategory.gradientColors(holder.itemView.getContext(), a.category));
        holder.tvCategory.setText(ArticleCategory.getLabel(a.category) + "  ·  " + ArticleCategory.englishLabel(a.category));
        holder.tvTitle.setText(a.title);

        if (a.reason != null && !a.reason.isEmpty()) {
            holder.tvSuggestionReason.setVisibility(View.VISIBLE);
            holder.tvSuggestionReason.setText(a.reason);
            holder.vSpacer.setVisibility(View.GONE);
        } else {
            holder.tvSuggestionReason.setVisibility(View.GONE);
            holder.vSpacer.setVisibility(View.VISIBLE);
        }

        holder.tvRatingBadge.setText("♥ rating " + a.rating);
        holder.tvExerciseBadge.setVisibility(a.hasExercise ? View.VISIBLE : View.GONE);
        holder.tvPrice.setText(a.price);
        holder.tvTime.setText(a.time);
        holder.tvRating.setText(a.rating + " ★");

        holder.btnFavorite.setImageResource(saved ? R.drawable.ic_heart : R.drawable.ic_heart_outline);
        holder.btnBookmark.setImageResource(bookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
        if (bookmarked) {
            holder.btnBookmark.setColorFilter(android.graphics.Color.parseColor("#3A74B8"));
        } else {
            holder.btnBookmark.setColorFilter(android.graphics.Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            Animation press = AnimationUtils.loadAnimation(v.getContext(), R.anim.card_press);
            v.startAnimation(press);
            if (listener != null) {
                v.postDelayed(() -> listener.onOpen(a), 90);
            }
        });

        holder.btnFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleFavorite(a);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        holder.btnBookmark.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleBookmark(a);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        holder.btnReadNow.setOnClickListener(v -> {
            if (listener != null) listener.onOpen(a);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        WaveArtView waveArt;
        TextView tvCategory, tvTitle, tvRatingBadge, tvExerciseBadge, tvPrice, tvTime, tvRating, btnReadNow, tvSuggestionReason;
        ImageButton btnFavorite, btnBookmark;
        View vSpacer;

        VH(@NonNull View itemView) {
            super(itemView);
            waveArt = itemView.findViewById(R.id.waveArt);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRatingBadge = itemView.findViewById(R.id.tvRatingBadge);
            tvExerciseBadge = itemView.findViewById(R.id.tvExerciseBadge);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRating = itemView.findViewById(R.id.tvRating);
            btnReadNow = itemView.findViewById(R.id.btnReadNow);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
            tvSuggestionReason = itemView.findViewById(R.id.tvSuggestionReason);
            vSpacer = itemView.findViewById(R.id.spacer);
        }
    }
}
