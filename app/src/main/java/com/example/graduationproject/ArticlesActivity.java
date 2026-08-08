package com.example.graduationproject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.adapters.ArticlesAdapter;
import com.example.graduationproject.databinding.ActivityArticlesBinding;
import com.example.graduationproject.models.ArticleModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArticlesActivity extends AppCompatActivity implements ArticlesAdapter.OnArticleClickListener {
    ActivityArticlesBinding binding;
    ArticlesAdapter adapter;
    List<ArticleModel> articlesList;
    List<ArticleModel> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityArticlesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutMainRoot, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnBack.setOnClickListener(v -> finish());

        setupData();
        setupRecyclerView();
        setupSearchAndFilters();
        startEntranceAnimations();
    }

    private void setupData() {
        articlesList = new ArrayList<>();
        articlesList.add(new ArticleModel(
                "التنفس • BREATHING",
                "٧ تقنيات للتنفس في الأزمات الأكثر قراءة",
                Arrays.asList("تمارين", "4.8 rating")
        ));

        articlesList.add(new ArticleModel(
                "علاجي • CBT",
                "كيف تتعاملين مع الحزن بدون قمعه؟",
                Arrays.asList("عربي", "CBT", "5.0 rating")
        ));

        articlesList.add(new ArticleModel(
                "التعلق • ATTACHMENT",
                "لماذا يؤلمنا التعلق؟ دليل علمي بسيط",
                Arrays.asList("٦ دقائق قراءة", "علم النفس")
        ));

        articlesList.add(new ArticleModel(
                "النوم • SLEEP",
                "دليل النوم العميق للتخلص من الأرق",
                Arrays.asList("نوم", "هدوء")
        ));

        filteredList = new ArrayList<>(articlesList);
    }

    private void setupRecyclerView() {
        binding.rvArticles.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ArticlesAdapter(filteredList, this);
        binding.rvArticles.setAdapter(adapter);
    }

    private void setupSearchAndFilters() {
        // Search Logic
        View searchLayout = findViewById(R.id.layoutSearchBarIncluded);
        android.widget.EditText etSearch = searchLayout.findViewById(R.id.etSearch);
        
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter Buttons
        binding.btnFilterAll.setOnClickListener(v -> applyFilter("ALL"));
        binding.btnFilterSleep.setOnClickListener(v -> applyFilter("SLEEP"));
        binding.btnFilterBreathing.setOnClickListener(v -> applyFilter("BREATHING"));
        binding.btnFilterCbt.setOnClickListener(v -> applyFilter("CBT"));

        // Set initial state
        applyFilter("ALL");
    }

    private void applyFilter(String category) {
        // Update UI of buttons
        resetFilterButtons();
        if (category.equals("ALL")) {
            setSelectedStyle(binding.btnFilterAll);
            filteredList = new ArrayList<>(articlesList);
        } else {
            if (category.equals("SLEEP")) setSelectedStyle(binding.btnFilterSleep);
            if (category.equals("BREATHING")) setSelectedStyle(binding.btnFilterBreathing);
            if (category.equals("CBT")) setSelectedStyle(binding.btnFilterCbt);

            filteredList = new ArrayList<>();
            for (ArticleModel item : articlesList) {
                if (item.getTag().toUpperCase().contains(category)) {
                    filteredList.add(item);
                }
            }
        }
        adapter.updateList(filteredList);
    }

    private void resetFilterButtons() {
        setUnselectedStyle(binding.btnFilterAll);
        setUnselectedStyle(binding.btnFilterSleep);
        setUnselectedStyle(binding.btnFilterBreathing);
        setUnselectedStyle(binding.btnFilterCbt);
    }

    private void setSelectedStyle(Button btn) {
        btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#3A74B8")));
        btn.setTextColor(android.graphics.Color.WHITE);
    }

    private void setUnselectedStyle(Button btn) {
        btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#1A3A74B8")));
        btn.setTextColor(android.graphics.Color.parseColor("#3A74B8"));
    }

    private void filter(String query) {
        List<ArticleModel> temp = new ArrayList<>();
        for (ArticleModel item : articlesList) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                item.getTag().toLowerCase().contains(query.toLowerCase())) {
                temp.add(item);
            }
        }
        adapter.updateList(temp);
    }

    @Override
    public void onArticleClick(ArticleModel article) {
        // Here we handle navigation to details
        // Since we don't have a specific ArticleDetailActivity, we show a message
        // Or if it exists under another name, it should be launched here.
        Toast.makeText(this, "فتح المقال: " + article.getTitle(), Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(this, ArticleDetailActivity.class);
        // startActivity(intent);
    }

    @Override
    public void onBookmarkClick(ArticleModel article, int position) {
        // Handle bookmarking logic (e.g. save to database or prefs)
    }

    private void startEntranceAnimations() {
        // Initial state
        binding.tvArticlesSectionTag.setAlpha(0f);
        binding.tvArticlesSectionTag.setTranslationY(20f);
        binding.tvLibraryMainTitle.setAlpha(0f);
        binding.tvLibraryMainTitle.setTranslationY(30f);
        binding.tvLibrarySubTitle.setAlpha(0f);
        binding.tvLibrarySubTitle.setTranslationY(30f);
        binding.layoutSearchBarIncluded.getRoot().setAlpha(0f);
        binding.layoutSearchBarIncluded.getRoot().setScaleX(0.9f);
        binding.horizontalScrollCategories.setAlpha(0f);
        binding.horizontalScrollCategories.setTranslationX(-50f);
        binding.tvRecommendedLabel.setAlpha(0f);
        binding.cardFeaturedArticle.setAlpha(0f);
        binding.cardFeaturedArticle.setTranslationY(50f);
        binding.tvThisWeekLabel.setAlpha(0f);
        binding.rvArticles.setAlpha(0f);

        // Header Animations
        AnimatorSet headerSet = new AnimatorSet();
        headerSet.playTogether(
                ObjectAnimator.ofFloat(binding.tvArticlesSectionTag, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvArticlesSectionTag, "translationY", 20f, 0f),
                ObjectAnimator.ofFloat(binding.tvLibraryMainTitle, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvLibraryMainTitle, "translationY", 30f, 0f),
                ObjectAnimator.ofFloat(binding.tvLibrarySubTitle, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvLibrarySubTitle, "translationY", 30f, 0f)
        );
        headerSet.setDuration(800);
        headerSet.setInterpolator(new DecelerateInterpolator());

        // Search & Categories
        ObjectAnimator searchAnim = ObjectAnimator.ofFloat(binding.layoutSearchBarIncluded.getRoot(), "alpha", 0f, 1f);
        ObjectAnimator searchScale = ObjectAnimator.ofFloat(binding.layoutSearchBarIncluded.getRoot(), "scaleX", 0.9f, 1f);
        ObjectAnimator categoriesAnim = ObjectAnimator.ofFloat(binding.horizontalScrollCategories, "alpha", 0f, 1f);
        ObjectAnimator categoriesMove = ObjectAnimator.ofFloat(binding.horizontalScrollCategories, "translationX", -50f, 0f);

        AnimatorSet midSet = new AnimatorSet();
        midSet.playTogether(searchAnim, searchScale, categoriesAnim, categoriesMove);
        midSet.setDuration(700);
        midSet.setStartDelay(300);

        // Featured Card
        ObjectAnimator featuredAnim = ObjectAnimator.ofFloat(binding.cardFeaturedArticle, "alpha", 0f, 1f);
        ObjectAnimator featuredMove = ObjectAnimator.ofFloat(binding.cardFeaturedArticle, "translationY", 50f, 0f);
        featuredAnim.setDuration(800);
        featuredAnim.setStartDelay(500);
        featuredMove.setInterpolator(new OvershootInterpolator());

        // RecyclerView Staggered Entrance
        ObjectAnimator rvAnim = ObjectAnimator.ofFloat(binding.rvArticles, "alpha", 0f, 1f);
        rvAnim.setDuration(1000);
        rvAnim.setStartDelay(700);

        headerSet.start();
        midSet.start();
        featuredAnim.start();
        featuredMove.start();
        rvAnim.start();
        
        binding.tvRecommendedLabel.animate().alpha(1f).setDuration(500).setStartDelay(600).start();
        binding.tvThisWeekLabel.animate().alpha(1f).setDuration(500).setStartDelay(800).start();
    }
}