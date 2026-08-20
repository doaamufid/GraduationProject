package com.example.graduationproject.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.ArticlesActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.data.AppState;
import com.example.graduationproject.data.ArticleRepository;
import com.example.graduationproject.models.Article;
import com.example.graduationproject.models.ArticleCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Java port of the <Library/> component: category filter chips + search + article feed.
 */
public class LibraryFragment extends Fragment implements ArticleAdapter.Listener {

    private String activeCategory = ArticleCategory.ALL;
    private String query = "";

    private LinearLayout chipContainer;
    private RecyclerView recyclerArticles;
    private ArticleAdapter adapter;

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.articles_fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chipContainer = view.findViewById(R.id.chipContainer);
        recyclerArticles = view.findViewById(R.id.recyclerArticles);
        EditText etSearch = view.findViewById(R.id.etSearch);

        recyclerArticles.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ArticleAdapter(this);
        recyclerArticles.setAdapter(adapter);

        buildChips();
        refreshList();

        view.findViewById(R.id.btnNotes).setOnClickListener(v -> {
            if (getActivity() instanceof ArticlesActivity) {
                ((ArticlesActivity) getActivity()).openNotes();
            }
        });
        view.findViewById(R.id.btnFavArticles).setOnClickListener(v -> {
            if (getActivity() instanceof ArticlesActivity) {
                ((ArticlesActivity) getActivity()).openFavoriteArticles();
            }
        });
        view.findViewById(R.id.btnBookmarkArticles).setOnClickListener(v -> {
            if (getActivity() instanceof ArticlesActivity) {
                ((ArticlesActivity) getActivity()).openBookmarkedArticles();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString().trim();
                refreshList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void buildChips() {
        chipContainer.removeAllViews();
        for (String cat : ArticleCategory.ALL_TABS) {
            View chipView = LayoutInflater.from(getContext())
                    .inflate(R.layout.articles_item_category_chip, chipContainer, false);
            
            TextView tvLabel = chipView.findViewById(R.id.txt_chip);
            TextView tvIcon = chipView.findViewById(R.id.txt_category_icon);
            View ring = chipView.findViewById(R.id.category_ring);
            View container = chipView.findViewById(R.id.category_icon_container);

            tvLabel.setText(ArticleCategory.getLabel(cat));
            tvIcon.setText(ArticleCategory.getIcon(cat));
            
            boolean isSelected = cat.equals(activeCategory);
            ring.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            container.setBackgroundResource(isSelected ? 0 : R.drawable.bg_category_border);
            tvLabel.setAlpha(isSelected ? 1.0f : 0.6f);
            tvIcon.setAlpha(isSelected ? 1.0f : 0.8f);

            chipView.setOnClickListener(v -> {
                activeCategory = cat;
                buildChips();
                refreshList();
            });
            chipContainer.addView(chipView);
        }
    }

    private void refreshList() {
        List<Article> base = ArticleRepository.getByCategory(activeCategory);
        if (query.isEmpty()) {
            adapter.submitList(base);
            return;
        }
        List<Article> filtered = new ArrayList<>();
        for (Article a : base) {
            if (a.title.contains(query) || a.author.contains(query)) filtered.add(a);
        }
        adapter.submitList(filtered);
    }

    @Override
    public void onResume() {
        super.onResume();
        // reflect any favorite/bookmark changes made from the reader while this list was backgrounded
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onOpen(Article article) {
        if (getActivity() instanceof ArticlesActivity) {
            ((ArticlesActivity) getActivity()).openReader(article);
        }
    }

    @Override
    public void onToggleFavorite(Article article) {
        AppState.get().toggleSaved(article.id);
    }

    @Override
    public void onToggleBookmark(Article article) {
        AppState.get().toggleBookmarked(article.id);
    }
}
