package com.example.graduationproject.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.R;
import com.example.graduationproject.widget.FadeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Equivalent of &lt;AddTraitDialog/&gt;: 4 suggestion chips, a name field
 * (tapping a chip fills it in, matching the original's `setName(s)`), an
 * optional note, and a save button disabled until a name is present.
 */
public class AddTraitDialogFragment extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "add_trait_result";
    public static final String KEY_NAME = "name";
    public static final String KEY_NOTE = "note";

    private static final List<String> SUGGESTIONS = Arrays.asList("الامتنان", "المرونة", "الإصرار", "اللطف");

    public static AddTraitDialogFragment newInstance() {
        return new AddTraitDialogFragment();
    }

    private String selectedName = "";
    private LinearLayout llSuggestionChips;
    private EditText edtName, edtNote;
    private TextView btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_add_trait, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        llSuggestionChips = root.findViewById(R.id.llSuggestionChips);
        edtName = root.findViewById(R.id.edtName);
        edtNote = root.findViewById(R.id.edtNote);
        btnSave = root.findViewById(R.id.btnSave);

        buildSuggestionChips();

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                selectedName = s.toString();
                renderSuggestionChips();
                renderSaveEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            if (name.isEmpty()) return;
            String note = edtNote.getText().toString().trim();

            Bundle result = new Bundle();
            result.putString(KEY_NAME, name);
            result.putString(KEY_NOTE, note);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });

        renderSaveEnabled();
        FadeUtils.slideInUp(root, 30, 400, 0);
        return root;
    }

    private void buildSuggestionChips() {
        llSuggestionChips.removeAllViews();
        for (String suggestion : SUGGESTIONS) {
            TextView chip = (TextView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_suggestion_chip, llSuggestionChips, false);
            chip.setText(suggestion);
            chip.setOnClickListener(v -> {
                selectedName = suggestion;
                edtName.setText(suggestion);
                edtName.setSelection(suggestion.length());
                renderSuggestionChips();
                renderSaveEnabled();
            });
            llSuggestionChips.addView(chip);
        }
        renderSuggestionChips();
    }

    private void renderSuggestionChips() {
        for (int i = 0; i < llSuggestionChips.getChildCount(); i++) {
            TextView chip = (TextView) llSuggestionChips.getChildAt(i);
            boolean selected = chip.getText().toString().equals(selectedName);
            chip.setBackgroundResource(selected ? R.drawable.bg_pill_primary : R.drawable.bg_pill_overlay);
        }
    }

    private void renderSaveEnabled() {
        boolean hasName = !edtName.getText().toString().trim().isEmpty();
        btnSave.setEnabled(hasName);
        btnSave.setAlpha(hasName ? 1f : 0.4f);
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_StrengthsBank_BottomSheet;
    }
}
