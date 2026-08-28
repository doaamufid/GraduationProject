package com.example.graduationproject.dialogs;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.R;
import com.example.graduationproject.models.kidsCalmWordModel;
import com.example.graduationproject.util.kidsCalmAnimUtils;
import com.example.graduationproject.util.kidsCalmAppState;
import com.example.graduationproject.util.kidsCalmDurChipsHelper;

/** Mirrors the React wordSheetOpen/wordDraft modal ("كلمتي الخاصة"). */
public class kidsCalmWordEditDialog extends DialogFragment {

    public interface Listener {
        void onWordSaved();
    }

    private Listener listener;
    public void setListener(Listener l) { this.listener = l; }

    private String draftText = "";
    private String draftEmoji;
    private String draftDurKey = "short";

    private TextView saveButton;
    private GridLayout emojiRow;
    private LinearLayout durChipsContainer;

    public static kidsCalmWordEditDialog newInstance() {
        return new kidsCalmWordEditDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        kidsCalmAppState state = kidsCalmAppState.get();
        draftEmoji = state.wordEmojis.get(0);

        Dialog dialog = new Dialog(requireContext(), R.style.kids_calm_Dialog_Rounded);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.kids_calm_dialog_word_edit, null);
        dialog.setContentView(v);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        EditText input = v.findViewById(R.id.wordInput);
        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                draftText = s.toString();
                refreshSaveState();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        emojiRow = v.findViewById(R.id.emojiRow);
        buildEmojiRow(state);

        durChipsContainer = v.findViewById(R.id.wordDurChips);
        renderDurChips();

        v.findViewById(R.id.wordCancelButton).setOnClickListener(x -> dismiss());

        saveButton = v.findViewById(R.id.wordSaveButton);
        saveButton.setOnClickListener(x -> save());
        refreshSaveState();

        kidsCalmAnimUtils.pop(v);
        return dialog;
    }

    private void renderDurChips() {
        kidsCalmDurChipsHelper.render(durChipsContainer, requireContext(), draftDurKey, key -> {
            draftDurKey = key;
            renderDurChips();
        });
    }

    private void buildEmojiRow(kidsCalmAppState state) {
        emojiRow.removeAllViews();
        for (String emoji : state.wordEmojis) {
            TextView cell = new TextView(requireContext());
            cell.setText(emoji);
            cell.setTextSize(18);
            cell.setGravity(android.view.Gravity.CENTER);
            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = dp(40);
            glp.height = dp(40);
            glp.setMargins(0, 0, dp(8), dp(8));
            cell.setLayoutParams(glp);
            cell.setBackgroundResource(emoji.equals(draftEmoji) ? R.drawable.kids_calm_bg_emoji_option_selected : R.drawable.kids_calm_bg_emoji_option);
            cell.setOnClickListener(x -> {
                draftEmoji = emoji;
                buildEmojiRow(state);
            });
            emojiRow.addView(cell);
        }
    }

    private void refreshSaveState() {
        boolean enabled = draftText != null && !draftText.trim().isEmpty();
        saveButton.setBackgroundResource(enabled ? R.drawable.kids_calm_bg_big_button_mint : R.drawable.kids_calm_bg_big_button_disabled);
        saveButton.setTextColor(getResources().getColor(enabled ? R.color.kids_calm_white : R.color.kids_calm_navySoft));
    }

    private void save() {
        if (draftText == null || draftText.trim().isEmpty()) return;
        kidsCalmAppState state = kidsCalmAppState.get();
        kidsCalmWordModel w = new kidsCalmWordModel(state.nextId(), draftText.trim(), draftEmoji, true, draftDurKey);
        state.words.add(0, w);
        state.notifyChanged();
        if (listener != null) listener.onWordSaved();
        dismiss();
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
