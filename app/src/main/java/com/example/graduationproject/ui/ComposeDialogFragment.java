package com.example.graduationproject.ui;

import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
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

import com.example.graduationproject.R;
import com.example.graduationproject.models.DateOption;
import com.example.graduationproject.models.FutureMessage;
import com.example.graduationproject.models.FutureSelfRepository;
import com.example.graduationproject.util.DateUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Equivalent of <ComposeDialog/>: handles BOTH "new message" and "edit
 * message" modes (mirrors the original's `initial` prop / `isEdit` flag),
 * with a text area, a row of date-option chips (custom / week / month /
 * 3 months / year), and a save button disabled until the form is valid.
 */
public class ComposeDialogFragment extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "compose_result";
    private static final String ARG_EDIT_ID = "edit_id";
    private static final String KEY_CUSTOM = "custom";

    private final FutureSelfRepository repo = FutureSelfRepository.getInstance();

    private boolean isEdit;
    private FutureMessage editingMessage;

    private String selectedWhen = null; // null | "custom" | DateOption.key
    private Date customDate = null;

    private EditText edtText, edtCustomDate;
    private View edtCustomDateContainer;
    private FlexboxLayout flexOptions;
    private TextView btnSave, tvDialogTitle;
    private TextView chipCustom;

    public static ComposeDialogFragment newInstanceForCreate() {
        return new ComposeDialogFragment();
    }

    public static ComposeDialogFragment newInstanceForEdit(long messageId) {
        ComposeDialogFragment fragment = new ComposeDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_EDIT_ID, messageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_compose, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        long editId = getArguments() != null ? getArguments().getLong(ARG_EDIT_ID, -1) : -1;
        isEdit = editId != -1;
        if (isEdit) {
            editingMessage = repo.findById(editId);
            if (editingMessage == null) {
                dismiss();
                return root;
            }
        }

        edtText = root.findViewById(R.id.edtText);
        edtCustomDate = root.findViewById(R.id.edtCustomDate);
        edtCustomDateContainer = root.findViewById(R.id.edtCustomDateContainer);
        flexOptions = root.findViewById(R.id.flexOptions);
        btnSave = root.findViewById(R.id.btnSave);
        tvDialogTitle = root.findViewById(R.id.tvDialogTitle);

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        tvDialogTitle.setText(isEdit ? R.string.compose_title_edit : R.string.compose_title_new);
        btnSave.setText(isEdit ? R.string.save_edit : R.string.save_new);

        if (isEdit) {
            edtText.setText(editingMessage.text);
            selectedWhen = KEY_CUSTOM;
            customDate = editingMessage.targetDate;
        }

        buildOptionChips();
        renderCustomDateField();

        edtCustomDate.setOnClickListener(v -> openDatePicker());
        if (edtCustomDateContainer != null) {
            edtCustomDateContainer.setOnClickListener(v -> openDatePicker());
        }

        edtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                renderSaveEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnSave.setOnClickListener(v -> handleSave());

        renderSaveEnabled();
        return root;
    }

    private void buildOptionChips() {
        flexOptions.removeAllViews();

        chipCustom = (TextView) LayoutInflater.from(requireContext())
                .inflate(R.layout.item_date_chip, flexOptions, false);
        chipCustom.setText(R.string.option_custom_date);
        chipCustom.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_calendar, 0, 0, 0);
        chipCustom.setCompoundDrawablePadding(dp(4));
        chipCustom.setOnClickListener(v -> {
            selectedWhen = KEY_CUSTOM;
            renderOptionChips();
            renderCustomDateField();
            renderSaveEnabled();
            openDatePicker();
        });
        flexOptions.addView(chipCustom);

        for (DateOption option : FutureSelfRepository.DATE_OPTIONS) {
            TextView chip = (TextView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_date_chip, flexOptions, false);
            chip.setText(option.label);
            chip.setTag(option.key);
            chip.setOnClickListener(v -> {
                selectedWhen = option.key;
                renderOptionChips();
                renderCustomDateField();
                renderSaveEnabled();
            });
            flexOptions.addView(chip);
        }

        renderOptionChips();
    }

    private void renderOptionChips() {
        for (int i = 0; i < flexOptions.getChildCount(); i++) {
            TextView chip = (TextView) flexOptions.getChildAt(i);
            boolean isCustomChip = chip == chipCustom;
            boolean selected = isCustomChip
                    ? KEY_CUSTOM.equals(selectedWhen)
                    : chip.getTag() != null && chip.getTag().equals(selectedWhen);

            chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
            chip.setTextColor(getResources().getColor(selected ? R.color.white : R.color.text_soft));
            if (isCustomChip) {
                chip.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(
                        getResources().getColor(selected ? R.color.white : R.color.text_soft)));
            }
        }
    }

    private void renderCustomDateField() {
        boolean showCustom = KEY_CUSTOM.equals(selectedWhen);
        if (edtCustomDateContainer != null) {
            edtCustomDateContainer.setVisibility(showCustom ? View.VISIBLE : View.GONE);
        } else {
            edtCustomDate.setVisibility(showCustom ? View.VISIBLE : View.GONE);
        }

        if (showCustom && customDate != null) {
            edtCustomDate.setText(DateUtils.formatDate(customDate));
        } else if (showCustom) {
            edtCustomDate.setText("");
        }
    }

    private void openDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (customDate != null) cal.setTime(customDate);

        Calendar minCal = Calendar.getInstance();
        minCal.add(Calendar.DAY_OF_MONTH, 1); // matches `min={addDays(new Date(),1)}`

        DatePickerDialog picker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, dayOfMonth, 0, 0, 0);
                    picked.set(Calendar.MILLISECOND, 0);
                    customDate = picked.getTime();
                    renderCustomDateField();
                    renderSaveEnabled();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        picker.getDatePicker().setMinDate(minCal.getTimeInMillis());
        picker.show();
    }

    private boolean isFormValid() {
        boolean hasText = !edtText.getText().toString().trim().isEmpty();
        boolean hasWhen = selectedWhen != null;
        boolean customOk = !KEY_CUSTOM.equals(selectedWhen) || customDate != null;
        return hasText && hasWhen && customOk;
    }

    private void renderSaveEnabled() {
        boolean valid = isFormValid();
        btnSave.setEnabled(valid);
        btnSave.setAlpha(valid ? 1f : 0.4f);
    }

    private void handleSave() {
        if (!isFormValid()) return;

        String text = edtText.getText().toString().trim();
        Date target;
        if (KEY_CUSTOM.equals(selectedWhen)) {
            target = customDate;
        } else {
            DateOption option = null;
            for (DateOption o : FutureSelfRepository.DATE_OPTIONS) {
                if (o.key.equals(selectedWhen)) option = o;
            }
            target = DateUtils.addDays(new Date(), option != null ? option.days : 0);
        }

        if (isEdit) {
            repo.updateMessage(editingMessage.id, text, target);
        } else {
            repo.addMessage(text, target);
        }

        getParentFragmentManager().setFragmentResult(REQUEST_KEY, new Bundle());
        dismiss();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_FutureSelf_BottomSheet;
    }
}
