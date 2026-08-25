package com.example.graduationproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.graduationproject.data.AppRepository;
import com.example.graduationproject.ui.DhikrFragment;
import com.example.graduationproject.ui.GalleryFragment;
import com.example.graduationproject.ui.InfoDialogFragment;
import com.example.graduationproject.ui.SimulateFragment;

/**
 * Root shell — equivalent to the outer JSX in <App/>: the header (title +
 * subtitle + info button), the 3-tab bar (بطاقاتي / أذكاري / سيناريو الأزمة),
 * the fragment content area, and the bottom toast pill.
 */
public class OneClickCalmActivity extends AppCompatActivity implements AppHost, AppRepository.Listener {

    private TextView appSubtitle;
    private TextView tabGallery, tabDhikr, tabSimulate;
    private TextView toastView;
    private FrameLayout fragmentContainer;

    private int currentTab = 0;
    private final Handler toastHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingHideToast;

    private final AppRepository repo = AppRepository.get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_once_click_calm);

        appSubtitle = findViewById(R.id.appSubtitle);
        tabGallery = findViewById(R.id.tabGallery);
        tabDhikr = findViewById(R.id.tabDhikr);
        tabSimulate = findViewById(R.id.tabSimulate);
        toastView = findViewById(R.id.toastView);
        fragmentContainer = findViewById(R.id.fragmentContainer);

        ImageButton btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> new InfoDialogFragment().show(getSupportFragmentManager(), "info"));

        tabGallery.setOnClickListener(v -> switchTab(0));
        tabDhikr.setOnClickListener(v -> switchTab(1));
        tabSimulate.setOnClickListener(v -> switchTab(2));

        switchTab(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        repo.addListener(this);
        refreshHeader();
    }

    @Override
    protected void onStop() {
        super.onStop();
        repo.removeListener(this);
    }

    @Override
    public void onDataChanged() {
        refreshHeader();
    }

    @Override
    public void switchTab(int index) {
        currentTab = index;
        highlightTab(index);

        Fragment fragment;
        switch (index) {
            case 1:
                fragment = new DhikrFragment();
                break;
            case 2:
                fragment = new SimulateFragment();
                break;
            default:
                fragment = new GalleryFragment();
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.replace(R.id.fragmentContainer, fragment);
        tx.commit();

        refreshHeader();
    }

    private void highlightTab(int index) {
        TextView[] tabs = {tabGallery, tabDhikr, tabSimulate};
        for (int i = 0; i < tabs.length; i++) {
            boolean active = i == index;
            tabs[i].setBackgroundResource(active ? R.drawable.bg_tab_selected : 0);
            tabs[i].setTextColor(getResources().getColor(active ? R.color.amber : R.color.mutedDim));
        }
    }

    @Override
    public void refreshHeader() {
        switch (currentTab) {
            case 1: {
                int favCount = repo.getFavoriteDhikr().size();
                int res = favCount == 1 ? R.string.subtitle_dhikr : R.string.subtitle_dhikr_plural;
                appSubtitle.setText(getString(res, favCount));
                break;
            }
            case 2:
                appSubtitle.setText(R.string.subtitle_simulate);
                break;
            default: {
                int cardCount = repo.getCards().size();
                int res = cardCount == 1 ? R.string.subtitle_cards : R.string.subtitle_cards_plural;
                appSubtitle.setText(getString(res, cardCount));
            }
        }
    }

    @Override
    public void showToast(String message) {
        toastView.setText(message);
        toastView.setAlpha(0f);
        toastView.setTranslationY(30f);
        toastView.setVisibility(TextView.VISIBLE);
        toastView.animate().alpha(1f).translationY(0f).setDuration(300).start();

        if (pendingHideToast != null) toastHandler.removeCallbacks(pendingHideToast);
        pendingHideToast = () -> toastView.animate().alpha(0f).setDuration(200)
                .withEndAction(() -> toastView.setVisibility(TextView.GONE)).start();
        toastHandler.postDelayed(pendingHideToast, 2000);
    }
}
