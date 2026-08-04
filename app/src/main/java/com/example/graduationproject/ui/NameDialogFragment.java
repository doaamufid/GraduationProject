package com.example.graduationproject.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/** Equivalent of &lt;NameDialog/&gt;: a single text field pre-filled with the current nickname. */
public class NameDialogFragment extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "name_dialog_result";
    public static final String KEY_NAME = "name";
    private static final String ARG_CURRENT = "current_name";

    public static NameDialogFragment newInstance(String currentName) {
        NameDialogFragment fragment = new NameDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT, currentName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_name, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        String currentName = getArguments() != null ? getArguments().getString(ARG_CURRENT, "") : "";

        EditText edtName = root.findViewById(R.id.edtName);
        edtName.setText(currentName);
        edtName.setSelection(currentName.length());

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        root.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String value = edtName.getText().toString();
            Bundle result = new Bundle();
            result.putString(KEY_NAME, value);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });

        return root;
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_JourneyProfile_BottomSheet;
    }
}
