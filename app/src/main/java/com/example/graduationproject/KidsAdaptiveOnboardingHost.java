package com.example.graduationproject;

import com.example.graduationproject.models.KidsAdaptiveOnboardingData;

/**
 * Contract KidsAdaptiveMainActivity fulfills for every onboarding fragment. Mirrors the `nav` object
 * and shared `data`/`dispatch` passed as props to every React screen component.
 */
public interface KidsAdaptiveOnboardingHost {
    KidsAdaptiveOnboardingData getData();
    void goNext();
    void goBack();
    void goTo(int index);
    void finishOnboarding();
    /** Triggers the teddy's quick selection "pop" (mirrors onPulse()). */
    void pulseTeddy();
    boolean isReducedMotion();
}
