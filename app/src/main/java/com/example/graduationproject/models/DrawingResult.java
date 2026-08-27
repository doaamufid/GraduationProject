package com.example.graduationproject.models;

public class DrawingResult {
    private final String imagePath;
    private final String feedbackText;
    private final long timestamp;

    public DrawingResult(String imagePath, String feedbackText, long timestamp) {
        this.imagePath = imagePath;
        this.feedbackText = feedbackText;
        this.timestamp = timestamp;
    }

    public String getImagePath() { return imagePath; }
    public String getFeedbackText() { return feedbackText; }
    public long getTimestamp() { return timestamp; }
}