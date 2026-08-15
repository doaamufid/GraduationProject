package com.example.graduationproject.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ExercisePrefs;
import com.example.graduationproject.databinding.ItemExercisePanelBinding;
import com.example.graduationproject.models.ExerciseArea;

import java.util.List;

public class ExerciseAreaAdapter extends RecyclerView.Adapter<ExerciseAreaAdapter.VH> {

    public interface OnStartExerciseListener {
        void onStart(ExerciseArea area, int position);
    }

    private final List<ExerciseArea> items;
    private final ExercisePrefs prefs;
    private final OnStartExerciseListener listener;
    private int expandedPosition = -1;

    public ExerciseAreaAdapter(List<ExerciseArea> items, ExercisePrefs prefs,
                               OnStartExerciseListener listener) {
        this.items = items;
        this.prefs = prefs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExercisePanelBinding b = ItemExercisePanelBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ExerciseArea area = items.get(position);
        ItemExercisePanelBinding b = holder.binding;

        int areaColor = Color.parseColor(area.badgeTextColor);

        b.tvTitle.setText(area.title);
        b.tvSubtitle.setText(area.subtitle);

        // Dot color
        GradientDrawable dot = (GradientDrawable) ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.circle_outer);
        if (dot != null) {
            dot = (GradientDrawable) dot.mutate();
            dot.setColor(areaColor);
            b.viewAreaDot.setBackground(dot);
        }

        int tryCount = prefs.getTryCount(area.key);
        if (tryCount > 0) {
            b.tvTryCount.setVisibility(View.VISIBLE);
            b.tvTryCount.setText("جربتها " + tryCount + " مرات");
            b.tvTryCount.setTextColor(areaColor);
            
            GradientDrawable badgeBg = new GradientDrawable();
            badgeBg.setCornerRadius(dpToPx(holder.itemView, 12));
            badgeBg.setColor(adjustAlpha(areaColor, 0.1f));
            b.tvTryCount.setBackground(badgeBg);
        } else {
            b.tvTryCount.setVisibility(View.GONE);
        }

        boolean isExpanded = position == expandedPosition;
        b.cardExercise.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // Outer Card Border
        GradientDrawable rootBg = new GradientDrawable();
        rootBg.setCornerRadius(dpToPx(holder.itemView, 18));
        rootBg.setColor(Color.WHITE);
        rootBg.setStroke(isExpanded ? dpToPx(holder.itemView, 2) : dpToPx(holder.itemView, 1), 
                isExpanded ? areaColor : Color.parseColor("#D0E2F3"));
        b.root.setBackground(rootBg);

        if (isExpanded) {
            b.tvExerciseTag.setText("تمرين • " + area.title);
            b.tvExerciseTag.setTextColor(areaColor);
            b.tvExerciseTitle.setText(area.exerciseTitle);
            b.tvExerciseDesc.setText(area.exerciseDesc);
            b.tvOffline.setText(area.isOffline ? "offline 🚫" : "online ✅");
            b.tvDuration.setText(area.durationMinutes + " دقائق 🕒");
            b.tvReps.setText(area.repsCount + " مرات");

            // Expanded Container Color
            GradientDrawable innerBg = new GradientDrawable();
            innerBg.setCornerRadius(dpToPx(holder.itemView, 16));
            innerBg.setColor(adjustAlpha(areaColor, 0.05f));
            b.cardExercise.setBackground(innerBg);

            // Action Button Color
            b.btnStart.setBackgroundTintList(android.content.res.ColorStateList.valueOf(areaColor));
            
            // Animation for expanding
            b.cardExercise.setAlpha(0f);
            b.cardExercise.setTranslationY(-20f);
            b.cardExercise.animate().alpha(1f).translationY(0f).setDuration(300).start();
        }

        b.btnArea.setOnClickListener(v -> {
            int previous = expandedPosition;
            expandedPosition = isExpanded ? -1 : position;
            if (previous != -1) notifyItemChanged(previous);
            notifyItemChanged(expandedPosition == -1 ? position : expandedPosition);
        });

        // Set click listener on the whole root view as well
        b.root.setOnClickListener(v -> b.btnArea.performClick());

        b.btnStart.setOnClickListener(v -> listener.onStart(area, position));
    }

    private int dpToPx(View view, int dp) {
        float density = view.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** يستدعى من الخارج عند الضغط على نقطة على مجسم الجسد */
    public void expand(int position) {
        int previous = expandedPosition;
        expandedPosition = position;
        if (previous != -1) notifyItemChanged(previous);
        notifyItemChanged(position);
    }

    public int indexOfKey(String key) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).key.equals(key)) return i;
        }
        return -1;
    }

    /** يستدعى بعد الرجوع من شاشة التمرين لتحديث شارة "جربتها" */
    public void refreshTryCount(int position) {
        notifyItemChanged(position);
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemExercisePanelBinding binding;
        VH(ItemExercisePanelBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}