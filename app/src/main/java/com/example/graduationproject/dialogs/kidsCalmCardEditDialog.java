package com.example.graduationproject.dialogs;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
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

import com.example.graduationproject.R;
import com.example.graduationproject.models.kidsCalmKidCardModel;
import com.example.graduationproject.models.kidsCalmSticker;
import com.example.graduationproject.util.kidsCalmAnimUtils;
import com.example.graduationproject.util.kidsCalmAppState;
import com.example.graduationproject.view.kidsCalmKidCardView;

/** Mirrors the React sheetOpen/draft card create+edit modal. */
public class kidsCalmCardEditDialog extends DialogFragment {

    public interface Listener {
        void onCardSaved();
    }

    private static final String ARG_ID = "id";

    private Listener listener;
    private Long editingId = null;

    private String draftPhrase = "";
    private kidsCalmSticker draftSticker;
    private Uri draftPhoto = null;

    private EditText phraseInput;
    private kidsCalmKidCardView preview;
    private TextView saveButton;
    private TextView photoButton;
    private GridLayout stickerGrid;

    private ActivityResultLauncher<String> imagePicker;

    public static kidsCalmCardEditDialog newInstanceCreate() {
        return new kidsCalmCardEditDialog();
    }

    public static kidsCalmCardEditDialog newInstanceEdit(long id) {
        kidsCalmCardEditDialog d = new kidsCalmCardEditDialog();
        Bundle b = new Bundle();
        b.putLong(ARG_ID, id);
        d.setArguments(b);
        return d;
    }

    public void setListener(Listener l) { this.listener = l; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                try {
                    requireContext().getContentResolver().takePersistableUriPermission(uri,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception ignored) { }
                draftPhoto = uri;
                photoButton.setText(R.string.kids_calm_dialog_photo_btn_set);
                refreshPreview();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        kidsCalmAppState state = kidsCalmAppState.get();

        if (getArguments() != null && getArguments().containsKey(ARG_ID)) {
            long id = getArguments().getLong(ARG_ID);
            for (kidsCalmKidCardModel c : state.cards) {
                if (c.id == id) {
                    editingId = id;
                    draftPhrase = c.phrase;
                    draftSticker = c.sticker;
                    draftPhoto = c.photoUri;
                }
            }
        }
        if (draftSticker == null && draftPhoto == null) draftSticker = state.stickers.get(0);

        Dialog dialog = new Dialog(requireContext(), R.style.kids_calm_Dialog_Rounded);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.kids_calm_dialog_card_edit, null);
        dialog.setContentView(v);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextView title = v.findViewById(R.id.dialogCardTitle);
        title.setText(editingId == null ? R.string.kids_calm_dialog_new_card_title : R.string.kids_calm_dialog_edit_card_title);

        phraseInput = v.findViewById(R.id.phraseInput);
        phraseInput.setText(draftPhrase);
        phraseInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                draftPhrase = s.toString();
                refreshPreview();
                refreshSaveState();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        LinearLayout quickRow = v.findViewById(R.id.quickPhrasesRow);
        for (String phrase : state.kidPhrases) {
            TextView chip = new TextView(requireContext());
            chip.setText(phrase);
            chip.setBackgroundResource(R.drawable.kids_calm_bg_pill_outline);
            chip.setTextColor(getResources().getColor(R.color.kids_calm_navySoft));
            chip.setTextSize(12);
            int pad = dp(8);
            chip.setPadding(dp(13), pad, dp(13), pad);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(8));
            chip.setLayoutParams(lp);
            chip.setOnClickListener(x -> {
                draftPhrase = phrase;
                phraseInput.setText(phrase);
                phraseInput.setSelection(phrase.length());
            });
            quickRow.addView(chip);
        }

        stickerGrid = v.findViewById(R.id.stickerGrid);
        buildStickerGrid(state);

        photoButton = v.findViewById(R.id.photoButton);
        photoButton.setText(draftPhoto != null ? R.string.kids_calm_dialog_photo_btn_set : R.string.kids_calm_dialog_photo_btn);
        photoButton.setOnClickListener(x -> imagePicker.launch("image/*"));

        preview = v.findViewById(R.id.previewKidCard);
        refreshPreview();

        TextView cancel = v.findViewById(R.id.cancelButton);
        cancel.setOnClickListener(x -> dismiss());

        saveButton = v.findViewById(R.id.saveCardButton);
        saveButton.setOnClickListener(x -> save());
        refreshSaveState();

        kidsCalmAnimUtils.pop(v);
        return dialog;
    }

    private void buildStickerGrid(kidsCalmAppState state) {
        stickerGrid.removeAllViews();
        int screenWidthDp = 340;
        int colWidth = dp((screenWidthDp - 16) / 3);
        for (kidsCalmSticker s : state.stickers) {
            View cell = LayoutInflater.from(requireContext()).inflate(R.layout.kids_calm_item_sticker_option, stickerGrid, false);
            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = 0;
            glp.height = ViewGroupHeight();
            glp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            glp.setMargins(0, 0, dp(8), dp(8));
            cell.setLayoutParams(glp);

            View bg = cell.findViewById(R.id.stickerBg);
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                    new int[]{s.colorStart, s.colorEnd});
            gd.setCornerRadius(dp(16));
            bg.setBackground(gd);

            TextView emoji = cell.findViewById(R.id.stickerOptionEmoji);
            TextView label = cell.findViewById(R.id.stickerOptionLabel);
            emoji.setText(s.emoji);
            label.setText(s.label);

            View ring = cell.findViewById(R.id.stickerSelectedRing);
            boolean selected = draftPhoto == null && draftSticker != null && draftSticker.id.equals(s.id);
            ring.setVisibility(selected ? View.VISIBLE : View.GONE);

            cell.setOnClickListener(x -> {
                draftSticker = s;
                draftPhoto = null;
                photoButton.setText(R.string.kids_calm_dialog_photo_btn);
                buildStickerGrid(state);
                refreshPreview();
            });

            stickerGrid.addView(cell);
        }
    }

    private int ViewGroupHeight() { return dp(66); }

    private void refreshPreview() {
        if (preview != null) preview.setContent(draftPhrase, draftSticker, draftPhoto);
    }

    private void refreshSaveState() {
        boolean enabled = draftPhrase != null && !draftPhrase.trim().isEmpty();
        saveButton.setBackgroundResource(enabled ? R.drawable.kids_calm_bg_big_button_coral : R.drawable.kids_calm_bg_big_button_disabled);
        saveButton.setTextColor(getResources().getColor(enabled ? R.color.kids_calm_white : R.color.kids_calm_navySoft));
    }

    private void save() {
        if (draftPhrase == null || draftPhrase.trim().isEmpty()) return;
        kidsCalmAppState state = kidsCalmAppState.get();
        String phrase = draftPhrase.trim();

        if (editingId == null) {
            boolean makeActive = state.cards.isEmpty();
            kidsCalmKidCardModel card = new kidsCalmKidCardModel(state.nextId(), phrase,
                    draftPhoto != null ? null : draftSticker, draftPhoto, makeActive);
            if (makeActive) for (kidsCalmKidCardModel c : state.cards) c.active = false;
            state.cards.add(card);
        } else {
            for (kidsCalmKidCardModel c : state.cards) {
                if (c.id == editingId) {
                    c.phrase = phrase;
                    c.sticker = draftPhoto != null ? null : draftSticker;
                    c.photoUri = draftPhoto;
                }
            }
        }
        state.notifyChanged();
        if (listener != null) listener.onCardSaved();
        dismiss();
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
