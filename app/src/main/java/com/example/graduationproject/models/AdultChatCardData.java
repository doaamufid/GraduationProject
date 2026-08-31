package com.example.graduationproject.models;

/** Attachment shown under a bot message: exercise / dhikr / article / video / moment / sos. */
public class AdultChatCardData {
    public enum Type { EXERCISE, DHIKR, ARTICLE, VIDEO, MOMENT, SOS }

    public final Type type;
    public String title;
    public String badge;
    public String duration;
    public String desc;
    public String quote;
    public String tag;
    public String headline;
    public String sub;
    public String preview;
    public String trail;

    public AdultChatCardData(Type type) {
        this.type = type;
    }

    public static AdultChatCardData exercise(String title, String badge, String duration, String desc) {
        AdultChatCardData c = new AdultChatCardData(Type.EXERCISE);
        c.title = title; c.badge = badge; c.duration = duration; c.desc = desc;
        return c;
    }

    public static AdultChatCardData article(String tag, String headline, String sub, String preview) {
        AdultChatCardData c = new AdultChatCardData(Type.ARTICLE);
        c.tag = tag; c.headline = headline; c.sub = sub; c.preview = preview;
        return c;
    }

    public static AdultChatCardData dhikr(String title, String quote) {
        AdultChatCardData c = new AdultChatCardData(Type.DHIKR);
        c.title = title; c.quote = quote;
        return c;
    }

    public static AdultChatCardData video(String title, String tag, String duration) {
        AdultChatCardData c = new AdultChatCardData(Type.VIDEO);
        c.title = title; c.tag = tag; c.duration = duration;
        return c;
    }

    public static AdultChatCardData moment(String trail) {
        AdultChatCardData c = new AdultChatCardData(Type.MOMENT);
        c.trail = trail;
        return c;
    }

    public static AdultChatCardData sos() {
        return new AdultChatCardData(Type.SOS);
    }
}
