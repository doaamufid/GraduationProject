package com.example.graduationproject.models;

/**
 * Mirrors a saved-quote object from saveQuote()/savedQuotes in the React source:
 * { id, articleId, articleTitle, text, date }
 */
public class SavedQuote {

    public final long id;
    public final int articleId;
    public final String articleTitle;
    public final String text;
    public final String date;

    public SavedQuote(long id, int articleId, String articleTitle, String text, String date) {
        this.id = id;
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.text = text;
        this.date = date;
    }
}
