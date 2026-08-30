package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.FavoriteStoryAdapter;
import com.example.graduationproject.data.FavoriteStoryDbHelper;
import com.example.graduationproject.models.FavoriteStory;

import java.util.List;

/**
 * شاشة القصص المفضلة: تعرض كل القصص اللي ضافها الطفل من شاشة StoryPlaybackActivity.
 * الضغط على أي قصة يفتحها مباشرة (بدون توليد جديد)، وفي زر حذف على كل عنصر.
 */
public class FavoriteStoriesActivity extends AppCompatActivity {

    private FavoriteStoryDbHelper dbHelper;
    private FavoriteStoryAdapter adapter;

    private RecyclerView favoritesRecycler;
    private View favoritesEmptyLayout;
    private View favoritesBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_stories);

        favoritesRecycler = findViewById(R.id.favoritesRecycler);
        favoritesEmptyLayout = findViewById(R.id.favoritesEmptyLayout);
        favoritesBackButton = findViewById(R.id.favoritesBackButton);

        favoritesBackButton.setOnClickListener(v -> finish());

        dbHelper = new FavoriteStoryDbHelper(this);
        favoritesRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FavoriteStoryAdapter(
                dbHelper.getAllFavorites(),
                this::openStory,
                this::deleteFavorite
        );
        favoritesRecycler.setAdapter(adapter);

        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // نحدّث القائمة كل مرة نرجع فيها للشاشة (ممكن تكون انضافت أو انحذفت قصة من مكان تاني)
        refreshList();
    }

    private void refreshList() {
        List<FavoriteStory> favorites = dbHelper.getAllFavorites();
        adapter.updateList(favorites);

        boolean isEmpty = favorites.isEmpty();
        favoritesEmptyLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        favoritesRecycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void openStory(FavoriteStory story) {
        Intent intent = new Intent(this, StoryPlaybackActivity.class);
        intent.putExtra(StoryPlaybackActivity.EXTRA_CATEGORY, story.getCategory());
        intent.putExtra(StoryPlaybackActivity.EXTRA_TITLE, story.getTitle());
        intent.putExtra(StoryPlaybackActivity.EXTRA_STORY_TEXT, story.getStoryText());
        startActivity(intent);
    }

    private void deleteFavorite(FavoriteStory story) {
        dbHelper.removeFavoriteById(story.getId());
        refreshList();
    }
}