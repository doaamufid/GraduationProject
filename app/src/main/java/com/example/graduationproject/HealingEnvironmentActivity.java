package com.example.graduationproject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.databinding.ActivityHealingEnvironmentBinding;

public class HealingEnvironmentActivity extends AppCompatActivity {

    private ActivityHealingEnvironmentBinding binding;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityHealingEnvironmentBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());


        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
        }

        binding.btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer == null) {
                    Toast.makeText(HealingEnvironmentActivity.this, "ملف الصوت غير موجود!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isPlaying) {
                    mediaPlayer.pause();
                    binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                    Toast.makeText(HealingEnvironmentActivity.this, "تم الإيقاف المؤقت", Toast.LENGTH_SHORT).show();
                } else {
                    mediaPlayer.start();
                    binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    Toast.makeText(HealingEnvironmentActivity.this, "جاري التشغيل...", Toast.LENGTH_SHORT).show();
                }
                isPlaying = !isPlaying;
            }
        });

        binding.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.tvVolumePercentage.setText(progress + "%");

                float volume = (float) progress / 100f;
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(volume, volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}