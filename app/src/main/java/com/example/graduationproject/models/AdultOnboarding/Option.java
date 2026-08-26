package com.example.graduationproject.models.AdultOnboarding;

/** Generic (id/emoji/label/sub) option used across choice cards, chips and bubbles. */
public class Option {
    public final String id;
    public final String emoji;
    public final int iconRes;
    public final String label;
    public final int labelRes;
    public final String sub;

    public Option(String id, String emoji, String label) {
        this(id, emoji, label, null);
    }

    public Option(String id, String emoji, int labelRes) {
        this(id, emoji, labelRes, 0);
    }

    public Option(String id, String emoji, int labelRes, int iconRes) {
        this.id = id;
        this.emoji = emoji;
        this.iconRes = iconRes;
        this.label = null;
        this.labelRes = labelRes;
        this.sub = null;
    }

    public Option(String id, String emoji, String label, String sub) {
        this.id = id;
        this.emoji = emoji;
        this.iconRes = 0;
        this.label = label;
        this.labelRes = 0;
        this.sub = sub;
    }
}
