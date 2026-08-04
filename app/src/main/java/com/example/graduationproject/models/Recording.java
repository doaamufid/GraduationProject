package com.example.graduationproject.models;

/**
 * موديل بسيط يمثل تسجيل صوتي محفوظ للطفل:
 * - الجملة اللي قالها
 * - مسار الملف الصوتي على الجهاز
 * - تاريخ/وقت الحفظ (بالميلي ثانية)
 */
public class Recording {

    private String phrase;
    private String filePath;
    private long savedAtMillis;

    public Recording() {
        // مطلوب فاضي عشان نبنيه من JSON
    }

    public Recording(String phrase, String filePath, long savedAtMillis) {
        this.phrase = phrase;
        this.filePath = filePath;
        this.savedAtMillis = savedAtMillis;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getSavedAtMillis() {
        return savedAtMillis;
    }

    public void setSavedAtMillis(long savedAtMillis) {
        this.savedAtMillis = savedAtMillis;
    }
}
