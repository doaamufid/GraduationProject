package com.example.graduationproject.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.example.graduationproject.data.ContentRecommendationManager;
import com.example.graduationproject.data.SalamGeminiService;
import com.example.graduationproject.models.CandidateItem;
import com.example.graduationproject.data.YouTubeApiService;
import com.example.graduationproject.models.YouTubeResponse;
import com.example.graduationproject.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Video-library list screen.
 *
 * <p>Displays {@link ContentItem} objects (videos &amp; podcasts) as cards in a
 * RecyclerView.  Supports category filtering via chips and live search.  Clicking
 * a card navigates to {@link PlayerFragment} through the {@link ContentItemHost}
 * interface implemented by the host Activity.</p>
 */
public class VideoLibraryFragment extends Fragment implements ContentAdapter.Listener {

    private static final String DEBUG_TAG = "YouTubeDebug";
    private String activeCategory = "الكل";
    private String query = "";

    private LinearLayout chipContainer;
    private RecyclerView recyclerVideos;
    private ContentAdapter adapter;
    private YouTubeApiService youtubeApi;

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

        // Removed TopBar inset listener as activity_video_library.xml uses fitsSystemWindows="true"

        // Back button (top bar)
        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity().onBackPressed());

        recyclerVideos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContentAdapter(this);
        recyclerVideos.setAdapter(adapter);

        initRetrofit();
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

        fetchRecommendations();
    }

    private void fetchRecommendations() {
        String moodId = requireContext().getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE)
                .getString("today_mood_id", "neutral");

        if (ContentRecommendationManager.shouldRefresh(requireContext(), moodId)) {
            new SalamGeminiService(requireContext()).getSuggestedContent(
                    ContentRecommendationManager.getShortlist(requireContext(), moodId),
                    moodId,
                    new SalamGeminiService.GeminiCallback() {
                        @Override
                        public void onSuccess(String json) {
                            if (isAdded()) {
                                List<ContentRecommendationManager.RecommendationResponse> recs =
                                        ContentRecommendationManager.getCachedRecommendationsFromText(json);
                                if (recs != null) {
                                    ContentRecommendationManager.saveRecommendations(requireContext(), recs, moodId);
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> refreshList());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                        }
                    });
        }
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

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        youtubeApi = retrofit.create(YouTubeApiService.class);
    }

    private void fetchYouTubeVideos(String query) {
        String apiKey = BuildConfig.YOUTUBE_API_KEY;
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY_HERE")) {
            return;
        }

        youtubeApi.getEmbeddableVideos("snippet", query, "video", "true", 10, apiKey)
                .enqueue(new Callback<YouTubeResponse>() {
                    @Override
                    public void onResponse(Call<YouTubeResponse> call, Response<YouTubeResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().items != null) {
                            validateAndDisplayVideos(response.body().items);
                        }
                    }

                    @Override
                    public void onFailure(Call<YouTubeResponse> call, Throwable t) {
                        Log.e(DEBUG_TAG, "Search failed: " + t.getMessage());
                    }
                });
    }

    private void validateAndDisplayVideos(List<YouTubeResponse.YouTubeItem> searchItems) {
        String apiKey = BuildConfig.YOUTUBE_API_KEY;
        StringBuilder idsBuilder = new StringBuilder();
        for (YouTubeResponse.YouTubeItem item : searchItems) {
            String vid = item.getVideoId();
            if (vid != null) {
                if (idsBuilder.length() > 0) idsBuilder.append(",");
                idsBuilder.append(vid);
            }
        }

        if (idsBuilder.length() == 0) {
            adapter.submitList(new ArrayList<>());
            return;
        }

        youtubeApi.getVideoDetails("status", idsBuilder.toString(), apiKey)
                .enqueue(new Callback<YouTubeResponse>() {
                    @Override
                    public void onResponse(Call<YouTubeResponse> call, Response<YouTubeResponse> response) {
                        List<ContentItem> finalItems = new ArrayList<>();
                        Set<String> validVideoIds = new HashSet<>();

                        if (response.isSuccessful() && response.body() != null && response.body().items != null) {
                            for (YouTubeResponse.YouTubeItem detail : response.body().items) {
                                String vid = detail.getVideoId();
                                boolean isPublic = detail.status != null && "public".equals(detail.status.privacyStatus);
                                boolean isEmbeddable = detail.status != null && detail.status.embeddable;
                                
                                if (vid != null && isPublic && isEmbeddable) {
                                    validVideoIds.add(vid);
                                } else {
                                    Log.d(DEBUG_TAG, "Rejecting video: " + vid + " (Public=" + isPublic + ", Embeddable=" + isEmbeddable + ")");
                                }
                            }

                            int i = 0;
                            for (YouTubeResponse.YouTubeItem searchItem : searchItems) {
                                String vid = searchItem.getVideoId();
                                if (validVideoIds.contains(vid)) {
                                    finalItems.add(mapToContentItem(searchItem, i++));
                                }
                            }
                        } else {
                            // On validation API error, don't incorrectly classify all as unavailable.
                            // Requirements: Follow existing error pattern and proceed with search results.
                            Log.e(DEBUG_TAG, "Validation response unsuccessful. Code: " + response.code());
                            int i = 0;
                            for (YouTubeResponse.YouTubeItem searchItem : searchItems) {
                                finalItems.add(mapToContentItem(searchItem, i++));
                            }
                        }

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> adapter.submitList(finalItems));
                        }
                    }

                    @Override
                    public void onFailure(Call<YouTubeResponse> call, Throwable t) {
                        Log.e(DEBUG_TAG, "Validation failed: " + t.getMessage());
                        List<ContentItem> fallbackItems = new ArrayList<>();
                        int i = 0;
                        for (YouTubeResponse.YouTubeItem searchItem : searchItems) {
                            fallbackItems.add(mapToContentItem(searchItem, i++));
                        }
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> adapter.submitList(fallbackItems));
                        }
                    }
                });
    }

    private ContentItem mapToContentItem(YouTubeResponse.YouTubeItem item, int index) {
        String vid = item.getVideoId();
        String high = (item.snippet.thumbnails != null && item.snippet.thumbnails.high != null) ? item.snippet.thumbnails.high.url : null;
        String med = (item.snippet.thumbnails != null && item.snippet.thumbnails.medium != null) ? item.snippet.thumbnails.medium.url : null;
        String def = (item.snippet.thumbnails != null && item.snippet.thumbnails.defaultThumb != null) ? item.snippet.thumbnails.defaultThumb.url : null;

        Log.d(DEBUG_TAG, "Mapping result - videoId: " + vid + ", highUrl: " + high);

        return new ContentItem(
                2000 + index,
                item.snippet.title,
                item.snippet.channelTitle != null ? item.snippet.channelTitle : "YouTube",
                "فيديو",
                true,
                "YouTube",
                activeCategory,
                vid,
                high,
                med,
                def,
                android.graphics.Color.parseColor("#2E5C86"),
                android.graphics.Color.parseColor("#1F3A60"),
                ""
        );
    }

    /** Filter ContentRepository by active category + search query, then push to adapter. */
    private void refreshList() {
        if (!query.isEmpty()) {
            fetchYouTubeVideos(query);
            return;
        }

        List<ContentItem> base = ContentRepository.filterByCategory(activeCategory);
        List<ContentRecommendationManager.RecommendationResponse> recs = ContentRecommendationManager.getCachedRecommendations(requireContext());

        List<ContentItem> processed = new ArrayList<>();
        for (ContentItem item : base) {
            String reason = null;
            if (recs != null) {
                for (ContentRecommendationManager.RecommendationResponse r : recs) {
                    if ("video".equals(r.type) && r.id == item.id) {
                        reason = r.reason;
                        break;
                    }
                }
            }

            ContentItem processedItem = item;
            if (reason != null) {
                processedItem = new ContentItem(item, item.videoId, item.thumbnailUrl, item.mediumThumbnailUrl, item.defaultThumbnailUrl, "🤖 " + reason);
            }

            if (query.isEmpty()) {
                if (reason != null) processed.add(0, processedItem);
                else processed.add(processedItem);
            } else {
                if (item.title.contains(query) || item.src.contains(query)) {
                    processed.add(processedItem);
                }
            }
        }
        adapter.submitList(processed);
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

    @Override
    public void onToggleFavorite(ContentItem item) {
        AppState.get().toggleContentSaved(item.id);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onToggleBookmark(ContentItem item) {
        AppState.get().toggleContentBookmarked(item.id);
        adapter.notifyDataSetChanged();
    }
}
