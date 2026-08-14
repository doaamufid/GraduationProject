package com.example.graduationproject.ui.profile.settings;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.R;
import com.example.graduationproject.widget.FadeUtils;

/**
 * Equivalent of &lt;DeleteAllDialog/&gt;: a 2-step destructive confirmation.
 * Step 1 warns and asks to proceed; step 2 (same dialog, re-rendered) is
 * the final "no going back" confirmation, matching the original's local
 * `step` state that swaps the title/body/button text in place.
 */
public class DeleteAllDialogFragment extends DialogFragment {

    public static final String REQUEST_KEY = "delete_all_result";

    public static DeleteAllDialogFragment newInstance() {
        return new DeleteAllDialogFragment();
    }

    private int step = 1;
    private TextView tvStepTitle, tvStepBody, btnConfirm, btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_delete_all, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        tvStepTitle = root.findViewById(R.id.tvStepTitle);
        tvStepBody = root.findViewById(R.id.tvStepBody);
        btnConfirm = root.findViewById(R.id.btnConfirm);
        btnCancel = root.findViewById(R.id.btnCancel);

        btnConfirm.setOnClickListener(v -> {
            if (step == 1) {
                step = 2;
                render();
            } else {
                getParentFragmentManager().setFragmentResult(REQUEST_KEY, new Bundle());
                dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());

        FadeUtils.dialogIn(root);

        render();
        return root;
    }

    private void render() {
        if (step == 1) {
            tvStepTitle.setText(R.string.delete_step1_title);
            tvStepBody.setText(R.string.delete_step1_body);
            btnConfirm.setText(R.string.delete_step1_confirm);
            btnCancel.setText(R.string.delete_step1_cancel);
        } else {
            tvStepTitle.setText(R.string.delete_step2_title);
            tvStepBody.setText(R.string.delete_step2_body);
            btnConfirm.setText(R.string.delete_step2_confirm);
            btnCancel.setText(R.string.delete_step2_cancel);
        }
    }

    @Override
    public int getTheme() {
        return R.style.Theme_SettingsApp_Dialog;
    }
}
