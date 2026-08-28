package com.example.graduationproject.util;

import android.graphics.Color;

import com.example.graduationproject.models.kidsCalmDurationOption;
import com.example.graduationproject.models.kidsCalmKidCardModel;
import com.example.graduationproject.models.kidsCalmSticker;
import com.example.graduationproject.models.kidsCalmWordModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Single shared in-memory store, standing in for the React component's
 * top-level useState() calls (cards, words, stars, activeCard...).
 * Kept as a simple singleton so every fragment/dialog reads & writes the
 * exact same state, same as sibling components sharing App's state via props.
 */
public final class kidsCalmAppState {

    private static kidsCalmAppState instance;

    public static kidsCalmAppState get() {
        if (instance == null) instance = new kidsCalmAppState();
        return instance;
    }

    // ----- STICKERS -----
    public final List<kidsCalmSticker> stickers = new ArrayList<>();

    // ----- KID_PHRASES (quick pick chips) -----
    public final List<String> kidPhrases = new ArrayList<>();

    // ----- DUR_OPTIONS -----
    public final List<kidsCalmDurationOption> durOptions = new ArrayList<>();

    // ----- WORD_EMOJIS -----
    public final List<String> wordEmojis = new ArrayList<>();

    // ----- state -----
    public final List<kidsCalmKidCardModel> cards = new ArrayList<>();
    public final List<kidsCalmWordModel> words = new ArrayList<>();
    public int stars = 2;

    public interface Listener {
        void onStateChanged();
    }

    private final List<Listener> listeners = new ArrayList<>();

    public void addListener(Listener l) { listeners.add(l); }
    public void removeListener(Listener l) { listeners.remove(l); }
    public void notifyChanged() {
        for (Listener l : new ArrayList<>(listeners)) l.onStateChanged();
    }

    private kidsCalmAppState() {
        seedStickers();
        seedPhrases();
        seedDurations();
        seedWordEmojis();
        seedCards();
        seedWords();
    }

    private void seedStickers() {
        stickers.add(new kidsCalmSticker("unicorn", "🦄", "وحيد القرن", Color.parseColor("#FFE5F2"), Color.parseColor("#E5D6FF")));
        stickers.add(new kidsCalmSticker("dog", "🐶", "كلبي", Color.parseColor("#FFF0D6"), Color.parseColor("#FFD9BD")));
        stickers.add(new kidsCalmSticker("balloon", "🎈", "بالوناتي", Color.parseColor("#FFE0EA"), Color.parseColor("#FFBACC")));
        stickers.add(new kidsCalmSticker("rainbow", "🌈", "قوس قزح", Color.parseColor("#E0F5FF"), Color.parseColor("#C2ECFF")));
        stickers.add(new kidsCalmSticker("lion", "🦁", "أسد شجاع", Color.parseColor("#FFF7E0"), Color.parseColor("#FFE3A3")));
        stickers.add(new kidsCalmSticker("rocket", "🚀", "صاروخي", Color.parseColor("#EBFAFF"), Color.parseColor("#C9B2FF")));
    }

    private void seedPhrases() {
        kidPhrases.add("أنا شجاع زي الأسد 🦁");
        kidPhrases.add("بحبك يا رب 💙");
        kidPhrases.add("كل شي رح يصير تمام ✨");
        kidPhrases.add("أنا مو لحالي، في حدا حابني 🤗");
        kidPhrases.add("بقدر أتنفس وأهدى 🌬️");
        kidPhrases.add("أنا حلو زي ما أنا 🌟");
    }

    private void seedDurations() {
        durOptions.add(new kidsCalmDurationOption("short", "🐢", "kids_calm_dur_short", 1));
        durOptions.add(new kidsCalmDurationOption("medium", "🐰", "kids_calm_dur_medium", 2));
        durOptions.add(new kidsCalmDurationOption("long", "🦋", "kids_calm_dur_long", 4));
    }

    public kidsCalmDurationOption durByKey(String key) {
        for (kidsCalmDurationOption d : durOptions) if (d.key.equals(key)) return d;
        return durOptions.get(0);
    }

    private void seedWordEmojis() {
        for (String e : new String[]{"💙", "🌸", "✨", "🛡️", "🌟", "🕊️", "🌈", "🤍"}) wordEmojis.add(e);
    }

    private kidsCalmSticker stickerById(String id) {
        for (kidsCalmSticker s : stickers) if (s.id.equals(id)) return s;
        return stickers.get(0);
    }

    private void seedCards() {
        cards.add(new kidsCalmKidCardModel(1, "أنا شجاع زي الأسد 🦁", stickerById("lion"), null, true));
        cards.add(new kidsCalmKidCardModel(2, "بحبك يا رب 💙", stickerById("rainbow"), null, false));
        cards.add(new kidsCalmKidCardModel(3, "بقدر أتنفس وأهدى 🌬️", stickerById("balloon"), null, false));
    }

    private void seedWords() {
        words.add(new kidsCalmWordModel(1, "الله معي دايماً", "💙", true, "short"));
        words.add(new kidsCalmWordModel(2, "استغفر الله", "🌸", true, "short"));
        words.add(new kidsCalmWordModel(3, "الحمدلله أنا بخير", "✨", false, "short"));
        words.add(new kidsCalmWordModel(4, "الله يحميني ويحمي أهلي", "🛡️", true, "medium"));
        words.add(new kidsCalmWordModel(5, "توكلت على الله", "🌟", false, "short"));
        words.add(new kidsCalmWordModel(6, "يا رب ثبّتني وطمّني", "🕊️", false, "medium"));
    }

    // ----- derived helpers -----
    public kidsCalmKidCardModel getActiveCard() {
        for (kidsCalmKidCardModel c : cards) if (c.active) return c;
        return null;
    }

    public List<kidsCalmWordModel> getFavoriteWords() {
        List<kidsCalmWordModel> fav = new ArrayList<>();
        for (kidsCalmWordModel w : words) if (w.favorite) fav.add(w);
        return fav;
    }

    public void activateCard(long id) {
        for (kidsCalmKidCardModel c : cards) c.active = (c.id == id);
        notifyChanged();
    }

    public void deleteCard(long id) {
        boolean wasActive = false;
        for (kidsCalmKidCardModel c : cards) if (c.id == id) wasActive = c.active;
        cards.removeIf(c -> c.id == id);
        if (wasActive && !cards.isEmpty()) cards.get(0).active = true;
        notifyChanged();
    }

    public long nextId() {
        return System.currentTimeMillis();
    }
}
