package com.example.graduationproject.Kids;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ImageView imgDrawingSmall = findViewById(R.id.imgDrawingSmall);
        String uriString = getIntent().getStringExtra("photo_uri");
        if (uriString != null) {
            imgDrawingSmall.setImageURI(Uri.parse(uriString));
        }

        FrameLayout btnPlayAudio = findViewById(R.id.btnPlayAudio);
        btnPlayAudio.setOnClickListener(v -> {
            // TODO: hook up MediaPlayer with the actual voice-over feedback file
        });

        Button btnDrawMore = findViewById(R.id.btnDrawMore);
        btnDrawMore.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, DrawInstructionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        Button btnFinish = findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(v -> finish());
    }
}
