package com.example.graduationproject.models;

public class HomeAction {

    private int icon;
    private int iconBackground;
    private String title;
    private String subtitle;

    public HomeAction(int icon, int iconBackground, String title, String subtitle) {
        this.icon = icon;
        this.iconBackground = iconBackground;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getIcon() {
        return icon;
    }

    public int getIconBackground() {
        return iconBackground;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
