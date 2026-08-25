package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.adapters.CategoryAdapter;
import com.example.graduationproject.adapters.CategoryAdapter2;
import com.example.graduationproject.adapters.VideoAdapter;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityVideosBinding;
import com.example.graduationproject.models.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class VideosActivity extends AppCompatActivity {

    private ActivityVideosBinding binding;
    private ChildProfileStore dbStore;
    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =ActivityVideosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbStore = new ChildProfileStore(this);

        setupVideosGrid();     // 1) أنشئ videoAdapter أولاً
        setupCategoryFilter(); // 2) بعدين استخدمه بأمان جوا filterVideos()
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

        // أول تصنيف محدد افتراضياً عند فتح الشاشة
        filterVideos(categories.get(0));
    }

    private void setupVideosGrid() {
        binding.videosRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        videoAdapter = new VideoAdapter(new ArrayList<>(), video -> {
            Intent intent = new Intent(VideosActivity.this, VideoDetailActivity.class);
            intent.putExtra(VideoDetailActivity.EXTRA_TITLE, video.getTitle());
            intent.putExtra(VideoDetailActivity.EXTRA_SUBTITLE, video.getSubtitle());
            intent.putExtra(VideoDetailActivity.EXTRA_THUMBNAIL_NAME, video.getThumbnailName());
            intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_FILE, video.getVideoFile());
            intent.putExtra(VideoDetailActivity.EXTRA_DURATION, video.getDuration());
            startActivity(intent);
        });
        binding.videosRecycler.setAdapter(videoAdapter);
    }

    private void filterVideos(String category) {
        List<VideoItem> filtered = dbStore.getVideosByCategory(category);
        android.util.Log.d("VideosDebug", "Category: " + category + " | Count: " + filtered.size());

        videoAdapter.updateList(filtered);

        binding.emptyStateText.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        binding.videosRecycler.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }
}