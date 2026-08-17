package com.example.graduationproject.data;

import java.util.regex.Pattern;

public class Moderation {

    private static final Pattern BANNED_CONTACT_PATTERN =
            Pattern.compile("(\\+?\\d{7,}|@\\w+|instagram|whatsapp|واتساب|انستا)", Pattern.CASE_INSENSITIVE);

    private static final String[] BANNED_NEGATIVE_WORDS = {"غبي", "احمق", "تافه", "فاشل انت"};

    private static final String[] CRISIS_WORDS = {"أبي أموت", "ما أبي أعيش", "أذية نفسي"};

    public static class Result {
        public final boolean ok;
        public final boolean crisis;
        public final String reasonResKey; // used by caller to resolve a string resource

        Result(boolean ok, boolean crisis, String reasonResKey) {
            this.ok = ok;
            this.crisis = crisis;
            this.reasonResKey = reasonResKey;
        }
    }

    /** Reason keys: "crisis", "contact", "negative", "short" or null when ok */
    public static Result moderate(String text) {
        for (String w : CRISIS_WORDS) {
            if (text.contains(w)) return new Result(false, true, "crisis");
        }
        if (BANNED_CONTACT_PATTERN.matcher(text).find()) {
            return new Result(false, false, "contact");
        }
        for (String w : BANNED_NEGATIVE_WORDS) {
            if (text.contains(w)) return new Result(false, false, "negative");
        }
        if (text.trim().length() < 10) {
            return new Result(false, false, "short");
        }
        return new Result(true, false, null);
    }
}
