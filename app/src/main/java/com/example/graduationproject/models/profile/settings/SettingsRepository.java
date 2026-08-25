package com.example.graduationproject.models.profile.settings;

import android.content.Context;

import com.example.graduationproject.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static/seed data source. Equivalent of THEMES and the
 * ChildProfilesScreen's initial `children` state. The children list is
 * kept as an in-memory singleton so edits persist while the app runs.
 */
public final class SettingsRepository {

    private static SettingsRepository instance;

    public final List<ChildProfile> children = new ArrayList<>();

    // Settings state
    public boolean disguise = false;
    public boolean autoDelete = true;
    public boolean appLock = false;
    public boolean autoDark = true;
    public String theme = "blue";
    public boolean cloudAI = true;
    public boolean breathHaptic = true;
    public boolean reduceMotion = false;

    private SettingsRepository() {
        children.add(new ChildProfile(1, "يوسف", 10, "\uD83E\uDDD2"));
        children.add(new ChildProfile(2, "سارة", 6, "\uD83D\uDC67"));
    }

    public static synchronized SettingsRepository getInstance() {
        if (instance == null) instance = new SettingsRepository();
        return instance;
    }

    public static List<ThemeOption> themes(Context context) {
        return Arrays.asList(
                new ThemeOption("blue", context.getColor(R.color.primary)),
                new ThemeOption("sage", context.getColor(R.color.sage)),
                new ThemeOption("sand", context.getColor(R.color.sand))
        );
    }

    public void removeChild(int id) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).id == id) children.remove(i);
        }
    }
}
