package com.example.graduationproject.ui;

import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.KidsRoutineEmojiGridAdapter;
import com.example.graduationproject.models.KidsRoutineRoutineItem;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Mirrors the "خطوة جديدة بيومك" bottom sheet: emoji grid, label input,
 * period chips (صباح/ظهر/مساء) and a confirm button that's disabled until
 * a label is typed — exactly like the React version's `disabled={!newLabel.trim()}`.
 */
public class KidsRoutineAddItemBottomSheet extends BottomSheetDialogFragment {

    public interface OnAddListener {
        void onAdd(String emoji, String label, String period);
    }

    private OnAddListener listener;
    private String selectedPeriod = KidsRoutineRoutineItem.PERIOD_MORNING;
    private KidsRoutineEmojiGridAdapter emojiAdapter;

    private TextView chipMorning, chipNoon, chipEvening, btnConfirm;
    private EditText etLabel;

    public void setOnAddListener(OnAddListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.kids_routine_Theme_SalamRoutine_BottomSheet);
        dialog.setOnShowListener(d -> {
            BottomSheetDialog bsd = (BottomSheetDialog) d;
            View sheet = bsd.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (sheet != null) {
                BottomSheetBehavior.from(sheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                sheet.setBackgroundColor(0); // transparent, our own drawable draws the rounded top
                
                // Set width to 90% of screen or similar to avoid it being too narrow on large screens
                // or just ensure it's not constrained too tightly.
                ViewGroup.LayoutParams params = sheet.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                sheet.setLayoutParams(params);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kids_routine_bottomsheet_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView emojiGrid = view.findViewById(R.id.emojiGrid);
        etLabel = view.findViewById(R.id.etLabel);
        chipMorning = view.findViewById(R.id.chipMorning);
        chipNoon = view.findViewById(R.id.chipNoon);
        chipEvening = view.findViewById(R.id.chipEvening);
        btnConfirm = view.findViewById(R.id.btnConfirmAdd);

        emojiAdapter = new KidsRoutineEmojiGridAdapter(emoji -> { /* selection tracked internally */ });
        emojiGrid.setLayoutManager(new GridLayoutManager(getContext(), 8));
        emojiGrid.setAdapter(emojiAdapter);

        chipMorning.setOnClickListener(v -> selectPeriod(KidsRoutineRoutineItem.PERIOD_MORNING));
        chipNoon.setOnClickListener(v -> selectPeriod(KidsRoutineRoutineItem.PERIOD_NOON));
        chipEvening.setOnClickListener(v -> selectPeriod(KidsRoutineRoutineItem.PERIOD_EVENING));
        selectPeriod(KidsRoutineRoutineItem.PERIOD_MORNING);

        etLabel.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = s.toString().trim().length() > 0;
                btnConfirm.setAlpha(hasText ? 1f : 0.5f);
                btnConfirm.setEnabled(hasText);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnConfirm.setOnClickListener(v -> {
            String label = etLabel.getText().toString().trim();
            if (label.isEmpty()) return;
            if (listener != null) {
                listener.onAdd(emojiAdapter.getSelected(), label, selectedPeriod);
            }
            dismiss();
        });
    }

    private void selectPeriod(String period) {
        selectedPeriod = period;

        applyChipState(chipMorning, period.equals(KidsRoutineRoutineItem.PERIOD_MORNING), R.color.kids_routine_period_morning);
        applyChipState(chipNoon, period.equals(KidsRoutineRoutineItem.PERIOD_NOON), R.color.kids_routine_period_noon);
        applyChipState(chipEvening, period.equals(KidsRoutineRoutineItem.PERIOD_EVENING), R.color.kids_routine_period_evening);
    }

    private void applyChipState(TextView chip, boolean selected, int colorRes) {
        if (selected) {
            GradientDrawable bg = (GradientDrawable) ContextCompat
                    .getDrawable(requireContext(), R.drawable.kids_routine_bg_chip_selected).mutate();
            bg.setColor(ContextCompat.getColor(requireContext(), colorRes));
            chip.setBackground(bg);
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.kids_routine_card_white));
        } else {
            chip.setBackgroundResource(R.drawable.kids_routine_bg_chip_unselected);
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.kids_routine_muted_purple));
        }
    }
}
