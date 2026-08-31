package com.example.graduationproject;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.view.AdultHabitProgAdultProgressCardView;

/** Port of the JS <AdultCardScreen/> preview harness around <AdultProgressCard/>. */
public class AdultHabitProgAdultCardActivity extends AppCompatActivity {

    private AdultHabitProgAdultProgressCardView cardView;
    private int total = 5;
    private int completed = 0;
    private boolean soundOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adult_habit_prog_activity_adult_card);

        cardView = findViewById(R.id.adultCardView);
        cardView.setTotal(total);
        cardView.setStreakDays(6);
        cardView.setSoundEnabled(soundOn);
        cardView.setCompleted(completed);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

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
