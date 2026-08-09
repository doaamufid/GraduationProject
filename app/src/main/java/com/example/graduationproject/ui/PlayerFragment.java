package com.example.graduationproject.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.R;
import com.example.graduationproject.VideoLibraryActivity;
import com.example.graduationproject.models.ContentItem;
import com.example.graduationproject.models.ContentRepository;
import com.example.graduationproject.widget.FadeUtils;
import com.example.graduationproject.widget.ToastController;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

/**
 * Equivalent of <Player/>: embeds the video via WebView (youtube-nocookie
 * iframe URL, identical to the original <iframe src=...>), the
 * like/dislike feedback row, the animated dislike-reason chip panel,
 * the "why suggested" transparency toggle, curated "continue watching"
 * suggestions, and the bottom auto-dismissing toast.
 */
public class PlayerFragment extends Fragment {

    private static final String ARG_ITEM_ID = "item_id";

    public static PlayerFragment newInstance(int itemId) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    private ContentItem item;

    // state (mirrors the React useState hooks)
    private String feedback = null; // null | "up" | "down"
    private boolean showReasons = false;
    private String selectedReason = null;
    private boolean saved = false;
    private boolean showWhy = false;

    private ImageButton btnSaveTop, btnLike, btnDislike;
    private LinearLayout groupReasons, btnWhy, llSuggestions;
    private FlexboxLayout flexReasons;
    private TextView tvWhyReason, tvContinueWatchingLabel;
    private ToastController toastController;

