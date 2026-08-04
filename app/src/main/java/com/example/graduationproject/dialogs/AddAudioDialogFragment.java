package com.example.graduationproject.dialogs;

import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.R;
import com.example.graduationproject.widget.PulseAnimator;
import com.example.graduationproject.widget.WaveBarAnimator;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Locale;

/**
 * Equivalent of <AddAudioDialog/>. State machine: idle -> recording -> preview,
 * exactly like the original `useState("idle")` + `setInterval` seconds counter.
 */
public class AddAudioDialogFragment extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "add_audio_result";
    public static final String KEY_LABEL = "label";
    public static final String KEY_DURATION = "duration";

    private static final String STATE_IDLE = "idle";
    private static final String STATE_RECORDING = "recording";
    private static final String STATE_PREVIEW = "preview";

    public static AddAudioDialogFragment newInstance() {
        return new AddAudioDialogFragment();
    }

    private String state = STATE_IDLE;
    private int seconds = 0;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable secondsTicker;
    private ValueAnimator pulseAnimator;
    private List<ValueAnimator> waveAnimators;

    private View pulseRing;
    private ImageButton btnRecordCircle;
    private LinearLayout llWaveBars;
    private TextView tvStatus, tvReRecord, btnSave;
    private EditText edtLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_add_audio, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        pulseRing = root.findViewById(R.id.pulseRing);
        btnRecordCircle = root.findViewById(R.id.btnRecordCircle);
        llWaveBars = root.findViewById(R.id.llWaveBars);
        tvStatus = root.findViewById(R.id.tvStatus);
        tvReRecord = root.findViewById(R.id.tvReRecord);
        edtLabel = root.findViewById(R.id.edtLabel);
        btnSave = root.findViewById(R.id.btnSave);

        btnRecordCircle.setOnClickListener(v -> {
            if (state.equals(STATE_IDLE)) {
                setState(STATE_RECORDING);
            } else if (state.equals(STATE_RECORDING)) {
                setState(STATE_PREVIEW);
            }
        });

        tvReRecord.setOnClickListener(v -> {
            seconds = 0;
            setState(STATE_IDLE);
        });

        btnSave.setOnClickListener(v -> {
            String label = edtLabel.getText().toString().trim();
            if (label.isEmpty()) label = getString(R.string.default_audio_label);

            Bundle result = new Bundle();
            result.putString(KEY_LABEL, label);
            result.putInt(KEY_DURATION, seconds);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });

        render();
        return root;
    }

    private void setState(String newState) {
        state = newState;
        render();

        if (state.equals(STATE_RECORDING)) {
            startTicking();
        } else {
            stopTicking();
        }
    }

    private void render() {
        boolean idle = state.equals(STATE_IDLE);
        boolean recording = state.equals(STATE_RECORDING);
        boolean preview = state.equals(STATE_PREVIEW);

        btnRecordCircle.setImageResource(recording ? R.drawable.ic_square : R.drawable.ic_mic);
        btnRecordCircle.setBackgroundResource(recording
                ? R.drawable.bg_icon_circle_record : R.drawable.bg_icon_circle_primary);

        if (recording) {
            pulseAnimator = PulseAnimator.start(pulseRing);
            waveAnimators = WaveBarAnimator.start(requireContext(), llWaveBars);
            llWaveBars.setVisibility(View.VISIBLE);
        } else {
            PulseAnimator.stop(pulseAnimator, pulseRing);
            WaveBarAnimator.stop(waveAnimators);
            llWaveBars.setVisibility(View.GONE);
        }

        // "Preview" swaps the record circle for a static play icon.
        if (preview) {
            btnRecordCircle.setVisibility(View.VISIBLE);
            btnRecordCircle.setImageResource(R.drawable.ic_play_light);
            btnRecordCircle.setBackgroundResource(R.drawable.bg_icon_circle_primary_soft);
            btnRecordCircle.setClickable(false);
        } else {
            btnRecordCircle.setVisibility(View.VISIBLE);
            btnRecordCircle.setClickable(true);
        }

        tvStatus.setVisibility(preview ? View.GONE : View.VISIBLE);
        if (idle) {
            tvStatus.setText(R.string.tap_to_record);
        } else if (recording) {
            tvStatus.setText(getString(R.string.recording_now_format, formatTime(seconds)));
        }

        tvReRecord.setVisibility(preview ? View.VISIBLE : View.GONE);
        edtLabel.setVisibility(preview ? View.VISIBLE : View.GONE);
        btnSave.setVisibility(preview ? View.VISIBLE : View.GONE);

        if (preview) {
            tvReRecord.setText(getString(R.string.preview_recording_format, formatTime(seconds))
                    + "   \u00B7   " + getString(R.string.re_record));
        }
    }

    private String formatTime(int totalSeconds) {
        return String.format(Locale.US, "%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    private void startTicking() {
        stopTicking();
        secondsTicker = new Runnable() {
            @Override
            public void run() {
                seconds++;
                if (state.equals(STATE_RECORDING)) {
                    tvStatus.setText(getString(R.string.recording_now_format, formatTime(seconds)));
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(secondsTicker, 1000);
    }

    private void stopTicking() {
        if (secondsTicker != null) {
            handler.removeCallbacks(secondsTicker);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTicking();
        PulseAnimator.stop(pulseAnimator, pulseRing);
        WaveBarAnimator.stop(waveAnimators);
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_SurvivalBox_BottomSheet;
    }
}
