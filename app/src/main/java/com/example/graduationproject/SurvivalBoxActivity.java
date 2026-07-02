package com.example.graduationproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.adapters.SurvivalAdapter;
import com.example.graduationproject.databinding.ActivitySurvivalBoxBinding;
import com.example.graduationproject.models.SurvivalItemModel;

import java.util.ArrayList;
import java.util.List;

public class SurvivalBoxActivity extends AppCompatActivity {
ActivitySurvivalBoxBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding=ActivitySurvivalBoxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rvSurvivalItems.setLayoutManager(new LinearLayoutManager(this));

        List<SurvivalItemModel> dataList = new ArrayList<>();
        dataList.add(new SurvivalItemModel("صوتي • AUDIO", "تسجيلات صوتية", "٣ رسائل"));
        dataList.add(new SurvivalItemModel("صور • PHOTOS", "صور مريحة", "٥ صور"));
        dataList.add(new SurvivalItemModel("محبة • LOVE", "رسائل محبة", "٣ رسائل"));
        dataList.add(new SurvivalItemModel("ذكر • DHIKR", "أذكار مفضلة", "٨ أذكار"));

        SurvivalAdapter adapter = new SurvivalAdapter(dataList);
        binding.rvSurvivalItems.setAdapter(adapter);

    }
}