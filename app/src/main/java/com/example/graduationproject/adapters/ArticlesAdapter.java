package com.example.graduationproject.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.graduationproject.R;
import com.example.graduationproject.models.ArticleModel;
import java.util.ArrayList;
import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> {

    private List<ArticleModel> articlesList;
    private List<ArticleModel> fullList;
    private OnArticleClickListener listener;

    public interface OnArticleClickListener {
        void onArticleClick(ArticleModel article);
        void onBookmarkClick(ArticleModel article, int position);
    }

    public ArticlesAdapter(List<ArticleModel> articlesList, OnArticleClickListener listener) {
        this.articlesList = new ArrayList<>(articlesList);
        this.fullList = new ArrayList<>(articlesList);
        this.listener = listener;
    }

    public void updateList(List<ArticleModel> newList) {
        this.articlesList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_card, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleModel article = articlesList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(article.getTitle());
        holder.tvTag.setText(article.getTag());

        holder.badgesContainer.removeAllViews();

        if (article.getBadges() != null) {
            for (String badgeText : article.getBadges()) {
                LinearLayout badgeLayout = new LinearLayout(context);
                badgeLayout.setOrientation(LinearLayout.HORIZONTAL);
                badgeLayout.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, intToDp(context, 16), 0);
                badgeLayout.setLayoutParams(lp);

                ImageView icon = new ImageView(context);
                icon.setImageResource(R.drawable.ic_heart_filled_white);
                icon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#7A8B9B")));
                icon.setLayoutParams(new LinearLayout.LayoutParams(intToDp(context, 14), intToDp(context, 14)));
                icon.setAlpha(0.6f);

                TextView tv = new TextView(context);
                tv.setText(badgeText);
                tv.setTextSize(12);
                tv.setTextColor(Color.parseColor("#7A8B9B"));
                LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvLp.setMargins(intToDp(context, 6), 0, 0, 0);
                tv.setLayoutParams(tvLp);

                badgeLayout.addView(icon);
                badgeLayout.addView(tv);
                holder.badgesContainer.addView(badgeLayout);
            }
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onArticleClick(article);
        });

        if (holder.btnRead != null) {
            holder.btnRead.setOnClickListener(v -> {
                if (listener != null) listener.onArticleClick(article);
            });
        }

        if (holder.cardBookmark != null) {
            holder.cardBookmark.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookmarkClick(article, position);
                    // Toggle effect (simple visual feedback)
                    ImageView iv = holder.cardBookmark.findViewById(R.id.ivBookmarkIcon);
                    if (iv != null) {
                        iv.setImageResource(R.drawable.ic_bookmark_filled);
                        Toast.makeText(context, "تمت الإضافة للمفضلة", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Staggered entrance animation for each card
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(50f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(200 + (position * 100L))
                .start();
    }

    private int intToDp(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvTitle;
        LinearLayout badgesContainer;
        View btnRead, cardBookmark;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvCardTag);
            tvTitle = itemView.findViewById(R.id.tvArticleTitle);
            badgesContainer = itemView.findViewById(R.id.badgesContainer);
            btnRead = itemView.findViewById(R.id.btnRead);
            cardBookmark = itemView.findViewById(R.id.cardBookmark);
        }
    }
}