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

import com.example.graduationproject.R;
import com.example.graduationproject.ContentItemHost;
import com.example.graduationproject.data.AppState;
import com.example.graduationproject.models.ContentItem;
import com.example.graduationproject.models.ContentRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Video-library list screen.
 *
 * <p>Displays {@link ContentItem} objects (videos &amp; podcasts) as cards in a
 * RecyclerView.  Supports category filtering via chips and live search.  Clicking
 * a card navigates to {@link PlayerFragment} through the {@link ContentItemHost}
 * interface implemented by the host Activity.</p>
 */
public class VideoLibraryFragment extends Fragment implements ContentAdapter.Listener {

    private String activeCategory = "الكل";
    private String query = "";

    private LinearLayout chipContainer;
    private RecyclerView recyclerVideos;
    private ContentAdapter adapter;

    public static VideoLibraryFragment newInstance() {
        return new VideoLibraryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chipContainer = view.findViewById(R.id.llCategories);
        recyclerVideos = view.findViewById(R.id.recyclerVideos);
        EditText etSearch = view.findViewById(R.id.etSearch);

        // Back button (top bar)
        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity().onBackPressed());

        recyclerVideos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContentAdapter(this);
        recyclerVideos.setAdapter(adapter);

        buildChips();
        refreshList();

        // Header action buttons (like the articles library top bar)
        view.findViewById(R.id.btnFavContent).setOnClickListener(v ->
                openContentList(VideoContentListFragment.MODE_FAVORITES));
        view.findViewById(R.id.btnBookmarkContent).setOnClickListener(v ->
                openContentList(VideoContentListFragment.MODE_BOOKMARKS));

        // Live search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString().trim();
                refreshList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    /** Opens the favorites / bookmarks list screen (like ArticlesActivity.openFavoriteArticles). */
    private void openContentList(String mode) {
        if (getActivity() == null) return;
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragmentContainer, VideoContentListFragment.newInstance(mode))
                .addToBackStack(null)
                .commit();
    }

    /** Build the horizontal category-filter chips. */
    private void buildChips() {
        chipContainer.removeAllViews();
        String[] categories = ContentRepository.CATEGORIES.toArray(new String[0]);
        String[] icons = {"📚", "😰", "😴", "💔", "👥"};

        for (int i = 0; i < categories.length; i++) {
            String cat = categories[i];
            View chipView = LayoutInflater.from(getContext())
                    .inflate(R.layout.articles_item_category_chip, chipContainer, false);

            TextView tvLabel = chipView.findViewById(R.id.txt_chip);
            TextView tvIcon = chipView.findViewById(R.id.txt_category_icon);
            View ring = chipView.findViewById(R.id.category_ring);
            View container = chipView.findViewById(R.id.category_icon_container);

            tvLabel.setText(cat);
            if (i < icons.length) tvIcon.setText(icons[i]);

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

    /** Filter ContentRepository by active category + search query, then push to adapter. */
    private void refreshList() {
        List<ContentItem> base = ContentRepository.filterByCategory(activeCategory);
        if (query.isEmpty()) {
            adapter.submitList(base);
            return;
        }
        List<ContentItem> filtered = new ArrayList<>();
        for (ContentItem item : base) {
            if (item.title.contains(query) || item.src.contains(query)) {
                filtered.add(item);
            }
        }
        adapter.submitList(filtered);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reflect bookmark state changes made from the player while backgrounded
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    // ---- ContentAdapter.Listener ----

    @Override
    public void onOpen(ContentItem item) {
        if (getActivity() instanceof ContentItemHost) {
            ((ContentItemHost) getActivity()).openPlayer(item);
        }
    }
}