package com.example.graduationproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.graduationproject.models.ContentItem;
import com.example.graduationproject.ui.VideoLibraryFragment;
import com.example.graduationproject.ui.VideoContentListFragment;
import com.example.graduationproject.ui.PlayerFragment;


/**
 * Hosts both screens (Library, Player) inside a single fragment
 * container, equivalent to the `stage` state switch ("library" |
 * "player") in the original root component. Navigation between them
 * uses the FragmentManager back stack.
 */
public class VideoLibraryActivity extends AppCompatActivity implements ContentItemHost {

    public static final String EXTRA_OPEN = "open";
    public static final String OPEN_BOOKMARKS = "bookmarks";

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(AppLanguageManager.wrapContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.activity.EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_library);

        // Force this window's direction (the app theme hardcodes RTL globally)
        applyWindowDirection();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new VideoLibraryFragment())
                    .commit();

            // Opened directly on the video bookmarks list (e.g. from the profile page)
            if (OPEN_BOOKMARKS.equals(getIntent().getStringExtra(EXTRA_OPEN))) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer,
                                VideoContentListFragment.newInstance(VideoContentListFragment.MODE_BOOKMARKS))
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    /** Forces this window's layout + text direction to match the saved language. */
    private void applyWindowDirection() {
        boolean rtl = AppLanguageManager.isRtl(AppLanguageManager.getSavedLanguage(this));
        android.view.View decor = getWindow().getDecorView();
        decor.setLayoutDirection(rtl
                ? android.view.View.LAYOUT_DIRECTION_RTL
                : android.view.View.LAYOUT_DIRECTION_LTR);
        decor.setTextDirection(rtl
                ? android.view.View.TEXT_DIRECTION_RTL
                : android.view.View.TEXT_DIRECTION_LTR);
    }

    /** Equivalent of `open(it)`: setActive(it) + setStage("player"). */
    public void openPlayer(ContentItem item) {
        androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
        androidx.fragment.app.Fragment currentFragment = fm.findFragmentById(R.id.fragmentContainer);

        androidx.fragment.app.FragmentTransaction transaction = fm.beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, PlayerFragment.newInstance(item.id));

        // If we are currently on the Library list, add to backstack so we can return to it.
        // If we are already in the Player (e.g. clicking a suggestion), don't add to backstack
        // so that 'back' always returns to the Library list, not the previous video.
        if (currentFragment instanceof VideoLibraryFragment
                || currentFragment instanceof VideoContentListFragment) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            // If we are at the LibraryFragment (root of this activity), 
            // call super or finish() to ensure the activity is removed from the stack.
            super.onBackPressed();
            finish();
        }
    }
}

