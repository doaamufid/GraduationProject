package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.MainActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.data.Repository;
import com.example.graduationproject.models.Message;

/**
 * Mirrors ApprovedScreen: shows the just-published message in a static
 * card (no heart/pin/share controls, matching the JS component which
 * renders a bare preview) then a CTA back to the wall.
 */
public class ApprovedFragment extends Fragment {

    private static final String ARG_MSG_ID = "msg_id";

    public static ApprovedFragment newInstance(long messageId) {
        ApprovedFragment f = new ApprovedFragment();
        Bundle b = new Bundle();
        b.putLong(ARG_MSG_ID, messageId);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_approved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = (MainActivity) requireActivity();

        long id = getArguments() != null ? getArguments().getLong(ARG_MSG_ID) : -1;
        Message msg = Repository.get().findById(id);

        FrameLayout holder = view.findViewById(R.id.approvedCardHolder);
        if (msg != null) {
            View card = LayoutInflater.from(requireContext()).inflate(R.layout.item_approved_card, holder, false);
            TextView text = card.findViewById(R.id.approvedCardText);
            text.setText(msg.text);
            TextView emoji = card.findViewById(R.id.approvedCardEmoji);
            if (msg.emoji != null) {
                emoji.setVisibility(View.VISIBLE);
                emoji.setText(msg.emoji);
            } else {
                emoji.setVisibility(View.GONE);
            }
            card.startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.card_in));
            holder.addView(card);
        }

        view.findViewById(R.id.backToWallBtn).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack(null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            activity.showWall(false);
        });
    }
}
