package com.example.graduationproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * A minimal flow/wrap layout: lays children left-to-right (respecting RTL), wrapping to
 * a new line when a child would overflow the available width. Used for goal chips,
 * feeling tags, and any other "flex-wrap" grouping from the React source.
 */
public class KidsAdaptiveFlowLayout extends ViewGroup {

    private int horizontalSpacing = dp(8);
    private int verticalSpacing = dp(8);

    public KidsAdaptiveFlowLayout(Context context) { super(context); }
    public KidsAdaptiveFlowLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void setSpacing(int horizontalPx, int verticalPx) {
        this.horizontalSpacing = horizontalPx;
        this.verticalSpacing = verticalPx;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int availableWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int rowWidth = 0, rowHeight = 0, totalHeight = getPaddingTop() + getPaddingBottom();
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (rowWidth + childWidth > availableWidth && rowWidth > 0) {
                totalHeight += rowHeight + verticalSpacing;
                rowWidth = 0;
                rowHeight = 0;
            }
            rowWidth += childWidth + horizontalSpacing;
            rowHeight = Math.max(rowHeight, childHeight);
        }
        totalHeight += rowHeight;

        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, resolveSize(totalHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        boolean isRtl = getLayoutDirection() == LAYOUT_DIRECTION_RTL;
        int availableWidth = (r - l) - getPaddingLeft() - getPaddingRight();
        int x = 0, y = getPaddingTop();
        int rowHeight = 0;
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (x + childWidth > availableWidth && x > 0) {
                x = 0;
                y += rowHeight + verticalSpacing;
                rowHeight = 0;
            }

            int left = isRtl ? (availableWidth - x - childWidth) : x;
            left += getPaddingLeft();
            child.layout(left, y, left + childWidth, y + childHeight);

            x += childWidth + horizontalSpacing;
            rowHeight = Math.max(rowHeight, childHeight);
        }
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
