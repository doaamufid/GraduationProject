package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityKidsTreeIntroBinding;

public class KidsTreeIntroActivity extends AppCompatActivity {

    private ActivityKidsTreeIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Inflate ViewBinding
        binding = ActivityKidsTreeIntroBinding.inflate(getLayoutInflater());

        // 2. Set ContentView FIRST
        setContentView(binding.getRoot());

        // 3. Button Click Action
        binding.btnGoToTree.setOnClickListener(v -> {
            Intent intent = new Intent(KidsTreeIntroActivity.this, KidsTreeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}