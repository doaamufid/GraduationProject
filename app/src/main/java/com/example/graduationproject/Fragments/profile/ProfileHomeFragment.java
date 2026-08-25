
package com.example.graduationproject.Fragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.ProfileNavigator;
import com.example.graduationproject.R;
import com.example.graduationproject.SettingsActivity;
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
        
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().finish());

        ImageButton btnSettings = view.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(v -> startActivity(new Intent(requireContext(), SettingsActivity.class)));

        String name = getString(R.string.child_sara);
        TextView txtAvatarInitial = view.findViewById(R.id.txt_avatar_initial);
        TextView txtProfileName = view.findViewById(R.id.txt_profile_name);
        txtAvatarInitial.setText(name.substring(0, 1));
        txtProfileName.setText(name);

        renderBadges(view);

        View btnReports = view.findViewById(R.id.btn_reports_link);
        btnReports.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.graduationproject.AdultMoodStatsActivity.class));
        });
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

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
