package com.example.graduationproject.models;

import android.content.Context;

import com.example.graduationproject.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Static data source. Equivalent of AVATAR_PATTERNS and BADGES constants. */
public final class ProfileRepository {

    private ProfileRepository() {
    }

    public static List<Integer> avatarPatterns(Context context) {
        return Arrays.asList(
                context.getColor(R.color.avatar_1),
                context.getColor(R.color.avatar_2),
                context.getColor(R.color.avatar_3),
                context.getColor(R.color.avatar_4),
                context.getColor(R.color.avatar_5)
        );
    }

    public static List<Badge> badges(Context context) {
        List<Badge> list = new ArrayList<>();
        list.add(new Badge(1, context.getString(R.string.badge_week_title), R.drawable.ic_flame,
                true, context.getColor(R.color.sand), context.getString(R.string.badge_week_need)));
        list.add(new Badge(2, context.getString(R.string.badge_ideas_title), R.drawable.ic_pen_line,
                true, context.getColor(R.color.primary), context.getString(R.string.badge_ideas_need)));
        list.add(new Badge(3, context.getString(R.string.badge_strengths_label), R.drawable.ic_heart,
                true, context.getColor(R.color.pink), context.getString(R.string.badge_strengths_need)));
        list.add(new Badge(4, context.getString(R.string.badge_month_title), R.drawable.ic_calendar,
                false, context.getColor(R.color.sage), context.getString(R.string.badge_month_need)));
        list.add(new Badge(5, context.getString(R.string.badge_message_title), R.drawable.ic_mail,
                true, context.getColor(R.color.purple), context.getString(R.string.badge_message_need)));
        list.add(new Badge(6, context.getString(R.string.badge_body_map_label), R.drawable.ic_sparkles,
                false, context.getColor(R.color.primary), context.getString(R.string.badge_body_map_need)));
        return list;
    }

    public static List<StatItem> quickStats(Context context) {
        return Arrays.asList(
                new StatItem(context.getString(R.string.stat_active_days_value), context.getString(R.string.stat_active_days_label)),
                new StatItem(context.getString(R.string.stat_streak_value), context.getString(R.string.stat_streak_label)),
                new StatItem(context.getString(R.string.stat_sessions_value), context.getString(R.string.stat_sessions_label_user))
        );
    }

    public static List<ArchiveItem> archiveItems(Context context) {
        List<ArchiveItem> list = new ArrayList<>();
        list.add(new ArchiveItem(context.getString(R.string.archive_thoughts_label),
                context.getString(R.string.archive_thoughts_sub), R.drawable.ic_pen_line, context.getColor(R.color.primary)));
        list.add(new ArchiveItem(context.getString(R.string.archive_strengths_label),
                context.getString(R.string.archive_strengths_sub), R.drawable.ic_heart, context.getColor(R.color.pink)));
        list.add(new ArchiveItem(context.getString(R.string.archive_messages_label),
                context.getString(R.string.archive_messages_sub), R.drawable.ic_mail, context.getColor(R.color.purple)));
        return list;
    }
}
