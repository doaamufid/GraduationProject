package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.graduationproject.Fragments.AdultOnboarding.FrequentEmotionsFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.GoalsFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.HelpfulFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.IdentityFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.IntenseFearFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.MoodDemoFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.OverallMoodFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.PreviewFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.PrivacyFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.ReadyFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.ResumeDialogFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.SafetyFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.TimelineFragment;
import com.example.graduationproject.Fragments.AdultOnboarding.WelcomeFragment;
import com.example.graduationproject.models.AdultOnboarding.OnboardingData;

/**
 * Root activity — the Java equivalent of the exported `SalamOnboarding`
 * React component. Owns the shared OnboardingData, the current screen
 * index/direction, and the onboarding-vs-home phase, and drives fragment
 * transitions with slide/fade animations that mirror the JS
 * `enterFwd` / `enterBack` keyframes.
 */
public class AdultOnboardingMainActivity extends AppCompatActivity implements AdultOnboardingHost {

    private OnboardingData data = new OnboardingData();
    private int currentIndex = 0;
    private String phase = "onboarding"; // "onboarding" | "home"
    private boolean everStarted = false;
    private boolean reducedMotion = false;

    private OnboardingData savedSnapshot = null;
    private int savedIndex = 0;

    private View simulateReopenButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adult_onboarding_main);

        reducedMotion = false; // Android has no direct "prefers-reduced-motion" signal; default to full motion.

        // Force RTL + Arabic, matching dir="rtl" lang="ar" on the JS root.
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        simulateReopenButton = findViewById(R.id.btnSimulateReopen);
        simulateReopenButton.setOnClickListener(v -> simulateReopen());

        if (savedInstanceState == null) {
            showScreen(0, "fwd", false);
        }
    }

    @Override
    public OnboardingData getData() { return data; }

    @Override
    public boolean isReducedMotion() { return reducedMotion; }

    @Override
    public void goNext() {
        int next = Math.min(currentIndex + 1, AdultOnboardingAppData.TOTAL_SCREENS - 1);
        showScreen(next, "fwd", true);
    }

    @Override
    public void goBack() {
        int prev = Math.max(currentIndex - 1, 0);
        showScreen(prev, "back", true);
    }

    @Override
    public void goTo(int index) {
        showScreen(index, "back", true);
    }

    @Override
    public void triggerCompanionPulse() {
        // Companion pulse itself is triggered on the view directly by fragments;
        // this hook exists for any activity-level reactions in the future.
    }

    @Override
    public void completeOnboarding() {
        data.onboardingCompleted = true;
        // The final "Ready" screen button routes the user to the Reflection screen.
        Intent intent = new Intent(AdultOnboardingMainActivity.this, ReflectionActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void restartOnboarding() {
        data.reset();
        currentIndex = 0;
        phase = "onboarding";
        savedSnapshot = null;
        everStarted = false;
        updateSimulateButtonVisibility();
        showScreen(0, "fwd", false);
    }

    @Override
    public void simulateReopen() {
        savedSnapshot = data.copy();
        savedIndex = currentIndex;
        ResumeDialogFragment dialog = new ResumeDialogFragment();
        dialog.show(getSupportFragmentManager(), "resume");
    }

    @Override
    public void continueSavedSession() {
        if (savedSnapshot != null) {
            data.restoreFrom(savedSnapshot);
            currentIndex = savedIndex;
            showScreen(currentIndex, "back", false);
        }
    }

    private void showScreen(int index, String direction, boolean animate) {
        currentIndex = index;
        if (index > 0) everStarted = true;
        updateSimulateButtonVisibility();

        Fragment fragment = createFragmentFor(index);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        if (animate) {
            if ("fwd".equals(direction)) {
                tx.setCustomAnimations(R.anim.enter_fwd, R.anim.exit_fade);
            } else {
                tx.setCustomAnimations(R.anim.enter_back, R.anim.exit_fade);
            }
        }
        tx.replace(R.id.screenContainer, fragment);
        tx.commit();
    }

    private void updateSimulateButtonVisibility() {
        boolean visible = "onboarding".equals(phase) && everStarted && currentIndex > 0 && currentIndex < AdultOnboardingAppData.TOTAL_SCREENS - 1;
        simulateReopenButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private Fragment createFragmentFor(int index) {
        switch (index) {
            case 0: return new WelcomeFragment();
            case 1: return new PrivacyFragment();
            case 2: return new IdentityFragment();
            case 3: return new OverallMoodFragment();
            case 4: return new FrequentEmotionsFragment();
            case 5: return new SafetyFragment();
            case 6: return new IntenseFearFragment();
            case 7: return new TimelineFragment();
            case 8: return new HelpfulFragment();
            case 9: return new GoalsFragment();
            case 10: return new MoodDemoFragment();
            case 11: return new PreviewFragment();
            case 12: return new ReadyFragment();
            default: return new WelcomeFragment();
        }
    }
}
