package com.example.graduationproject.Kids;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityFeaturedChildBinding;
import com.example.graduationproject.models.ChildProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class FeaturedChildActivity extends AppCompatActivity {

    private ActivityFeaturedChildBinding binding;
    private ChildProfileStore childProfileStore;
    private com.example.graduationproject.Kids.FeaturedChildAdapter adapter;
    private final List<ChildProfile> restOfChildren = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityFeaturedChildBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);

        childProfileStore = new ChildProfileStore(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnBack.setOnClickListener(v -> finish());

        adapter = new com.example.graduationproject.Kids.FeaturedChildAdapter(restOfChildren);
        binding.rvRestOfChildren.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRestOfChildren.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLeaderboard();
    }

    @Override
    protected void onDestroy() {
        childProfileStore.close();
        super.onDestroy();
    }

    private void loadLeaderboard() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<ChildProfile> sorted = childProfileStore.getProfilesSortedByStars();
            runOnUiThread(() -> bindLeaderboard(sorted));
        });
    }

    private void bindLeaderboard(List<ChildProfile> sorted) {
        if (sorted.isEmpty()) {
            binding.emptyState.setVisibility(View.VISIBLE);
            binding.featuredCard.setVisibility(View.GONE);
            binding.rvRestOfChildren.setVisibility(View.GONE);
            return;
        }

        binding.emptyState.setVisibility(View.GONE);
        binding.featuredCard.setVisibility(View.VISIBLE);
        binding.rvRestOfChildren.setVisibility(View.VISIBLE);

        int topStars = sorted.get(0).getStars();

        // --- الطفل المميز (الأول بالقائمة) ---
        ChildProfile featured = sorted.get(0);
        binding.tvFeaturedAvatar.setText(featured.getAvatar());
        binding.tvFeaturedName.setText(featured.getName());
        binding.tvFeaturedStarsCount.setText(String.valueOf(featured.getStars()));
        binding.tvFeaturedStarsVisual.setText(
                starsToEmoji(calculateVisualStars(featured.getStars(), topStars)));

        // --- باقي الأطفال ---
        restOfChildren.clear();
        if (sorted.size() > 1) {
            restOfChildren.addAll(sorted.subList(1, sorted.size()));
        }
        adapter.setTopStars(topStars);
        adapter.notifyDataSetChanged();
    }

    private static int calculateVisualStars(int childStars, int topStars) {
        if (topStars <= 0) return 0;
        double ratio = (double) childStars / topStars;
        return (int) Math.round(ratio * 5);
    }

    private static String starsToEmoji(int filledStars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < filledStars ? "⭐" : "☆");
        }
        return sb.toString();
    }
}