package com.example.graduationproject.ui.profile.settings;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.R;
import com.example.graduationproject.models.profile.settings.SettingsRepository;
import com.example.graduationproject.widget.FadeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Equivalent of &lt;DialectDialog/&gt;: a list of dialect options; tapping
 * one both selects it and immediately closes the sheet, matching the
 * original's `onPick={(d) => { setDialect(d); setDialectOpen(false); }}`.
 */
public class DialectDialogFragment extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "dialect_result";
    public static final String KEY_DIALECT = "dialect";
    private static final String ARG_CURRENT = "current_dialect";

    public static DialectDialogFragment newInstance(String currentDialect) {
        DialectDialogFragment fragment = new DialectDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT, currentDialect);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_dialect, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        FadeUtils.dialogIn(root);

        String current = getArguments() != null ? getArguments().getString(ARG_CURRENT, "") : "";
        LinearLayout llOptions = root.findViewById(R.id.llDialectOptions);
        llOptions.removeAllViews();

        for (String key : SettingsRepository.DIALECT_KEYS) {
            View item = LayoutInflater.from(requireContext()).inflate(R.layout.item_dialect_option, llOptions, false);
            TextView tvLabel = item.findViewById(R.id.tvDialectLabel);
            ImageView ivCheck = item.findViewById(R.id.ivDialectCheck);

            tvLabel.setText(SettingsRepository.getDialectName(requireContext(), key));
            boolean selected = key.equals(current);
            item.setBackgroundResource(selected ? R.drawable.bg_dialect_item_selected : R.drawable.bg_dialect_item_unselected);
            ivCheck.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);

            item.setOnClickListener(v -> {
                Bundle result = new Bundle();
                result.putString(KEY_DIALECT, key);
                getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
                dismiss();
            });

            llOptions.addView(item);
        }

        return root;
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_SettingsApp_BottomSheet;
    }
}
