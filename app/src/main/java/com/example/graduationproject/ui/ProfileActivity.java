package com.example.graduationproject.ui;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.graduationproject.R;
import com.example.graduationproject.models.ArchiveItem;
import com.example.graduationproject.models.Badge;
import com.example.graduationproject.models.ProfileRepository;
import com.example.graduationproject.models.StatItem;

import java.util.List;

/**
 * Full Java/Android port of "JourneyProfileScreen": identity card with
 * editable avatar color + nickname, a privacy notice, quick stats,
 * an achievements grid, and static archive/parent quick-link rows.
 */
public class ProfileActivity extends AppCompatActivity {

    // ------- state (mirrors the React useState hooks) -------
    private int avatarColor;
    private String name = "سارة";

    private List<Badge> badges;

    // ------- views -------
    private View avatarBg;
    private TextView tvAvatarInitial, tvName, tvEarnedCount;
    private LinearLayout llQuickStats, llBadgesGrid, llArchiveLinks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ضبط لون شريط الحالة ليتناسق مع واجهة الملف الشخصي (أزرق فاتح)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.profile_bg));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        setContentView(R.layout.activity_profile);

        avatarColor = getColor(R.color.primary);
        badges = ProfileRepository.badges(this);

        bindViews();
        setListeners();

        renderIdentity();
        buildQuickStats();
        buildBadgesGrid();
        buildArchiveLinks();

        getSupportFragmentManager().setFragmentResultListener(
                NameDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    name = bundle.getString(NameDialogFragment.KEY_NAME);
                    renderIdentity();
                });

        getSupportFragmentManager().setFragmentResultListener(
                AvatarPickerDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    avatarColor = bundle.getInt(AvatarPickerDialogFragment.KEY_COLOR);
                    renderIdentity();
                });
    }

    private void bindViews() {
        avatarBg = findViewById(R.id.avatarBg);
        tvAvatarInitial = findViewById(R.id.tvAvatarInitial);
        tvName = findViewById(R.id.tvName);
        tvEarnedCount = findViewById(R.id.tvEarnedCount);
        llQuickStats = findViewById(R.id.llQuickStats);
        llBadgesGrid = findViewById(R.id.llBadgesGrid);
        llArchiveLinks = findViewById(R.id.llArchiveLinks);
    }

    private void setListeners() {
        findViewById(R.id.btnAvatar).setOnClickListener(v ->
                AvatarPickerDialogFragment.newInstance(avatarColor)
                        .show(getSupportFragmentManager(), "avatar_picker"));

        findViewById(R.id.btnEditName).setOnClickListener(v ->
                NameDialogFragment.newInstance(name)
                        .show(getSupportFragmentManager(), "edit_name"));
    }

    // ===================== IDENTITY =====================

    private void renderIdentity() {
        // Equivalent of `linear-gradient(160deg, ${avatarColor}, ${avatarColor}cc)` -
        // a diagonal gradient from the full color to the same color at ~80% alpha.
        int semiTransparent = (avatarColor & 0x00FFFFFF) | 0xCC000000;
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, new int[]{avatarColor, semiTransparent});
        gradient.setShape(GradientDrawable.OVAL);
        avatarBg.setBackground(gradient);

        tvAvatarInitial.setText(name.isEmpty() ? "" : name.substring(0, 1));
        tvName.setText(name);
    }

    // ===================== QUICK STATS =====================

    private void buildQuickStats() {
        llQuickStats.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        List<StatItem> stats = ProfileRepository.quickStats(this);

        for (StatItem stat : stats) {
            View item = LayoutInflater.from(this).inflate(R.layout.item_stat, llQuickStats, false);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) item.getLayoutParams();
            lp.setMarginStart((int) (4 * density));
            lp.setMarginEnd((int) (4 * density));
            item.setLayoutParams(lp);

            ((TextView) item.findViewById(R.id.tvStatValue)).setText(stat.value);
            ((TextView) item.findViewById(R.id.tvStatLabel)).setText(stat.label);
            llQuickStats.addView(item);
        }
    }

    // ===================== ACHIEVEMENTS =====================

    private void buildBadgesGrid() {
        int earnedCount = 0;
        for (Badge b : badges) if (b.earned) earnedCount++;
        tvEarnedCount.setText(getString(R.string.earned_count_format, earnedCount, badges.size()));

        llBadgesGrid.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        int gutter = (int) (5 * density);

        for (int i = 0; i < badges.size(); i += 3) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowLp.bottomMargin = gutter * 2;
            row.setLayoutParams(rowLp);

            for (int j = i; j < Math.min(i + 3, badges.size()); j++) {
                Badge badge = badges.get(j);
                View cell = LayoutInflater.from(this).inflate(R.layout.item_badge, row, false);
                LinearLayout.LayoutParams cellLp = new LinearLayout.LayoutParams(0, 0, 1f);
                cellLp.height = (int) (100 * density); // square-ish aspect, matches `aspect-square`
                cellLp.setMarginStart(gutter);
                cellLp.setMarginEnd(gutter);
                cell.setLayoutParams(cellLp);

                ImageView ivIcon = cell.findViewById(R.id.ivBadgeIcon);
                TextView tvLabel = cell.findViewById(R.id.tvBadgeLabel);

                ivIcon.setImageResource(badge.iconRes);
                ivIcon.setColorFilter(badge.earned ? badge.colorInt : getColor(R.color.text_soft));
                tvLabel.setText(badge.label);
                cell.setAlpha(badge.earned ? 1f : 0.45f);

                cell.setOnClickListener(v ->
                        BadgeDialogFragment.newInstance(badge.id).show(getSupportFragmentManager(), "badge"));

                row.addView(cell);
            }

            llBadgesGrid.addView(row);
        }
    }

    /** Looks up a badge by id - used by {@link BadgeDialogFragment}. */
    public Badge findBadge(int id) {
        for (Badge b : badges) if (b.id == id) return b;
        return null;
    }

    // ===================== ARCHIVE LINKS =====================

    private void buildArchiveLinks() {
        llArchiveLinks.removeAllViews();
        List<ArchiveItem> items = ProfileRepository.archiveItems(this);

        for (ArchiveItem item : items) {
            View row = LayoutInflater.from(this).inflate(R.layout.item_archive_link, llArchiveLinks, false);

            ((TextView) row.findViewById(R.id.txt_link_label)).setText(item.label);
            ((TextView) row.findViewById(R.id.txt_link_sub)).setText(item.sub);

            ImageView ivIcon = row.findViewById(R.id.img_link_icon_bg);
            ivIcon.setImageResource(item.iconRes);
            ivIcon.setColorFilter(item.colorInt);

            View iconBg = row.findViewById(R.id.img_link_icon_bg);
            iconBg.getBackground().mutate().setTint(withAlpha(item.colorInt, 0x18));

            // Matches the original: these rows have no onClick handler attached.
            llArchiveLinks.addView(row);
        }
    }

    private int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }
}
