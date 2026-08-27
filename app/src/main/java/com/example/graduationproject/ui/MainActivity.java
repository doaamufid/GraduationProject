package com.example.graduationproject.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.graduationproject.AppLanguageManager;
import com.example.graduationproject.ContentItemHost;
import com.example.graduationproject.R;
import com.example.graduationproject.models.ContentItem;

/**
 * Hosts both screens (Library, Player) inside a single fragment
 * container, equivalent to the `stage` state switch ("library" |
 * "player") in the original root component. Navigation between them
 * uses the FragmentManager back stack.
 */
public class MainActivity extends AppCompatActivity implements ContentItemHost {

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(AppLanguageManager.wrapContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Arabic + RTL by default; English + LTR when the saved app language is English
        AppLanguageManager.applySavedLanguage(this);

        setContentView(R.layout.activity_video_library);

        // Force this window's direction (the app theme hardcodes RTL globally)
        boolean rtl = AppLanguageManager.isRtl(AppLanguageManager.getSavedLanguage(this));
        android.view.View decor = getWindow().getDecorView();
        decor.setLayoutDirection(rtl
                ? android.view.View.LAYOUT_DIRECTION_RTL
                : android.view.View.LAYOUT_DIRECTION_LTR);
        decor.setTextDirection(rtl
                ? android.view.View.TEXT_DIRECTION_RTL
                : android.view.View.TEXT_DIRECTION_LTR);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new VideoLibraryFragment())
                    .commit();
        }
    }

    /** Equivalent of `open(it)`: setActive(it) + setStage("player"). */
    public void openPlayer(ContentItem item) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, PlayerFragment.newInstance(item.id))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}



