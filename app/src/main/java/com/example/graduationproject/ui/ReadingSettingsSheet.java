package com.example.graduationproject.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.graduationproject.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Java port of <ReadingSettingsPanel/>. Presents theme swatches, font size, weight and a
 * brightness slider; every change is pushed back to the host immediately (same as the
 * onChange({...settings, ...}) pattern in the React source).
 */
public class ReadingSettingsSheet extends BottomSheetDialogFragment {

    public interface Listener {
        void onSettingsChanged(ReaderSettings settings);
    }

    private ReaderSettings settings;
    private Listener listener;

    private final Map<String, Integer> themeBg = new LinkedHashMap<>();
    private final Map<String, Integer> themeText = new LinkedHashMap<>();
    private final Map<String, Integer> themeLabelRes = new LinkedHashMap<>();

    public static ReadingSettingsSheet newInstance(ReaderSettings settings, Listener listener) {
        ReadingSettingsSheet sheet = new ReadingSettingsSheet();
        sheet.settings = settings;
        sheet.listener = listener;
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.articles_dialog_reading_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        themeBg.put(ReaderSettings.THEME_CLASSIC, R.color.theme_classic_bg);
        themeBg.put(ReaderSettings.THEME_TRADITIONAL, R.color.theme_traditional_bg);
        themeBg.put(ReaderSettings.THEME_NIGHT, R.color.theme_night_bg);
        themeBg.put(ReaderSettings.THEME_TYPEWRITER, R.color.theme_typewriter_bg);

        themeText.put(ReaderSettings.THEME_CLASSIC, R.color.theme_classic_text);
        themeText.put(ReaderSettings.THEME_TRADITIONAL, R.color.theme_traditional_text);
        themeText.put(ReaderSettings.THEME_NIGHT, R.color.theme_night_text);
        themeText.put(ReaderSettings.THEME_TYPEWRITER, R.color.theme_typewriter_text);

        themeLabelRes.put(ReaderSettings.THEME_CLASSIC, R.string.theme_classic);
        themeLabelRes.put(ReaderSettings.THEME_TRADITIONAL, R.string.theme_traditional);
        themeLabelRes.put(ReaderSettings.THEME_NIGHT, R.string.theme_night);
        themeLabelRes.put(ReaderSettings.THEME_TYPEWRITER, R.string.theme_typewriter);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        buildThemeRow(view.findViewById(R.id.themeRow));
        buildFontSizeRow(view.findViewById(R.id.fontSizeRow));

        TextView btnNormal = view.findViewById(R.id.btnWeightNormal);
        TextView btnBold = view.findViewById(R.id.btnWeightBold);
        styleWeightButtons(btnNormal, btnBold);
        btnNormal.setOnClickListener(v -> {
            settings.weight = ReaderSettings.WEIGHT_NORMAL;
            styleWeightButtons(btnNormal, btnBold);
            emit();
        });
        btnBold.setOnClickListener(v -> {
            settings.weight = ReaderSettings.WEIGHT_BOLD;
            styleWeightButtons(btnNormal, btnBold);
            emit();
        });

        SeekBar seek = view.findViewById(R.id.seekBrightness);
        seek.setProgress(settings.brightness - 20);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings.brightness = progress + 20;
                if (fromUser) emit();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void buildThemeRow(LinearLayout row) {
        row.removeAllViews();
        for (String key : new String[]{ReaderSettings.THEME_CLASSIC, ReaderSettings.THEME_TRADITIONAL, ReaderSettings.THEME_NIGHT, ReaderSettings.THEME_TYPEWRITER}) {
            View swatch = LayoutInflater.from(getContext()).inflate(R.layout.articles_item_theme_swatch, row, false);
            TextView preview = swatch.findViewById(R.id.swatchPreview);
            TextView label = swatch.findViewById(R.id.swatchLabel);
            preview.setBackgroundColor(getResources().getColor(themeBg.get(key)));
            preview.setTextColor(getResources().getColor(themeText.get(key)));
            label.setText(themeLabelRes.get(key));
            boolean selected = key.equals(settings.theme);
            label.setBackgroundColor(getResources().getColor(selected ? R.color.primary : R.color.bgAlt));
            label.setTextColor(getResources().getColor(selected ? R.color.white : R.color.textSoft));
            swatch.setOnClickListener(v -> {
                settings.theme = key;
                buildThemeRow(row);
                emit();
            });
            row.addView(swatch);
        }
    }

    private void buildFontSizeRow(LinearLayout row) {
        row.removeAllViews();
        int[] sizes = {14, 16, 18, 21};
        for (int size : sizes) {
            TextView btn = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.articles_item_font_size_btn, row, false);
            btn.setTextSize(12 + (size - 14) / 2f);
            boolean selected = size == settings.fontSize;
            btn.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_note_box);
            btn.setTextColor(getResources().getColor(selected ? R.color.white : R.color.text));
            btn.setOnClickListener(v -> {
                settings.fontSize = size;
                buildFontSizeRow(row);
                emit();
            });
            row.addView(btn);
        }
    }

    private void styleWeightButtons(TextView normal, TextView bold) {
        boolean isBold = ReaderSettings.WEIGHT_BOLD.equals(settings.weight);
        normal.setBackgroundResource(!isBold ? R.drawable.bg_chip_selected : R.drawable.bg_note_box);
        normal.setTextColor(getResources().getColor(!isBold ? R.color.white : R.color.text));
        bold.setBackgroundResource(isBold ? R.drawable.bg_chip_selected : R.drawable.bg_note_box);
        bold.setTextColor(getResources().getColor(isBold ? R.color.white : R.color.text));
        bold.setTypeface(null, Typeface.BOLD);
    }

    private void emit() {
        if (listener != null) listener.onSettingsChanged(settings);
    }
}
