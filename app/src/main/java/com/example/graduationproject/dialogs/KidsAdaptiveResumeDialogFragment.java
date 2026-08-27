package com.example.graduationproject.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;

/**
 * Mirrors <ResumePrompt onContinue onRestart>: a centered white card, shown over a dimmed
 * scrim, offering to continue the saved onboarding progress or start over.
 */
public class KidsAdaptiveResumeDialogFragment extends DialogFragment {

    public interface Listener {
        void onResumeContinue();
        void onResumeRestart();
    }

    private Listener listener;

    public static KidsAdaptiveResumeDialogFragment newInstance(Listener listener) {
        KidsAdaptiveResumeDialogFragment f = new KidsAdaptiveResumeDialogFragment();
        f.listener = listener;
        f.setCancelable(false);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xF2FFF1D6));
        }
        setCancelable(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        int pad = dp(24);
        root.setPadding(pad, pad, pad, pad);

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setBackgroundResource(R.drawable.kids_adaptive_bg_dialog_card);
        int cardPad = dp(26);
        card.setPadding(cardPad, cardPad, cardPad, cardPad);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(dp(300), LinearLayout.LayoutParams.WRAP_CONTENT);
        root.addView(card, cardLp);

        KidsAdaptiveTeddyBuddyView teddy = new KidsAdaptiveTeddyBuddyView(requireContext());
        teddy.setMood(KidsAdaptiveTeddyBuddyView.MOOD_WARM);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(dp(56), dp(56));
        tlp.bottomMargin = dp(8);
        card.addView(teddy, tlp);

        TextView title = new TextView(requireContext());
        title.setText(getString(R.string.kids_adaptive_resume_title));
        title.setTextSize(18);
        title.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()), Typeface.BOLD);
        title.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleLp = wrap(); titleLp.bottomMargin = dp(6);
        card.addView(title, titleLp);

        TextView body = new TextView(requireContext());
        body.setText(getString(R.string.kids_adaptive_resume_body));
        body.setTextSize(13.5f);
        body.setTypeface(KidsAdaptiveTypefaces.body(requireContext()));
        body.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        body.setAlpha(0.7f);
        body.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams bodyLp = wrap(); bodyLp.bottomMargin = dp(18);
        card.addView(body, bodyLp);

        Button continueBtn = new Button(requireContext());
        continueBtn.setText(getString(R.string.kids_adaptive_resume_continue));
        continueBtn.setAllCaps(false);
        continueBtn.setTextColor(Color.parseColor("#4A2A12"));
        continueBtn.setBackgroundResource(R.drawable.kids_adaptive_bg_primary_button);
        continueBtn.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()), Typeface.BOLD);
        continueBtn.setOnClickListener(v -> {
            if (listener != null) listener.onResumeContinue();
            dismissAllowingStateLoss();
        });
        LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cbLp.bottomMargin = dp(10);
        card.addView(continueBtn, cbLp);

        TextView restart = new TextView(requireContext());
        restart.setText(getString(R.string.kids_adaptive_resume_restart));
        restart.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        restart.setAlpha(0.6f);
        restart.setTextSize(14);
        restart.setGravity(Gravity.CENTER);
        restart.setPaintFlags(restart.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        restart.setOnClickListener(v -> {
            if (listener != null) listener.onResumeRestart();
            dismissAllowingStateLoss();
        });
        card.addView(restart, wrap());

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private LinearLayout.LayoutParams wrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density); }
}
