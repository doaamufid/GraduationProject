package com.example.graduationproject.Fragments;

import android.animation.ValueAnimator;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.R;
import com.example.graduationproject.models.AudioItem;
import com.example.graduationproject.models.DhikrItem;
import com.example.graduationproject.models.LoveItem;
import com.example.graduationproject.models.PhotoItem;
import com.example.graduationproject.models.SurvivalBoxRepository;
import com.example.graduationproject.widget.FadeUtils;
import com.example.graduationproject.widget.PulseAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Equivalent of <CrisisMode/>: builds a sequence from the FIRST item of
 * each non-empty category (photo, audio, love, dhikr - in that order,
 * exactly like the original `sequence.push(...)` calls), then walks
 * through it one step at a time with a cross-fade transition, ending on
 * a "you're okay now" screen with replay/close actions.
 */
public class CrisisModeFragment extends DialogFragment {

    private static final String TYPE_PHOTO = "photo";
    private static final String TYPE_AUDIO = "audio";
    private static final String TYPE_LOVE = "love";
    private static final String TYPE_DHIKR = "dhikr";

    private static class Step {
        final String type;
        final Object item;

        Step(String type, Object item) {
            this.type = type;
            this.item = item;
        }
    }

    private final List<Step> sequence = new ArrayList<>();
    private int idx = 0;

    private View groupStep, groupDone;
    private ImageView ivStepPhoto;
    private FrameLayout groupStepAudio;
    private View pulseRingCrisis;
    private TextView tvStepText, tvStepCaption, btnNext;
    private ValueAnimator pulseAnimator;

    @Override
    public void onStart() {
        super.onStart();
        // Full-screen, no dim insets - matches the original absolute-positioned overlay.
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public int getTheme() {
        return R.style.Theme_SurvivalBox_FullScreenDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_crisis_mode, container, false);

        buildSequence();

        groupStep = root.findViewById(R.id.groupStep);
        groupDone = root.findViewById(R.id.groupDone);
        ivStepPhoto = root.findViewById(R.id.ivStepPhoto);
        groupStepAudio = root.findViewById(R.id.groupStepAudio);
        pulseRingCrisis = root.findViewById(R.id.pulseRingCrisis);
        tvStepText = root.findViewById(R.id.tvStepText);
        tvStepCaption = root.findViewById(R.id.tvStepCaption);
        btnNext = root.findViewById(R.id.btnNext);

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        root.findViewById(R.id.btnCloseDone).setOnClickListener(v -> dismiss());
        root.findViewById(R.id.btnReplay).setOnClickListener(v -> {
            idx = 0;
            showDoneOrStep(true);
        });

        btnNext.setOnClickListener(v -> {
            idx++;
            showDoneOrStep(true);
        });

        showDoneOrStep(false);
        return root;
    }

    private void buildSequence() {
        SurvivalBoxRepository repo = SurvivalBoxRepository.getInstance();
        sequence.clear();
        List<PhotoItem> photos = repo.getPhotos();
        List<AudioItem> audio = repo.getAudio();
        List<LoveItem> love = repo.getLove();
        List<DhikrItem> dhikr = repo.getDhikr();

        if (!photos.isEmpty()) sequence.add(new Step(TYPE_PHOTO, photos.get(0)));
        if (!audio.isEmpty()) sequence.add(new Step(TYPE_AUDIO, audio.get(0)));
        if (!love.isEmpty()) sequence.add(new Step(TYPE_LOVE, love.get(0)));
        if (!dhikr.isEmpty()) sequence.add(new Step(TYPE_DHIKR, dhikr.get(0)));
    }

    /** @param animate whether to cross-fade into the new state (skip on first render) */
    private void showDoneOrStep(boolean animate) {
        boolean done = idx >= sequence.size();

        PulseAnimator.stop(pulseAnimator, pulseRingCrisis);

        if (done) {
            groupStep.setVisibility(View.GONE);
            groupDone.setVisibility(View.VISIBLE);
            if (animate) FadeUtils.fadeInUp(groupDone);
        } else {
            groupDone.setVisibility(View.GONE);
            groupStep.setVisibility(View.VISIBLE);
            bindStep(sequence.get(idx));
            if (animate) FadeUtils.fadeInUp(groupStep);
        }
    }

    private void bindStep(Step step) {
        ivStepPhoto.setVisibility(View.GONE);
        groupStepAudio.setVisibility(View.GONE);
        tvStepCaption.setVisibility(View.GONE);
        tvStepText.setTypeface(Typeface.DEFAULT);
        tvStepText.setTextSize(15);

        switch (step.type) {
            case TYPE_PHOTO: {
                PhotoItem photo = (PhotoItem) step.item;
                ivStepPhoto.setVisibility(View.VISIBLE);
                ivStepPhoto.setImageURI(Uri.parse(photo.uri));
                boolean hasCaption = photo.caption != null && !photo.caption.trim().isEmpty();
                tvStepText.setText(hasCaption ? photo.caption : getString(R.string.crisis_calm_photo_fallback));
                tvStepText.setTextSize(13);
                break;
            }
            case TYPE_AUDIO: {
                AudioItem audioItem = (AudioItem) step.item;
                groupStepAudio.setVisibility(View.VISIBLE);
                pulseAnimator = PulseAnimator.start(pulseRingCrisis);
                tvStepText.setText(audioItem.label);
                tvStepText.setTypeface(Typeface.DEFAULT_BOLD);
                tvStepText.setTextSize(15);
                tvStepCaption.setVisibility(View.VISIBLE);
                tvStepCaption.setText(R.string.crisis_audio_playing);
                break;
            }
            case TYPE_LOVE: {
                LoveItem love = (LoveItem) step.item;
                tvStepText.setText(love.text);
                tvStepText.setTypeface(Typeface.DEFAULT_BOLD);
                tvStepText.setTextSize(17);
                break;
            }
            case TYPE_DHIKR: {
                DhikrItem dhikr = (DhikrItem) step.item;
                tvStepText.setText(getString(R.string.dhikr_emoji) + "\n\n" + dhikr.text);
                tvStepText.setTypeface(Typeface.DEFAULT_BOLD);
                tvStepText.setTextSize(19);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PulseAnimator.stop(pulseAnimator, pulseRingCrisis);
    }
}
