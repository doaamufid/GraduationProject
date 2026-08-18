package com.example.graduationproject.models;

/**
 * Mirrors a highlight object created in Reader / HighlightPopover in the React source:
 * { id, articleId, articleTitle, text, color, note, active, date }
 */
public class Highlight {

    public final long id;
    public final int articleId;
    public final String articleTitle;
    /** Which paragraph (index into Article.body) this highlight lives in. */
    public final int paragraphIndex;
    public String text;
    public int color;      // ARGB color int (one of the 5 marker colors)
    public String note;
    public boolean active;
    public final String date;

    public Highlight(long id, int articleId, String articleTitle, int paragraphIndex,
                      String text, int color, String note, boolean active, String date) {
        this.id = id;
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.paragraphIndex = paragraphIndex;
        this.text = text;
        this.color = color;
        this.note = note;
        this.active = active;
        this.date = date;
    }
}
