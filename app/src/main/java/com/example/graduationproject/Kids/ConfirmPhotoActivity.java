package com.example.graduationproject.Kids;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;

public class ConfirmPhotoActivity extends AppCompatActivity {

    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_photo);

        ImageView imgDrawing = findViewById(R.id.imgDrawing);
        Button btnSendToNoor = findViewById(R.id.btnSendToNoor);
        Button btnRetake = findViewById(R.id.btnRetake);

        String uriString = getIntent().getStringExtra("photo_uri");
        if (uriString != null) {
            photoUri = Uri.parse(uriString);
            imgDrawing.setImageURI(photoUri);
        }

        btnSendToNoor.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmPhotoActivity.this, ProcessingActivity.class);
            if (photoUri != null) {
                intent.putExtra("photo_uri", photoUri.toString());
            }
            startActivity(intent);
            finish();
        });

        btnRetake.setOnClickListener(v -> finish());
    }
}
