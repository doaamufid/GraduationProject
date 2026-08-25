package com.example.graduationproject;

import com.example.graduationproject.models.AdultOnboarding.OnboardingData;

/** Implemented by MainActivity; lets fragments read/mutate shared state and navigate. */
public interface AdultOnboardingHost {
    OnboardingData getData();
    void goNext();
    void goBack();
    void goTo(int index);
    void triggerCompanionPulse();
    boolean isReducedMotion();
    void completeOnboarding();
    void restartOnboarding();
    void simulateReopen();
    void continueSavedSession();
}
