package com.example.graduationproject.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.KidsAdaptiveOnboardingHost;
import com.example.graduationproject.R;
import com.example.graduationproject.models.KidsAdaptiveOnboardingData;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Mirrors <HomePreview>: the simple "app home" landing shown after onboarding completes. */
public class KidsAdaptiveHomeFragment extends Fragment {

    private KidsAdaptiveOnboardingHost host;

    private static final Map<String, String[]> HELPFUL_ITEMS = new LinkedHashMap<>();
    static {
        HELPFUL_ITEMS.put("audio", new String[]{"🎧", "أسمع أغاني أو قصص"});
        HELPFUL_ITEMS.put("breathing", new String[]{"🫁", "أتنفس بهدوء"});
        HELPFUL_ITEMS.put("spiritual", new String[]{"🕌", "أذكار أو دعاء"});
        HELPFUL_ITEMS.put("writing", new String[]{"✍️", "أرسم أو أكتب"});
        HELPFUL_ITEMS.put("talking", new String[]{"💬", "أحكي مع حد أحبه"});
        HELPFUL_ITEMS.put("movement", new String[]{"🚶", "ألعب أو أتحرك"});
        HELPFUL_ITEMS.put("activity", new String[]{"🎮", "لعبة بسيطة"});
        HELPFUL_ITEMS.put("unsure", new String[]{"😶", "ما أدري إيش يساعدني"});
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof KidsAdaptiveOnboardingHost) host = (KidsAdaptiveOnboardingHost) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundResource(R.drawable.kids_adaptive_bg_home_gradient);
        int padH = dp(22), padTop = dp(26), padBottom = dp(22);
        root.setPadding(padH, padTop, padH, padBottom);
        root.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in));

        KidsAdaptiveOnboardingData data = host.getData();

        // Greeting row
        LinearLayout greetingRow = new LinearLayout(requireContext());
        greetingRow.setOrientation(LinearLayout.HORIZONTAL);
        greetingRow.setGravity(Gravity.CENTER_VERTICAL);
        root.addView(greetingRow, matchWrap());

        KidsAdaptiveTeddyBuddyView avatar = new KidsAdaptiveTeddyBuddyView(requireContext());
        avatar.setReducedMotion(host.isReducedMotion());
        avatar.setMood(KidsAdaptiveTeddyBuddyView.MOOD_WARM);
        LinearLayout.LayoutParams avatarLp = new LinearLayout.LayoutParams(dp(48), dp(48));
        avatarLp.setMarginEnd(dp(10));
        greetingRow.addView(avatar, avatarLp);

        LinearLayout greetingCol = new LinearLayout(requireContext());
        greetingCol.setOrientation(LinearLayout.VERTICAL);
        greetingRow.addView(greetingCol, wrap());

        TextView hi = new TextView(requireContext());
        hi.setText(getString(R.string.kids_adaptive_home_greeting_label));
        hi.setTextSize(13);
        hi.setTypeface(KidsAdaptiveTypefaces.body(requireContext()));
        hi.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        hi.setAlpha(0.6f);
        greetingCol.addView(hi);

        TextView name = new TextView(requireContext());
        String nickname = data.nickname;
        name.setText(nickname != null && !nickname.trim().isEmpty() ? nickname.trim() : getString(R.string.kids_adaptive_home_default_name));
        name.setTextSize(18);
        name.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()), Typeface.BOLD);
        name.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        greetingCol.addView(name);

        // Suggestion card
        LinearLayout suggestionCard = new LinearLayout(requireContext());
        suggestionCard.setOrientation(LinearLayout.VERTICAL);
        suggestionCard.setBackgroundResource(R.drawable.kids_adaptive_bg_card_white);
        int scPad = dp(18);
        suggestionCard.setPadding(scPad, scPad, scPad, scPad);
        LinearLayout.LayoutParams scLp = matchWrap(); scLp.topMargin = dp(18);
        root.addView(suggestionCard, scLp);

        TextView suggestionTitle = new TextView(requireContext());
        suggestionTitle.setText(getString(R.string.kids_adaptive_home_suggested_now));
        suggestionTitle.setTextSize(14);
        suggestionTitle.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()));
        suggestionTitle.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        LinearLayout.LayoutParams stlp = matchWrap(); stlp.bottomMargin = dp(10);
        suggestionCard.addView(suggestionTitle, stlp);

        LinearLayout tilesRow = new LinearLayout(requireContext());
        tilesRow.setOrientation(LinearLayout.HORIZONTAL);
        suggestionCard.addView(tilesRow, matchWrap());

        List<String> ids = new ArrayList<>(data.helpfulActivities);
        if (ids.isEmpty()) ids.add("breathing");
        for (int i = 0; i < Math.min(3, ids.size()); i++) {
            String id = ids.get(i);
            String[] item = HELPFUL_ITEMS.containsKey(id) ? HELPFUL_ITEMS.get(id) : HELPFUL_ITEMS.get("breathing");

            LinearLayout tile = new LinearLayout(requireContext());
            tile.setOrientation(LinearLayout.VERTICAL);
            tile.setGravity(Gravity.CENTER);
            tile.setBackgroundResource(R.drawable.kids_adaptive_bg_home_tile);
            int tp = dp(14);
            tile.setPadding(tp, tp, tp, tp);
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tlp.setMargins(dp(4), 0, dp(4), 0);
            tilesRow.addView(tile, tlp);

            TextView emoji = new TextView(requireContext());
            emoji.setText(item[0]);
            emoji.setTextSize(24);
            emoji.setGravity(Gravity.CENTER);
            tile.addView(emoji);

            TextView label = new TextView(requireContext());
            label.setText(item[1]);
            label.setTextSize(11);
            label.setTypeface(KidsAdaptiveTypefaces.body(requireContext()));
            label.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
            label.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams llp = matchWrap(); llp.topMargin = dp(6);
            tile.addView(label, llp);
        }

        // Restart link, pinned to bottom
        LinearLayout spacer = new LinearLayout(requireContext());
        LinearLayout.LayoutParams spacerLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        root.addView(spacer, spacerLp);

        TextView restart = new TextView(requireContext());
        restart.setText(getString(R.string.kids_adaptive_home_restart));
        restart.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        restart.setAlpha(0.6f);
        restart.setTextSize(14);
        restart.setGravity(Gravity.CENTER);
        restart.setPaintFlags(restart.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        restart.setPadding(dp(6), dp(6), dp(6), dp(6));
        restart.setOnClickListener(v -> {
            // KidsAdaptiveMainActivity exposes restart via a package-visible cast below.
            if (getActivity() instanceof com.example.graduationproject.KidsAdaptiveMainActivity) {
                ((com.example.graduationproject.KidsAdaptiveMainActivity) getActivity()).restart();
            }
        });
        LinearLayout.LayoutParams rlp = matchWrap();
        root.addView(restart, rlp);

        return root;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private LinearLayout.LayoutParams wrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density); }
}
