package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.adapters.CategoryAdapter;
import com.example.graduationproject.adapters.VideoAdapter;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityVideosBinding;
import com.example.graduationproject.models.ChildProfile;
import com.example.graduationproject.models.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class VideosActivity extends AppCompatActivity {

    private ActivityVideosBinding binding;
    private ChildProfileStore dbStore;
    private VideoAdapter videoAdapter;
    private long childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVideosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbStore = new ChildProfileStore(this);
        childId = getChildId();

        // 🌟 تحميل الأفاتار الخاص بكِ
        loadChildAvatar();

        setupVideosGrid();      // 1) إنشاء المحول أولاً
        setupCategoryFilter();  // 2) تطبيق الفلترة
        setupFavoritesButton(); // 3) ربط زر شاشة المفضلة (من كود صديقتك)
    }

    private void setupCategoryFilter() {
        binding.categoryRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<String> categories = new ArrayList<>();
        categories.add("لعبة");
        categories.add("صداقة");
        categories.add("نوم");
        categories.add("مشاعر");

        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, this::filterVideos);
        binding.categoryRecycler.setAdapter(categoryAdapter);

        // التصنيف الافتراضي
        filterVideos(categories.get(0));
    }

    private void setupVideosGrid() {
        binding.videosRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        videoAdapter = new VideoAdapter(new ArrayList<>(), video -> {
            // فتح شاشة القصة المولّدة عبر Gemini مع تمرير البيانات و ID الطفل
            Intent intent = new Intent(VideosActivity.this, StoryPlaybackActivity.class);
            intent.putExtra(StoryPlaybackActivity.EXTRA_CATEGORY, video.getCategory());
            intent.putExtra(StoryPlaybackActivity.EXTRA_TITLE, video.getTitle());
            intent.putExtra("CHILD_ID", childId);
            startActivity(intent);
        });
        binding.videosRecycler.setAdapter(videoAdapter);
    }

    // 🌟 ربط زر المفضلة (من كود صديقتك)
    private void setupFavoritesButton() {
        if (binding.favoritesEntryButton != null) {
            binding.favoritesEntryButton.setOnClickListener(v -> {
                Intent intent = new Intent(VideosActivity.this, FavoriteStoriesActivity.class);
                intent.putExtra("CHILD_ID", childId);
                startActivity(intent);
            });
        }
    }

    private void filterVideos(String category) {
        List<VideoItem> filtered = dbStore.getVideosByCategory(category);
        android.util.Log.d("VideosDebug", "Category: " + category + " | Count: " + filtered.size());

        videoAdapter.updateList(filtered);

        binding.emptyStateText.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        binding.videosRecycler.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // 🌟 دالة تحميل الأفاتار الخاصة بكِ
    private void loadChildAvatar() {
        try {
            List<ChildProfile> profiles = dbStore.getProfiles();
            for (ChildProfile profile : profiles) {
                if (profile.getId() == childId) {
                    String avatar = profile.getAvatar();
                    if (binding.bearIcon != null && avatar != null && !avatar.trim().isEmpty()) {
                        binding.bearIcon.setText(avatar);
                    }
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private long getChildId() {
        long id = getIntent().getLongExtra("CHILD_ID", -1L);
        return (id == -1L) ? 1L : id;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbStore != null) {
            dbStore.close();
        }
        binding = null; // تفادي تسريب الذاكرة
    }
}