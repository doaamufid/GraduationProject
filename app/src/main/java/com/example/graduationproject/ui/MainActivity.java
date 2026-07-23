package com.example.graduationproject.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.graduationproject.R;
import com.example.graduationproject.models.ContentItem;

/**
 * Hosts both screens (Library, Player) inside a single fragment
 * container, equivalent to the `stage` state switch ("library" |
 * "player") in the original root component. Navigation between them
 * uses the FragmentManager back stack.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new LibraryFragment())
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
