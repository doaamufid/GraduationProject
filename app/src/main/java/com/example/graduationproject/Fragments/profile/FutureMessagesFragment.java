package com.example.graduationproject.Fragments.profile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.ProfileNavigator;
import com.example.graduationproject.R;
import com.example.graduationproject.data.profile.ArabicDateUtils;
import com.example.graduationproject.data.profile.SeedData;
import com.example.graduationproject.models.profile.FutureMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mirrors <FutureMessagesScreen/> + <MessageRow/>.
 */
public class FutureMessagesFragment extends Fragment {

    private List<FutureMessage> messages;
    private final Set<Long> armedForDelete = new HashSet<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private LinearLayout container;
    private View overlayDetail, dialogContent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messages = new ArrayList<>(SeedData.getSeedMessages(requireContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_future_messages, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileNavigator activity = (ProfileNavigator) requireActivity();

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> activity.showHome());

        container = view.findViewById(R.id.messages_container);
        overlayDetail = view.findViewById(R.id.overlay_message_detail);
        dialogContent = overlayDetail.findViewById(R.id.dialog_content);

        overlayDetail.setOnClickListener(v -> closeDetail());
        dialogContent.setOnClickListener(v -> { /* swallow */ });

        render(activity);
    }

    private void render(ProfileNavigator activity) {
        container.removeAllViews();
        List<FutureMessage> sorted = new ArrayList<>(messages);
        Collections.sort(sorted, Comparator.comparing(m -> m.targetDate));

        for (FutureMessage msg : sorted) {
            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_profile_message_row, container, false);
            bindRow(row, msg, activity);
            container.addView(row);
        }
    }

    private void bindRow(View row, FutureMessage msg, ProfileNavigator activity) {
        FrameLayout iconBg = row.findViewById(R.id.icon_bg);
        ImageView statusIcon = row.findViewById(R.id.img_status_icon);
        LinearLayout btnView = row.findViewById(R.id.btn_view_message);
        TextView preview = row.findViewById(R.id.txt_message_preview);
        TextView meta = row.findViewById(R.id.txt_message_meta);
        LinearLayout btnDelete = row.findViewById(R.id.btn_delete);
        TextView deleteLabel = row.findViewById(R.id.txt_delete_label);

        boolean armed = armedForDelete.contains(msg.id);

        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        int primary = ContextCompat.getColor(requireContext(), R.color.primary);
        int border = ContextCompat.getColor(requireContext(), R.color.border);
        int bgAlt = ContextCompat.getColor(requireContext(), R.color.bg_alt);
        bg.setColor(msg.arrived ? primary : bgAlt);
        bg.setStroke(dp(1), msg.arrived ? primary : border);
        iconBg.setBackground(bg);
        statusIcon.setImageResource(msg.arrived ? R.drawable.ic_mail : R.drawable.ic_lock);
        statusIcon.setColorFilter(msg.arrived
                ? ContextCompat.getColor(requireContext(), R.color.white)
                : ContextCompat.getColor(requireContext(), R.color.text_soft));

        preview.setText(msg.arrived ? msg.text : getString(R.string.message_locked));
        if (msg.arrived) {
            meta.setText(getString(R.string.message_arrived_fmt, ArabicDateUtils.formatDate(requireContext(), msg.targetDate)));
        } else {
            meta.setText(getString(R.string.message_locked_fmt,
                    ArabicDateUtils.formatDate(requireContext(), msg.targetDate),
                    ArabicDateUtils.toAr(ArabicDateUtils.daysLeft(msg.targetDate))));
        }

        btnView.setOnClickListener(v -> openDetail(msg));

        deleteLabel.setText(armed ? R.string.confirm_delete_short : R.string.delete_button);
        deleteLabel.setTextColor(ContextCompat.getColor(requireContext(), armed ? R.color.pink : R.color.text_soft));
        btnDelete.setBackground(armed ? roundedFlat(ContextCompat.getColor(requireContext(), R.color.delete_armed_bg)) : null);

        btnDelete.setOnClickListener(v -> {
            if (!armed) {
                armedForDelete.add(msg.id);
                handler.postDelayed(() -> { armedForDelete.remove(msg.id); render(activity); }, 2000);
                render(activity);
                return;
            }
            armedForDelete.remove(msg.id);
            messages.removeIf(m -> m.id == msg.id);
            render(activity);
        });
    }

    private void openDetail(FutureMessage msg) {
        ImageView icon = overlayDetail.findViewById(R.id.img_dialog_icon);
        TextView text = overlayDetail.findViewById(R.id.txt_dialog_text);
        Button close = overlayDetail.findViewById(R.id.btn_dialog_close);

        if (msg.arrived) {
            icon.setImageResource(R.drawable.ic_mail);
            icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary));
            text.setText(msg.text);
        } else {
            icon.setImageResource(R.drawable.ic_lock);
            icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_soft));
            text.setText(R.string.message_locked_title);
        }
        close.setOnClickListener(v -> closeDetail());

        overlayDetail.setVisibility(View.VISIBLE);
        overlayDetail.setAlpha(0f);
        overlayDetail.animate().alpha(1f).setDuration(200).start();
    }

    private void closeDetail() {
        overlayDetail.animate().alpha(0f).setDuration(150)
                .withEndAction(() -> overlayDetail.setVisibility(View.GONE)).start();
    }

    private android.graphics.drawable.GradientDrawable roundedFlat(int color) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(10 * getResources().getDisplayMetrics().density);
        return d;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
