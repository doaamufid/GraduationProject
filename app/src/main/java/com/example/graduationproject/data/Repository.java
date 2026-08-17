package com.example.graduationproject.data;


import com.example.graduationproject.models.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * In-memory singleton holding all wall state: messages, display order,
 * hearted ids, pinned ids and the currently selected category.
 * Mirrors: messages / order / heartedIds / pinnedIds / cat useState hooks.
 */
public class Repository {

    private static Repository instance;

    public static Repository get() {
        if (instance == null) instance = new Repository();
        return instance;
    }

    private final List<Message> messages = new ArrayList<>();
    private final List<Long> order = new ArrayList<>();
    private final LinkedHashSet<Long> heartedIds = new LinkedHashSet<>();
    private final LinkedHashSet<Long> pinnedIds = new LinkedHashSet<>();
    private String currentCategory = "الكل";

    private Repository() {
        messages.addAll(SeedData.seed());
        for (Message m : messages) order.add(m.id);
        Collections.shuffle(order);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Message findById(long id) {
        for (Message m : messages) if (m.id == id) return m;
        return null;
    }

    public List<Long> getOrder() {
        return order;
    }

    public void shuffleOrder() {
        Collections.shuffle(order);
    }

    public String getCurrentCategory() {
        return currentCategory;
    }

    public void setCurrentCategory(String cat) {
        currentCategory = cat;
    }

    public boolean isHearted(long id) {
        return heartedIds.contains(id);
    }

    public void toggleHeart(long id) {
        if (heartedIds.contains(id)) heartedIds.remove(id);
        else heartedIds.add(id);
    }

    public boolean isPinned(long id) {
        return pinnedIds.contains(id);
    }

    /** returns true if it just became pinned, false if it was just unpinned */
    public boolean togglePin(long id) {
        if (pinnedIds.contains(id)) {
            pinnedIds.remove(id);
            return false;
        } else {
            pinnedIds.add(id);
            return true;
        }
    }

    public int pinnedCount() {
        return pinnedIds.size();
    }

    public List<Message> getPinnedList() {
        List<Message> out = new ArrayList<>();
        for (Long id : pinnedIds) {
            Message m = findById(id);
            if (m != null) out.add(m);
        }
        return out;
    }

    public List<Message> getMineList() {
        List<Message> out = new ArrayList<>();
        for (Message m : messages) if (m.isMine) out.add(m);
        return out;
    }

    /** Top 5 messages sorted by hearts desc, mirrors topSlides useMemo */
    public List<Message> getTopSlides() {
        List<Message> copy = new ArrayList<>(messages);
        Collections.sort(copy, (a, b) -> Integer.compare(b.hearts, a.hearts));
        if (copy.size() > 5) copy = copy.subList(0, 5);
        return copy;
    }

    /** Grid list: order minus top-slide ids, filtered by current category */
    public List<Message> getVisibleGrid() {
        List<Message> top = getTopSlides();
        List<Long> topIds = new ArrayList<>();
        for (Message m : top) topIds.add(m.id);

        List<Message> out = new ArrayList<>();
        for (Long id : order) {
            Message m = findById(id);
            if (m == null) continue;
            if (topIds.contains(m.id)) continue;
            if (!currentCategory.equals("الكل") && !m.cat.equals(currentCategory)) continue;
            out.add(m);
        }
        return out;
    }

    /** Adds a freshly approved message to the top of messages + order, mirrors submitDraft */
    public Message addNewMessage(String text, String cat, String img, String emoji, int colorIndex) {
        Message m = new Message(System.currentTimeMillis(), text, cat, 0, img, emoji, true, colorIndex);
        messages.add(0, m);
        order.add(0, m.id);
        return m;
    }
}
