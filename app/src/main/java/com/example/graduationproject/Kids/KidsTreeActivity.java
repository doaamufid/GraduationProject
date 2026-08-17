package com.example.graduationproject.Kids;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.databinding.ActivityKidsTreeBinding;
import com.example.graduationproject.databinding.BottomSheetKidsActionsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class KidsTreeActivity extends AppCompatActivity {

    private ActivityKidsTreeBinding binding;
    private int currentPoints = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKidsTreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        updatePointsDisplay();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnOpenDailyActions.setOnClickListener(v -> showActionsBottomSheet());

        setupBadgeClickListeners();
        setupStageClickListeners();
    }

    private void updatePointsDisplay() {
        binding.tvPointsCount.setText(getString(R.string.tree_today_points, currentPoints));
    }

    private void setupBadgeClickListeners() {
        binding.badgeYawmi.setOnClickListener(v ->
                showDetailDialog(
                        "⭐",
                        getString(R.string.daily_badge_title),
                        getString(R.string.daily_badge_desc),
                        getString(R.string.badge_unlocked_message),
                        "#FFF9C4",
                        "#F57F17"
                )
        );

        binding.badgeMotamel.setOnClickListener(v ->
                showDetailDialog(
                        "🧠",
                        getString(R.string.badge_meditator_title),
                        getString(R.string.badge_meditator_desc),
                        getString(R.string.badge_unlocked_message),
                        "#E8F5E9",
                        "#2E7D32"
                )
        );

        binding.badgeMostamer.setOnClickListener(v ->
                showDetailDialog(
                        "🔒",
                        getString(R.string.badge_consistent_title),
                        getString(R.string.badge_consistent_desc),
                        getString(R.string.badge_consistent_locked_status),
                        "#E0E0E0",
                        "#616161"
                )
        );
    }

    private void setupStageClickListeners() {
        binding.stage1Icon.setOnClickListener(v ->
                showDetailDialog(
                        "🌱",
                        getString(R.string.stage_seed_title),
                        getString(R.string.stage_seed_desc),
                        getString(R.string.stage_achieved_status),
                        "#E8F5E9",
                        "#2E7D32"
                )
        );

        binding.stage2Icon.setOnClickListener(v ->
                showDetailDialog(
                        "🌿",
                        getString(R.string.stage_sprout_title),
                        getString(R.string.stage_sprout_desc),
                        getString(R.string.stage_achieved_status),
                        "#E8F5E9",
                        "#2E7D32"
                )
        );

        binding.stage3Icon.setOnClickListener(v ->
                showDetailDialog(
                        "🌳",
                        getString(R.string.stage_tree_title),
                        getString(R.string.stage_tree_desc),
                        getString(R.string.stage_current_status),
                        "#E8F5E9",
                        "#2E7D32"
                )
        );

        binding.stage4Icon.setOnClickListener(v ->
                showDetailDialog(
                        "🔒",
                        getString(R.string.stage_fruit_tree_title),
                        getString(R.string.stage_fruit_tree_desc),
                        getString(R.string.stage_fruit_tree_locked_status),
                        "#E0E0E0",
                        "#616161"
                )
        );
    }

    private void showDetailDialog(String icon, String title, String desc, String statusText, String bgColorHex, String textColorHex) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_badge_detail);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvIcon = dialog.findViewById(R.id.tvDialogIcon);
        TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvDesc = dialog.findViewById(R.id.tvDialogDescription);
        TextView tvStatus = dialog.findViewById(R.id.tvDialogStatusText);
        android.view.View statusBox = dialog.findViewById(R.id.layoutStatusBox);
        Button btnClose = dialog.findViewById(R.id.btnDialogClose);

        tvIcon.setText(icon);
        tvTitle.setText(title);
        tvDesc.setText(desc);
        tvStatus.setText(statusText);
        statusBox.setBackgroundColor(Color.parseColor(bgColorHex));
        tvStatus.setTextColor(Color.parseColor(textColorHex));

        btnClose.setText(R.string.close);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showActionsBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        BottomSheetKidsActionsBinding sheetBinding = BottomSheetKidsActionsBinding.inflate(getLayoutInflater());
        dialog.setContentView(sheetBinding.getRoot());

        sheetBinding.tvTitle.setText(R.string.what_did_you_achieve_today);
        sheetBinding.btnActionBreath.setText(R.string.breathing_exercise);
        sheetBinding.btnActionChat.setText(R.string.chat_with_friend);
        sheetBinding.btnActionJournal.setText(R.string.write_journal);

        sheetBinding.btnActionBreath.setOnClickListener(v -> {
            addPoints(10);
            dialog.dismiss();
        });

        sheetBinding.btnActionChat.setOnClickListener(v -> {
            addPoints(10);
            dialog.dismiss();
        });

        sheetBinding.btnActionJournal.setOnClickListener(v -> {
            addPoints(10);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void addPoints(int points) {
        currentPoints += points;
        updatePointsDisplay();
        Toast.makeText(this, getString(R.string.tree_points_added, points), Toast.LENGTH_SHORT).show();

        if (currentPoints >= 50) {
            Intent intent = new Intent(KidsTreeActivity.this, KidsTreeLevelUpActivity.class);
            startActivity(intent);
        }
    }
}