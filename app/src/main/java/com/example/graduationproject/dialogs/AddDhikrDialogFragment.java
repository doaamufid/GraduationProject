package com.example.graduationproject.dialogs;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.example.graduationproject.models.DhikrItem;
import com.example.graduationproject.models.SurvivalBoxRepository;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Equivalent of <AddDhikrDialog/>: a togglable preset list (pre-checked from
 * whatever is already in `existing`) plus a "write your own" custom mode.
 * On save, the WHOLE favourites list is replaced by the final selection -
 * exactly like the original `onSave(customMode && custom ? [...favorited, custom] : favorited)`.
 */
public class AddDhikrDialogFragment extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "add_dhikr_result";
    public static final String KEY_TEXTS = "texts";

    public static AddDhikrDialogFragment newInstance() {
        return new AddDhikrDialogFragment();
    }

    private boolean customMode = false;
    private String customText = "";
    private final List<String> favorited = new ArrayList<>();

    private LinearLayout llPresetList;
    private TextView tvWriteCustom, tvBackToList, btnSave;
    private EditText edtCustom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_add_dhikr, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        llPresetList = root.findViewById(R.id.llPresetList);
        tvWriteCustom = root.findViewById(R.id.tvWriteCustom);
        tvBackToList = root.findViewById(R.id.tvBackToList);
        edtCustom = root.findViewById(R.id.edtCustom);
        btnSave = root.findViewById(R.id.btnSave);

        // Pre-check whatever is already favorited (existing.map(d => d.text)).
        for (DhikrItem item : SurvivalBoxRepository.getInstance().getDhikr()) {
            favorited.add(item.text);
        }

        buildPresetList();

        tvWriteCustom.setOnClickListener(v -> {
            customMode = true;
            render();
        });
        tvBackToList.setOnClickListener(v -> {
            customMode = false;
            render();
        });

        edtCustom.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                customText = s.toString();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        btnSave.setOnClickListener(v -> {
            List<String> finalList = new ArrayList<>(favorited);
            if (customMode && !customText.trim().isEmpty()) {
                finalList.add(customText.trim());
            }
            Bundle result = new Bundle();
            result.putStringArrayList(KEY_TEXTS, new ArrayList<>(finalList));
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });

        render();
        return root;
    }

    private void buildPresetList() {
        llPresetList.removeAllViews();
        for (String dhikr : SurvivalBoxRepository.PRESET_DHIKR) {
            View row = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_preset_dhikr, llPresetList, false);
            TextView tvText = row.findViewById(R.id.tvText);
            ImageView ivStar = row.findViewById(R.id.ivStar);
            tvText.setText(dhikr);

            row.setOnClickListener(v -> {
                if (favorited.contains(dhikr)) {
                    favorited.remove(dhikr);
                } else {
                    favorited.add(dhikr);
                }
                updateStar(ivStar, dhikr);
            });

            updateStar(ivStar, dhikr);
            llPresetList.addView(row);
        }
    }

    private void updateStar(ImageView ivStar, String dhikr) {
        boolean fav = favorited.contains(dhikr);
        ivStar.setImageResource(fav ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
    }

    private void render() {
        llPresetList.setVisibility(customMode ? View.GONE : View.VISIBLE);
        tvWriteCustom.setVisibility(customMode ? View.GONE : View.VISIBLE);
        edtCustom.setVisibility(customMode ? View.VISIBLE : View.GONE);
        tvBackToList.setVisibility(customMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_SurvivalBox_BottomSheet;
    }
}
