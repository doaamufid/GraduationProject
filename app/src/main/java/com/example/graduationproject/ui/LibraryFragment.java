package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.ArticlesActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.models.Article;
import com.example.graduationproject.models.ArticleRepository;
import com.example.graduationproject.models.CategoryStyle;
import com.example.graduationproject.widget.ArticleArtBinder;
import com.example.graduationproject.widget.TapBounce;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Equivalent of <Library/>: search bar (visual only, matching the
 * original's non-functional input), category filter chips, a featured
 * hero card (shown only on the "الكل" tab), and a 2-column grid of
 * <ArticleCard/> items.
 */
public class LibraryFragment extends Fragment {

    private String currentCategory = ArticleRepository.CAT_ALL;
    private final Set<Integer> savedIds = new HashSet<>();

    private RecyclerView recyclerCategories, recyclerArticles;
    private CategoryAdapter categoryAdapter;
    private ArticleGridAdapter gridAdapter;

    private View groupFeatured;
    private TextView tvSectionLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_library, container, false);

        TopBarHelper.bind(root, getString(R.string.library_title),
                () -> {
                    // matches `onBack` not being wired in the original Library screen (root level, no-op)
                }, null);

        recyclerCategories = root.findViewById(R.id.recyclerCategories);
        recyclerArticles = root.findViewById(R.id.recyclerArticles);
        groupFeatured = root.findViewById(R.id.groupFeatured);
        tvSectionLabel = root.findViewById(R.id.tvSectionLabel);

        recyclerCategories.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter();
        recyclerCategories.setAdapter(categoryAdapter);

        recyclerArticles.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        gridAdapter = new ArticleGridAdapter();
        recyclerArticles.setAdapter(gridAdapter);

        bindFeaturedCard(root);
        groupFeatured.setOnClickListener(v -> {
            Article featured = ArticleRepository.getFeatured();
            if (featured != null) openArticle(featured);
        });
        TapBounce.attach(groupFeatured);

        render();
        return root;
    }

    private void bindFeaturedCard(View root) {
        Article featured = ArticleRepository.getFeatured();
        if (featured == null) {
            groupFeatured.setVisibility(View.GONE);
            return;
        }
        CategoryStyle style = ArticleRepository.styleFor(featured.category);

        View includeArt = root.findViewById(R.id.includeFeaturedArt);
        ArticleArtBinder.bind(includeArt, featured.category, 130);

        TextView tvTag = root.findViewById(R.id.tvFeaturedTag);
        TextView tvTime = root.findViewById(R.id.tvFeaturedTime);
        TextView tvTitle = root.findViewById(R.id.tvFeaturedTitle);
        TextView tvAuthor = root.findViewById(R.id.tvFeaturedAuthor);

        tvTag.setText(featured.tagEn);
        tvTime.setText(featured.time);
        tvTitle.setText(featured.title);
        tvAuthor.setText(featured.author);
    }

    private void render() {
        boolean showFeatured = currentCategory.equals(ArticleRepository.CAT_ALL);
        groupFeatured.setVisibility(showFeatured ? View.VISIBLE : View.GONE);

        tvSectionLabel.setText(showFeatured
                ? getString(R.string.section_this_week)
                : getString(R.string.section_category_format, currentCategory));

        categoryAdapter.notifyDataSetChanged();
        gridAdapter.submit(ArticleRepository.getGridArticles(currentCategory));
    }

    private void openArticle(Article article) {
        if (getActivity() instanceof ArticlesActivity) {
            ((ArticlesActivity) getActivity()).openReader(article);
        }
    }

    private boolean toggleSave(int articleId) {
        boolean nowSaved;
        if (savedIds.contains(articleId)) {
            savedIds.remove(articleId);
            nowSaved = false;
        } else {
            savedIds.add(articleId);
            nowSaved = true;
        }
        return nowSaved;
    }

    // ===================== Category chips adapter =====================

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView chip = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category_chip, parent, false);
            return new VH(chip);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            String category = ArticleRepository.CATEGORIES.get(position);
            holder.chip.setText(category);

            boolean selected = category.equals(currentCategory);
            holder.chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
            holder.chip.setTextColor(getResources().getColor(selected ? R.color.white : R.color.text_soft));

            holder.chip.setOnClickListener(v -> {
                currentCategory = category;
                render();
            });
        }

        @Override
        public int getItemCount() {
            return ArticleRepository.CATEGORIES.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView chip;

            VH(@NonNull View itemView) {
                super(itemView);
                chip = (TextView) itemView;
            }
        }
    }

    // ===================== Article grid adapter =====================

    private class ArticleGridAdapter extends RecyclerView.Adapter<ArticleGridAdapter.VH> {
        private List<Article> items = new ArrayList<>();

        void submit(List<Article> newItems) {
            items = newItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View card = ArticleCardBinder.create(LayoutInflater.from(parent.getContext()), parent);
            // Half-width column with a small gutter, matching `grid grid-cols-2 gap-3`.
            GridLayoutManager.LayoutParams lp = new GridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int gutter = (int) (6 * getResources().getDisplayMetrics().density);
            lp.setMargins(gutter, gutter, gutter, gutter);
            card.setLayoutParams(lp);
            return new VH(card);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Article article = items.get(position);
            ArticleCardBinder.bind(holder.itemView, article, savedIds.contains(article.id),
                    LibraryFragment.this::openArticle, LibraryFragment.this::toggleSave);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class VH extends RecyclerView.ViewHolder {
            VH(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
