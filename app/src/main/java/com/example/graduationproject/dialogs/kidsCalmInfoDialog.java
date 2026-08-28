package com.example.graduationproject.dialogs;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.R;
import com.example.graduationproject.util.kidsCalmAnimUtils;

/** Mirrors the React showInfo modal ("شو هاد المكان؟"). */
public class kidsCalmInfoDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), R.style.kids_calm_Dialog_Rounded);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.kids_calm_dialog_info, null);
        dialog.setContentView(v);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        v.findViewById(R.id.infoOkButton).setOnClickListener(x -> dismiss());
        kidsCalmAnimUtils.pop(v);
        return dialog;
    }
}
