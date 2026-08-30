package com.example.graduationproject.models;

public class DrawingResult {
    private final String imagePath;
    private final String feedbackText;
    private final long timestamp;
    private long childId = -1L; // -1 يعني "غير محدد" (سجلات قديمة قبل إضافة هالحقل)

    public DrawingResult(String imagePath, String feedbackText, long timestamp) {
        this.imagePath = imagePath;
        this.feedbackText = feedbackText;
        this.timestamp = timestamp;
    }

    public DrawingResult(String imagePath, String feedbackText, long timestamp, long childId) {
        this.imagePath = imagePath;
        this.feedbackText = feedbackText;
        this.timestamp = timestamp;
        this.childId = childId;
    }

    public String getImagePath() { return imagePath; }
    public String getFeedbackText() { return feedbackText; }
    public long getTimestamp() { return timestamp; }

    public long getChildId() { return childId; }
    public void setChildId(long childId) { this.childId = childId; }
}