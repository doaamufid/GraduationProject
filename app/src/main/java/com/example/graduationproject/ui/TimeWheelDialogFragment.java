package com.example.graduationproject.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Equivalent of &lt;TimeWheel/&gt;: three tap-to-select columns (hour,
 * minute, AM/PM). Like the original, this is a plain scrollable list of
 * buttons rather than a true snapping wheel - the original component
 * itself only implements tap-to-select despite the "wheel" name, and
 * even limits minutes to "00".."05" (kept exactly as-is for fidelity).
 */
public class TimeWheelDialogFragment extends BottomSheetDialogFragment {

    public static final String RESULT_KEY = "time_wheel_result";
    public static final String KEY_TIME_PARTS = "time_parts";
    private static final String ARG_INITIAL = "initial";

    private static final String[] HOURS = {"09", "10", "11", "12", "01", "02", "03", "04", "05"};
    private static final String[] MINUTES = {"00", "01", "02", "03", "04", "05"};
    private static final String[] AMPM = {"AM", "PM"};

    private String[] value;

    public static TimeWheelDialogFragment newInstance(String[] initial) {
        TimeWheelDialogFragment fragment = new TimeWheelDialogFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_INITIAL, initial);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_time_wheel, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        String[] initial = getArguments() != null ? getArguments().getStringArray(ARG_INITIAL) : null;
        value = (initial != null && initial.length == 3) ? initial.clone() : new String[]{"12", "02", "AM"};

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        ScrollView scrollHours = root.findViewById(R.id.scrollHours);
        ScrollView scrollMinutes = root.findViewById(R.id.scrollMinutes);
        ScrollView scrollAmPm = root.findViewById(R.id.scrollAmPm);

        buildColumn(scrollHours, HOURS, 0);
        buildColumn(scrollMinutes, MINUTES, 1);
        buildColumn(scrollAmPm, AMPM, 2);

        root.findViewById(R.id.btnDone).setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putStringArray(KEY_TIME_PARTS, value);
            getParentFragmentManager().setFragmentResult(RESULT_KEY, result);
            dismiss();
        });

        return root;
    }

    /** @param columnIndex 0 = hour, 1 = minute, 2 = AM/PM, matching the `value` array layout. */
    private void buildColumn(ScrollView scrollView, String[] items, int columnIndex) {
        LinearLayout column = new LinearLayout(requireContext());
        column.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(column);

        for (String item : items) {
            TextView option = (TextView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_time_option, column, false);
            option.setText(item);
            applySelectedStyle(option, item.equals(value[columnIndex]));

            option.setOnClickListener(v -> {
                value[columnIndex] = item;
                for (int i = 0; i < column.getChildCount(); i++) {
                    TextView sibling = (TextView) column.getChildAt(i);
                    applySelectedStyle(sibling, sibling.getText().toString().equals(item));
                }
            });
            column.addView(option);
        }
    }

    private void applySelectedStyle(TextView option, boolean selected) {
        option.setTextColor(getResources().getColor(selected ? R.color.primary : R.color.text_soft));
        option.setBackgroundResource(selected ? R.drawable.bg_time_item_selected : android.R.color.transparent);
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_DailyHabits_BottomSheet;
    }
}
