package com.example.graduationproject.Kids;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityKidsTreeLevelUpBinding;

public class KidsTreeLevelUpActivity extends AppCompatActivity {

    private ActivityKidsTreeLevelUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKidsTreeLevelUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnClaimReward.setOnClickListener(v -> finish());
    }
}