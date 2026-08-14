package com.example.graduationproject.data.profile;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.graduationproject.R;
import com.example.graduationproject.models.profile.Badge;
import com.example.graduationproject.models.profile.BalancedThought;
import com.example.graduationproject.models.profile.ChildAlert;
import com.example.graduationproject.models.profile.ChildDetail;
import com.example.graduationproject.models.profile.ChildFeature;
import com.example.graduationproject.models.profile.ChildHistoryEntry;
import com.example.graduationproject.models.profile.ChildProfile;
import com.example.graduationproject.models.profile.ChildStats;
import com.example.graduationproject.models.profile.FutureMessage;
import com.example.graduationproject.models.profile.Trait;
import com.example.graduationproject.models.profile.TraitEvidence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SeedData {

    // ---- Balanced Thoughts pattern -> color key mapping (mirrors PATTERN_COLORS) ----
    public static int colorForPattern(Context ctx, String pattern) {
        if (pattern.equals(ctx.getString(R.string.pattern_overgeneralization))) {
            return ContextCompat.getColor(ctx, R.color.pattern_overgeneralization);
        } else if (pattern.equals(ctx.getString(R.string.pattern_all_or_nothing))) {
            return ContextCompat.getColor(ctx, R.color.pattern_all_or_nothing);
        } else if (pattern.equals(ctx.getString(R.string.pattern_catastrophizing))) {
            return ContextCompat.getColor(ctx, R.color.pattern_catastrophizing);
        } else if (pattern.equals(ctx.getString(R.string.pattern_discounting_positive))) {
            return ContextCompat.getColor(ctx, R.color.pattern_discounting_positive);
        }
        return ContextCompat.getColor(ctx, R.color.primary);
    }

    public static List<BalancedThought> getSeedThoughts(Context ctx) {
        List<BalancedThought> list = new ArrayList<>();
        list.add(new BalancedThought(1, ctx.getString(R.string.pattern_overgeneralization),
                ctx.getString(R.string.thought_1_date),
                ctx.getString(R.string.thought_1_original),
                ctx.getString(R.string.thought_1_balanced),
                ctx.getString(R.string.thought_1_exercise)));
        list.add(new BalancedThought(2, ctx.getString(R.string.pattern_catastrophizing),
                ctx.getString(R.string.thought_2_date),
                ctx.getString(R.string.thought_2_original),
                ctx.getString(R.string.thought_2_balanced),
                ctx.getString(R.string.thought_2_exercise)));
        list.add(new BalancedThought(3, ctx.getString(R.string.pattern_discounting_positive),
                ctx.getString(R.string.thought_3_date),
                ctx.getString(R.string.thought_3_original),
                ctx.getString(R.string.thought_3_balanced),
                ctx.getString(R.string.thought_3_exercise)));
        return list;
    }

    public static List<Trait> getInitialTraits(Context ctx) {
        List<Trait> list = new ArrayList<>();
        list.add(new Trait("patience", ctx.getString(R.string.trait_patience_name), ContextCompat.getColor(ctx, R.color.trait_patience), 8,
                ctx.getString(R.string.trait_patience_quote), false,
                Arrays.asList(
                        new TraitEvidence(ctx.getString(R.string.evidence_last_tuesday), ctx.getString(R.string.evidence_work_pressure)),
                        new TraitEvidence(ctx.getString(R.string.evidence_last_week), ctx.getString(R.string.evidence_waiting_result))
                ),
                ctx.getString(R.string.trait_patience_exercise)));
        list.add(new Trait("courage", ctx.getString(R.string.trait_courage_name), ContextCompat.getColor(ctx, R.color.trait_courage), 6,
                ctx.getString(R.string.trait_courage_quote), false,
                Arrays.asList(new TraitEvidence(ctx.getString(R.string.evidence_yesterday), ctx.getString(R.string.evidence_future_fears))),
                ctx.getString(R.string.trait_courage_exercise)));
        list.add(new Trait("empathy", ctx.getString(R.string.trait_empathy_name), ContextCompat.getColor(ctx, R.color.trait_empathy), 5,
                ctx.getString(R.string.trait_empathy_quote), false,
                Arrays.asList(new TraitEvidence(ctx.getString(R.string.evidence_today), ctx.getString(R.string.evidence_friend_distress))),
                ctx.getString(R.string.trait_empathy_exercise)));
        return list;
    }

    public static List<FutureMessage> getSeedMessages(Context ctx) {
        Date today0 = new Date();
        List<FutureMessage> list = new ArrayList<>();
        list.add(new FutureMessage(1, ctx.getString(R.string.message_1_text), today0, true));
        list.add(new FutureMessage(2, ctx.getString(R.string.message_2_text),
                ArabicDateUtils.addDays(today0, 23), false));
        list.add(new FutureMessage(3, ctx.getString(R.string.message_3_text),
                ArabicDateUtils.addDays(today0, 210), false));
        return list;
    }

    public static List<ChildProfile> getInitialChildren(Context ctx) {
        List<ChildProfile> list = new ArrayList<>();
        list.add(new ChildProfile(1, ctx.getString(R.string.child_youssef), 10, "🧒"));
        list.add(new ChildProfile(2, ctx.getString(R.string.child_sara), 6, "👧"));
        return list;
    }

    public static List<Badge> getBadges() {
        List<Badge> list = new ArrayList<>();
        list.add(new Badge(R.string.badge_week_label, R.drawable.ic_flame, true, R.color.sand));
        list.add(new Badge(R.string.badge_ideas_label, R.drawable.ic_pen_line, true, R.color.primary));
        list.add(new Badge(R.string.badge_traits_label, R.drawable.ic_heart, true, R.color.pink));
        list.add(new Badge(R.string.badge_month_label, R.drawable.ic_calendar, false, R.color.sage));
        list.add(new Badge(R.string.badge_message_label, R.drawable.ic_mail, true, R.color.purple));
        list.add(new Badge(R.string.badge_body_label, R.drawable.ic_sparkles, false, R.color.primary));
        return list;
    }

    // ---------------------------------------------------------------
    // Child detail dashboards (mirrors CHILD_DATA)
    // ---------------------------------------------------------------
    public static ChildDetail getChildDetail(Context ctx, long id) {
        // Day abbreviations should also be localized if we want full LTR/RTL support
        // But for now let's keep them as is or use localized names.
        String[] days = ctx.getResources().getStringArray(R.array.day_abbreviations);

        if (id == 1) {
            int color = android.graphics.Color.parseColor("#5B8FD1");
            return new ChildDetail(
                    1, ctx.getString(R.string.child_youssef), 10, "🧒", color, ctx.getString(R.string.child_detail_active_today),
                    new ChildStats(12, 18, 0),
                    new int[]{ 3, 4, 2, 5, 4, 6, 5 }, days,
                    Arrays.asList(
                            new ChildFeature(ctx.getString(R.string.feature_safe_room), 18, R.drawable.ic_home),
                            new ChildFeature(ctx.getString(R.string.feature_breathing), 12, R.drawable.ic_wind),
                            new ChildFeature(ctx.getString(R.string.feature_calm_env), 7, R.drawable.ic_sparkles)
                    ),
                    new ChildAlert(
                            ctx.getString(R.string.alert_youssef_title),
                            ctx.getString(R.string.alert_youssef_body),
                            ctx.getString(R.string.alert_youssef_note)
                    ),
                    Arrays.asList(
                            ctx.getString(R.string.recommend_youssef_1),
                            ctx.getString(R.string.recommend_youssef_2),
                            ctx.getString(R.string.recommend_youssef_3)
                    ),
                    Arrays.asList(
                            new ChildHistoryEntry(ctx.getString(R.string.history_3_days_ago), ctx.getString(R.string.alert_youssef_title)),
                            new ChildHistoryEntry(ctx.getString(R.string.evidence_last_week), ctx.getString(R.string.history_no_patterns))
                    )
            );
        }

        if (id == 2) {
            int color = android.graphics.Color.parseColor("#E0668A");
            return new ChildDetail(
                    2, ctx.getString(R.string.child_sara), 6, "👧", color, ctx.getString(R.string.child_detail_active_yesterday),
                    new ChildStats(6, 9, 1),
                    new int[]{ 5, 6, 6, 4, 6, 7, 6 }, days,
                    Arrays.asList(
                            new ChildFeature(ctx.getString(R.string.feature_recovery_tree), 9, R.drawable.ic_sparkles),
                            new ChildFeature(ctx.getString(R.string.feature_dandelion), 8, R.drawable.ic_wind),
                            new ChildFeature(ctx.getString(R.string.feature_treasure_box), 4, R.drawable.ic_home)
                    ),
                    null, // no alert -> "no patterns to worry about" state
                    Arrays.asList(
                            ctx.getString(R.string.recommend_sara_1),
                            ctx.getString(R.string.recommend_sara_2)
                    ),
                    Arrays.asList(
                            new ChildHistoryEntry(ctx.getString(R.string.history_2_weeks_ago), ctx.getString(R.string.history_no_patterns))
                    )
            );
        }

        return null;
    }
}
