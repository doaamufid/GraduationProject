package com.example.graduationproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.models.FutureMessage;
import com.example.graduationproject.models.FutureSelfRepository;
import com.example.graduationproject.widget.CurvedTimelineView;
import com.example.graduationproject.widget.TimelineGeometry;
import com.example.graduationproject.ui.AllMessagesActivity;
import com.example.graduationproject.ui.ComposeDialogFragment;
import com.example.graduationproject.ui.MessageDetailDialogFragment;

import java.util.List;

public class FutureActivity extends AppCompatActivity {

    private final FutureSelfRepository repo = FutureSelfRepository.getInstance();

    private FrameLayout timelineContainer;
    private CurvedTimelineView curvedTimelineView;
    private TextView tvFooterPreview, tvTodayPreviewText;
    private ImageView btnViewAll;
    private View cardTodayPreview;

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
    }

    private void bindViews() {
        timelineContainer = findViewById(R.id.timelineContainer);
        curvedTimelineView = findViewById(R.id.curvedTimelineView);
        tvFooterPreview = findViewById(R.id.tvFooterPreview);
        btnViewAll = findViewById(R.id.btnViewAll);
        tvTodayPreviewText = findViewById(R.id.tvTodayPreviewText);
        cardTodayPreview = findViewById(R.id.cardTodayPreview);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setListeners() {
        btnViewAll.setOnClickListener(v -> openAllMessages());
    }

    private void openAllMessages() {
        startActivity(new Intent(this, AllMessagesActivity.class));
    }

    private void renderAll() {
        renderTimeline();
        renderFooter();
        // Today preview is now rendered inside the timeline context
    }

    private void renderTimeline() {
        // Remove previously-added node views, keep the CurvedTimelineView itself.
        for (int i = timelineContainer.getChildCount() - 1; i >= 0; i--) {
            View child = timelineContainer.getChildAt(i);
            if (child != curvedTimelineView && child != cardTodayPreview) {
                timelineContainer.removeViewAt(i);
            }
        }

        List<PointF> points = TimelineGeometry.computePoints(0);
        curvedTimelineView.setPoints(points);

        // Milestone Labels matching "Unit X" design
        String[] labels = {"Unit 1", "Unit 2", "Unit 3", "Unit 4"};

        for (int i = 0; i < 4; i++) {
            addMilestoneNode(i, labels[i], points.get(i));
        }

        // Add the large Start button at the last point
        addSendButton(points.get(4));

        // Position Today's Preview Bubble
        positionTodayPreview(points.get(4));
    }

    private void addMilestoneNode(int index, String label, PointF pointDp) {
        View node = LayoutInflater.from(this).inflate(R.layout.item_milestone_node, timelineContainer, false);
        View circleBg = node.findViewById(R.id.nodeCircleBg);
        ImageView ivIcon = node.findViewById(R.id.ivNodeIcon);
        TextView tvLabelBottom = node.findViewById(R.id.tvLabelBottom);

        tvLabelBottom.setText(label);
        
        // Match the organic design
        if (index % 2 == 0) {
            circleBg.setRotation(index * 45f);
        } else {
            circleBg.setRotation(index * -30f);
        }

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.TOP | Gravity.START;
        flp.leftMargin = Math.round((pointDp.x - 50) * density);
        flp.topMargin = Math.round((pointDp.y - 50) * density);
        node.setLayoutParams(flp);

        timelineContainer.addView(node);
    }

    private void addSendButton(PointF pointDp) {
        View btn = LayoutInflater.from(this).inflate(R.layout.item_send_button_node, timelineContainer, false);
        btn.setOnClickListener(v ->
                ComposeDialogFragment.newInstanceForCreate()
                        .show(getSupportFragmentManager(), "compose"));

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.TOP | Gravity.START;
        flp.leftMargin = Math.round((pointDp.x - 70) * density);
        flp.topMargin = Math.round((pointDp.y - 70) * density);
        btn.setLayoutParams(flp);

        timelineContainer.addView(btn);
    }

    private void positionTodayPreview(PointF sendPointDp) {
        FutureMessage latestArrived = null;
        for (FutureMessage m : repo.getMessages()) {
            if (m.arrived) {
                latestArrived = m;
                break;
            }
        }

        if (latestArrived != null) {
            cardTodayPreview.setVisibility(View.VISIBLE);
            tvTodayPreviewText.setText(latestArrived.text);

            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) cardTodayPreview.getLayoutParams();
            // Position the bubble at the bottom left, near the end of the road
            lp.gravity = Gravity.TOP | Gravity.START;
            lp.leftMargin = Math.round(20 * density);
            lp.topMargin = Math.round((sendPointDp.y - 140) * density);
            cardTodayPreview.setLayoutParams(lp);
        } else {
            cardTodayPreview.setVisibility(View.GONE);
        }
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
            tvFooterPreview.setText("لا رسائل سابقة بعد");
        } else {
            tvFooterPreview.setText(latestArrived.text);
            final FutureMessage msg = latestArrived;
            tvFooterPreview.setOnClickListener(v ->
                    MessageDetailDialogFragment.newInstance(msg.id)
                            .show(getSupportFragmentManager(), "detail"));
        }
    }
}
