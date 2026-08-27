package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.R;
import com.example.graduationproject.models.AdultOnboarding.OnboardingData;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.FlowLayout;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TimelineFragment extends BaseScreenFragment {

    private static final Map<String, Integer> SCENE_RES = new LinkedHashMap<>();
    static {
        SCENE_RES.put("morning", R.drawable.scene_morning);
        SCENE_RES.put("day", R.drawable.scene_sun);
        SCENE_RES.put("evening", R.drawable.scene_evening);
        SCENE_RES.put("night", R.drawable.scene_moon);
    }

    private LinearLayout followupsContainer;

    @Override protected int getScreenIndex() { return 7; }
    @Override protected String getCompanionMood() { return CompanionView.MOOD_NEUTRAL; }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        addToContent(content, Widgets.heading(requireContext(), getString(R.string.adaptive_adult_onboarding_timeline_title), Color.WHITE), 4);

        GridLayout grid = new GridLayout(requireContext());
        grid.setColumnCount(2);
        grid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        grid.setUseDefaultMargins(false); // We set them manually
        addToContent(content, grid, 10);

        followupsContainer = new LinearLayout(requireContext());
        followupsContainer.setOrientation(LinearLayout.VERTICAL);
        addToContent(content, followupsContainer, 4);

        renderGrid(grid);
        renderFollowups();
    }

    private void renderGrid(GridLayout grid) {
        grid.removeAllViews();
        for (String id : AdultOnboardingAppData.TIME_PERIOD_IDS) {
            boolean selected = data.difficultTimes.contains(id);
            android.view.View card = Widgets.timePeriodCard(requireContext(), SCENE_RES.get(id), getString(AdultOnboardingAppData.TIME_PERIOD_LABELS.get(id)), selected, () -> {
                OnboardingData.toggle(data.difficultTimes, id);
                renderGrid(grid);
                renderFollowups();
            });
            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = 0;
            glp.height = GridLayout.LayoutParams.WRAP_CONTENT;
            glp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            glp.setMargins(dp(6), dp(6), dp(6), dp(6));
            grid.addView(card, glp);
        }
    }

    private void renderFollowups() {
        followupsContainer.removeAllViews();
        for (String id : AdultOnboardingAppData.TIME_PERIOD_IDS) {
            if (!data.difficultTimes.contains(id)) continue;
            AdultOnboardingAppData.Followup fu = AdultOnboardingAppData.FOLLOWUPS.get(id);
            if (fu == null) continue;

            LinearLayout box = new LinearLayout(requireContext());
            box.setOrientation(LinearLayout.VERTICAL);
            box.setBackgroundResource(R.drawable.bg_followup_box);
            box.setPadding(dp(14), dp(14), dp(14), dp(14));
            LinearLayout.LayoutParams boxLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            boxLp.topMargin = dp(6);
            followupsContainer.addView(box, boxLp);

            TextView prompt = new TextView(requireContext());
            prompt.setText(fu.promptRes);
            prompt.setTextColor(AdultOnboardingAppData.INK); // Changed to ink
            prompt.setTextSize(15);
            prompt.setTypeface(com.example.graduationproject.AdultOnboardingUiUtils.tajawal(true));
            box.addView(prompt);

            FlowLayout flow = new FlowLayout(requireContext());
            LinearLayout.LayoutParams flowLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            flowLp.topMargin = dp(10);
            box.addView(flow, flowLp);

            List<String> selectedList = getFieldList(fu.field);
            for (int optRes : fu.optionsRes) {
                String opt = getString(optRes);
                boolean sel = selectedList.contains(opt);
                flow.addView(Widgets.darkPill(requireContext(), opt, sel, () -> {
                    OnboardingData.toggle(selectedList, opt);
                    renderFollowups();
                }));
            }
        }
    }

    private List<String> getFieldList(String field) {
        switch (field) {
            case "morningFeelings": return data.morningFeelings;
            case "dayFeelings": return data.dayFeelings;
            case "eveningFeelings": return data.eveningFeelings;
            case "nightFeelings": return data.nightFeelings;
            default: return data.dayFeelings;
        }
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.primaryButton(requireContext(), getString(R.string.adaptive_adult_onboarding_continue), false, () -> host.goNext()));
    }
}
