package com.example.graduationproject.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Article;
import com.example.graduationproject.data.ArticleRepository;
import com.example.graduationproject.models.CategoryStyle;
import com.example.graduationproject.widget.TapBounce;

/**
 * Builds and binds a single {@code item_article_card.xml} view.
 */
public final class ArticleCardBinder {

    public interface OnOpen {
        void open(Article article);
    }

    public interface OnSaveToggle {
        /** @return the new saved state after toggling. */
        boolean toggle(int articleId);
    }

    private ArticleCardBinder() {
    }

    public static View create(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.item_article_card, parent, false);
    }

    public static void bind(View card, Article article, boolean saved,
                             OnOpen onOpen, OnSaveToggle onSaveToggle) {
        CategoryStyle style = ArticleRepository.styleFor(article.category);

        ImageView ivHeader = card.findViewById(R.id.ivArticleHeader);
        // ArticleArtBinder.bind(ivHeader, article.category, 84); // ArticleArtBinder expected a FrameLayout?

        View btnBookmark = card.findViewById(R.id.cardBookmark);
        ImageView ivBookmarkIcon = card.findViewById(R.id.ivBookmarkIcon);
        TextView tvBadge = card.findViewById(R.id.tvCardTag);
        TextView tvTitle = card.findViewById(R.id.tvArticleTitle);
        TextView tvTime = card.findViewById(R.id.tvDuration);

        int accentColor = card.getResources().getColor(style.accentColorRes);

        tvBadge.setText(article.category);
        tvBadge.setTextColor(accentColor);

        tvTitle.setText(article.title);
        tvTime.setText(article.time);

        updateBookmarkIcon(ivBookmarkIcon, saved, accentColor);

        card.setOnClickListener(v -> onOpen.open(article));
        TapBounce.attach(card);

        btnBookmark.setOnClickListener(v -> {
            boolean newState = onSaveToggle.toggle(article.id);
            updateBookmarkIcon(ivBookmarkIcon, newState, accentColor);
        });
    }

    private static void updateBookmarkIcon(ImageView iv, boolean saved, int accentColor) {
        iv.setImageResource(saved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
        if (saved) {
            iv.setColorFilter(android.graphics.Color.parseColor("#3A74B8"));
        } else {
            iv.setColorFilter(android.graphics.Color.BLACK);
        }
    }
}
