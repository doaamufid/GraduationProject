package com.example.graduationproject.models;

public class SurvivalItemModel {
    private String tag;
    private String title;
    private String buttonText;

    public SurvivalItemModel(String tag, String title, String buttonText) {
        this.tag = tag;
        this.title = title;
        this.buttonText = buttonText;
    }

    public String getTag() { return tag; }
    public String getTitle() { return title; }
    public String getButtonText() { return buttonText; }
}