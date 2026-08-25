package com.example.graduationproject.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.AppHost;
import com.example.graduationproject.R;
import com.example.graduationproject.data.AppRepository;
import com.example.graduationproject.data.Constants;
import com.example.graduationproject.models.CardItem;
import com.example.graduationproject.models.CardPhoto;
import com.example.graduationproject.models.Preset;
import com.example.graduationproject.view.CalmCardView;

/**
 * Java equivalent of the JS create/edit bottom sheet: phrase textarea with
 * an 80-char counter, horizontally-scrolling phrase suggestions, a 3x2
 * preset gradient grid, an upload-photo button (image picker), a live
 * CalmCardView preview, and cancel/save buttons (save disabled until a
 * phrase is entered).
 */
public class CardEditDialogFragment extends DialogFragment {

    private static final String ARG_CARD_ID = "card_id";

    public static CardEditDialogFragment newInstanceCreate() {
        return new CardEditDialogFragment();
    }

    public static CardEditDialogFragment newInstanceEdit(long cardId) {
        CardEditDialogFragment f = new CardEditDialogFragment();
        Bundle b = new Bundle();
        b.putLong(ARG_CARD_ID, cardId);
        f.setArguments(b);
        return f;
    }

    private final AppRepository repo = AppRepository.get();
    private Long editingId = null;

    private String draftPhrase = "";
    private CardPhoto draftPhoto = null;

    private EditText phraseInput;
    private TextView charCounter;
    private LinearLayout suggestionsContainer;
    private GridLayout presetGrid;
    private TextView btnUploadPhoto, btnRemovePhoto;
    private CalmCardView previewCard;
    private TextView btnCancel, btnSave;
    private TextView dialogTitle;

