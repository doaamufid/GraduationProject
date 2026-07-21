package com.example.graduationproject.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Habit;
import com.example.graduationproject.models.HabitRepository;
import com.example.graduationproject.models.Reminder;
import com.example.graduationproject.widget.FadeUtils;
import com.example.graduationproject.widget.TapBounce;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Equivalent of <HabitDialog/>: handles both "add" and "edit" modes,
 * with a name field, a 7-icon picker, and a reminder toggle that reveals
 * a time field (opens {@link TimeWheelDialogFragment}) when switched on.
 */
public class HabitDialogFragment extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "habit_result";
    private static final String ARG_EDIT_ID = "edit_id";

    private final HabitRepository repo = HabitRepository.getInstance();

    private boolean isEdit;
    private Habit editingHabit;

    private String selectedIcon;
    private boolean reminderOn;
    private String[] time = {"12", "02", "AM"}; // [hour, minute, AM/PM]

    private EditText edtName;
    private LinearLayout llIconPicker, groupReminderDetail, btnPickTime;
    private SwitchMaterial swReminder;
    private ImageView ivReminderIcon;
    private TextView tvTimeDisplay, btnSave;

    public static HabitDialogFragment newInstanceForCreate() {
        return new HabitDialogFragment();
    }

    public static HabitDialogFragment newInstanceForEdit(long habitId) {
        HabitDialogFragment fragment = new HabitDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_EDIT_ID, habitId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_habit, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        long editId = getArguments() != null ? getArguments().getLong(ARG_EDIT_ID, -1) : -1;
        isEdit = editId != -1;
        if (isEdit) {
            editingHabit = repo.findById(editId);
            if (editingHabit == null) {
                dismiss();
                return root;
            }
        }

        bindViews(root);

        TextView tvTitle = root.findViewById(R.id.tvDialogTitle);
        tvTitle.setText(isEdit ? R.string.habit_edit_title : R.string.habit_new_title);
        btnSave.setText(isEdit ? R.string.save_edits : R.string.add_habit_btn);

        selectedIcon = isEdit ? editingHabit.icon : HabitRepository.ICONS[0];
        reminderOn = isEdit && editingHabit.reminder.enabled;
        if (isEdit) {
            edtName.setText(editingHabit.name);
            String[] parts = editingHabit.reminder.time.split("[: ]");
            if (parts.length == 3) time = parts;
        }

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        buildIconPicker();
        renderReminderState(false);

        swReminder.setChecked(reminderOn);
        swReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminderOn = isChecked;
            renderReminderState(true);
        });

        btnPickTime.setOnClickListener(v -> {
            TimeWheelDialogFragment picker = TimeWheelDialogFragment.newInstance(time);
            getChildFragmentManager().setFragmentResultListener(
                    TimeWheelDialogFragment.RESULT_KEY, this, (key, bundle) -> {
                        time = bundle.getStringArray(TimeWheelDialogFragment.KEY_TIME_PARTS);
                        renderTimeDisplay();
                    });
            picker.show(getChildFragmentManager(), "time_wheel");
        });

        edtName.addTextChangedListener(new TextWatcher() {
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

        renderTimeDisplay();
        renderSaveEnabled();

        FadeUtils.dialogFade(root);
        return root;
    }

    private void bindViews(View root) {
        edtName = root.findViewById(R.id.edtName);
        llIconPicker = root.findViewById(R.id.llIconPicker);
        swReminder = root.findViewById(R.id.swReminder);
        ivReminderIcon = root.findViewById(R.id.ivReminderIcon);
        groupReminderDetail = root.findViewById(R.id.groupReminderDetail);
        btnPickTime = root.findViewById(R.id.btnPickTime);
        tvTimeDisplay = root.findViewById(R.id.tvTimeDisplay);
        btnSave = root.findViewById(R.id.btnSave);
    }

    private void buildIconPicker() {
        llIconPicker.removeAllViews();
        for (String icon : HabitRepository.ICONS) {
            TextView cell = (TextView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_icon_pick, llIconPicker, false);
            cell.setText(icon);
            cell.setOnClickListener(v -> {
                selectedIcon = icon;
                renderIconPicker();
            });
            TapBounce.attach(cell, 0.85f);
            llIconPicker.addView(cell);
        }
        renderIconPicker();
    }

    private void renderIconPicker() {
        for (int i = 0; i < llIconPicker.getChildCount(); i++) {
            TextView cell = (TextView) llIconPicker.getChildAt(i);
            boolean selected = cell.getText().toString().equals(selectedIcon);
            cell.setBackgroundResource(selected ? R.drawable.bg_icon_pick_selected : R.drawable.bg_icon_pick_unselected);
        }
    }

    private void renderReminderState(boolean animate) {
        ivReminderIcon.setImageResource(reminderOn ? R.drawable.ic_bell : R.drawable.ic_bell_off);

        groupReminderDetail.setVisibility(reminderOn ? View.VISIBLE : View.GONE);
        if (reminderOn && animate) {
            FadeUtils.reminderFade(groupReminderDetail);
        }
    }

    private void renderTimeDisplay() {
        tvTimeDisplay.setText(time[0] + ":" + time[1] + " " + time[2]);
    }

    private boolean isFormValid() {
        return !edtName.getText().toString().trim().isEmpty();
    }

    private void renderSaveEnabled() {
        boolean valid = isFormValid();
        btnSave.setEnabled(valid);
        btnSave.setAlpha(valid ? 1f : 0.4f);
    }

    private void handleSave() {
        if (!isFormValid()) return;

        String name = edtName.getText().toString().trim();
        String displayTime = time[0] + ":" + time[1] + " " + time[2];
        Reminder reminder = new Reminder(reminderOn, displayTime);

        if (isEdit) {
            repo.updateHabit(editingHabit.id, name, selectedIcon, reminder);
        } else {
            repo.addHabit(name, selectedIcon, reminder);
        }

        getParentFragmentManager().setFragmentResult(REQUEST_KEY, new Bundle());
        dismiss();
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_DailyHabits_BottomSheet;
    }
}
