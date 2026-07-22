package com.example.graduationproject.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        b.tvTitle.setText(area.title);
        b.tvSubtitle.setText(area.subtitle);

        int tryCount = prefs.getTryCount(area.key);
        if (tryCount > 0) {
            b.tvTryCount.setVisibility(android.view.View.VISIBLE);
            b.tvTryCount.setText("جربتها " + tryCount + " مرات");
            b.tvTryCount.setTextColor(Color.parseColor(area.badgeTextColor));
            b.tvTryCount.setBackgroundColor(Color.parseColor(area.badgeBgColor));
        } else {
            b.tvTryCount.setVisibility(android.view.View.GONE);
        }

        boolean isExpanded = position == expandedPosition;
        b.cardExercise.setVisibility(isExpanded ? android.view.View.VISIBLE : android.view.View.GONE);

        if (isExpanded) {
            b.tvExerciseTitle.setText(area.exerciseTitle);
            b.tvExerciseDesc.setText(area.exerciseDesc);
            b.tvOffline.setText(area.isOffline ? "offline" : "online");
            b.tvDuration.setText(area.durationMinutes + " دقائق");
            b.tvReps.setText(area.repsCount + " مرات");
        }

        b.btnArea.setOnClickListener(v -> {
            int previous = expandedPosition;
            expandedPosition = isExpanded ? -1 : position;
            if (previous != -1) notifyItemChanged(previous);
            notifyItemChanged(expandedPosition == -1 ? position : expandedPosition);
        });

        b.btnStart.setOnClickListener(v -> listener.onStart(area, position));
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