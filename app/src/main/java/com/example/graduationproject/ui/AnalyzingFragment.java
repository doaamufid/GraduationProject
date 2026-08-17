package com.example.graduationproject.ui;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.MainActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.data.Moderation;
import com.example.graduationproject.data.Repository;
import com.example.graduationproject.models.Message;

/**
 * Mirrors AnalyzingScreen: shows a pulsing sparkles icon for ~1600ms
 * (matching the JS setTimeout in submitDraft) then routes to
 * approved or rejected based on Moderation.moderate(...).
 */
public class AnalyzingFragment extends Fragment {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ValueAnimator pulseAnim;

    public static AnalyzingFragment newInstance() {
        return new AnalyzingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analyzing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View pulseCircle = view.findViewById(R.id.pulseCircle);
        pulseAnim = ValueAnimator.ofFloat(1f, 1.1f, 1f);
        pulseAnim.setDuration(1400);
        pulseAnim.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnim.setInterpolator(new LinearInterpolator());
        pulseAnim.addUpdateListener(a -> {
            float v = (float) a.getAnimatedValue();
            pulseCircle.setScaleX(v);
            pulseCircle.setScaleY(v);
        });
        pulseAnim.start();

        handler.postDelayed(() -> {
            if (!isAdded()) return;
            MainActivity activity = (MainActivity) requireActivity();
            String text = ComposeFragment.DraftHolder.text;
            String cat = ComposeFragment.DraftHolder.cat;
            String img = ComposeFragment.DraftHolder.img;
            String emoji = ComposeFragment.DraftHolder.emoji;
            int colorIndex = ComposeFragment.DraftHolder.colorIndex;

            Moderation.Result result = Moderation.moderate(text == null ? "" : text);
            if (result.ok) {
                Message newMsg = Repository.get().addNewMessage(text, cat, img, emoji, colorIndex);
                activity.showApproved(newMsg);
            } else {
                activity.showRejected(result.reasonResKey, result.crisis);
            }
        }, 1600);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        if (pulseAnim != null) pulseAnim.cancel();
    }
}