    private String selectedPresetId = null;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        draftPhoto = CardPhoto.fromUpload(uri);
                        selectedPresetId = null;
                        refreshPhotoUi();
                    }
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_CalmApp_Dialog);
        if (getArguments() != null && getArguments().containsKey(ARG_CARD_ID)) {
            editingId = getArguments().getLong(ARG_CARD_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_card_edit, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window w = dialog.getWindow();
            w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            w.setGravity(Gravity.CENTER);
            w.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialogTitle = view.findViewById(R.id.dialogTitle);
        phraseInput = view.findViewById(R.id.phraseInput);
        charCounter = view.findViewById(R.id.charCounter);
        suggestionsContainer = view.findViewById(R.id.suggestionsContainer);
        presetGrid = view.findViewById(R.id.presetGrid);
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto);
        btnRemovePhoto = view.findViewById(R.id.btnRemovePhoto);
        previewCard = view.findViewById(R.id.previewCard);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);

        // load existing card if editing
        CardItem editing = null;
        if (editingId != null) {
            for (CardItem c : repo.getCards()) if (c.id == editingId) editing = c;
        }
        if (editing != null) {
            dialogTitle.setText(R.string.card_edit_title);
            draftPhrase = editing.phrase;
            draftPhoto = editing.photo;
            selectedPresetId = (draftPhoto != null && draftPhoto.type == CardPhoto.Type.PRESET)
                    ? draftPhoto.preset.id : null;
        } else {
            dialogTitle.setText(R.string.card_new_title);
        }

        phraseInput.setText(draftPhrase);
        phraseInput.setSelection(draftPhrase.length());
        phraseInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                draftPhrase = s.toString();
                updateCounterAndSaveState();
                updatePreview();
            }
        });

        buildSuggestions();
        buildPresetGrid();
        refreshPhotoUi();
        updateCounterAndSaveState();
        updatePreview();

        btnUploadPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        btnRemovePhoto.setOnClickListener(v -> {
            draftPhoto = null;
            selectedPresetId = null;
            refreshPhotoUi();
            updatePreview();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String trimmed = draftPhrase.trim();
            if (trimmed.isEmpty()) return;
            if (editingId != null) {
                repo.updateCard(editingId, trimmed, draftPhoto);
                notifyToast(getString(R.string.toast_updated_card));
            } else {
                repo.addCard(trimmed, draftPhoto);
                notifyToast(getString(R.string.toast_added_card));
            }
            dismiss();
        });
    }

    private void notifyToast(String msg) {
        if (getActivity() instanceof AppHost) ((AppHost) getActivity()).showToast(msg);
    }

    private void buildSuggestions() {
        suggestionsContainer.removeAllViews();
        float d = getResources().getDisplayMetrics().density;
        for (String phrase : Constants.PHRASE_SUGGESTIONS) {
            TextView chip = new TextView(getContext());
            chip.setText(phrase);
            chip.setTextColor(getResources().getColor(R.color.amber));
            chip.setTextSize(12.5f);
            chip.setBackgroundResource(R.drawable.bg_chip);
            chip.setPadding((int) (14 * d), (int) (8 * d), (int) (14 * d), (int) (8 * d));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd((int) (8 * d));
            chip.setLayoutParams(lp);
            chip.setOnClickListener(v -> {
                draftPhrase = phrase;
                phraseInput.setText(phrase);
                phraseInput.setSelection(phrase.length());
            });
            suggestionsContainer.addView(chip);
        }
    }

    private void buildPresetGrid() {
        presetGrid.removeAllViews();
        float d = getResources().getDisplayMetrics().density;
        int colCount = 3;
        for (int i = 0; i < Constants.PRESETS.length; i++) {
            Preset preset = Constants.PRESETS[i];

            LinearLayout tile = new LinearLayout(getContext());
            tile.setOrientation(LinearLayout.VERTICAL);
            tile.setGravity(Gravity.CENTER);
            tile.setBackgroundResource(preset.gradientDrawableRes);

            TextView emoji = new TextView(getContext());
            emoji.setText(preset.emoji);
            emoji.setTextSize(17);
            emoji.setGravity(Gravity.CENTER);
            tile.addView(emoji);

            TextView label = new TextView(getContext());
            label.setText(preset.label);
            label.setTextSize(9.5f);
            label.setTextColor(0xE6FFFFFF);
            label.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            labelLp.topMargin = (int) (3 * d);
            tile.addView(label, labelLp);

            boolean selected = preset.id.equals(selectedPresetId);
            if (selected) {
                android.graphics.drawable.GradientDrawable ring = new android.graphics.drawable.GradientDrawable();
                ring.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
                ring.setCornerRadius(14 * d);
                ring.setStroke((int) (2 * d), getResources().getColor(R.color.amber));
                tile.setForeground(ring);
                tile.setScaleX(1.03f);
                tile.setScaleY(1.03f);
            } else {
                tile.setScaleX(1f);
                tile.setScaleY(1f);
            }

            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = 0;
            glp.height = (int) (68 * d);
            glp.columnSpec = GridLayout.spec(i % colCount, 1f);
            glp.rowSpec = GridLayout.spec(i / colCount);
            glp.setMargins((int) (4 * d), (int) (4 * d), (int) (4 * d), (int) (4 * d));
            tile.setLayoutParams(glp);

            tile.setOnClickListener(v -> {
                draftPhoto = CardPhoto.fromPreset(preset);
                selectedPresetId = preset.id;
                buildPresetGrid();
                refreshPhotoUi();
                updatePreview();
            });

            presetGrid.addView(tile);
        }
    }

    private void refreshPhotoUi() {
        boolean hasUpload = draftPhoto != null && draftPhoto.type == CardPhoto.Type.UPLOAD;
        btnUploadPhoto.setText(hasUpload ? R.string.upload_photo_selected : R.string.upload_photo);
        btnUploadPhoto.setBackgroundResource(hasUpload ? R.drawable.bg_chip : R.drawable.bg_btn_dashed);
        btnRemovePhoto.setVisibility(draftPhoto != null ? View.VISIBLE : View.GONE);
    }

    private void updateCounterAndSaveState() {
        int len = draftPhrase.length();
        charCounter.setText(len + "/80");
        boolean enabled = !draftPhrase.trim().isEmpty();
        btnSave.setEnabled(enabled);
        btnSave.setBackgroundResource(enabled ? R.drawable.bg_btn_primary : R.drawable.bg_btn_primary_disabled);
        btnSave.setTextColor(getResources().getColor(enabled ? R.color.cardBtnText : R.color.mutedDim));
    }

    private void updatePreview() {
        previewCard.setCard(draftPhoto, draftPhrase, false);
    }
}
