package com.example.graduationproject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.graduationproject.models.Message;
import com.example.graduationproject.ui.AnalyzingFragment;
import com.example.graduationproject.ui.ApprovedFragment;
import com.example.graduationproject.ui.ComposeFragment;
import com.example.graduationproject.ui.ListFragment;
import com.example.graduationproject.ui.RejectedFragment;
import com.example.graduationproject.ui.WallFragment;
import com.example.graduationproject.util.CardHost;

import java.util.Locale;

/**
 * Hosts the whole flow as fragments: wall -> compose -> analyzing -> approved/rejected,
 * plus "mine" and "pinned" list screens. Mirrors the `screen` useState switch in the
 * original React component (CommunityWallFlow).
 */
public class SalamCommunityActivity extends AppCompatActivity implements CardHost {

    private FrameLayout toastOverlay;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salam_community);

        toastOverlay = findViewById(R.id.toastOverlay);

        if (savedInstanceState == null) {
            showWall(false);
        }
    }

    // ---- navigation helpers, mirror setScreen(...) calls in the JS ----

    public void showWall(boolean addToBackStack) {
        navigate(new WallFragment(), addToBackStack);
    }

    public void showCompose() {
        navigate(new ComposeFragment(), true);
    }

    public void showAnalyzing() {
        navigate(AnalyzingFragment.newInstance(), false);
    }

    public void showApproved(Message message) {
        navigate(ApprovedFragment.newInstance(message.id), false);
    }

    public void showRejected(String reasonKey, boolean crisis) {
        navigate(RejectedFragment.newInstance(reasonKey, crisis), false);
    }

    public void showMine() {
        navigate(ListFragment.newInstance(ListFragment.MODE_MINE), true);
    }

    public void showPinned() {
        navigate(ListFragment.newInstance(ListFragment.MODE_PINNED), true);
    }

    private void navigate(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.setCustomAnimations(
                R.anim.screen_in, R.anim.fade_out_fast,
                R.anim.screen_in, R.anim.fade_out_fast);
        tx.replace(R.id.fragmentContainer, fragment);
        if (addToBackStack) tx.addToBackStack(null);
        tx.commit();
    }

    // ---- CardHost ----

    @Override
    public FrameLayout getToastOverlay() {
        return toastOverlay;
    }

    @Override
    public void copyToClipboard(String text) {
        try {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("salam", text);
            cm.setPrimaryClip(clip);
        } catch (Exception e) {
            Toast.makeText(this, R.string.toast_copy_fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPinnedCountChanged() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (current instanceof WallFragment) {
            ((WallFragment) current).refreshPinnedBadge();
        }
    }
}
