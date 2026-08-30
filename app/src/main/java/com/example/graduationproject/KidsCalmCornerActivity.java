package com.example.graduationproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.graduationproject.dialogs.kidsCalmInfoDialog;
import com.example.graduationproject.Fragments.kidsCalmAdventureFragment;
import com.example.graduationproject.Fragments.kidsCalmGalleryFragment;
import com.example.graduationproject.Fragments.kidsCalmWordsFragment;
import com.example.graduationproject.util.kidsCalmAnimUtils;
import com.example.graduationproject.util.kidsCalmAppState;

/** Mirrors the top-level React <App> component: header, tabs, toast, info modal. */
public class KidsCalmCornerActivity extends AppCompatActivity implements
        kidsCalmGalleryFragment.Host, kidsCalmWordsFragment.Host, kidsCalmAdventureFragment.Host, kidsCalmAppState.Listener {

    private static final String TAB_GALLERY = "gallery";
    private static final String TAB_WORDS = "words";
    private static final String TAB_SIMULATE = "simulate";

    private TextView tabGallery, tabWords, tabSimulate;
    private TextView starsText;
    private TextView toastView;

    private final Handler toastHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingToastHide;

    private String currentTab = TAB_GALLERY;

    private kidsCalmGalleryFragment galleryFragment;
    private kidsCalmWordsFragment wordsFragment;
    private kidsCalmAdventureFragment adventureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force Arabic Locale
        java.util.Locale locale = new java.util.Locale("ar");
        java.util.Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.kids_calm_activity_kids_calm_corner);

        tabGallery = findViewById(R.id.tabGallery);
        tabWords = findViewById(R.id.tabWords);
        tabSimulate = findViewById(R.id.tabSimulate);
        starsText = findViewById(R.id.starsText);
        toastView = findViewById(R.id.toastView);

        findViewById(R.id.infoButton).setOnClickListener(v ->
                new kidsCalmInfoDialog().show(getSupportFragmentManager(), "info"));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tabGallery.setOnClickListener(v -> switchTab(TAB_GALLERY));
        tabWords.setOnClickListener(v -> switchTab(TAB_WORDS));
        tabSimulate.setOnClickListener(v -> switchTab(TAB_SIMULATE));

        galleryFragment = new kidsCalmGalleryFragment();
        galleryFragment.setHost(this);
        wordsFragment = new kidsCalmWordsFragment();
        wordsFragment.setHost(this);
        adventureFragment = new kidsCalmAdventureFragment();
        adventureFragment.setHost(this);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.add(R.id.fragmentContainer, adventureFragment, TAB_SIMULATE).hide(adventureFragment);
        t.add(R.id.fragmentContainer, wordsFragment, TAB_WORDS).hide(wordsFragment);
        t.add(R.id.fragmentContainer, galleryFragment, TAB_GALLERY);
        t.commit();

        updateTabStyles();
        updateStars();
        updateNavigationBar();
        kidsCalmAppState.get().addListener(this);
    }

    private void updateNavigationBar() {
        Window window = getWindow();
        int navColor = getColor(R.color.kids_calm_skyTop);
        window.setNavigationBarColor(navColor);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
        double luminance = (0.299 * android.graphics.Color.red(navColor) +
                0.587 * android.graphics.Color.green(navColor) +
                0.114 * android.graphics.Color.blue(navColor)) / 255.0;
        controller.setAppearanceLightNavigationBars(luminance > 0.5);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        kidsCalmAppState.get().removeListener(this);
    }

    private void switchTab(String tab) {
        if (tab.equals(currentTab)) return;
        currentTab = tab;

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.hide(galleryFragment);
        t.hide(wordsFragment);
        t.hide(adventureFragment);

        Fragment target = tab.equals(TAB_GALLERY) ? galleryFragment
                : tab.equals(TAB_WORDS) ? wordsFragment
                : adventureFragment;
        t.show(target);
        t.commit();

        updateTabStyles();

        if (tab.equals(TAB_GALLERY)) galleryFragment.refresh();
        if (tab.equals(TAB_SIMULATE)) adventureFragment.render();
    }

    private void updateTabStyles() {
        styleTab(tabGallery, currentTab.equals(TAB_GALLERY));
        styleTab(tabWords, currentTab.equals(TAB_WORDS));
        styleTab(tabSimulate, currentTab.equals(TAB_SIMULATE));
    }

    private void styleTab(TextView tab, boolean active) {
        tab.setBackgroundResource(active ? R.drawable.kids_calm_bg_tab_active : R.drawable.kids_calm_bg_tab_idle);
        tab.setTextColor(getColor(active ? R.color.kids_calm_coralDeep : R.color.kids_calm_navySoft));
    }

    private void updateStars() {
        starsText.setText(String.valueOf(kidsCalmAppState.get().stars));
    }

    // ---------- kidsCalmGalleryFragment.Host / kidsCalmWordsFragment.Host ----------
    @Override
    public void showToast(String message) {
        if (pendingToastHide != null) toastHandler.removeCallbacks(pendingToastHide);
        toastView.setText(message);
        toastView.setVisibility(android.view.View.VISIBLE);
        kidsCalmAnimUtils.fadeUp(toastView);
        pendingToastHide = () -> toastView.setVisibility(android.view.View.GONE);
        toastHandler.postDelayed(pendingToastHide, 1900);
    }

    // ---------- kidsCalmAdventureFragment.Host ----------
    @Override
    public void goToGalleryTab() {
        switchTab(TAB_GALLERY);
    }

    @Override
    public void goToWordsTab() {
        switchTab(TAB_WORDS);
    }

    @Override
    public void onAdventureFinished() {
        // stars already incremented inside kidsCalmAdventureFragment via kidsCalmAppState
    }

    // ---------- kidsCalmAppState.Listener ----------
    @Override
    public void onStateChanged() {
        updateStars();
    }
}
