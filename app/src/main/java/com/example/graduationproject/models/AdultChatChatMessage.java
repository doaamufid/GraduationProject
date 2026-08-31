package com.example.graduationproject.models;

/** One row in the chat: a text/voice bubble from the user, from the bot, or a system pill. */
public class AdultChatChatMessage {
    public enum Kind { BOT, USER, SYSTEM }

    public final long id;
    public final Kind kind;
    public String text;
    public String time;
    public AdultChatCardData card;

    public boolean voice = false;
    public String voiceDuration;

    public boolean deleted = false;
    public boolean edited = false;
    public boolean seen = false;

    /** "up" / "down" / null */
    public String rating;

    // reply-to quote
    public boolean hasReplyTo = false;
    public boolean replyToFromUser;
    public String replyToSnippet;

    public AdultChatChatMessage(long id, Kind kind, String text, String time) {
        this.id = id;
        this.kind = kind;
        this.text = text;
        this.time = time;
    }

    public static AdultChatChatMessage system(long id, String text) {
        AdultChatChatMessage m = new AdultChatChatMessage(id, Kind.SYSTEM, text, null);
        return m;
    }
}
