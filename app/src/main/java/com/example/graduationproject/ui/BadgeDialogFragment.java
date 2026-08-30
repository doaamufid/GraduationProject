package com.example.graduationproject.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Badge;

/**
 * Equivalent of &lt;BadgeDialog/&gt;: a centered card showing the badge's
 * icon, label, and either "earned" celebration text or the requirement
 * needed to unlock it.
 */
public class BadgeDialogFragment extends DialogFragment {

    private static final String ARG_BADGE_ID = "badge_id";

    public static BadgeDialogFragment newInstance(int badgeId) {
        BadgeDialogFragment fragment = new BadgeDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BADGE_ID, badgeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_badge, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        int badgeId = getArguments() != null ? getArguments().getInt(ARG_BADGE_ID, -1) : -1;
        Badge badge = (getActivity() instanceof ProfileActivity) ? ((ProfileActivity) getActivity()).findBadge(badgeId) : null;
        if (badge == null) {
            dismiss();
            return root;
        }

        View badgeIconBg = root.findViewById(R.id.badgeIconBg);
        ImageView ivIcon = root.findViewById(R.id.ivBadgeIcon);
        TextView tvLabel = root.findViewById(R.id.tvBadgeLabel);
        TextView tvStatus = root.findViewById(R.id.tvBadgeStatus);

        int bgAlpha = badge.earned ? 0x22 : 0x12;
        badgeIconBg.getBackground().mutate().setTint(withAlpha(badge.colorInt, bgAlpha));

        ivIcon.setImageResource(badge.iconRes);
        ivIcon.setColorFilter(badge.earned ? badge.colorInt : getResources().getColor(R.color.text_soft));

        tvLabel.setText(badge.label);
        tvStatus.setText(badge.earned
                ? getString(R.string.badge_earned_status)
                : getString(R.string.badge_need_format, badge.need));

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        return root;
    }

    private int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

//    @Override
//    public int getTheme() {
//        return R.style.Theme_JourneyProfile_Dialog;
//    }
}
