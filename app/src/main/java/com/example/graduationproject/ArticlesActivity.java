package com.example.graduationproject;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Article;
import com.example.graduationproject.ui.LibraryFragment;
import com.example.graduationproject.ui.ReaderFragment;

/**
 * Hosts both screens of the app inside a single fragment container,
 * mirroring the `stage` state switch ("library" | "reader") in the
 * original root component. Navigation uses the FragmentManager back
 * stack instead of a `stage` string.
 */
public class ArticlesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#E1F1FF"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        setContentView(R.layout.activity_articles);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new LibraryFragment())
                    .commit();
        }
    }
    public void openReader(Article article) {
        FragmentManager fm = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction tx = fm.beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, ReaderFragment.newInstance(article.id));

        if (fm.getBackStackEntryCount() == 0) {
            tx.addToBackStack(null);
        }
        tx.commit();
    }


}
