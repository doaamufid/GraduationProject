package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.example.graduationproject.models.HabitRepository;
import com.example.graduationproject.models.Suggestion;
import com.example.graduationproject.widget.CheckPopAnimator;
import com.example.graduationproject.widget.FadeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Equivalent of <AISuggestionsScreen/>: a togglable list of AI-suggested
 * habits with a footer button that adds every selected one and returns
 * to the main screen (matches `onAddSelected` -> `setSuggestOpen(false)`).
 */
public class AISuggestionsActivity extends AppCompatActivity {

    private final HabitRepository repo = HabitRepository.getInstance();
    private LinearLayout llSuggestions;
    private TextView btnAddSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_suggestions);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCloseTop).setOnClickListener(v -> finish());

        llSuggestions = findViewById(R.id.llSuggestions);
        btnAddSelected = findViewById(R.id.btnAddSelected);

        View root = findViewById(android.R.id.content);
        FadeUtils.screenFade(root);

        renderList();

        btnAddSelected.setOnClickListener(v -> {
            List<Suggestion> picked = new ArrayList<>();
            for (Suggestion s : repo.getSuggestions()) {
                if (s.done) picked.add(s);
            }
            if (picked.isEmpty()) return;
            repo.addFromSuggestions(picked);
            finish();
        });
    }

    private void renderList() {
        llSuggestions.removeAllViews();
        for (Suggestion s : repo.getSuggestions()) {
            llSuggestions.addView(buildSuggestionRow(s));
        }
        renderFooter();
    }

    private View buildSuggestionRow(Suggestion s) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_suggestion_row, llSuggestions, false);

        View checkBg = row.findViewById(R.id.checkBg);
        ImageView ivCheck = row.findViewById(R.id.ivCheck);
        TextView tvName = row.findViewById(R.id.tvName);
        TextView tvReason = row.findViewById(R.id.tvReason);
        TextView tvIcon = row.findViewById(R.id.tvIcon);

        tvName.setText(s.name);
        tvReason.setText(s.reason);
        tvIcon.setText(s.icon);

        bindSuggestionState(s, row, checkBg, ivCheck);

        row.setOnClickListener(v -> {
            boolean wasDone = s.done;
            s.done = !s.done;
            bindSuggestionState(s, row, checkBg, ivCheck);
            if (!wasDone && s.done) {
                CheckPopAnimator.pop(ivCheck);
            }
            renderFooter();
        });

        return row;
    }

    private void bindSuggestionState(Suggestion s, View row, View checkBg, ImageView ivCheck) {
        row.setBackgroundResource(s.done ? R.drawable.bg_suggestion_row_selected : R.drawable.bg_suggestion_row_unselected);
        checkBg.setBackgroundResource(s.done ? R.drawable.bg_checkbox_done : R.drawable.bg_checkbox_undone);
        ivCheck.setVisibility(s.done ? View.VISIBLE : View.GONE);
        ivCheck.setScaleX(1f);
        ivCheck.setScaleY(1f);
        ivCheck.setAlpha(1f);
    }

    private void renderFooter() {
        int count = 0;
        for (Suggestion s : repo.getSuggestions()) if (s.done) count++;

        btnAddSelected.setText(getString(R.string.add_selected_format, count));
        boolean enabled = count > 0;
        btnAddSelected.setEnabled(enabled);
        btnAddSelected.setAlpha(enabled ? 1f : 0.4f);
    }
}
