package com.example.graduationproject.dialogs;

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
import com.example.graduationproject.models.LoveItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Equivalent of <AddLoveDialog/>: a source toggle ("من نفسي" / "من شخص أحبه")
 * plus a multiline message, with the save button disabled until text is present
 * (mirrors `disabled={!text}` in the original).
 */
public class AddLoveDialogFragment extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "add_love_result";
    public static final String KEY_TEXT = "text";
    public static final String KEY_SOURCE = "source";

    public static AddLoveDialogFragment newInstance() {
        return new AddLoveDialogFragment();
    }

    private String source = LoveItem.SOURCE_SELF;
    private TextView btnSourceSelf, btnSourceOther, btnSave;
    private EditText edtText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_add_love, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        btnSourceSelf = root.findViewById(R.id.btnSourceSelf);
        btnSourceOther = root.findViewById(R.id.btnSourceOther);
        edtText = root.findViewById(R.id.edtText);
        btnSave = root.findViewById(R.id.btnSave);

        btnSourceSelf.setOnClickListener(v -> {
            source = LoveItem.SOURCE_SELF;
            renderSourceToggle();
        });
        btnSourceOther.setOnClickListener(v -> {
            source = LoveItem.SOURCE_OTHER;
            renderSourceToggle();
        });

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

        btnSave.setOnClickListener(v -> {
            String text = edtText.getText().toString().trim();
            if (text.isEmpty()) return;
            Bundle result = new Bundle();
            result.putString(KEY_TEXT, text);
            result.putString(KEY_SOURCE, source);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });

        renderSourceToggle();
        renderSaveEnabled();
        return root;
    }

    private void renderSourceToggle() {
        btnSourceSelf.setSelected(LoveItem.SOURCE_SELF.equals(source));
        btnSourceOther.setSelected(LoveItem.SOURCE_OTHER.equals(source));
    }

    private void renderSaveEnabled() {
        boolean hasText = !edtText.getText().toString().trim().isEmpty();
        btnSave.setEnabled(hasText);
        btnSave.setAlpha(hasText ? 1f : 0.4f);
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_SurvivalBox_BottomSheet;
    }
}
