package com.example.graduationproject.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.KidsAdaptiveOnboardingHost;
import com.example.graduationproject.R;
import com.example.graduationproject.models.KidsAdaptiveOnboardingData;
import com.example.graduationproject.util.KidsAdaptiveStages;
import com.example.graduationproject.view.KidsAdaptiveProgressPathView;
import com.example.graduationproject.view.KidsAdaptiveSkyBackgroundView;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;

public abstract class KidsAdaptiveBaseOnboardingFragment extends Fragment {

    protected KidsAdaptiveOnboardingHost host;
    protected LinearLayout contentContainer;
    public KidsAdaptiveTeddyBuddyView teddyHeader;
    protected TextView avatarHeaderView; // 🌟 عرض الأفاتار النصي/الإيموجي بدلاً من الدب
    protected Button btnPrimary;

    public abstract int getScreenIndex();
    protected abstract void buildContent(LinearLayout container, LayoutInflater inflater);
    protected void onPrimaryClick() { host.goNext(); }
    protected String getPrimaryButtonText() { return getString(R.string.kids_adaptive_btn_continue); }
    protected boolean showSkip() { return true; }
    protected void onSkipClick() { host.goNext(); }
    protected String getSkipLabel() { return getString(R.string.kids_adaptive_skip); }
    protected String getCompanionMood() { return KidsAdaptiveTeddyBuddyView.MOOD_NEUTRAL; }

    protected boolean showHeaderTeddy() {
        int index = getScreenIndex();
        return index != 0 && index != 12 && index != 4;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof KidsAdaptiveOnboardingHost) {
            host = (KidsAdaptiveOnboardingHost) context;
        } else if (getActivity() instanceof KidsAdaptiveOnboardingHost) {
            host = (KidsAdaptiveOnboardingHost) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.kids_adaptive_fragment_onboarding_base, container, false);

        KidsAdaptiveSkyBackgroundView sky = root.findViewById(R.id.sky_view);
        sky.setStage(KidsAdaptiveStages.stageForScreen(getScreenIndex()));

        KidsAdaptiveProgressPathView progress = root.findViewById(R.id.progress_path);
        progress.setProgress(KidsAdaptiveStages.TOTAL_SCREENS, getScreenIndex());

        Button backFooter = root.findViewById(R.id.btn_back_footer);
        if (getScreenIndex() > 0) {
            backFooter.setVisibility(View.VISIBLE);
            backFooter.setOnClickListener(v -> host.goBack());
        } else {
            backFooter.setVisibility(View.GONE);
        }

        TextView skip = root.findViewById(R.id.btn_skip);
        if (showSkip()) {
            skip.setVisibility(View.VISIBLE);
            skip.setText(getSkipLabel());
            skip.setOnClickListener(v -> onSkipClick());
        } else {
            skip.setVisibility(View.INVISIBLE);
        }

        teddyHeader = root.findViewById(R.id.teddy_header);

        // 🌟 إخفاء الدب الأصلي وتجهيز عرض الأفاتار المختار
        if (teddyHeader != null) {
            teddyHeader.setVisibility(View.GONE);
        }

        // 🌟 استدعاء الدالة بدون تمرير root لإنهاء الخطأ الأحادي
        setupSelectedAvatarHeader();

        btnPrimary = root.findViewById(R.id.btn_primary);
        btnPrimary.setText(getPrimaryButtonText());
        btnPrimary.setOnClickListener(v -> onPrimaryClick());

        contentContainer = root.findViewById(R.id.content_container);
        buildContent(contentContainer, inflater);

        return root;
    }

    public void setupSelectedAvatarHeader() {
        if (teddyHeader == null) return;

        // 1. إخفاء الدب الأصلي بوضوح لمنع الظهور المزدوج
        teddyHeader.setVisibility(View.GONE);

        if (!showHeaderTeddy()) {
            if (avatarHeaderView != null) avatarHeaderView.setVisibility(View.GONE);
            return;
        }

        // 2. جلب الأفاتار المختار من OnboardingData أو SharedPreferences
        String selectedAvatar = data() != null ? data().demoMoodSelected : null;
        if (selectedAvatar == null || selectedAvatar.trim().isEmpty()) {
            SharedPreferences prefs = requireContext().getSharedPreferences("KidsApp", Context.MODE_PRIVATE);
            selectedAvatar = prefs.getString("current_child_avatar", "🦁");
        }

        // 3. إنشاء الـ TextView مرة واحدة فقط وإضافته في مكان الدب
        ViewGroup parent = (ViewGroup) teddyHeader.getParent();
        if (parent != null && avatarHeaderView == null) {
            avatarHeaderView = new TextView(requireContext());
            avatarHeaderView.setTextSize(44);
            avatarHeaderView.setGravity(Gravity.CENTER);

            ViewGroup.LayoutParams lp = teddyHeader.getLayoutParams();
            parent.addView(avatarHeaderView, parent.indexOfChild(teddyHeader), lp);
        }

        // 4. تحديث النص الظاهر للأفاتار
        if (avatarHeaderView != null) {
            avatarHeaderView.setText(selectedAvatar);
            avatarHeaderView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupSelectedAvatarHeader();
    }

    protected KidsAdaptiveOnboardingData data() {
        return host != null ? host.getData() : new KidsAdaptiveOnboardingData();
    }

    protected int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    protected float sp(float v) { return v; }
}