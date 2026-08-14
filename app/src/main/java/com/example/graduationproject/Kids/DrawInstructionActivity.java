package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;

public class DrawInstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_instruction);

        Button btnTakePhotoNow = findViewById(R.id.btnTakePhotoNow);
        btnTakePhotoNow.setOnClickListener(v -> {
            Intent intent = new Intent(DrawInstructionActivity.this, UploadPhotoActivity.class);
            startActivity(intent);
        });
    }
}
