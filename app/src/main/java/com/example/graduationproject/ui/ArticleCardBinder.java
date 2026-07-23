package com.example.graduationproject.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Article;
import com.example.graduationproject.models.ArticleRepository;
import com.example.graduationproject.models.CategoryStyle;
import com.example.graduationproject.widget.ArticleArtBinder;
import com.example.graduationproject.widget.TapBounce;

/**
 * Builds and binds a single {@code item_article_card.xml} view.
 * Equivalent of the <ArticleCard/> component - reused both by the
 * library grid (RecyclerView) and the reader's "continue reading"
 * suggestions (plain inflated views), exactly like the original reuses
 * <ArticleCard/> in both places.
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

        View includeArt = card.findViewById(R.id.includeArt);
        ArticleArtBinder.bind(includeArt, article.category, 84);

        ImageButton btnBookmark = card.findViewById(R.id.btnBookmark);
        TextView tvBadge = card.findViewById(R.id.tvCardTag);
        TextView tvTitle = card.findViewById(R.id.tvArticleTitle);
        TextView tvTime = card.findViewById(R.id.tvTime);

        int accentColor = card.getResources().getColor(style.accentColorRes);
        int badgeBg = card.getResources().getColor(style.badgeBgColorRes);

        tvBadge.setText(article.category);
        tvBadge.setTextColor(accentColor);
        tvBadge.getBackground().mutate().setTint(badgeBg);

        tvTitle.setText(article.title);
        tvTime.setText(article.time);

        updateBookmarkIcon(btnBookmark, saved, accentColor);

        card.setOnClickListener(v -> onOpen.open(article));
        TapBounce.attach(card);

        btnBookmark.setOnClickListener(v -> {
            boolean newState = onSaveToggle.toggle(article.id);
            updateBookmarkIcon(btnBookmark, newState, accentColor);
        });
    }

    private static void updateBookmarkIcon(ImageButton btn, boolean saved, int accentColor) {
        btn.setImageResource(saved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
        btn.setColorFilter(accentColor);
    }
}
