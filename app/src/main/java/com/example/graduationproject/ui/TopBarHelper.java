package com.example.graduationproject.ui;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.example.graduationproject.R;

public class TopBarHelper {
    public static void bind(View root, String title, Runnable onBack, View rightView) {
        bind(root, title, null, onBack, rightView);
    }

    public static void bind(View root, String title, String subtitle, Runnable onBack, View rightView) {
        TextView tvTitle = root.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setText(title);

        TextView tvSubtitle = root.findViewById(R.id.tvTopBarSubtitle);
        if (tvSubtitle != null) {
            if (subtitle != null && !subtitle.isEmpty()) {
                tvSubtitle.setText(subtitle);
                tvSubtitle.setVisibility(View.VISIBLE);
            } else {
                tvSubtitle.setVisibility(View.GONE);
            }
        }

        View btnBack = root.findViewById(R.id.btnBack);
        if (btnBack != null && onBack != null) {
            btnBack.setOnClickListener(v -> onBack.run());
        }

        FrameLayout containerRight = root.findViewById(R.id.topBarRight);
        if (containerRight != null) {
            containerRight.removeAllViews();
            if (rightView != null) {
                containerRight.addView(rightView);
            }
        }
    }
}