    @Nullable
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_player, container, false);

        int itemId = getArguments() != null ? getArguments().getInt(ARG_ITEM_ID, -1) : -1;
        item = ContentRepository.findById(itemId);
        if (item == null) {
            if (getActivity() != null) getActivity().onBackPressed();
            return root;
        }

        toastController = new ToastController(root.findViewById(R.id.toastHost));

        // Bookmark toggle button placed in the top bar's right slot.
        btnSaveTop = new ImageButton(requireContext());
        btnSaveTop.setBackgroundResource(R.drawable.bg_icon_button);
        btnSaveTop.setImageResource(R.drawable.ic_bookmark_outline);
        int pad = dp(11);
        btnSaveTop.setPadding(pad, pad, pad, pad);
        btnSaveTop.setLayoutParams(new ViewGroup.LayoutParams(dp(40), dp(40)));
        btnSaveTop.setOnClickListener(v -> {
            saved = !saved;
            renderSaveButton();
        });

        TopBarHelper.bind(root, getString(R.string.player_title), null,
                () -> {
                    if (getActivity() != null) getActivity().onBackPressed();
                }, btnSaveTop);
        renderSaveButton();

        setupVideoAspectRatio(root);
        setupWebView(root);
        bindContentInfo(root);

        btnLike = root.findViewById(R.id.btnLike);
        btnDislike = root.findViewById(R.id.btnDislike);
        groupReasons = root.findViewById(R.id.groupReasons);
        flexReasons = root.findViewById(R.id.flexReasons);
        btnWhy = root.findViewById(R.id.btnWhy);
        tvWhyReason = root.findViewById(R.id.tvWhyReason);
        tvContinueWatchingLabel = root.findViewById(R.id.tvContinueWatchingLabel);
        llSuggestions = root.findViewById(R.id.llSuggestions);

        btnLike.setOnClickListener(v -> handleUp());
        btnDislike.setOnClickListener(v -> handleDown());
        btnWhy.setOnClickListener(v -> {
            showWhy = !showWhy;
            renderWhy();
        });

        buildReasonChips();
        renderFeedbackButtons();
        renderWhy();
        renderSuggestions();

        return root;
    }

    private void setupVideoAspectRatio(View root) {
        // Equivalent of `paddingBottom: "56.25%"` (16:9 aspect ratio box).
        View spacer = root.findViewById(R.id.videoAspectSpacer);
        spacer.post(() -> {
            int width = spacer.getWidth();
            ViewGroup.LayoutParams parentLp = ((View) spacer.getParent()).getLayoutParams();
            parentLp.height = (int) (width * 0.5625f);
            ((View) spacer.getParent()).setLayoutParams(parentLp);
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(View root) {
        WebView webView = root.findViewById(R.id.webViewPlayer);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        webView.setBackgroundColor(android.graphics.Color.BLACK);

        String src = "https://www.youtube-nocookie.com/embed/" + item.videoId
                + "?rel=0&modestbranding=1&iv_load_policy=3&playsinline=1";
        webView.loadUrl(src);
    }

    private void bindContentInfo(View root) {
        TextView tvTypePill = root.findViewById(R.id.tvTypePill);
        TextView tvDuration = root.findViewById(R.id.tvDuration);
        TextView tvTitle = root.findViewById(R.id.tvTitle);
        TextView tvSrc = root.findViewById(R.id.tvSrc);

        tvTypePill.setText(item.type);
        tvDuration.setText(item.duration);
        tvTitle.setText(item.title);
        tvSrc.setText(item.src);
    }

    private void renderSaveButton() {
        btnSaveTop.setImageResource(saved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
    }

    // ===================== FEEDBACK LOGIC =====================

    /** Equivalent of `handleUp()`. */
    private void handleUp() {
        feedback = "up";
        showReasons = false;
        renderFeedbackButtons();
        renderReasonsPanel(false);
        toastController.show(getString(R.string.toast_like));
    }

    /** Equivalent of `handleDown()`. */
    private void handleDown() {
        feedback = "down";
        showReasons = true;
        renderFeedbackButtons();
        renderReasonsPanel(true);
    }

    /** Equivalent of `pickReason(r)`. */
    private void pickReason(String reason) {
        selectedReason = reason;
        showReasons = false;
        renderReasonChipsSelection();
        renderReasonsPanel(false);
        toastController.show(getString(R.string.toast_dislike_reason));
    }

    private void renderFeedbackButtons() {
        boolean up = "up".equals(feedback);
        boolean down = "down".equals(feedback);

        btnLike.setBackgroundResource(up ? R.drawable.bg_reaction_btn_like : R.drawable.bg_reaction_btn_neutral);
        btnLike.setImageResource(up ? R.drawable.ic_thumb_up_filled : R.drawable.ic_thumb_up_outline);

        btnDislike.setBackgroundResource(down ? R.drawable.bg_reaction_btn_dislike : R.drawable.bg_reaction_btn_neutral);
        btnDislike.setImageResource(down ? R.drawable.ic_thumb_down_filled : R.drawable.ic_thumb_down_outline);
    }

    private void buildReasonChips() {
        flexReasons.removeAllViews();
        for (String reason : ContentRepository.DISLIKE_REASONS) {
            TextView chip = (TextView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_reason_chip, flexReasons, false);
            chip.setText(reason);
            chip.setOnClickListener(v -> pickReason(reason));
            flexReasons.addView(chip);
        }
        renderReasonChipsSelection();
    }

    private void renderReasonChipsSelection() {
        for (int i = 0; i < flexReasons.getChildCount(); i++) {
            TextView chip = (TextView) flexReasons.getChildAt(i);
            boolean selected = chip.getText().toString().equals(selectedReason);
            chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
            chip.setTextColor(getResources().getColor(selected ? R.color.white : R.color.text_soft));
        }
    }

    /** Equivalent of the `.reason-fade` conditional render block. */
    private void renderReasonsPanel(boolean animate) {
        groupReasons.setVisibility(showReasons ? View.VISIBLE : View.GONE);
        if (showReasons && animate) {
            FadeUtils.reasonFade(groupReasons);
        }
    }

    private void renderWhy() {
        tvWhyReason.setVisibility(showWhy ? View.VISIBLE : View.GONE);
        if (showWhy) {
            tvWhyReason.setText(item.reason);
        }
    }

    private void renderSuggestions() {
        List<ContentItem> related = ContentRepository.relatedTo(item);
        boolean hasSuggestions = !related.isEmpty();

        tvContinueWatchingLabel.setVisibility(hasSuggestions ? View.VISIBLE : View.GONE);
        llSuggestions.removeAllViews();

        for (ContentItem suggestion : related) {
            View row = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_suggestion_small, llSuggestions, false);

            View thumbGradient = row.findViewById(R.id.thumbGradient);
            ImageView ivTypeIcon = row.findViewById(R.id.ivTypeIcon);
            TextView tvTitle = row.findViewById(R.id.tvTitle);

            applyGradient(thumbGradient, suggestion.gradStart, suggestion.gradEnd);
            ivTypeIcon.setImageResource(suggestion.isVideo ? R.drawable.ic_play : R.drawable.ic_headphones);
            tvTitle.setText(suggestion.title);

            row.setOnClickListener(v -> {
                if (getActivity() instanceof VideoLibraryActivity) {
                    // Navigate back to library list first (by replacing with a new LibraryFragment)
                    // and then open the new player, or simply replace the current player.
                    // The user requested the back arrow to go to the full list, 
                    // which is achieved by not adding the player to the back stack.
                    ((VideoLibraryActivity) getActivity()).openPlayer(suggestion);
                }
            });

            llSuggestions.addView(row);
        }
    }

    private void applyGradient(View view, int startColor, int endColor) {
        android.graphics.drawable.GradientDrawable gradient = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TL_BR,
                new int[]{startColor, endColor});
        gradient.setCornerRadius(10 * getResources().getDisplayMetrics().density);
        view.setBackground(gradient);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (toastController != null) {
            toastController.cancel();
        }
    }
}
