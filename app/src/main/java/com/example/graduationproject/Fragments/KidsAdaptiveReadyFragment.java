package com.example.graduationproject.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;

public class KidsAdaptiveReadyFragment extends KidsAdaptiveBaseOnboardingFragment {

    @Override public int getScreenIndex() { return 12; }
    @Override protected boolean showSkip() { return false; }
    @Override protected String getPrimaryButtonText() { return getString(R.string.kids_adaptive_ready_cta); }

    @Override
    protected void onPrimaryClick() {
        saveFinalAvatar();
        host.finishOnboarding();
    }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        container.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dp(400));
        container.setLayoutParams(lp);

        // 🌟 استبدال عرض الدب الرسومي بـ TextView لعرض الأفاتار/الإيموجي المختار بحجم كبير
        TextView heroAvatar = new TextView(requireContext());
        heroAvatar.setText(getSavedOrDataAvatar());
        heroAvatar.setTextSize(70); // حجم الأفاتار المركزي
        heroAvatar.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams heroLp = new LinearLayout.LayoutParams(dp(110), dp(110));
        heroLp.gravity = Gravity.CENTER_HORIZONTAL;
        heroLp.bottomMargin = dp(32);
        container.addView(heroAvatar, heroLp);

        // 🌟 تفاعل النقر على الأفاتار
        heroAvatar.setOnClickListener(v -> {
            if (host != null) {
                host.pulseTeddy();
            }
        });

        String nickname = data().nickname;
        String thanks = getString(R.string.kids_adaptive_ready_thanks) + (nickname != null && !nickname.trim().isEmpty() ? "، " + nickname.trim() : "") + " 🧸";
        LinearLayout.LayoutParams tlp = wrap();
        tlp.gravity = Gravity.CENTER_HORIZONTAL;
        tlp.bottomMargin = dp(24);
        container.addView(KidsAdaptiveUiHelpers.title(requireContext(), thanks, 22), tlp);

        container.addView(KidsAdaptiveUiHelpers.body(requireContext(), getString(R.string.kids_adaptive_ready_body)), wrap());
    }

    private String getSavedOrDataAvatar() {
        if (data().demoMoodSelected != null && !data().demoMoodSelected.trim().isEmpty()) {
            return data().demoMoodSelected;
        }
        SharedPreferences prefs = requireContext().getSharedPreferences("KidsApp", Context.MODE_PRIVATE);
        return prefs.getString("current_child_avatar", "🦁");
    }

    private void saveFinalAvatar() {
        String chosenAvatar = data().demoMoodSelected;
        if (chosenAvatar != null && !chosenAvatar.trim().isEmpty()) {
            SharedPreferences prefs = requireContext().getSharedPreferences("KidsApp", Context.MODE_PRIVATE);
            prefs.edit().putString("current_child_avatar", chosenAvatar).apply();
        }
    }

    private LinearLayout.LayoutParams wrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}