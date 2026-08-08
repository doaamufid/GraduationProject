package com.example.graduationproject;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.models.Habit;
import com.example.graduationproject.models.HabitRepository;
import com.example.graduationproject.widget.CheckPopAnimator;
import com.example.graduationproject.widget.TapBounce;
import com.example.graduationproject.ui.AISuggestionsActivity;
import com.example.graduationproject.ui.HabitDialogFragment;

import java.util.List;

/**
 * Full Java/Android port of the "DailyHabitsFlow" React component's
 * main screen: progress card, AI-suggest card, habit list, and the
 * "add manually" entry point.
 */
public class DailyHabitsActivity extends AppCompatActivity {

    private final HabitRepository repo = HabitRepository.getInstance();

    private TextView tvProgressCount;
    private View progressFill;
    private LinearLayout llHabits;
    private float density;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_habits);
        density = getResources().getDisplayMetrics().density;

        tvProgressCount = findViewById(R.id.tvProgressCount);
        progressFill = findViewById(R.id.progressFill);
        llHabits = findViewById(R.id.llHabits);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnSuggest).setOnClickListener(v ->
                startActivity(new Intent(this, AISuggestionsActivity.class)));

        LinearLayout btnAddManually = findViewById(R.id.btnAddManually);
        btnAddManually.setOnClickListener(v ->
                HabitDialogFragment.newInstanceForCreate().show(getSupportFragmentManager(), "habit"));

        getSupportFragmentManager().setFragmentResultListener(
                HabitDialogFragment.REQUEST_KEY, this, (key, bundle) -> renderHabits());
        
        // Initial entrance animations
        animateEntrance();
    }

    private void animateEntrance() {
        View topBar = findViewById(R.id.topBar);
        View progressCard = (View) tvProgressCount.getParent().getParent();
        View suggestCard = findViewById(R.id.btnSuggest);
        View addBtn = findViewById(R.id.btnAddManually);

        topBar.setAlpha(0f);
        topBar.setTranslationY(-20f);
        progressCard.setAlpha(0f);
        progressCard.setTranslationY(50f);
        suggestCard.setAlpha(0f);
        suggestCard.setTranslationY(50f);
        addBtn.setAlpha(0f);
        addBtn.setTranslationY(50f);

        topBar.animate().alpha(1f).translationY(0f).setDuration(400).start();
        progressCard.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100).start();
        suggestCard.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(200).start();
        addBtn.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(600).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderHabits();
    }

    private void renderHabits() {
        List<Habit> habits = repo.getHabits();

        int doneCount = 0;
        for (Habit h : habits) if (h.done) doneCount++;
        float pct = habits.isEmpty() ? 0f : (float) doneCount / habits.size();

        tvProgressCount.setText(getString(R.string.done_today_format, doneCount, habits.size()));
        
        // Animated progress fill
        progressFill.post(() -> {
            int targetWidth = Math.round(((View) progressFill.getParent()).getWidth() * pct);
            ValueAnimator anim = ValueAnimator.ofInt(progressFill.getLayoutParams().width, targetWidth);
            anim.addUpdateListener(animation -> {
                ViewGroup.LayoutParams lp = progressFill.getLayoutParams();
                lp.width = (int) animation.getAnimatedValue();
                progressFill.setLayoutParams(lp);
            });
            anim.setDuration(800);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();
        });

        llHabits.removeAllViews();
        for (int i = 0; i < habits.size(); i++) {
            Habit habit = habits.get(i);
            View row = buildHabitRow(habit);
            
            // Staggered entrance for items
            row.setAlpha(0f);
            row.setTranslationY(30f);
            row.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(300 + (i * 100L)).start();
            
            llHabits.addView(row);
        }
    }

    private View buildHabitRow(Habit habit) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_habit_row, llHabits, false);

        FrameLayout btnCheck = row.findViewById(R.id.btnCheck);
        View checkBg = row.findViewById(R.id.checkBg);
        ImageView ivCheck = row.findViewById(R.id.ivCheck);
        LinearLayout btnOpen = row.findViewById(R.id.btnOpen);
        TextView tvName = row.findViewById(R.id.tvHabitName);
        TextView tvMeta = row.findViewById(R.id.tvHabitMeta);
        ImageView ivReminderBell = row.findViewById(R.id.ivReminderBell);
        TextView tvIcon = row.findViewById(R.id.tvHabitIcon);

        bindHabitVisuals(habit, checkBg, ivCheck, tvName, tvMeta, ivReminderBell, tvIcon);

        TapBounce.attach(row, 0.99f);
        TapBounce.attach(btnCheck, 0.85f);

        btnCheck.setOnClickListener(v -> {
            boolean wasDone = habit.done;
            repo.toggleHabit(habit.id);
            bindHabitVisuals(habit, checkBg, ivCheck, tvName, tvMeta, ivReminderBell, tvIcon);
            if (!wasDone && habit.done) {
                CheckPopAnimator.pop(ivCheck);
            }
            renderProgressOnly();
        });

        btnOpen.setOnClickListener(v ->
                HabitDialogFragment.newInstanceForEdit(habit.id).show(getSupportFragmentManager(), "habit"));

        return row;
    }

    private void bindHabitVisuals(Habit habit, View checkBg, ImageView ivCheck, TextView tvName,
                                   TextView tvMeta, ImageView ivReminderBell, TextView tvIcon) {
        checkBg.setBackgroundResource(habit.done ? R.drawable.bg_checkbox_done : R.drawable.bg_checkbox_undone);
        ivCheck.setVisibility(habit.done ? View.VISIBLE : View.GONE);
        ivCheck.setScaleX(1f);
        ivCheck.setScaleY(1f);
        ivCheck.setAlpha(1f);

        tvName.setText(habit.name);
        tvName.setPaintFlags(habit.done
                ? (tvName.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG)
                : (tvName.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
        tvName.setAlpha(habit.done ? 0.55f : 1f);

        if (habit.reminder.enabled) {
            ivReminderBell.setVisibility(View.VISIBLE);
            tvMeta.setText(habit.reminder.time);
        } else {
            ivReminderBell.setVisibility(View.GONE);
            tvMeta.setText(getString(R.string.streak_format, habit.streak));
        }

        tvIcon.setText(habit.icon);
    }

    /** Cheap refresh for just the progress card after a toggle, without rebuilding the whole list. */
    private void renderProgressOnly() {
        List<Habit> habits = repo.getHabits();
        int doneCount = 0;
        for (Habit h : habits) if (h.done) doneCount++;
        float pct = habits.isEmpty() ? 0f : (float) doneCount / habits.size();

        tvProgressCount.setText(getString(R.string.done_today_format, doneCount, habits.size()));
        ViewGroup.LayoutParams lp = progressFill.getLayoutParams();
        int trackWidth = ((View) progressFill.getParent()).getWidth();
        lp.width = Math.round(trackWidth * pct);
        progressFill.setLayoutParams(lp);

        for (int i = 0; i < llHabits.getChildCount(); i++) {
            View row = llHabits.getChildAt(i);
            TextView tvMeta = row.findViewById(R.id.tvHabitMeta);
            Habit habit = habits.get(i);
            if (!habit.reminder.enabled) {
                tvMeta.setText(getString(R.string.streak_format, habit.streak));
            }
        }
    }
}
