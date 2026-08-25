package com.example.graduationproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.graduationproject.Fragments.profile.BalancedThoughtsFragment;
import com.example.graduationproject.Fragments.profile.ChildDetailFragment;
import com.example.graduationproject.Fragments.profile.ChildProfilesFragment;
import com.example.graduationproject.Fragments.profile.FutureMessagesFragment;
import com.example.graduationproject.Fragments.profile.ProfileHomeFragment;
import com.example.graduationproject.Fragments.profile.StrengthsBankFragment;

import java.util.Locale;

/**
 * Java/XML port of ConnectedProfileFlow (JSX root router).
 * Screens: home | thoughts | strengths | messages | children | childDetail
 */
public class AdultProfileActivity extends AppCompatActivity implements ProfileNavigator {

    private TextView txtToast;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pendingToastHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force Arabic locale
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adult_profile);

        txtToast = findViewById(R.id.txt_toast);
        findViewById(R.id.btn_settings).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        if (savedInstanceState == null) {
            String nav = getIntent().getStringExtra("navigate_to");
            if (nav != null) {
                navigate(nav);
            } else {
                showHome();
            }
        }
    }

    // ---------------------------------------------------------------
    // Navigation (mirrors setScreen("..."))
    // ---------------------------------------------------------------
    public void showHome() { swap(new ProfileHomeFragment()); }
    public void showThoughts() { swap(new BalancedThoughtsFragment()); }
    public void showStrengths() { swap(new StrengthsBankFragment()); }
    public void showMessages() { swap(new FutureMessagesFragment()); }
    public void showChildren() { swap(new ChildProfilesFragment()); }

    /** Mirrors openChild(id): setActiveChildId(id); setScreen("childDetail"). */
    public void showChildDetail(long childId) { swap(ChildDetailFragment.newInstance(childId)); }

    /** Mirrors navigate(key) used by ProfileHome's archive links + children link. */
    public void navigate(String key) {
        switch (key) {
            case "thoughts": showThoughts(); break;
            case "strengths": showStrengths(); break;
            case "messages": showMessages(); break;
            case "children": showChildren(); break;
        }
    }

    private void swap(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(
                android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out
        );
        tx.replace(R.id.fragment_container, fragment);
        tx.commit();
    }

    // ---------------------------------------------------------------
    // Toast (mirrors showToast(msg) + setTimeout(..., 2400))
    // ---------------------------------------------------------------
    public void showToast(String message) {
        if (pendingToastHide != null) handler.removeCallbacks(pendingToastHide);

        txtToast.setText(message);
        txtToast.setVisibility(View.VISIBLE);
        txtToast.setAlpha(0f);
        txtToast.setTranslationY(8 * getResources().getDisplayMetrics().density);
        txtToast.animate().alpha(1f).translationY(0f).setDuration(300).start();

        pendingToastHide = () -> txtToast.animate().alpha(0f).setDuration(200)
                .withEndAction(() -> txtToast.setVisibility(View.GONE)).start();
        handler.postDelayed(pendingToastHide, 2400);
    }

    public void goBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }
}
