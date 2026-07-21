package com.example.graduationproject.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.R;
import com.example.graduationproject.models.FutureMessage;
import com.example.graduationproject.models.FutureSelfRepository;
import com.example.graduationproject.util.DateUtils;

/**
 * Equivalent of <MessageDetailOverlay/>: shows either the full message
 * (arrived) or a "still locked" countdown card, centered over a dim
 * scrim, dismissed by tapping the backdrop or the close button.
 */
public class MessageDetailDialogFragment extends DialogFragment {

    private static final String ARG_MESSAGE_ID = "message_id";

    public static MessageDetailDialogFragment newInstance(long messageId) {
        MessageDetailDialogFragment fragment = new MessageDetailDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_MESSAGE_ID, messageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_message_detail, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        long id = getArguments() != null ? getArguments().getLong(ARG_MESSAGE_ID, -1) : -1;
        FutureMessage message = FutureSelfRepository.getInstance().findById(id);
        if (message == null) {
            dismiss();
            return root;
        }

        View groupArrived = root.findViewById(R.id.groupArrived);
        View groupLocked = root.findViewById(R.id.groupLocked);

        if (message.arrived) {
            groupArrived.setVisibility(View.VISIBLE);
            groupLocked.setVisibility(View.GONE);

            TextView tvArrivedFrom = root.findViewById(R.id.tvArrivedFrom);
            TextView tvArrivedText = root.findViewById(R.id.tvArrivedText);
            tvArrivedFrom.setText(getString(R.string.arrived_from_format, message.createdLabel));
            tvArrivedText.setText(message.text);
            root.findViewById(R.id.btnCloseArrived).setOnClickListener(v -> dismiss());
        } else {
            groupArrived.setVisibility(View.GONE);
            groupLocked.setVisibility(View.VISIBLE);

            TextView tvLockedSub = root.findViewById(R.id.tvLockedSub);
            tvLockedSub.setText(getString(R.string.locked_sub_format,
                    DateUtils.formatDate(message.targetDate),
                    DateUtils.toAr(DateUtils.daysLeft(message.targetDate))));
            root.findViewById(R.id.btnCloseLocked).setOnClickListener(v -> dismiss());
        }

        return root;
    }

    @Override
    public int getTheme() {
        return R.style.Theme_FutureSelf_Dialog;
    }
}
