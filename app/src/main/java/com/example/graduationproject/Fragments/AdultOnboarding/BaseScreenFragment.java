package com.example.graduationproject.Fragments.AdultOnboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.AdultOnboardingHost;
import com.example.graduationproject.R;
import com.example.graduationproject.models.AdultOnboarding.OnboardingData;
import com.example.graduationproject.models.AdultOnboarding.Stage;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.ProgressPathView;
import com.example.graduationproject.view.AdultOnboarding.SkyView;

/**
 * Common "ScreenShell" behaviour shared by every onboarding screen: sky
 * background per stage, progress dots, back/skip controls, the companion
 * light, a scrollable content area and a footer button â€” mirrors the React
 * <ScreenShell/> wrapper so each concrete screen only supplies its content.
 */
public abstract class BaseScreenFragment extends Fragment {

    protected AdultOnboardingHost host;
    protected OnboardingData data;

    protected SkyView skyView;
    protected ProgressPathView progressPath;
    protected CompanionView companionView;
    protected LinearLayout contentContainer;
    protected View btnBack;
    protected TextView btnSkip;

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        host = (AdultOnboardingHost) requireActivity();
        data = host.getData();
    }

    protected abstract int getScreenIndex();

    protected String getCompanionMood() { return CompanionView.MOOD_NEUTRAL; }

    protected boolean showShellCompanion() { return true; }

    protected boolean showSkip() { return true; }

    protected String getSkipLabel() { return getString(R.string.adaptive_adult_onboarding_skip); }

    /** Called when the skip link is tapped; default behaviour just advances. */
    protected void onSkip() { host.goNext(); }

    protected void onBack() { host.goBack(); }

    protected abstract void populateContent(LayoutInflater inflater, LinearLayout content);

    /** Subclasses attach a configured button (usually com.salam view PrimaryButton) into footer. */
    protected abstract void populateFooter(LayoutInflater inflater, ViewGroup footer);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_screen_shell, container, false);

        skyView = root.findViewById(R.id.skyView);
        progressPath = root.findViewById(R.id.progressPath);
        companionView = root.findViewById(R.id.companionView);
        contentContainer = root.findViewById(R.id.contentContainer);
        ViewGroup footer = root.findViewById(R.id.footerContainer);
        btnBack = root.findViewById(R.id.btnBack);
        btnSkip = root.findViewById(R.id.btnSkip);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Pushing buttons slightly higher above the task bar
            footer.setPadding(footer.getPaddingLeft(), footer.getPaddingTop(), footer.getPaddingRight(), bars.bottom + dp(20));
            return insets;
        });

        int index = getScreenIndex();
        Stage stage = AdultOnboardingAppData.STAGES[AdultOnboardingAppData.stageForScreen(index)];
        skyView.setStage(stage, true);
        companionView.setVisibility(showShellCompanion() ? View.VISIBLE : View.GONE);
        companionView.setReducedMotion(host.isReducedMotion());
        companionView.setMood(getCompanionMood());
        progressPath.setProgress(AdultOnboardingAppData.TOTAL_SCREENS, index);

        // Update status and navigation bar colors to match the sky gradient
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(stage.fromColor);
            getActivity().getWindow().setNavigationBarColor(stage.toColor); // Match bottom color

            // If text color is dark (INK), the background is light, so use dark icons.
            boolean isLightBackground = (stage.textColor == AdultOnboardingAppData.INK);
            WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getActivity().getWindow(), getActivity().getWindow().getDecorView());
            if (controller != null) {
                controller.setAppearanceLightStatusBars(isLightBackground);
                controller.setAppearanceLightNavigationBars(isLightBackground);
            }
        }

        btnBack.setVisibility(index > 0 ? View.VISIBLE : View.INVISIBLE);
        btnBack.setOnClickListener(v -> onBack());

        if (showSkip()) {
            btnSkip.setVisibility(View.VISIBLE);
            btnSkip.setText(getSkipLabel());
            btnSkip.setOnClickListener(v -> onSkip());
        } else {
            btnSkip.setVisibility(View.INVISIBLE);
        }

        populateContent(inflater, contentContainer);
        populateFooter(inflater, footer);

        return root;
    }

    protected void addToContent(LinearLayout content, View v, int marginTopDp) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = com.example.graduationproject.AdultOnboardingUiUtils.dp(requireContext(), marginTopDp);
        content.addView(v, lp);
    }

    protected int dp(int v) { return com.example.graduationproject.AdultOnboardingUiUtils.dp(requireContext(), v); }

    protected void pulse() {
        companionView.pulse();
        host.triggerCompanionPulse();
    }
}
