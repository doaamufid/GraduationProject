
package com.example.graduationproject.Fragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.ArticlesActivity;
import com.example.graduationproject.ProfileNavigator;
import com.example.graduationproject.R;
import com.example.graduationproject.SettingsActivity;
import com.example.graduationproject.VideoLibraryActivity;
import com.example.graduationproject.data.profile.ArabicDateUtils;
import com.example.graduationproject.data.profile.SeedData;
import com.example.graduationproject.models.profile.Badge;

import java.util.List;

/**
 * Mirrors <ProfileHome/>.
 */
public class ProfileHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileNavigator activity = (ProfileNavigator) requireActivity();

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().finish());

        ImageButton btnSettings = view.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(v -> startActivity(new Intent(requireContext(), SettingsActivity.class)));

        String name = getString(R.string.child_sara);
        TextView txtAvatarInitial = view.findViewById(R.id.txt_avatar_initial);
        TextView txtProfileName = view.findViewById(R.id.txt_profile_name);
        txtAvatarInitial.setText(name.substring(0, 1));
        txtProfileName.setText(name);

        renderStats(view);
        renderBadges(view);
        renderArchiveLinks(view, activity);
        renderBookmarkLinks(view);

        LinearLayout btnChildren = view.findViewById(R.id.btn_children_link);
        btnChildren.setOnClickListener(v -> activity.navigate("children"));
    }

    private void renderStats(View root) {
        LinearLayout statsRow = root.findViewById(R.id.stats_row);
        statsRow.removeAllViews();
        String[][] stats = { 
                { ArabicDateUtils.toAr(47), getString(R.string.stat_days_active) },
                { ArabicDateUtils.toAr(12), getString(R.string.stat_streak) },
                { ArabicDateUtils.toAr(69), getString(R.string.stat_sessions) } 
        };

        for (String[] stat : stats) {
            View box = LayoutInflater.from(requireContext()).inflate(R.layout.item_stat_box, statsRow, false);
            ((TextView) box.findViewById(R.id.txt_stat_number)).setText(stat[0]);
            ((TextView) box.findViewById(R.id.txt_stat_label)).setText(stat[1]);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) box.getLayoutParams();
            lp.setMarginEnd(dp(6));
            box.setLayoutParams(lp);
            statsRow.addView(box);
        }
    }

    private void renderBadges(View root) {
        GridLayout grid = root.findViewById(R.id.badges_grid);
        grid.removeAllViews();
        List<Badge> badges = SeedData.getBadges();

        int earned = 0;
        for (Badge b : badges) if (b.earned) earned++;
        ((TextView) root.findViewById(R.id.txt_badges_count)).setText(earned + "/" + badges.size());

        int screenWidthDp = 350; // approximate content width inside 20dp padding on a ~390dp phone
        int cellSize = dp((screenWidthDp - 2 * 8) / 3);

        for (Badge b : badges) {
            View tile = LayoutInflater.from(requireContext()).inflate(R.layout.item_badge, grid, false);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = cellSize;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(dp(4), dp(4), dp(4), dp(4));
            tile.setLayoutParams(lp);

            ImageView icon = tile.findViewById(R.id.img_badge_icon);
            TextView label = tile.findViewById(R.id.txt_badge_label);
            icon.setImageResource(b.iconRes);
            label.setText(b.labelRes);

            int color = ContextCompat.getColor(requireContext(), b.earned ? b.colorRes : R.color.text_soft);
            icon.setColorFilter(color);
            tile.setAlpha(b.earned ? 1f : 0.45f);

            grid.addView(tile);
        }
    }

    private void renderArchiveLinks(View root, ProfileNavigator activity) {
        LinearLayout container = root.findViewById(R.id.archive_links_container);
        container.removeAllViews();

        Object[][] links = {
                { "thoughts", R.string.link_thoughts_label, R.string.link_thoughts_sub, R.drawable.ic_pen_line, R.color.primary },
                { "strengths", R.string.link_strengths_label, R.string.link_strengths_sub, R.drawable.ic_heart, R.color.pink },
                { "messages", R.string.link_messages_label, R.string.link_messages_sub, R.drawable.ic_mail, R.color.purple },
        };

        for (Object[] link : links) {
            String key = (String) link[0];
            int labelRes = (int) link[1];
            int subRes = (int) link[2];
            int iconRes = (int) link[3];
            int colorRes = (int) link[4];

            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_archive_link, container, false);
            ((TextView) row.findViewById(R.id.txt_link_label)).setText(labelRes);
            ((TextView) row.findViewById(R.id.txt_link_sub)).setText(subRes);

            ImageView iconBg = row.findViewById(R.id.img_link_icon_bg);
            iconBg.setImageResource(iconRes);
            int color = ContextCompat.getColor(requireContext(), colorRes);
            iconBg.setColorFilter(color);
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setColor((color & 0x00FFFFFF) | 0x18000000);
            bg.setCornerRadius(dp(12));
            iconBg.setBackground(bg);

            row.setOnClickListener(v -> {
                animateTap(row);
                activity.navigate(key);
            });
            container.addView(row);
        }
    }

    /** Saved-content shortcuts: article bookmarks + video bookmarks. */
    private void renderBookmarkLinks(View root) {
        LinearLayout container = root.findViewById(R.id.bookmark_links_container);
        container.removeAllViews();

        Object[][] links = {
                { "articles", R.string.bookmark_articles_title, R.string.bookmark_articles_sub, R.drawable.ic_bookmark, R.color.primary },
                { "videos", R.string.bookmark_videos_title, R.string.bookmark_videos_sub, R.drawable.ic_bookmark_filled, R.color.sage },
        };

        for (Object[] link : links) {
            String key = (String) link[0];
            int labelRes = (int) link[1];
            int subRes = (int) link[2];
            int iconRes = (int) link[3];
            int colorRes = (int) link[4];

            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_archive_link, container, false);
            ((TextView) row.findViewById(R.id.txt_link_label)).setText(labelRes);
            ((TextView) row.findViewById(R.id.txt_link_sub)).setText(subRes);

            ImageView iconBg = row.findViewById(R.id.img_link_icon_bg);
            iconBg.setImageResource(iconRes);
            int color = ContextCompat.getColor(requireContext(), colorRes);
            iconBg.setColorFilter(color);
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setColor((color & 0x00FFFFFF) | 0x18000000);
            bg.setCornerRadius(dp(12));
            iconBg.setBackground(bg);

            row.setOnClickListener(v -> {
                animateTap(row);
                openBookmarks("articles".equals(key));
            });
            container.addView(row);
        }
    }

    private void openBookmarks(boolean articles) {
        Intent intent = new Intent(requireContext(),
                articles ? ArticlesActivity.class : VideoLibraryActivity.class);
        intent.putExtra(articles ? ArticlesActivity.EXTRA_OPEN : VideoLibraryActivity.EXTRA_OPEN,
                "bookmarks");
        startActivity(intent);
    }

    /** Mirrors .archive-tap:active { transform: scale(0.97) } */
    private void animateTap(View v) {
        v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).withEndAction(() ->
                v.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
        ).start();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
