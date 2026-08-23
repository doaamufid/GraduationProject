package com.example.graduationproject.models;

/**
 * Mirrors one entry of the `messages` state array in the JSX, plus a
 * transient "typing indicator" variant (which in the JSX was a separate
 * `typing` boolean rendered inline instead of a real message).
 */
public class ChatMessage {

    public final long id;
    public final boolean fromUser;
    public final String text;      // null for the ephemeral typing indicator / voice messages
    public final String time;
    public final String cardType;  // "breathing" | "dhikr" | "article" | null
    public final boolean typingIndicator;

    // Voice message support
    public final String audioPath;        // null for text messages
    public final int audioDurationSec;    // 0 unless audioPath != null

    private ChatMessage(long id, boolean fromUser, String text, String time,
                        String cardType, boolean typingIndicator,
                        String audioPath, int audioDurationSec) {
        this.id = id;
        this.fromUser = fromUser;
        this.text = text;
        this.time = time;
        this.cardType = cardType;
        this.typingIndicator = typingIndicator;
        this.audioPath = audioPath;
        this.audioDurationSec = audioDurationSec;
    }

    public static ChatMessage bot(long id, String text, String time, String cardType) {
        return new ChatMessage(id, false, text, time, cardType, false, null, 0);
    }

    public static ChatMessage user(long id, String text, String time) {
        return new ChatMessage(id, true, text, time, null, false, null, 0);
    }

    public static ChatMessage voice(long id, String audioPath, int durationSec, String time) {
        return new ChatMessage(id, true, null, time, null, false, audioPath, durationSec);
    }

    public static ChatMessage typingIndicator(long id) {
        return new ChatMessage(id, false, null, null, null, true, null, 0);
    }

    public boolean isVoice() {
        return audioPath != null;
    }
}
