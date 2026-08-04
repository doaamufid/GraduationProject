package com.example.graduationproject.Kids;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.graduationproject.R;

import java.io.File;
import java.io.IOException;

public class UploadPhotoActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private LinearLayout uploadPlaceholder;
    private Uri photoUri;

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoUri != null) {
                    showPreview(photoUri);
                }
            });

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    photoUri = uri;
                    showPreview(uri);
                }
            });

    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    launchCamera();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        imgPreview = findViewById(R.id.imgPreview);
        uploadPlaceholder = findViewById(R.id.uploadPlaceholder);

        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnGallery = findViewById(R.id.btnGallery);
        FrameLayout uploadBox = findViewById(R.id.uploadBox);

        btnCamera.setOnClickListener(v -> checkCameraPermissionAndLaunch());
        btnGallery.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        uploadBox.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            File photoFile = File.createTempFile("drawing_", ".jpg", getCacheDir());
            photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );
            takePictureLauncher.launch(photoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPreview(Uri uri) {
        imgPreview.setImageURI(uri);
        imgPreview.setVisibility(android.view.View.VISIBLE);
        uploadPlaceholder.setVisibility(android.view.View.GONE);

        Intent intent = new Intent(UploadPhotoActivity.this, ConfirmPhotoActivity.class);
        intent.putExtra("photo_uri", uri.toString());
        startActivity(intent);
    }
}
