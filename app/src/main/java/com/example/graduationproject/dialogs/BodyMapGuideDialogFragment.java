package com.example.graduationproject.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.R;
import com.example.graduationproject.databinding.DialogBodyMapGuideBinding;

public class BodyMapGuideDialogFragment extends DialogFragment {

    public interface OnAreaSelectedListener {
        void onAreaSelected(String key);
    }

    private DialogBodyMapGuideBinding binding;
    private OnAreaSelectedListener listener;
    private int currentStep = 0;

    private final String[] texts = {
            "خُذ نفساً عميقاً وأغلق عينيك لو تقدر.",
            "امسح جسدك بذهنك من رأسك حق قدميك، ببطء.",
            "لاحظ أي منطقة تحس فيها أكثر شد أو ثقل الآن.",
            "وين حسيت أكثر شد؟"
    };

    public static BodyMapGuideDialogFragment newInstance(OnAreaSelectedListener listener) {
        BodyMapGuideDialogFragment fragment = new BodyMapGuideDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogBodyMapGuideBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnClose.setOnClickListener(v -> dismiss());
        binding.btnNext.setOnClickListener(v -> nextStep());

        setupAreaButtons();
        updateUI();
    }

    private void nextStep() {
        if (currentStep < 3) {
            currentStep++;
            animateTransition();
        }
    }

    private void animateTransition() {
        binding.contentLayout.animate()
                .alpha(0f)
                .translationY(30f)
                .setDuration(300)
                .withEndAction(() -> {
                    updateUI();
                    binding.contentLayout.setTranslationY(-30f);
                    binding.contentLayout.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(400)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                }).start();
    }

    private void updateUI() {
        binding.tvGuideText.setText(texts[currentStep]);
        
        if (currentStep == 3) {
            binding.btnNext.setVisibility(View.GONE);
            binding.gridOptions.setVisibility(View.VISIBLE);
            binding.ivGuideIcon.setImageResource(R.drawable.body_map);
            binding.ivGuideIcon.setImageTintList(null);
        } else {
            binding.btnNext.setVisibility(View.VISIBLE);
            binding.gridOptions.setVisibility(View.GONE);
            binding.ivGuideIcon.setImageResource(R.drawable.air);
            binding.ivGuideIcon.setImageTintList(android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary)));
        }
    }

    private void setupAreaButtons() {
        binding.btnHead.setOnClickListener(v -> selectArea("head"));
        binding.btnChest.setOnClickListener(v -> selectArea("chest"));
        binding.btnShoulders.setOnClickListener(v -> selectArea("shoulders"));
        binding.btnStomach.setOnClickListener(v -> selectArea("stomach"));
    }

    private void selectArea(String key) {
        if (listener != null) {
            listener.onAreaSelected(key);
        }
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
