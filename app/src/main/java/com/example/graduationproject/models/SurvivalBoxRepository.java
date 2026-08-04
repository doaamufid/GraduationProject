package com.example.graduationproject.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Single in-memory source of truth for the whole screen, equivalent to
 * the `const [data, setData] = useState({ audio: [], photos: [], love: [], dhikr: [] })`
 * state in the original React root component. Kept as a process-wide
 * singleton so every Fragment / DialogFragment can read and mutate the
 * same lists, the same way sibling components shared `data` via props.
 *
 * Note: like the original (a client-only React demo with no backend),
 * this state is not persisted to disk and resets when the app process
 * is killed.
 */
public final class SurvivalBoxRepository {

    public static final List<String> PRESET_DHIKR = Arrays.asList(
            "سبحان الله وبحمده",
            "لا إله إلا الله",
            "الحمد لله",
            "حسبي الله ونعم الوكيل",
            "أستغفر الله",
            "لا حول ولا قوة إلا بالله"
    );

    private static SurvivalBoxRepository instance;

    private final List<AudioItem> audio = new ArrayList<>();
    private final List<PhotoItem> photos = new ArrayList<>();
    private final List<LoveItem> love = new ArrayList<>();
    private final List<DhikrItem> dhikr = new ArrayList<>();

    private SurvivalBoxRepository() {
    }

    public static synchronized SurvivalBoxRepository getInstance() {
        if (instance == null) {
            instance = new SurvivalBoxRepository();
        }
        return instance;
    }

    // ----- getters (live, mutable lists on purpose - simplest 1:1 port) -----
    public List<AudioItem> getAudio() {
        return audio;
    }

    public List<PhotoItem> getPhotos() {
        return photos;
    }

    public List<LoveItem> getLove() {
        return love;
    }

    public List<DhikrItem> getDhikr() {
        return dhikr;
    }

    // ----- mutators (equivalent of addAudio / addPhoto / addLove / saveDhikr) -----

    public AudioItem addAudio(String label, int durationSeconds) {
        AudioItem item = new AudioItem(System.currentTimeMillis(), label, durationSeconds);
        audio.add(item);
        return item;
    }

    public PhotoItem addPhoto(String uri, String caption) {
        PhotoItem item = new PhotoItem(System.currentTimeMillis(), uri, caption);
        photos.add(item);
        return item;
    }

    public LoveItem addLove(String text, String source) {
        LoveItem item = new LoveItem(System.currentTimeMillis(), text, source);
        love.add(item);
        return item;
    }

    /** Equivalent of `saveDhikr`: REPLACES the whole favorites list. */
    public void setDhikr(List<String> texts) {
        dhikr.clear();
        for (String t : texts) {
            dhikr.add(new DhikrItem(t));
        }
    }

    public void removeAudio(long id) {
        for (int i = audio.size() - 1; i >= 0; i--) {
            if (audio.get(i).id == id) audio.remove(i);
        }
    }

    public void removePhoto(long id) {
        for (int i = photos.size() - 1; i >= 0; i--) {
            if (photos.get(i).id == id) photos.remove(i);
        }
    }

    public void removeLove(long id) {
        for (int i = love.size() - 1; i >= 0; i--) {
            if (love.get(i).id == id) love.remove(i);
        }
    }

    public void removeDhikr(String text) {
        for (int i = dhikr.size() - 1; i >= 0; i--) {
            if (dhikr.get(i).text.equals(text)) dhikr.remove(i);
        }
    }
}
