package com.example.graduationproject.Kids;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;

public class ProcessingActivity extends AppCompatActivity {

    // Simulated "thinking" delay before showing feedback.
    // Replace this with a real callback once the analysis API is wired up.
    private static final long PROCESSING_DELAY_MS = 2500;

    private String photoUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        ImageView imgDrawingThumb = findViewById(R.id.imgDrawingThumb);
        photoUriString = getIntent().getStringExtra("photo_uri");
        if (photoUriString != null) {
            imgDrawingThumb.setImageURI(Uri.parse(photoUriString));
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(ProcessingActivity.this, ResultActivity.class);
            if (photoUriString != null) {
                intent.putExtra("photo_uri", photoUriString);
            }
            startActivity(intent);
            finish();
        }, PROCESSING_DELAY_MS);
    }
}
