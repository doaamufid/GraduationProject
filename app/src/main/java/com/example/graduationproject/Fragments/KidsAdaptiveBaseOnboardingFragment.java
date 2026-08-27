package com.example.graduationproject.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

/**
 * Mirrors <ScreenShell>: shared chrome for every onboarding screen (sky background,
 * back button, progress dots, skip link, teddy companion, scrollable content area,
 * primary footer button). Subclasses only populate the content container and
 * configure the footer/skip/teddy-mood behaviour for their specific screen.
 */
public abstract class KidsAdaptiveBaseOnboardingFragment extends Fragment {

    protected KidsAdaptiveOnboardingHost host;
    protected LinearLayout contentContainer;
    /** Public so KidsAdaptiveMainActivity (a different package) can trigger the selection "pulse". */
    public KidsAdaptiveTeddyBuddyView teddyHeader;
    protected Button btnPrimary;

    public abstract int getScreenIndex();
    /** Populate contentContainer with this screen's views. */
    protected abstract void buildContent(LinearLayout container, LayoutInflater inflater);
    /** Called when the primary footer button is tapped (default: goNext). */
    protected void onPrimaryClick() { host.goNext(); }
    /** Text shown on the footer button. */
    protected String getPrimaryButtonText() { return getString(R.string.kids_adaptive_btn_continue); }
    /** Whether the skip link is shown at all. */
    protected boolean showSkip() { return true; }
    /** Called when skip is tapped (default: goNext, matching most screens' `onSkip={nav.next}`). */
    protected void onSkipClick() { host.goNext(); }
    protected String getSkipLabel() { return getString(R.string.kids_adaptive_skip); }
    /** Teddy's facial expression for this screen ("neutral" | "warm" | "calm"). */
    protected String getCompanionMood() { return KidsAdaptiveTeddyBuddyView.MOOD_NEUTRAL; }

    @Override
    public void onAttach(@NonNull android.content.Context context) {
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

        ImageButton back = root.findViewById(R.id.btn_back);
        if (getScreenIndex() > 0) {
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(v -> host.goBack());
        } else {
            back.setVisibility(View.INVISIBLE);
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
        teddyHeader.setReducedMotion(host != null && host.isReducedMotion());
        teddyHeader.setMood(getCompanionMood());

        btnPrimary = root.findViewById(R.id.btn_primary);
        btnPrimary.setText(getPrimaryButtonText());
        btnPrimary.setOnClickListener(v -> onPrimaryClick());

        contentContainer = root.findViewById(R.id.content_container);
        buildContent(contentContainer, inflater);

        return root;
    }

    protected KidsAdaptiveOnboardingData data() {
        return host.getData();
    }

    protected int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    protected float sp(float v) { return v; }
}
