package com.example.graduationproject.Fragments.AdultOnboarding;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.AdultOnboardingHost;
import com.example.graduationproject.R;
import com.example.graduationproject.AdultOnboardingUiUtils;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

/** Mirrors <ResumePrompt/>: "Continue where you left off?" overlay. */
public class ResumeDialogFragment extends DialogFragment {

    private AdultOnboardingHost host;

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        host = (AdultOnboardingHost) requireActivity();
    }

    private int dp(int v) { return AdultOnboardingUiUtils.dp(requireContext(), v); }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window w = dialog.getWindow();
        if (w != null) {
            w.requestFeature(Window.FEATURE_NO_TITLE);
            w.setBackgroundDrawable(new ColorDrawable(Color.argb(225, 11, 13, 31)));
            w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        setCancelable(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout root = new LinearLayout(requireContext());
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(24), dp(24), dp(24), dp(24));

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setBackgroundResource(R.drawable.bg_resume_card);
        card.setPadding(dp(26), dp(26), dp(26), dp(26));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(dp(320), LinearLayout.LayoutParams.WRAP_CONTENT);
        root.addView(card, cardLp);

        TextView emoji = new TextView(requireContext());
        emoji.setText("Ã°Å¸Â¤Â");
        emoji.setTextSize(30);
        card.addView(emoji);

        TextView title = new TextView(requireContext());
        title.setText(R.string.adaptive_adult_onboarding_resume_title);
        title.setTextColor(AdultOnboardingAppData.CREAM);
        title.setTextSize(17);
        title.setTypeface(AdultOnboardingUiUtils.cairo(true));
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleLp.topMargin = dp(8);
        card.addView(title, titleLp);

        TextView body = new TextView(requireContext());
        body.setText(R.string.adaptive_adult_onboarding_resume_body);
        body.setTextColor(AdultOnboardingAppData.CREAM);
        body.setAlpha(0.75f);
        body.setTextSize(13.5f);
        body.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams bodyLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bodyLp.topMargin = dp(6);
        bodyLp.bottomMargin = dp(18);
        card.addView(body, bodyLp);

        card.addView(Widgets.primaryButton(requireContext(), getString(R.string.adaptive_adult_onboarding_continue), false, () -> {
            host.continueSavedSession();
            dismissAllowingStateLoss();
        }));

        LinearLayout restartWrap = new LinearLayout(requireContext());
        restartWrap.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams rLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rLp.topMargin = dp(10);
        restartWrap.addView(Widgets.textLink(requireContext(), getString(R.string.adaptive_adult_onboarding_restart), AdultOnboardingAppData.CREAM, () -> {
            host.restartOnboarding();
            dismissAllowingStateLoss();
        }));
        card.addView(restartWrap, rLp);

        return root;
    }
}
