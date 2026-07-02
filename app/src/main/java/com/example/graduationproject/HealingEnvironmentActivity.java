package com.example.graduationproject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.databinding.ActivityHealingEnvironmentBinding;

public class HealingEnvironmentActivity extends AppCompatActivity {

    // استخدام الـ Binding فقط وحذف المتغيرات القديمة المسببة للمشاكل
    private ActivityHealingEnvironmentBinding binding;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityHealingEnvironmentBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        // تجهيز ملف الصوت
        mediaPlayer = MediaPlayer.create(this, R.raw.tranquil_forest);

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
        }

        // 1. برمجة زر التشغيل والإيقاف عبر الـ Binding
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

        // 2. التحكم بالـ SeekBar عبر الـ Binding مباشرة (هنا كان الخطأ وتم إصلاحه)
        binding.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // تحديث نص النسبة المئوية ديناميكياً
                binding.tvVolumePercentage.setText(progress + "%");

                // التحكم بمستوى الصوت الفعلي
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

        // 3. زر الرجوع عبر الـ Binding
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