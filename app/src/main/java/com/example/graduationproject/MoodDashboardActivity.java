package com.example.graduationproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.databinding.ActivityMoodDashboardBinding;

public class MoodDashboardActivity extends AppCompatActivity {
ActivityMoodDashboardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoodDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnReadAllWeek.setOnClickListener(v -> {
        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}