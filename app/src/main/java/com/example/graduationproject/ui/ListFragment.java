package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.R;
import com.example.graduationproject.data.Repository;
import com.example.graduationproject.models.Message;
import com.example.graduationproject.util.CardBinder;
import com.example.graduationproject.util.CardHost;

import java.util.List;

/**
 * Shared implementation of CardListScreen for both "رسائلي" (mine) and
 * "المحفوظات" (pinned) — same structure, different data source + copy.
 */
public class ListFragment extends Fragment {

    public static final String MODE_MINE = "mine";
    public static final String MODE_PINNED = "pinned";
    private static final String ARG_MODE = "mode";

    public static ListFragment newInstance(String mode) {
        ListFragment f = new ListFragment();
        Bundle b = new Bundle();
        b.putString(ARG_MODE, mode);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardHost activity = (CardHost) requireActivity();

        String mode = getArguments() != null ? getArguments().getString(ARG_MODE) : MODE_MINE;
        boolean isMine = MODE_MINE.equals(mode);

        view.findViewById(R.id.backBtn).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        TextView titleView = view.findViewById(R.id.listTitle);
        TextView subView = view.findViewById(R.id.listSub);
        View emptyState = view.findViewById(R.id.emptyState);
        TextView emptyTitle = view.findViewById(R.id.emptyTitle);
        TextView emptyBody = view.findViewById(R.id.emptyBody);
        View gridWrap = view.findViewById(R.id.gridWrap);
        LinearLayout columnRight = view.findViewById(R.id.gridColumnRight);
        LinearLayout columnLeft = view.findViewById(R.id.gridColumnLeft);

        List<Message> list = isMine ? Repository.get().getMineList() : Repository.get().getPinnedList();

        if (isMine) {
            titleView.setText(R.string.mine_title);
            subView.setText(list.size() + " رسالة شاركتها مع المجتمع");
            emptyTitle.setText(R.string.mine_empty_title);
            emptyBody.setText(R.string.mine_empty_body);
        } else {
            titleView.setText(R.string.pinned_title);
            subView.setText(list.size() + " رسالة حفظتها عشان ترجعلها");
            emptyTitle.setText(R.string.pinned_empty_title);
            emptyBody.setText(R.string.pinned_empty_body);
        }

        if (list.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            gridWrap.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            gridWrap.setVisibility(View.VISIBLE);

            LayoutInflater inflater = LayoutInflater.from(requireContext());
            for (int i = 0; i < list.size(); i++) {
                Message msg = list.get(i);
                LinearLayout target = (i % 2 == 0) ? columnRight : columnLeft;
                View card = CardBinder.bind(requireContext(), inflater, target, msg, i, activity);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = dpToPx(12);
                card.setLayoutParams(lp);
                card.startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.card_in));
                target.addView(card);
            }
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
