package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.graduationproject.data.ActiveChildManager;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.dialogs.KidsAdaptiveResumeDialogFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveBaseOnboardingFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveFrequentEmotionsFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveGoalsFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveHelpfulFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveHomeFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveIdentityFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveIntenseFearFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveMoodDemoFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveOverallMoodFragment;
import com.example.graduationproject.Fragments.KidsAdaptivePreviewFragment;
import com.example.graduationproject.Fragments.KidsAdaptivePrivacyFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveReadyFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveSafetyFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveTimelineFragment;
import com.example.graduationproject.Fragments.KidsAdaptiveWelcomeFragment;
import com.example.graduationproject.models.KidsAdaptiveOnboardingData;
import com.example.graduationproject.models.KidsAdaptivePrefsManager;
import com.example.graduationproject.util.KidsAdaptiveStages;

/**
 * Root controller. Mirrors the React root component (`SalamKidsOnboarding`): owns the
 * `data` state, the current screen `index`, transition `direction`, the "onboarding" vs
 * "home" `phase`, and the debug "simulate reopen app" affordance that shows the resume
 * prompt (mirrors `savedRef` + `showResume`).
 */
public class KidsAdaptiveMainActivity extends AppCompatActivity implements KidsAdaptiveOnboardingHost, KidsAdaptiveResumeDialogFragment.Listener {

    private KidsAdaptiveOnboardingData data = new KidsAdaptiveOnboardingData();
    private int index = 0;
    private long childId = -1;
    private String direction = "fwd";
    private String phase = "onboarding"; // "onboarding" | "home"
    private boolean everStarted = false;

    // In-memory "saved" snapshot used only by the simulate-reopen debug affordance,
    // mirroring `savedRef` in the React source (kept separate from persistent storage).
    private KidsAdaptiveOnboardingData savedSnapshotData = null;
    private int savedSnapshotIndex = 0;

