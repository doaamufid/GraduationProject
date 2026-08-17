package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.MainActivity;
import com.example.graduationproject.R;

/**
 * Mirrors RejectedScreen. reasonKey is one of "crisis" | "contact" | "negative" | "short"
 * as returned by Moderation.moderate(...), mapped here to the matching string resource.
 */
public class RejectedFragment extends Fragment {

    private static final String ARG_REASON_KEY = "reason_key";
    private static final String ARG_CRISIS = "crisis";

    public static RejectedFragment newInstance(String reasonKey, boolean crisis) {
        RejectedFragment f = new RejectedFragment();
        Bundle b = new Bundle();
        b.putString(ARG_REASON_KEY, reasonKey);
        b.putBoolean(ARG_CRISIS, crisis);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rejected, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = (MainActivity) requireActivity();

        String reasonKey = getArguments() != null ? getArguments().getString(ARG_REASON_KEY) : "short";
        boolean crisis = getArguments() != null && getArguments().getBoolean(ARG_CRISIS);

        ImageView icon = view.findViewById(R.id.rejectedIcon);
        TextView title = view.findViewById(R.id.rejectedTitle);
        TextView reason = view.findViewById(R.id.rejectedReason);
        Button primaryBtn = view.findViewById(R.id.rejectedPrimaryBtn);
        Button backBtn = view.findViewById(R.id.rejectedBackBtn);

        icon.setColorFilter(ContextCompat.getColor(requireContext(), crisis ? R.color.primary : R.color.sand));
        title.setText(crisis ? R.string.rejected_title_crisis : R.string.rejected_title_normal);
        reason.setText(resolveReason(reasonKey));

        if (crisis) {
            primaryBtn.setText(R.string.rejected_cta_crisis);
            primaryBtn.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().popBackStack(null,
                        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                activity.showWall(false);
            });
        } else {
            primaryBtn.setText(R.string.rejected_cta_edit);
            primaryBtn.setOnClickListener(v -> goBackToCompose(activity));
        }

        backBtn.setOnClickListener(v -> goBackToCompose(activity));
    }

    private void goBackToCompose(MainActivity activity) {
        // returns to the Compose screen so the person can edit their draft, mirrors onEdit
        requireActivity().getSupportFragmentManager().popBackStack();
        if (requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) == this) {
            activity.showCompose();
        }
    }

    private int resolveReason(String key) {
        if (key == null) return R.string.reason_short;
        switch (key) {
            case "crisis":
                return R.string.reason_crisis;
            case "contact":
                return R.string.reason_contact;
            case "negative":
                return R.string.reason_negative;
            default:
                return R.string.reason_short;
        }
    }
}
