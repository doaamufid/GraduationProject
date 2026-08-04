package com.example.graduationproject.Kids;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.SoundAdapter;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivitySoundsBinding;
import com.example.graduationproject.models.SoundItem;

import java.util.List;

public class SoundsActivity extends AppCompatActivity {

    private ActivitySoundsBinding binding;
    private ChildProfileStore dbStore;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private String currentSelectedSound = null;
    private CardView selectedCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySoundsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbStore = new ChildProfileStore(this);

        binding.backButton.setOnClickListener(v -> finish());
        binding.playPauseButton.setOnClickListener(v -> togglePlayPause());

        loadBoxSoundsFromDb();
        loadCircleSoundsFromDb();
    }

    private void loadBoxSoundsFromDb() {
        List<SoundItem> boxSounds = dbStore.getBoxSounds();

        CardView[] cards = {
                binding.soundBox0,
                binding.soundBox1,
                binding.soundBox2,
                binding.soundBox3
        };
        ImageView[] icons = {
                binding.soundIcon0,
                binding.soundIcon1,
                binding.soundIcon2,
                binding.soundIcon3
        };
        TextView[] labels = {
                binding.soundLabel0,
                binding.soundLabel1,
                binding.soundLabel2,
                binding.soundLabel3
        };

        for (int i = 0; i < boxSounds.size() && i < cards.length; i++) {
            SoundItem item = boxSounds.get(i);
            bindSoundBox(cards[i], icons[i], labels[i], item);
        }

        if (boxSounds.size() > 1) {
            selectCard(cards[1], boxSounds.get(1).getAudioFileName());
        }
    }

    private void bindSoundBox(CardView card, ImageView icon, TextView label, SoundItem item) {
        int iconResId = getResources().getIdentifier(item.getIconName(), "drawable", getPackageName());
        if (iconResId != 0) icon.setImageResource(iconResId);
        label.setText(item.getTitle());

        card.setOnClickListener(v -> {
            selectCard(card, item.getAudioFileName());
            playSound(item.getAudioFileName());
        });
    }

    private void selectCard(CardView card, String soundKey) {
        if (selectedCard != null) {
            selectedCard.setForeground(null);
        }
        card.setForeground(getResources().getDrawable(R.drawable.selected_border));
        selectedCard = card;
        currentSelectedSound = soundKey;
    }

    private void loadCircleSoundsFromDb() {
        binding.circleSoundsRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<SoundItem> circleSounds = dbStore.getCircleSounds();
        SoundAdapter adapter = new SoundAdapter(circleSounds, this::playSound);
        binding.circleSoundsRecycler.setAdapter(adapter);
    }

    private void togglePlayPause() {
        if (isPlaying) {
            pauseSound();
        } else if (currentSelectedSound != null) {
            playSound(currentSelectedSound);
        }
    }

    private void playSound(String soundFileName) {
        stopCurrentSound();
        int soundResId = getResources().getIdentifier(soundFileName, "raw", getPackageName());
        if (soundResId == 0) return;

        mediaPlayer = MediaPlayer.create(this, soundResId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        isPlaying = true;
        currentSelectedSound = soundFileName;
        binding.playPauseButton.setImageResource(R.drawable.ic_pause_circle);
    }

    private void pauseSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            binding.playPauseButton.setImageResource(R.drawable.ic_play_circle_white);
        }
    }

    private void stopCurrentSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseSound();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCurrentSound();
        binding = null;
    }
}