package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.ContentItemHost;
import com.example.graduationproject.R;
import com.example.graduationproject.data.AppState;
import com.example.graduationproject.models.ContentItem;
import com.example.graduationproject.models.ContentRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Favorites / bookmarks list for video &amp; podcast content — the video-flow
 * equivalent of {@link ArticleListFragment}. Two modes:
 * {@link #MODE_FAVORITES} (heart) and {@link #MODE_BOOKMARKS} (save).
 */
public class VideoContentListFragment extends Fragment implements ContentAdapter.Listener {

    public static final String MODE_FAVORITES = "favorites";
    public static final String MODE_BOOKMARKS = "bookmarks";
    private static final String ARG_MODE = "arg_mode";

    private String mode;
    private ContentAdapter adapter;
    private RecyclerView recyclerContent;
    private TextView tvEmpty;

    public static VideoContentListFragment newInstance(String mode) {
        VideoContentListFragment f = new VideoContentListFragment();
        Bundle b = new Bundle();
        b.putString(ARG_MODE, mode);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mode = requireArguments().getString(ARG_MODE, MODE_FAVORITES);
        boolean isFav = MODE_FAVORITES.equals(mode);

        // Top bar: title + back + matching action icon (mirrors ArticleListFragment)
        ImageButton rightIcon = new ImageButton(requireContext());
        rightIcon.setBackgroundResource(R.drawable.bg_icon_button);
        rightIcon.setImageResource(isFav ? R.drawable.ic_heart : R.drawable.ic_bookmark);
        if (!isFav) {
            rightIcon.setImageTintList(android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_main)));
        }
        int pad = dp(8);
        rightIcon.setPadding(pad, pad, pad, pad);
        rightIcon.setLayoutParams(new ViewGroup.LayoutParams(dp(34), dp(34)));

        TopBarHelper.bind(view,
                getString(isFav ? R.string.fav_content_title : R.string.bookmark_content_list_title),
                null,
                () -> requireActivity().onBackPressed(),
                rightIcon);

        tvEmpty = view.findViewById(R.id.tvEmpty);
        recyclerContent = view.findViewById(R.id.recyclerContent);
        recyclerContent.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContentAdapter(this);
        recyclerContent.setAdapter(adapter);

        refresh();
    }

    private void refresh() {
        boolean isFav = MODE_FAVORITES.equals(mode);
        List<Integer> ids = isFav
                ? AppState.get().getSavedContentIds()
                : AppState.get().getBookmarkedContentIds();

        List<ContentItem> list = new ArrayList<>();
        for (ContentItem c : ContentRepository.getAllItems()) {
            if (ids.contains(c.id)) list.add(c);
        }

        adapter.submitList(list);
        boolean empty = list.isEmpty();
        tvEmpty.setText(isFav ? R.string.empty_fav_content : R.string.empty_bookmark_content);
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerContent.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reflect favorite/bookmark changes made while this list was backgrounded
        refresh();
    }

    // ---- ContentAdapter.Listener ----

    @Override
    public void onOpen(ContentItem item) {
        if (getActivity() instanceof ContentItemHost) {
            ((ContentItemHost) getActivity()).openPlayer(item);
        }
    }
}