package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class ArticleListFragment extends Fragment implements ArticleAdapter.Listener {

    public static final String MODE_FAVORITES = "favorites";
    public static final String MODE_BOOKMARKS = "bookmarks";
    private static final String ARG_MODE = "arg_mode";

    private String mode;
    private ArticleAdapter adapter;
    private RecyclerView recyclerArticles;
    private TextView tvEmpty;

    public static ArticleListFragment newInstance(String mode) {
        ArticleListFragment f = new ArticleListFragment();
        Bundle b = new Bundle();
        b.putString(ARG_MODE, mode);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.articles_fragment_article_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mode = requireArguments().getString(ARG_MODE, MODE_FAVORITES);

        View topBar = view.findViewById(R.id.topBar);
        TextView title = topBar.findViewById(R.id.tvTopBarTitle);
        ImageView rightIcon = new ImageView(getContext());
        addRightIcon(topBar, rightIcon);
        topBar.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());

        boolean isFav = MODE_FAVORITES.equals(mode);
        title.setText(isFav ? R.string.fav_articles_title : R.string.bookmark_articles_title);
        rightIcon.setImageResource(isFav ? R.drawable.ic_heart : R.drawable.ic_bookmark);
        rightIcon.setPadding(8, 8, 8, 8);

        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvEmpty.setText(isFav ? R.string.empty_fav_articles : R.string.empty_bookmark_articles);

        recyclerArticles = view.findViewById(R.id.recyclerArticles);
        recyclerArticles.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ArticleAdapter(this);
        recyclerArticles.setAdapter(adapter);

        refresh();
    }

    private void addRightIcon(View topBar, ImageView icon) {
        ViewGroup slot = topBar.findViewById(R.id.topBarRightSlot);
        slot.setBackgroundResource(R.drawable.bg_circle_surface);
        slot.addView(icon, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        List<Article> list = new ArrayList<>();
        List<Integer> ids = MODE_FAVORITES.equals(mode) ? AppState.get().getSavedIds() : AppState.get().getBookmarkedIds();
        for (Article a : ArticleRepository.getAll()) {
            if (ids.contains(a.id)) list.add(a);
        }
        adapter.submitList(list);
        boolean empty = list.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerArticles.setVisibility(empty ? View.GONE : View.VISIBLE);
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
        if (MODE_FAVORITES.equals(mode)) refresh();
    }

    @Override
    public void onToggleBookmark(Article article) {
        AppState.get().toggleBookmarked(article.id);
        if (MODE_BOOKMARKS.equals(mode)) refresh();
    }
}
