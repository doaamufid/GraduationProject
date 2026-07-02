package com.example.graduationproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.graduationproject.adapters.ArticlesAdapter;
import com.example.graduationproject.databinding.ActivityArticlesBinding;
import com.example.graduationproject.models.ArticleModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArticlesActivity extends AppCompatActivity {
    ActivityArticlesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticlesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvArticles.setLayoutManager(new LinearLayoutManager(this));

        List<ArticleModel> articlesList = new ArrayList<>();

        articlesList.add(new ArticleModel(
                "التنفس • BREATHING",
                "٧ تقنيات للتنفس في الأزمات الأكثر قراءة",
                Arrays.asList("تمارين")
        ));

        articlesList.add(new ArticleModel(
                "علاجي • CBT",
                "كيف تتعاملين مع الحزن بدون قمعه؟",
                Arrays.asList("عربي", "CBT")
        ));

        articlesList.add(new ArticleModel(
                "التعلق • ATTACHMENT",
                "لماذا يؤلمنا التعلق؟ دليل علمي بسيط",
                Arrays.asList("٦ دقائق قراءة", "علم النفس")
        ));

        ArticlesAdapter adapter = new ArticlesAdapter(articlesList);
        binding.rvArticles.setAdapter(adapter);
    }
}