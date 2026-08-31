package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.view.KidsHabitProgDailyProgressCardView;

/** Port of the JS <DailyCardScreen/> preview harness around <DailyProgressCard/>. */
public class KidsHabitProgDailyCardActivity extends AppCompatActivity {

    private KidsHabitProgDailyProgressCardView cardView;
    private int total = 5;
    private int completed = 0;
    private boolean soundOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kids_habit_prog_activity_daily_card);

        cardView = findViewById(R.id.dailyCardView);
        cardView.setChildName("يوسف");
        cardView.setTotal(total);
        cardView.setStreak(4);
        cardView.setSoundEnabled(soundOn);
        cardView.setCompleted(completed);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent intent = new Intent(this, SplashSelectActivity.class);
            ActivityUtils.startActivityAndFinishWithAnimation(this, intent);
        });

        findViewById(R.id.btn_switch_mode).setOnClickListener(v -> {
            Intent intent = new Intent(this, SplashSelectActivity.class);
            ActivityUtils.startActivityAndFinishWithAnimation(this, intent);
        });

        findViewById(R.id.btn_select_child).setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.graduationproject.Kids.ChildProfilesActivity.class);
            ActivityUtils.startActivityWithAnimation(this, intent);
        });

        findViewById(R.id.btnHedgehog).setOnClickListener(v -> cardView.setSpecies(KidsHabitProgDailyProgressCardView.Species.HEDGEHOG));
        findViewById(R.id.btnBear).setOnClickListener(v -> cardView.setSpecies(KidsHabitProgDailyProgressCardView.Species.BEAR));
        findViewById(R.id.btnButterfly).setOnClickListener(v -> cardView.setSpecies(KidsHabitProgDailyProgressCardView.Species.BUTTERFLY));

        findViewById(R.id.btnMinusHabit).setOnClickListener(v -> {
            completed = Math.max(0, completed - 1);
            cardView.setCompleted(completed);
        });
        findViewById(R.id.btnPlusHabit).setOnClickListener(v -> {
            completed = Math.min(total, completed + 1);
            cardView.setCompleted(completed);
        });
        findViewById(R.id.btnReset).setOnClickListener(v -> {
            completed = 0;
            cardView.setCompleted(completed);
        });

        Button soundBtn = findViewById(R.id.btnSound);
        soundBtn.setOnClickListener(v -> {
            soundOn = !soundOn;
            cardView.setSoundEnabled(soundOn);
            soundBtn.setText(soundOn ? "🔊" : "🔇");
        });

        findViewById(R.id.btnMinusTotal).setOnClickListener(v -> {
            total = Math.max(1, total - 1);
            cardView.setTotal(total);
            completed = Math.min(completed, total);
            cardView.setCompleted(completed);
        });
        findViewById(R.id.btnPlusTotal).setOnClickListener(v -> {
            total = Math.min(15, total + 1);
            cardView.setTotal(total);
        });
    }
}
