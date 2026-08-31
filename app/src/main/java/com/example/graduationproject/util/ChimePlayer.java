package com.example.graduationproject.util;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;

/**
 * Generates the same kind of soft generated sine-wave "chimes" the web version
 * built with the WebAudio API (oscillator + exponential gain envelope).
 * Port of useDpcChime / useApcBell / useAudio's playTone.
 */
public class ChimePlayer {

    private static final int SAMPLE_RATE = 44100;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean soundEnabled = true;

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    /** Single soft note with attack/decay envelope, in its own thread. */
    public void playNote(final float freqHz, final int durationMs, final float peakVolume) {
        if (!soundEnabled) return;
        Executors.newSingleThreadExecutor().execute(() -> {
            int numSamples = (SAMPLE_RATE * durationMs) / 1000;
            short[] buffer = new short[numSamples];
            for (int i = 0; i < numSamples; i++) {
                double t = i / (double) SAMPLE_RATE;
                double envelope;
                double attack = 0.02, total = durationMs / 1000.0;
                if (t < attack) {
                    envelope = t / attack;
                } else {
                    envelope = Math.exp(-4.0 * (t - attack) / Math.max(0.001, total - attack));
                }
                double sample = Math.sin(2 * Math.PI * freqHz * t) * envelope * peakVolume;
                buffer[i] = (short) (sample * Short.MAX_VALUE);
            }
            playBuffer(buffer);
        });
    }

    private void playBuffer(short[] buffer) {
        int minBuf = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack track = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setSampleRate(SAMPLE_RATE)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .setBufferSizeInBytes(Math.max(minBuf, buffer.length * 2))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build();
        try {
            track.write(buffer, 0, buffer.length);
            track.play();
            mainHandler.postDelayed(() -> {
                try {
                    track.stop();
                    track.release();
                } catch (Exception ignored) {}
            }, (long) (buffer.length / (double) SAMPLE_RATE * 1000) + 80);
        } catch (Exception ignored) {}
    }

    /** Kids "tick": two quick ascending notes. Port of playTick(). */
    public void playTick() {
        playNote(660, 100, 0.35f);
        mainHandler.postDelayed(() -> playNote(880, 130, 0.35f), 80);
    }

    /** Kids "fanfare" on full completion. Port of playFanfare(). */
    public void playFanfare() {
        float[] freqs = {523.25f, 659.25f, 784.0f, 1046.5f};
        for (int i = 0; i < freqs.length; i++) {
            final float f = freqs[i];
            mainHandler.postDelayed(() -> playNote(f, 240, 0.4f), i * 120L);
        }
    }

    /** Adult single-step bell. Port of playStep(). */
    public void playStep() {
        playNote(440, 500, 0.25f);
        mainHandler.postDelayed(() -> playNote(659.25f, 550, 0.2f), 30);
    }

    /** Adult "summit reached" chord. Port of playSummit(). */
    public void playSummit() {
        float[] freqs = {392f, 523.25f, 659.25f};
        for (int i = 0; i < freqs.length; i++) {
            final float f = freqs[i];
            mainHandler.postDelayed(() -> playNote(f, 1100, 0.28f), i * 180L);
        }
    }

    /** Breathing-phase cue tone (in/hold/out). */
    public void playPhaseTone(float freqHz) {
        playNote(freqHz, 500, 0.3f);
    }
}
