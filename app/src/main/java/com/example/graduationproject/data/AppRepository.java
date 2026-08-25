package com.example.graduationproject.data;

import com.example.graduationproject.models.CalmDhikrItem;
import com.example.graduationproject.models.CardItem;
import com.example.graduationproject.models.CardPhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Single in-memory data store shared across fragments/dialogs — the Java
 * equivalent of the `cards` / `dhikrList` useState hooks in the React root
 * component (App). Holds INITIAL_CARDS / INITIAL_DHIKR seed data and every
 * CRUD operation used by the UI, and notifies listeners so open screens
 * refresh immediately (just like React re-rendering on setState).
 */
public class AppRepository {

    private static AppRepository instance;
    public static AppRepository get() {
        if (instance == null) instance = new AppRepository();
        return instance;
    }

    private final List<CardItem> cards = new ArrayList<>();
    private final List<CalmDhikrItem> dhikrList = new ArrayList<>();

    public interface Listener {
        void onDataChanged();
    }

    private final List<Listener> listeners = new ArrayList<>();

    public void addListener(Listener l) { listeners.add(l); }
    public void removeListener(Listener l) { listeners.remove(l); }
    private void notifyChanged() {
        for (Listener l : new ArrayList<>(listeners)) l.onDataChanged();
    }

    private AppRepository() {
        seed();
    }

    private void seed() {
        cards.add(new CardItem(1, "هذا الشعور مؤقت، وبيمر", CardPhoto.fromPreset(Constants.PRESETS[0]), true));
        cards.add(new CardItem(2, "أنا أقوى من اللي أفكر فيه", null, false));
        cards.add(new CardItem(3, "تذكري: نجوتِ من كل يوم صعب مر عليك", CardPhoto.fromPreset(Constants.PRESETS[1]), false));
        cards.add(new CardItem(4, "خذي نفس... أنتِ بأمان الآن", CardPhoto.fromPreset(Constants.PRESETS[5]), false));
        cards.add(new CardItem(5, "صوتك يستاهل يُسمع", null, false));

        dhikrList.add(new CalmDhikrItem(1, "أستغفر الله العظيم وأتوب إليه", "عام", true, 3));
        dhikrList.add(new CalmDhikrItem(2, "حسبي الله ونعم الوكيل", "خوف", true, 2));
        dhikrList.add(new CalmDhikrItem(3, "لا حول ولا قوة إلا بالله", "قلق", false, 2));
        dhikrList.add(new CalmDhikrItem(4, "اللهم إني أسألك الطمأنينة", "قلق", true, 5));
        dhikrList.add(new CalmDhikrItem(5, "إن مع العسر يسرا", "حزن", false, 2));
        dhikrList.add(new CalmDhikrItem(6, "سبحان الله وبحمده", "عام", false, 2));
        dhikrList.add(new CalmDhikrItem(7, "يا حي يا قيوم برحمتك أستغيث", "حزن", false, 2));
        dhikrList.add(new CalmDhikrItem(8, "اللهم أنت ربي لا إله إلا أنت", "خوف", false, 2));
        dhikrList.add(new CalmDhikrItem(9, "الحمد لله على كل حال", "امتنان", false, 2));
    }

    /* ---------------- cards ---------------- */

    public List<CardItem> getCards() { return cards; }

    public CardItem getActiveCard() {
        for (CardItem c : cards) if (c.active) return c;
        return null;
    }

    public void activateCard(long id) {
        for (CardItem c : cards) c.active = (c.id == id);
        notifyChanged();
    }

    public void addCard(String phrase, CardPhoto photo) {
        boolean makeActive = cards.isEmpty();
        if (makeActive) for (CardItem c : cards) c.active = false;
        CardItem newCard = new CardItem(System.currentTimeMillis(), phrase, photo, makeActive);
        cards.add(newCard);
        notifyChanged();
    }

    public void updateCard(long id, String phrase, CardPhoto photo) {
        for (CardItem c : cards) {
            if (c.id == id) {
                c.phrase = phrase;
                c.photo = photo;
                break;
            }
        }
        notifyChanged();
    }

    public void deleteCard(long id) {
        boolean wasActive = false;
        for (CardItem c : cards) if (c.id == id && c.active) wasActive = true;
        cards.removeIf(c -> c.id == id);
        if (wasActive && !cards.isEmpty()) cards.get(0).active = true;
        notifyChanged();
    }

    /* ---------------- dhikr ---------------- */

    public List<CalmDhikrItem> getDhikrList() { return dhikrList; }

    public List<CalmDhikrItem> getFavoriteDhikr() {
        List<CalmDhikrItem> favs = new ArrayList<>();
        for (CalmDhikrItem d : dhikrList) if (d.favorite) favs.add(d);
        return favs;
    }

    public void toggleFavoriteDhikr(long id) {
        for (CalmDhikrItem d : dhikrList) if (d.id == id) d.favorite = !d.favorite;
        notifyChanged();
    }

    public void setDhikrMinutes(long id, int minutes) {
        for (CalmDhikrItem d : dhikrList) if (d.id == id) d.minutes = minutes;
        notifyChanged();
    }

    public void addDhikr(String text, String category, int minutes) {
        dhikrList.add(0, new CalmDhikrItem(System.currentTimeMillis(), text, category, true, minutes));
        notifyChanged();
    }
}
