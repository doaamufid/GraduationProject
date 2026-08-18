package com.example.graduationproject.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.graduationproject.R;
import com.example.graduationproject.models.Highlight;

/**
 * Java port of <HighlightPopover/>: shows the highlighted excerpt, a debounced auto-saving
 * note field, a recolor row, an active/inactive highlight toggle and a save-quote heart.
 */
public class NoteComposerSheet extends BottomSheetDialogFragment {

    public interface Listener {
        void onNoteChanged(Highlight highlight, String note);
        void onColorChanged(Highlight highlight, int color);
        void onActiveToggled(Highlight highlight, boolean active);
        void onDeleted(Highlight highlight);
        void onToggleSavedQuote(Highlight highlight);
        boolean isQuoteSaved(Highlight highlight);
        void onClosed();
    }

    private static final int[] COLORS = {
            0xFFFFE066, 0xFFFF9EC4, 0xFFB69CE8, 0xFF7FC8F0, 0xFF8FDDB0
    };

    private Highlight highlight;
    private Listener listener;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pendingSave;

    public static NoteComposerSheet newInstance(Highlight highlight, Listener listener) {
        NoteComposerSheet sheet = new NoteComposerSheet();
        sheet.highlight = highlight;
        sheet.listener = listener;
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.articles_bottomsheet_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvSaveState = view.findViewById(R.id.tvSaveState);
        TextView tvQuote = view.findViewById(R.id.tvQuoteText);
        View quoteBox = view.findViewById(R.id.quotePreviewBox);
        EditText etNote = view.findViewById(R.id.etNote);
        LinearLayout colorRow = view.findViewById(R.id.colorRow);
        TextView btnToggleActive = view.findViewById(R.id.btnToggleActive);
        TextView btnSaveNote = view.findViewById(R.id.btnSaveNote);
        ImageView btnFavToggle = view.findViewById(R.id.btnSaveQuoteToggle);

        tvQuote.setText(highlight.text);
        applyQuoteBoxTint(quoteBox);
        etNote.setText(highlight.note);
        refreshFavIcon(btnFavToggle);
        refreshToggleButton(btnToggleActive);
        buildColorRow(colorRow, quoteBox);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> {
            flushPendingSave();
            if (listener != null) listener.onClosed();
            dismiss();
        });

        view.findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (listener != null) listener.onDeleted(highlight);
            dismiss();
        });

        btnFavToggle.setOnClickListener(v -> {
            if (listener != null) listener.onToggleSavedQuote(highlight);
            refreshFavIcon(btnFavToggle);
        });

        btnToggleActive.setOnClickListener(v -> {
            highlight.active = !highlight.active;
            if (listener != null) listener.onActiveToggled(highlight, highlight.active);
            refreshToggleButton(btnToggleActive);
        });

        btnSaveNote.setOnClickListener(v -> {
            flushPendingSave();
            highlight.note = etNote.getText().toString();
            if (listener != null) listener.onNoteChanged(highlight, highlight.note);
            dismiss();
        });

        etNote.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvSaveState.setText(R.string.saving);
                if (pendingSave != null) handler.removeCallbacks(pendingSave);
                String value = s.toString();
                pendingSave = () -> {
                    highlight.note = value;
                    if (listener != null) listener.onNoteChanged(highlight, value);
                    tvSaveState.setText(R.string.saved_check);
                };
                handler.postDelayed(pendingSave, 500);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void flushPendingSave() {
        if (pendingSave != null) {
            handler.removeCallbacks(pendingSave);
            pendingSave.run();
            pendingSave = null;
        }
    }

    private void refreshFavIcon(ImageView icon) {
        boolean saved = listener != null && listener.isQuoteSaved(highlight);
        icon.setImageResource(saved ? R.drawable.ic_heart : R.drawable.ic_heart_outline);
    }

    private void refreshToggleButton(TextView btn) {
        String suffix = getString(highlight.active ? R.string.highlight_on : R.string.highlight_off);
        btn.setText(getString(R.string.highlight_prefix, suffix));
        if (highlight.active) {
            btn.setBackgroundResource(R.drawable.bg_chip_selected);
            btn.setTextColor(getResources().getColor(R.color.white));
        } else {
            btn.setBackgroundResource(R.drawable.bg_note_box);
            btn.setTextColor(getResources().getColor(R.color.textSoft));
        }
    }

    private void applyQuoteBoxTint(View quoteBox) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(14));
        bg.setColor(withAlpha(highlight.color, 0x3a));
        quoteBox.setBackground(bg);
    }

    private void buildColorRow(LinearLayout row, View quoteBox) {
        row.removeAllViews();
        for (int color : COLORS) {
            View swatch = LayoutInflater.from(getContext()).inflate(R.layout.articles_item_color_swatch, row, false);
            View circle = swatch.findViewById(R.id.swatchCircle);
            ImageView check = swatch.findViewById(R.id.swatchCheck);

            GradientDrawable circleBg = new GradientDrawable();
            circleBg.setShape(GradientDrawable.OVAL);
            circleBg.setColor(color);
            circle.setBackground(circleBg);

            boolean selected = color == highlight.color;
            check.setVisibility(selected ? View.VISIBLE : View.GONE);

            swatch.setOnClickListener(v -> {
                highlight.color = color;
                if (listener != null) listener.onColorChanged(highlight, color);
                applyQuoteBoxTint(quoteBox);
                buildColorRow(row, quoteBox);
            });
            row.addView(swatch);
        }
    }

    private int withAlpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private float dp(int value) {
        return value * getResources().getDisplayMetrics().density;
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        flushPendingSave();
    }
}
