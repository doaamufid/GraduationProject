package com.example.graduationproject.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.R;
import com.example.graduationproject.models.ProfileRepository;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

/**
 * Equivalent of &lt;AvatarPicker/&gt;: 5 solid-color swatches; tapping one
 * both selects it and immediately closes the sheet (matches the original's
 * `onPick={(c) => { setAvatarColor(c); setAvatarOpen(false); }}`).
 */
public class AvatarPickerDialogFragment extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "avatar_picker_result";
    public static final String KEY_COLOR = "color";
    private static final String ARG_CURRENT = "current_color";

    public static AvatarPickerDialogFragment newInstance(int currentColor) {
        AvatarPickerDialogFragment fragment = new AvatarPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT, currentColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_avatar_picker, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        int currentColor = getArguments() != null ? getArguments().getInt(ARG_CURRENT) : 0;
        LinearLayout llSwatches = root.findViewById(R.id.llSwatches);
        llSwatches.removeAllViews();

        List<Integer> colors = ProfileRepository.avatarPatterns(requireContext());
        for (int color : colors) {
            View swatch = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_avatar_swatch, llSwatches, false);

            View swatchBg = swatch.findViewById(R.id.swatchBg);
            ImageView ivCheck = swatch.findViewById(R.id.ivSwatchCheck);

            swatchBg.getBackground().mutate().setTint(color);
            ivCheck.setVisibility(color == currentColor ? View.VISIBLE : View.INVISIBLE);

            swatch.setOnClickListener(v -> {
                Bundle result = new Bundle();
                result.putInt(KEY_COLOR, color);
                getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
                dismiss();
            });

            llSwatches.addView(swatch);
        }

        return root;
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_JourneyProfile_BottomSheet;
    }
}
