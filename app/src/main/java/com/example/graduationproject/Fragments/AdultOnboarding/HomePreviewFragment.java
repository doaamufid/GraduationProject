package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.AdultOnboardingHost;
import com.example.graduationproject.R;
import com.example.graduationproject.AdultOnboardingUiUtils;
import com.example.graduationproject.models.AdultOnboarding.Option;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

/**
 * Minimal placeholder "home" screen shown right after onboarding completes Ã¢â‚¬â€
 * mirrors the React <HomePreview/>: greeting, companion, and a row of
 * suggested activities derived from what the user picked earlier.
 */
public class HomePreviewFragment extends Fragment {

    private AdultOnboardingHost host;

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        host = (AdultOnboardingHost) requireActivity();
    }

    private int dp(int v) { return AdultOnboardingUiUtils.dp(requireContext(), v); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundResource(R.drawable.bg_home_gradient);
        root.setPadding(dp(22), dp(26), dp(22), dp(26));

        LinearLayout header = new LinearLayout(requireContext());
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        CompanionView companion = new CompanionView(requireContext());
        companion.setMood(CompanionView.MOOD_WARM);
        companion.setReducedMotion(host.isReducedMotion());
        LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(dp(44), dp(44));
        cLp.setMarginEnd(dp(10));
        header.addView(companion, cLp);

        LinearLayout texts = new LinearLayout(requireContext());
        texts.setOrientation(LinearLayout.VERTICAL);
        TextView greetTime = new TextView(requireContext());
        greetTime.setText(R.string.adaptive_adult_onboarding_home_good_evening);
        greetTime.setTextColor(AdultOnboardingAppData.INK);
        greetTime.setAlpha(0.65f);
        greetTime.setTextSize(13);
        texts.addView(greetTime);

        String name = host.getData().nickname;
        TextView greetName = new TextView(requireContext());
        greetName.setText((name != null && !name.trim().isEmpty()) ? name.trim() : getString(R.string.adaptive_adult_onboarding_home_salam_friend));
        greetName.setTextColor(AdultOnboardingAppData.INK);
        greetName.setTextSize(18);
        greetName.setTypeface(AdultOnboardingUiUtils.cairo(true));
        texts.addView(greetName);

        header.addView(texts);
        root.addView(header);

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setCornerRadius(dp(20));
        cardBg.setColor(Color.argb(153, 255, 255, 255));
        card.setBackground(cardBg);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLp.topMargin = dp(18);
        root.addView(card, cardLp);

        TextView suggestTitle = new TextView(requireContext());
        suggestTitle.setText(R.string.adaptive_adult_onboarding_home_suggested);
        suggestTitle.setTextColor(AdultOnboardingAppData.INK);
        suggestTitle.setTextSize(14);
        LinearLayout.LayoutParams stLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        stLp.bottomMargin = dp(10);
        card.addView(suggestTitle, stLp);

        LinearLayout tilesRow = new LinearLayout(requireContext());
        tilesRow.setOrientation(LinearLayout.HORIZONTAL);
        card.addView(tilesRow, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        java.util.List<String> chosen = host.getData().helpfulActivities;
        java.util.List<String> ids = new java.util.ArrayList<>(chosen.isEmpty() ? java.util.Collections.singletonList("breathing") : chosen);
        int shown = 0;
        for (String id : ids) {
            if (shown >= 3) break;
            Option item = findHelpful(id);
            if (item == null) continue;
            LinearLayout tile = new LinearLayout(requireContext());
            tile.setOrientation(LinearLayout.VERTICAL);
            tile.setGravity(Gravity.CENTER);
            tile.setBackgroundResource(R.drawable.bg_home_tile);
            tile.setPadding(dp(6), dp(14), dp(6), dp(14));
            LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tLp.setMargins(dp(5), 0, dp(5), 0);

            TextView emoji = new TextView(requireContext());
            emoji.setText(item.emoji);
            emoji.setTextSize(22);
            emoji.setGravity(Gravity.CENTER);
            tile.addView(emoji);

            TextView label = new TextView(requireContext());
            label.setText(item.labelRes);
            label.setTextColor(AdultOnboardingAppData.INK);
            label.setTextSize(11.5f);
            label.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lLp.topMargin = dp(6);
            tile.addView(label, lLp);

            tilesRow.addView(tile, tLp);
            shown++;
        }

        View spacer = new View(requireContext());
        LinearLayout.LayoutParams spacerLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        root.addView(spacer, spacerLp);

        LinearLayout restartWrap = new LinearLayout(requireContext());
        restartWrap.setGravity(Gravity.CENTER);
        restartWrap.addView(Widgets.textLink(requireContext(), getString(R.string.adaptive_adult_onboarding_home_replay), AdultOnboardingAppData.INK, () -> host.restartOnboarding()));
        root.addView(restartWrap);

        return root;
    }

    private Option findHelpful(String id) {
        for (Option o : AdultOnboardingAppData.HELPFUL) if (o.id.equals(id)) return o;
        return AdultOnboardingAppData.HELPFUL[1];
    }
}
