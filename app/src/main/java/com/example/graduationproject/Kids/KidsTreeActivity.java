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

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnOpenDailyActions.setOnClickListener(v -> showActionsBottomSheet());

        setupBadgeClickListeners();
        setupStageClickListeners();
    }

    private void setupBadgeClickListeners() {
        binding.badgeYawmi.setOnClickListener(v ->
                showDetailDialog("⭐", "يومي", "دخلت كل يوم", "⭐ نور فخور فيك! حصلتها اليوم", "#FFF9C4", "#F57F17")
        );

        binding.badgeMotamel.setOnClickListener(v ->
                showDetailDialog("🧠", "مُتأمل", "أكملت تمارين التأمل والتنفس", "⭐ نور فخور فيك! حصلتها اليوم", "#E8F5E9", "#2E7D32")
        );

        binding.badgeMostamer.setOnClickListener(v ->
                showDetailDialog("🔒", "مستمر", "استمر في تسجيل الأنشطة يومياً", "🔒 كَمِّل 7 أيام متتالية لفتحها!", "#E0E0E0", "#616161")
        );
    }

    private void setupStageClickListeners() {
        binding.stage1Icon.setOnClickListener(v ->
                showDetailDialog("🌱", "بذرة", "بداية رحلة النمو والتعافي", "تحققت بجميل إنجازاتك!", "#E8F5E9", "#2E7D32")
        );

        binding.stage2Icon.setOnClickListener(v ->
                showDetailDialog("🌿", "برعم", "شجرتك بدأت تتفرع وتكبر", "تحققت بجميل إنجازاتك!", "#E8F5E9", "#2E7D32")
        );

        binding.stage3Icon.setOnClickListener(v ->
                showDetailDialog("🌳", "شجرة", "شجرة كبيرة يشوفها الكل!", "أنت في هذه المرحلة حالياً!", "#E8F5E9", "#2E7D32")
        );

        binding.stage4Icon.setOnClickListener(v ->
                showDetailDialog("🔒", "شجرة مثمرة", "أعلى مراحل نمو شجرة التعافي", "🔒 تحتاج 50 نقطة لوصولها!", "#E0E0E0", "#616161")
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

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showActionsBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        BottomSheetKidsActionsBinding sheetBinding = BottomSheetKidsActionsBinding.inflate(getLayoutInflater());
        dialog.setContentView(sheetBinding.getRoot());

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
        binding.tvPointsCount.setText("اليوم: " + currentPoints + " نقطة");
        Toast.makeText(this, "أضفت + " + points + " نقاط!", Toast.LENGTH_SHORT).show();

        if (currentPoints >= 50) {
            Intent intent = new Intent(KidsTreeActivity.this, KidsTreeLevelUpActivity.class);
            startActivity(intent);
        }
    }
}