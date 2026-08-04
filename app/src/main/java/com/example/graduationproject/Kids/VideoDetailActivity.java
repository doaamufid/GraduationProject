package com.example.graduationproject.Kids;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.databinding.ActivityVideoDetailBinding;

public class VideoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_video_title";
    public static final String EXTRA_SUBTITLE = "extra_video_subtitle";
    public static final String EXTRA_THUMBNAIL_NAME = "extra_video_thumbnail_name";
    public static final String EXTRA_VIDEO_FILE = "extra_video_file";
    public static final String EXTRA_DURATION = "extra_video_duration";

    private ActivityVideoDetailBinding binding;
    private String videoFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVideoDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataFromIntent();
        setupClickListeners();
    }

    private void loadDataFromIntent() {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String subtitle = getIntent().getStringExtra(EXTRA_SUBTITLE);
        String thumbnailName = getIntent().getStringExtra(EXTRA_THUMBNAIL_NAME);
        videoFileName = getIntent().getStringExtra(EXTRA_VIDEO_FILE);
        String duration = getIntent().getStringExtra(EXTRA_DURATION);

        binding.detailTitle.setText(title);

        if (subtitle != null && !subtitle.isEmpty()) {
            binding.detailSubtitle.setText(subtitle);
            binding.detailSubtitle.setVisibility(View.VISIBLE);
        } else {
            binding.detailSubtitle.setVisibility(View.GONE);
        }

        binding.durationLabel.setText(duration);

        if (thumbnailName != null) {
            int thumbResId = getResources().getIdentifier(thumbnailName, "drawable", getPackageName());
            if (thumbResId != 0) {
                binding.thumbnailOverlay.setImageResource(thumbResId);
            }
        }
    }

    private void setupClickListeners() {
        binding.backButton.setOnClickListener(v -> finish());
        binding.playButtonOverlay.setOnClickListener(v -> playVideo());
        binding.saveVideoButton.setOnClickListener(v ->
                Toast.makeText(this, "تم حفظ الفيديو", Toast.LENGTH_SHORT).show());
    }

    private void playVideo() {
        if (videoFileName == null) {
            Toast.makeText(this, "الفيديو غير متوفر", Toast.LENGTH_SHORT).show();
            return;
        }

        int videoResId = getResources().getIdentifier(videoFileName, "raw", getPackageName());
        if (videoResId == 0) {
            Toast.makeText(this, "ملف الفيديو مش موجود بـ res/raw", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.thumbnailOverlay.setVisibility(View.GONE);
        binding.playButtonOverlay.setVisibility(View.GONE);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.videoView);
        binding.videoView.setMediaController(mediaController);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);
        binding.videoView.setVideoURI(uri);
        binding.videoView.setOnPreparedListener(mp -> binding.videoView.start());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.videoView.stopPlayback();
        binding = null; // مهم لتجنب memory leak
    }
}