    private KidsAdaptivePrefsManager prefs;
    private ChildProfileStore profileStore;
    private TextView simulateReopenButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kids_adaptive_activity_main);

        prefs = new KidsAdaptivePrefsManager(this);
        profileStore = ChildProfileStore.getInstance(this);
        simulateReopenButton = findViewById(R.id.btn_simulate_reopen);
        simulateReopenButton.setOnClickListener(v -> simulateReopen());

        View navBlur = findViewById(R.id.system_nav_blur);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);

            if (navBlur != null) {
                navBlur.getLayoutParams().height = systemBars.bottom;
                navBlur.requestLayout();
            }
            return insets;
        });

        if (getIntent() != null) {
            childId = getIntent().getLongExtra("CHILD_ID", -1);
            String name = getIntent().getStringExtra("CHILD_NAME");
            if (name != null) {
                data.nickname = name;
                data.nicknameProvided = true;
            }
            // If we have a childId, we might want to start from index 3 to avoid re-asking identity
            if (childId != -1 && savedInstanceState == null) {
                index = 3; 
            }
        }

        if (savedInstanceState == null) {
            showFragment(currentFragment(), "fwd");
        }
    }

    /* --------------------------- KidsAdaptiveOnboardingHost impl --------------------------- */

    @Override
    public KidsAdaptiveOnboardingData getData() {
        return data;
    }

    @Override
    public void goNext() {
        direction = "fwd";
        index = Math.min(index + 1, KidsAdaptiveStages.TOTAL_SCREENS - 1);
        everStarted = true;
        showFragment(currentFragment(), direction);
        updateSimulateButtonVisibility();
    }

    @Override
    public void goBack() {
        direction = "back";
        index = Math.max(index - 1, 0);
        showFragment(currentFragment(), direction);
        updateSimulateButtonVisibility();
    }

    @Override
    public void goTo(int newIndex) {
        direction = "back";
        index = newIndex;
        showFragment(currentFragment(), direction);
        updateSimulateButtonVisibility();
    }

    @Override
    public void finishOnboarding() {
        data.onboardingCompleted = true;
        phase = "home";
        simulateReopenButton.setVisibility(View.GONE);

        if (childId == -1) {
            // 1. تحديد الشخصية/الأفاتار المخزن من شاشة الهوية
            String avatar = data.demoMoodSelected;

            // في حال لم يختر الطفل أي شكل، يتم تعيين شكل افتراضي حسب جنسه
            if (avatar == null || avatar.trim().isEmpty()) {
                if ("female".equals(data.gender)) {
                    avatar = "🎀";
                } else if ("male".equals(data.gender)) {
                    avatar = "⭐";
                } else {
                    avatar = "🧸";
                }
            }

            // 2. تحديد العمر بناءً على الاختيار
            int age = 10;
            if (data.ageRangeIndex != null) {
                switch (data.ageRangeIndex) {
                    case 0: age = 5; break;
                    case 1: age = 8; break;
                    case 2: age = 11; break;
                    case 3: age = 14; break;
                    case 4: age = 17; break;
                }
            }

            // 3. الحفظ النهائي في قاعدة البيانات ChildProfileStore
            childId = profileStore.addProfile(
                    data.nickname != null && !data.nickname.trim().isEmpty() ? data.nickname : "صديق سلام",
                    age,
                    data.gender != null ? data.gender : "غير محدد",
                    avatar // حقل الأفاتار المحفوظ
            );

            // ✅ تعيين الطفل الجديد كطفل نشط في ActiveChildManager
            ActiveChildManager.setActiveChildId(this, childId);
        }

        if (childId != -1) {
            Intent intent = new Intent(this, com.example.graduationproject.Kids.KidsReflectionActivity.class);
            intent.putExtra("FOR_KIDS", true);
            intent.putExtra("CHILD_ID", childId);
            intent.putExtra("CHILD_NAME", data.nickname);
            intent.putExtra("CHILD_AVATAR", data.demoMoodSelected);
            startActivity(intent);
            finish();
            return;
        }

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.anim.fade_in, 0);
        tx.replace(R.id.fragment_container, new KidsAdaptiveHomeFragment());
        tx.commit();
        applyStatusBarColor();
    }

    @Override
    public void pulseTeddy() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof KidsAdaptiveBaseOnboardingFragment && f.getView() != null) {
            ((KidsAdaptiveBaseOnboardingFragment) f).teddyHeader.pulse();
        }
    }

    @Override
    public boolean isReducedMotion() {
        // Closest Android analog to CSS `prefers-reduced-motion`: the user has turned off
        // (or scaled to 0) animations in Settings > Accessibility > Remove animations.
        float scale = android.provider.Settings.Global.getFloat(
                getContentResolver(), android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f);
        return scale == 0f;
    }

    /* ------------------------------ Resume flow -------------------------------- */

    /** Mirrors simulateReopen(): snapshots current state, then shows the resume prompt. */
    private void simulateReopen() {
        savedSnapshotData = data.copy();
        savedSnapshotIndex = index;
        KidsAdaptiveResumeDialogFragment dialog = KidsAdaptiveResumeDialogFragment.newInstance(this);
        dialog.show(getSupportFragmentManager(), "resume_dialog");
    }

    @Override
    public void onResumeContinue() {
        if (savedSnapshotData != null) {
            data = savedSnapshotData.copy();
            index = savedSnapshotIndex;
            showFragment(currentFragment(), "fwd");
        }
    }

    @Override
    public void onResumeRestart() {
        restart();
    }

    @Override
    protected void onDestroy() {
        // نستخدم Singleton، لذا لا نغلقه هنا
        super.onDestroy();
    }

    public void restart() {
        data = new KidsAdaptiveOnboardingData();
        index = 0;
        phase = "onboarding";
        everStarted = false;
        savedSnapshotData = null;
        simulateReopenButton.setVisibility(View.GONE);
        showFragment(currentFragment(), "fwd");
    }

    /* --------------------------------- helpers ---------------------------------- */

    private void updateSimulateButtonVisibility() {
        boolean show = "onboarding".equals(phase) && everStarted && index > 0 && index < KidsAdaptiveStages.TOTAL_SCREENS - 1;
        simulateReopenButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private Fragment currentFragment() {
        switch (index) {
            case 0: return new KidsAdaptiveWelcomeFragment();
            case 1: return new KidsAdaptivePrivacyFragment();
            case 2: return new KidsAdaptiveIdentityFragment();
            case 3: return new KidsAdaptiveOverallMoodFragment();
            case 4: return new KidsAdaptiveFrequentEmotionsFragment();
            case 5: return new KidsAdaptiveSafetyFragment();
            case 6: return new KidsAdaptiveIntenseFearFragment();
            case 7: return new KidsAdaptiveTimelineFragment();
            case 8: return new KidsAdaptiveHelpfulFragment();
            case 9: return new KidsAdaptiveGoalsFragment();
            case 10: return new KidsAdaptiveMoodDemoFragment();
            case 11: return new KidsAdaptivePreviewFragment();
            case 12: default: return new KidsAdaptiveReadyFragment();
        }
    }

    private void showFragment(Fragment fragment, String dir) {
        int enter = "back".equals(dir) ? R.anim.enter_back : R.anim.enter_fwd;
        int exit = "back".equals(dir) ? R.anim.exit_back : R.anim.exit_fwd;

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(enter, exit);
        tx.replace(R.id.fragment_container, fragment);
        tx.commitNowAllowingStateLoss();

        applyStatusBarColor();
    }

    /**
     * Keeps the status bar in sync with the changing screen background. The status bar sits at
     * the very top, so it takes the top (start) color of the current background gradient:
     * - Onboarding screens: the animated sky top color for the current stage.
     * - Home screen: the home gradient top color.
     */
    private void applyStatusBarColor() {
        int color;
        int bottomColor;
        if ("home".equals(phase)) {
            color = getResources().getColor(R.color.kids_adaptive_home_bg_from);
            bottomColor = getResources().getColor(R.color.kids_adaptive_home_bg_to);
        } else {
            int stage = KidsAdaptiveStages.stageForScreen(index);
            color = KidsAdaptiveStages.FROM_COLORS[stage];
            bottomColor = KidsAdaptiveStages.TO_COLORS[stage];
        }
        getWindow().setStatusBarColor(color);
        getWindow().setNavigationBarColor(bottomColor);

        View navBlur = findViewById(R.id.system_nav_blur);
        if (navBlur != null) {
            // Match the bottom color exactly as requested
            navBlur.setBackgroundColor(bottomColor);
        }

        // All kids background stages are light, so always use dark icons for readability.
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }
    }
}
