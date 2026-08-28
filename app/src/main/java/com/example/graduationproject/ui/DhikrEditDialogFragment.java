package com.example.graduationproject.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.AppHost;
import com.example.graduationproject.R;
import com.example.graduationproject.data.AppRepository;
import com.example.graduationproject.data.Constants;
import com.example.graduationproject.view.ChipRowHelper;
import com.google.android.flexbox.FlexboxLayout;

/**
 * Java equivalent of the JS custom-dhikr sheet: text input (60 char max),
 * category chip picker, duration chip picker, and a "حفظ وتفضيل" button
 * that always saves the new dhikr as an already-favorited item — matching
 * saveDhikrDraft() in the JS source.
 */
public class DhikrEditDialogFragment extends DialogFragment {

    private final AppRepository repo = AppRepository.get();

    private String draftText = "";
    private String draftCategory = Constants.CATEGORY_KEYS[3]; // "عام" default, matches JS initial state
    private int draftMinutes = 2;

    private EditText dhikrInput;
    private TextView dhikrCharCounter;
    private FlexboxLayout categoryChipsContainer;
    private LinearLayout durationChipsContainer;
    private TextView btnDhikrCancel, btnDhikrSave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_CalmApp_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_dhikr_edit, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window w = dialog.getWindow();
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            w.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            w.setGravity(Gravity.CENTER);
            w.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dhikrInput = view.findViewById(R.id.dhikrInput);
        dhikrCharCounter = view.findViewById(R.id.dhikrCharCounter);
        categoryChipsContainer = view.findViewById(R.id.categoryChipsContainer);
        durationChipsContainer = view.findViewById(R.id.dhikrDurationChipsContainer);
        btnDhikrCancel = view.findViewById(R.id.btnDhikrCancel);
        btnDhikrSave = view.findViewById(R.id.btnDhikrSave);

        dhikrInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                draftText = s.toString();
                updateCounterAndSaveState();
            }
        });

        rebuildCategoryChips();
        rebuildDurationChips();
        updateCounterAndSaveState();

        btnDhikrCancel.setOnClickListener(v -> dismiss());

        btnDhikrSave.setOnClickListener(v -> {
            String trimmed = draftText.trim();
            if (trimmed.isEmpty()) return;
            repo.addDhikr(trimmed, draftCategory, draftMinutes);
            if (getActivity() instanceof AppHost) {
                ((AppHost) getActivity()).showToast(getString(R.string.toast_added_dhikr));
            }
            dismiss();
        });
    }

    private void rebuildCategoryChips() {
        // Build chips into a temp LinearLayout via the shared helper, then move
        // them into the FlexboxLayout so long category lists wrap naturally.
        LinearLayout temp = new LinearLayout(getContext());
        ChipRowHelper.buildCategoryChips(requireContext(), temp, Constants.CATEGORY_KEYS, draftCategory,
                Constants.CATEGORY_META, index -> {
                    draftCategory = Constants.CATEGORY_KEYS[index];
                    rebuildCategoryChips();
                });
        categoryChipsContainer.removeAllViews();
        while (temp.getChildCount() > 0) {
            View child = temp.getChildAt(0);
            temp.removeViewAt(0);
            categoryChipsContainer.addView(child);
        }
    }

    private void rebuildDurationChips() {
        ChipRowHelper.buildDurationChips(requireContext(), durationChipsContainer, Constants.DURATION_OPTIONS,
                draftMinutes, index -> {
                    draftMinutes = Constants.DURATION_OPTIONS[index];
                    rebuildDurationChips();
                });
    }

    private void updateCounterAndSaveState() {
        int len = draftText.length();
        dhikrCharCounter.setText(len + "/60");
        boolean enabled = !draftText.trim().isEmpty();
        btnDhikrSave.setEnabled(enabled);
        btnDhikrSave.setBackgroundResource(enabled ? R.drawable.bg_btn_primary : R.drawable.bg_btn_primary_disabled);
        btnDhikrSave.setTextColor(getResources().getColor(enabled ? R.color.cardBtnText : R.color.mutedDim));
    }
}
