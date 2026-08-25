package com.example.graduationproject.view.AdultOnboarding;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Minimal wrapping row layout (like CSS `flex-wrap`), used anywhere the React
 * file used `flexWrap: "wrap"` — emotion bubbles, goal chips, timeline
 * follow-up pill options.
 */
public class FlowLayout extends ViewGroup {

    private int horizontalSpacingPx = 0;
    private int verticalSpacingPx = 0;
    private int gravity = Gravity.START;

    public FlowLayout(Context context) { super(context); }
    public FlowLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void setGravityCenter(boolean center) {
        this.gravity = center ? Gravity.CENTER_HORIZONTAL : Gravity.START;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int childCount = getChildCount();
        int lineWidth = 0, lineHeight = 0, totalHeight = getPaddingTop() + getPaddingBottom();
        int maxLineWidth = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int childW = child.getMeasuredWidth();
            int childH = child.getMeasuredHeight();

            if (widthMode != MeasureSpec.UNSPECIFIED && lineWidth + childW > width && lineWidth > 0) {
                totalHeight += lineHeight + verticalSpacingPx;
                maxLineWidth = Math.max(maxLineWidth, lineWidth);
                lineWidth = childW + horizontalSpacingPx;
                lineHeight = childH;
            } else {
                lineWidth += childW + horizontalSpacingPx;
                lineHeight = Math.max(lineHeight, childH);
            }
        }
        totalHeight += lineHeight;
        maxLineWidth = Math.max(maxLineWidth, lineWidth);

        int finalWidth = widthMode == MeasureSpec.UNSPECIFIED ? maxLineWidth : MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(finalWidth, resolveSize(totalHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l - getPaddingRight();
        int childCount = getChildCount();

        java.util.List<View> lineViews = new java.util.ArrayList<>();
        int lineWidth = 0, lineHeight = 0;
        int curY = getPaddingTop();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int childW = child.getMeasuredWidth();
            int childH = child.getMeasuredHeight();

            if (lineWidth + childW > width && !lineViews.isEmpty()) {
                layoutLine(lineViews, curY, lineHeight, width);
                curY += lineHeight + verticalSpacingPx;
                lineViews.clear();
                lineWidth = 0;
                lineHeight = 0;
            }
            lineViews.add(child);
            lineWidth += childW + horizontalSpacingPx;
            lineHeight = Math.max(lineHeight, childH);
        }
        if (!lineViews.isEmpty()) {
            layoutLine(lineViews, curY, lineHeight, width);
        }
    }

    private void layoutLine(java.util.List<View> lineViews, int y, int lineHeight, int availableWidth) {
        int lineContentWidth = 0;
        for (View v : lineViews) lineContentWidth += v.getMeasuredWidth() + horizontalSpacingPx;
        lineContentWidth -= horizontalSpacingPx;

        int startX = getPaddingLeft();
        if (gravity == Gravity.CENTER_HORIZONTAL) {
            startX += Math.max(0, (availableWidth - lineContentWidth) / 2);
        }
        int x = startX;
        for (View child : lineViews) {
            int childW = child.getMeasuredWidth();
            int childH = child.getMeasuredHeight();
            int childTop = y + (lineHeight - childH) / 2;
            child.layout(x, childTop, x + childW, childTop + childH);
            x += childW + horizontalSpacingPx;
        }
    }
}
