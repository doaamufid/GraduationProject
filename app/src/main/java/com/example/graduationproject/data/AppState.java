package com.example.graduationproject.data;

import com.example.graduationproject.models.Highlight;
import com.example.graduationproject.models.SavedQuote;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * In-memory app-wide state, equivalent to the useState hooks lifted to the root
 * <ArticleReaderFlow /> component in the React source:
 *   savedIds, bookmarkedIds, allHighlights, savedQuotes
 *
 * A single process-lifetime singleton is enough here since the original app
 * also keeps everything in memory (no persistence layer was implemented).
 */
public class AppState {

    private static AppState instance;

    public static synchronized AppState get() {
        if (instance == null) instance = new AppState();
        return instance;
    }

    private final Set<Integer> savedIds = new LinkedHashSet<>();
    private final Set<Integer> bookmarkedIds = new LinkedHashSet<>();
    private final List<Highlight> highlights = new ArrayList<>();
    private final List<SavedQuote> savedQuotes = new ArrayList<>();

    private AppState() {}

    // ---- favorites (heart on article cards) ----
    public boolean isSaved(int articleId) {
        return savedIds.contains(articleId);
    }

    public void toggleSaved(int articleId) {
        if (!savedIds.remove(articleId)) savedIds.add(articleId);
    }

    public List<Integer> getSavedIds() {
        return new ArrayList<>(savedIds);
    }

    // ---- bookmarks (read later) ----
    public boolean isBookmarked(int articleId) {
        return bookmarkedIds.contains(articleId);
    }

    public void toggleBookmarked(int articleId) {
        if (!bookmarkedIds.remove(articleId)) bookmarkedIds.add(articleId);
    }

    public List<Integer> getBookmarkedIds() {
        return new ArrayList<>(bookmarkedIds);
    }

    // ---- video / podcast content favorites & bookmarks (separate ID space from articles) ----
    private final Set<Integer> savedContentIds = new LinkedHashSet<>();
    private final Set<Integer> bookmarkedContentIds = new LinkedHashSet<>();

    public boolean isContentSaved(int contentId) {
        return savedContentIds.contains(contentId);
    }

    public void toggleContentSaved(int contentId) {
        if (!savedContentIds.remove(contentId)) savedContentIds.add(contentId);
    }

    public List<Integer> getSavedContentIds() {
        return new ArrayList<>(savedContentIds);
    }

    public boolean isContentBookmarked(int contentId) {
        return bookmarkedContentIds.contains(contentId);
    }

    public void toggleContentBookmarked(int contentId) {
        if (!bookmarkedContentIds.remove(contentId)) bookmarkedContentIds.add(contentId);
    }

    public List<Integer> getBookmarkedContentIds() {
        return new ArrayList<>(bookmarkedContentIds);
    }

    // ---- highlights ----
    public List<Highlight> getHighlights() {
        return highlights;
    }

    public List<Highlight> getHighlightsForArticle(int articleId) {
        List<Highlight> out = new ArrayList<>();
        for (Highlight h : highlights) if (h.articleId == articleId) out.add(h);
        return out;
    }

    public void addHighlight(Highlight h) {
        highlights.add(h);
    }

    public void removeHighlight(long id) {
        for (int i = 0; i < highlights.size(); i++) {
            if (highlights.get(i).id == id) { highlights.remove(i); return; }
        }
    }

    public Highlight findHighlight(long id) {
        for (Highlight h : highlights) if (h.id == id) return h;
        return null;
    }

    // ---- saved quotes (favorited excerpts) ----
    public List<SavedQuote> getSavedQuotes() {
        return savedQuotes;
    }

    public void addSavedQuote(SavedQuote q) {
        savedQuotes.add(q);
    }

    public void removeSavedQuote(long id) {
        for (int i = 0; i < savedQuotes.size(); i++) {
            if (savedQuotes.get(i).id == id) { savedQuotes.remove(i); return; }
        }
    }

    public boolean isQuoteSaved(int articleId, String text) {
        for (SavedQuote q : savedQuotes) {
            if (q.articleId == articleId && q.text.equals(text)) return true;
        }
        return false;
    }

    /** Toggles a saved-quote entry that mirrors a given highlight's text (used by the note sheet's heart icon). */
    public void toggleSavedQuoteForHighlight(Highlight h) {
        SavedQuote existing = null;
        for (SavedQuote q : savedQuotes) {
            if (q.articleId == h.articleId && q.text.equals(h.text)) { existing = q; break; }
        }
        if (existing != null) {
            savedQuotes.remove(existing);
        } else {
            savedQuotes.add(new SavedQuote(System.currentTimeMillis(), h.articleId, h.articleTitle, h.text, "الآن"));
        }
    }
}
