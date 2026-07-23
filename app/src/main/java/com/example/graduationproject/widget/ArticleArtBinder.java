package com.example.graduationproject.widget;

import android.view.View;
import android.widget.ImageView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.ArticleRepository;
import com.example.graduationproject.models.CategoryStyle;

/**
 * Binds the reusable {@code view_article_art.xml} include: sets the
 * per-category gradient background and centers the matching white icon,
 * with a larger icon for taller (hero) art. Equivalent of the
 * <ArticleArt cat={..} height={..}/> component.
 */
public final class ArticleArtBinder {

    private ArticleArtBinder() {
    }

    /** @param heightDp matches the `height` prop; icon scales up above 100dp, exactly like the original. */
    public static void bind(View artRoot, String category, int heightDp) {
        CategoryStyle style = ArticleRepository.styleFor(category);

        View bg = artRoot.findViewById(R.id.artBg);
        ImageView icon = artRoot.findViewById(R.id.artIcon);

        bg.setBackgroundResource(style.artBackgroundRes);
        icon.setImageResource(style.iconRes);

        int sizeDp = heightDp > 100 ? 30 : 22;
        float density = artRoot.getResources().getDisplayMetrics().density;
        int sizePx = (int) (sizeDp * density);
        android.view.ViewGroup.LayoutParams lp = icon.getLayoutParams();
        lp.width = sizePx;
        lp.height = sizePx;
        icon.setLayoutParams(lp);

        // Also apply the target height to the whole art container.
        android.view.ViewGroup.LayoutParams rootLp = artRoot.getLayoutParams();
        rootLp.height = (int) (heightDp * density);
        artRoot.setLayoutParams(rootLp);
    }
}
