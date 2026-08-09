package com.example.graduationproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.graduationproject.models.ContentItem;
import com.example.graduationproject.ui.LibraryFragment;
import com.example.graduationproject.ui.PlayerFragment;

/**
 * Hosts both screens (Library, Player) inside a single fragment
 * container, equivalent to the `stage` state switch ("library" |
 * "player") in the original root component. Navigation between them
 * uses the FragmentManager back stack.
 */
public class VideoLibraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // ضبط لون شريط الحالة ليكون أزرق فاتح (مثل الخلفية)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.parseColor("#E1F1FF"));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                // شريط الحالة فاتح، لذا نجعل الأيقونات داكنة
                getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        setContentView(R.layout.activity_video_library);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new LibraryFragment())
                    .commit();
        }
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
        if (currentFragment instanceof LibraryFragment) {
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
