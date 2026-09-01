package com.example.graduationproject;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.graduationproject.models.Article;
import com.example.graduationproject.ui.ArticleListFragment;
import com.example.graduationproject.ui.LibraryFragment;
import com.example.graduationproject.ui.NotesArchiveFragment;
import com.example.graduationproject.ui.ReaderFragment;

import java.util.Locale;

/**
 * Single-activity host. Screen switching mirrors the `stage` useState in the React root
 * (<ArticleReaderFlow/>): "library" | "reader" | "notes" | "favArticles" | "bookmarkArticles".
 * Every transaction uses slide/fade animations and is pushed to the back stack so the
 * system/back-button back navigation matches onBack() callbacks in the original.
 */
public class ArticlesActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_articles);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, LibraryFragment.newInstance())
                    .commit();

            // Opened directly on the article bookmarks list (e.g. from the profile page)
            if (OPEN_BOOKMARKS.equals(getIntent().getStringExtra(EXTRA_OPEN))) {
                navigateTo(ArticleListFragment.newInstance(ArticleListFragment.MODE_BOOKMARKS), true);
            }
        }
    }


    private void navigateTo(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right
        );
        tx.replace(R.id.fragmentContainer, fragment);
        if (addToBackStack) tx.addToBackStack(null);
        tx.commit();
    }

    // ---- navigation entry points, called by fragments (equivalent to setStage(...) calls) ----

    public void openReader(Article article) {
        navigateTo(ReaderFragment.newInstance(article.id), true);
    }

    public void openNotes() {
        navigateTo(NotesArchiveFragment.newInstance(), true);
    }

    public void openFavoriteArticles() {
        navigateTo(ArticleListFragment.newInstance(ArticleListFragment.MODE_FAVORITES), true);
    }

    public void openBookmarkedArticles() {
        navigateTo(ArticleListFragment.newInstance(ArticleListFragment.MODE_BOOKMARKS), true);
    }

    public void goBack() {
        getSupportFragmentManager().popBackStack();
    }
}
