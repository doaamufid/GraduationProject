package com.example.graduationproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.databinding.ActivityBreathingBinding;

public class BreathingActivity extends AppCompatActivity {
    ActivityBreathingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBreathingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}