package com.example.graduationproject;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.models.FutureMessage;
import com.example.graduationproject.models.FutureSelfRepository;
import com.example.graduationproject.util.DateUtils;
import com.example.graduationproject.widget.CurvedTimelineView;
import com.example.graduationproject.widget.FabPulseAnimator;
import com.example.graduationproject.widget.TimelineGeometry;
import com.example.graduationproject.ui.AllMessagesActivity;
import com.example.graduationproject.ui.ComposeDialogFragment;
import com.example.graduationproject.ui.MessageDetailDialogFragment;

import java.util.List;

/**
 * Full Java/Android port of the "MessageToFutureSelfScreen" React
 * component's main timeline view.
 */
public class FutureActivity extends AppCompatActivity {

    private final FutureSelfRepository repo = FutureSelfRepository.getInstance();

    private ImageButton btnShowAll, btnCompose;
    private FrameLayout timelineContainer;
    private CurvedTimelineView curvedTimelineView;
    private View groupEmpty, fabPulseRing;
    private TextView tvFooterPreview, btnViewAll;
    private ValueAnimator fabPulseAnimator;

    private float density;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_self);
        density = getResources().getDisplayMetrics().density;

        bindViews();
        setListeners();

        getSupportFragmentManager().setFragmentResultListener(
                ComposeDialogFragment.REQUEST_KEY, this, (key, bundle) -> renderAll());
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderAll();
        fabPulseAnimator = FabPulseAnimator.start(fabPulseRing);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FabPulseAnimator.stop(fabPulseAnimator, fabPulseRing);
    }

    private void bindViews() {
        btnShowAll = findViewById(R.id.btnShowAll);
        btnCompose = findViewById(R.id.btnCompose);
        timelineContainer = findViewById(R.id.timelineContainer);
        curvedTimelineView = findViewById(R.id.curvedTimelineView);
        groupEmpty = findViewById(R.id.groupEmpty);
        fabPulseRing = findViewById(R.id.fabPulseRing);
        tvFooterPreview = findViewById(R.id.tvFooterPreview);
        btnViewAll = findViewById(R.id.btnViewAll);
    }

    private void setListeners() {
        btnShowAll.setOnClickListener(v -> openAllMessages());
        btnViewAll.setOnClickListener(v -> openAllMessages());
        btnCompose.setOnClickListener(v ->
                ComposeDialogFragment.newInstanceForCreate()
                        .show(getSupportFragmentManager(), "compose"));
    }

    private void openAllMessages() {
        startActivity(new Intent(this, AllMessagesActivity.class));
    }

    private void renderAll() {
        renderTimeline();
        renderFooter();
    }

    private void renderTimeline() {
        List<FutureMessage> messages = repo.getMessages();
        boolean empty = messages.isEmpty();
        groupEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);

        // Remove previously-added node views, keep the CurvedTimelineView itself.
        for (int i = timelineContainer.getChildCount() - 1; i >= 0; i--) {
            View child = timelineContainer.getChildAt(i);
            if (child != curvedTimelineView) {
                timelineContainer.removeViewAt(i);
            }
        }

        if (empty) {
            ViewGroup.LayoutParams lp = timelineContainer.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            timelineContainer.setLayoutParams(lp);
            curvedTimelineView.setPoints(java.util.Collections.emptyList());
            return;
        }

        List<PointF> points = TimelineGeometry.computePoints(messages.size());
        curvedTimelineView.setPoints(points);

        ViewGroup.LayoutParams lp = timelineContainer.getLayoutParams();
        lp.height = Math.round(TimelineGeometry.totalHeightDp(messages.size()) * density);
        timelineContainer.setLayoutParams(lp);

        for (int i = 0; i < messages.size(); i++) {
            addMessageNode(messages.get(i), points.get(i));
        }
    }

    private void addMessageNode(FutureMessage message, PointF pointDp) {
        View node = LayoutInflater.from(this).inflate(R.layout.item_message_node, timelineContainer, false);

        View circleBg = node.findViewById(R.id.nodeCircleBg);
        ImageView ivIcon = node.findViewById(R.id.ivNodeIcon);
        TextView tvLabel = node.findViewById(R.id.tvNodeLabel);

        if (message.arrived) {
            circleBg.setBackgroundResource(R.drawable.bg_node_arrived);
            ivIcon.setImageResource(R.drawable.ic_heart_filled_white);
            tvLabel.setText(R.string.arrived_label);
        } else {
            circleBg.setBackgroundResource(R.drawable.bg_node_locked);
            ivIcon.setImageResource(R.drawable.ic_lock);
            tvLabel.setText(getString(R.string.days_left_format,
                    DateUtils.toAr(DateUtils.daysLeft(message.targetDate))));
        }

        node.setOnClickListener(v ->
                MessageDetailDialogFragment.newInstance(message.id)
                        .show(getSupportFragmentManager(), "detail"));

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.TOP | Gravity.START;
        // Matches `left: points[i].x - 22, top: points[i].y - 22` (22 = half of the 44dp circle).
        flp.leftMargin = Math.round((pointDp.x - 22) * density);
        flp.topMargin = Math.round((pointDp.y - 22) * density);
        node.setLayoutParams(flp);

        timelineContainer.addView(node);
    }

    private void renderFooter() {
        FutureMessage latestArrived = null;
        for (FutureMessage m : repo.getMessages()) {
            if (m.arrived) {
                latestArrived = m;
                break;
            }
        }

        if (latestArrived == null) {
            tvFooterPreview.setText(R.string.no_arrived_yet);
            tvFooterPreview.setOnClickListener(null);
            tvFooterPreview.setClickable(false);
        } else {
            tvFooterPreview.setText(latestArrived.text);
            final FutureMessage msg = latestArrived;
            tvFooterPreview.setClickable(true);
            tvFooterPreview.setOnClickListener(v ->
                    MessageDetailDialogFragment.newInstance(msg.id)
                            .show(getSupportFragmentManager(), "detail"));
        }
    }
}
