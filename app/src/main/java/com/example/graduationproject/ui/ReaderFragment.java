package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.ArticlesActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.models.Article;
import com.example.graduationproject.models.ArticleRepository;
import com.example.graduationproject.models.CategoryStyle;
import com.example.graduationproject.widget.ArticleArtBinder;
import com.example.graduationproject.widget.FadeUtils;
import com.example.graduationproject.widget.ToastController;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

/**
 * Full Java/Android port of <Reader/>: reading-progress strip, adjustable
 * font size, like/dislike feedback with reason chips, personalization
 * transparency panel, and curated "continue reading" suggestions.
 */
public class ReaderFragment extends Fragment {

    private static final String ARG_ARTICLE_ID = "article_id";
    private static final float[] FONT_SIZES_SP = {13f, 14.5f, 16f, 17.5f};

    public static ReaderFragment newInstance(int articleId) {
        ReaderFragment fragment = new ReaderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARTICLE_ID, articleId);
        fragment.setArguments(args);
        return fragment;
    }

    // ------- state (mirrors the React useState hooks) -------
    private String feedback = null; // null | "up" | "down"
    private boolean showReasons = false;
    private String selectedReason = null;
    private boolean saved = false;
    private boolean showWhy = false;
    private int fontStep = 0; // range -1..2

    private Article article;
    private ToastController toastController;

    // ------- views -------
    private View progressFill, progressTrack;
    private ImageButton btnSave, btnThumbUp, btnThumbDown;
    private TextView tvTag, tvTime, tvTitle, tvAuthor, tvRelatedLabel, tvWhyReason, tvSuggestionsLabel;
    private View groupRelatedExercise, groupReasons, btnWhy, llSuggestions;
    private ImageView ivRelatedIcon;
    private LinearLayout llBody;
    private FlexboxLayout flexReasons;
    private NestedScrollView scrollView;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reader, container, false);

        int articleId = getArguments() != null ? getArguments().getInt(ARG_ARTICLE_ID) : -1;
        article = ArticleRepository.findById(articleId);
        if (article == null) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return root;
        }

        bindViews(root);
        rootView = root;
        toastController = new ToastController(root.findViewById(R.id.toastHost));

        View fontControls = buildFontControls();
        TopBarHelper.bind(root, getString(R.string.reader_title),
                () -> requireActivity().getSupportFragmentManager().popBackStack(), fontControls);

        setupHero();
        setupHeader();
        setupBody();
        setupRelatedExercise();
        setupFeedback();
        setupWhy();
        setupSuggestions();
        setupScrollProgress();

        return root;
    }

    private void bindViews(View root) {
        progressTrack = root.findViewById(R.id.progressTrack);
        progressFill = root.findViewById(R.id.progressFill);
        scrollView = root.findViewById(R.id.scrollView);

        btnSave = root.findViewById(R.id.btnSave);
        tvTag = root.findViewById(R.id.tvTag);
        tvTime = root.findViewById(R.id.tvTime);
        tvTitle = root.findViewById(R.id.tvTitle);
        tvAuthor = root.findViewById(R.id.tvAuthor);
        llBody = root.findViewById(R.id.llBody);

        groupRelatedExercise = root.findViewById(R.id.groupRelatedExercise);
        ivRelatedIcon = root.findViewById(R.id.ivRelatedIcon);
        tvRelatedLabel = root.findViewById(R.id.tvRelatedLabel);

        btnThumbUp = root.findViewById(R.id.btnThumbUp);
        btnThumbDown = root.findViewById(R.id.btnThumbDown);

        groupReasons = root.findViewById(R.id.groupReasons);
        flexReasons = root.findViewById(R.id.flexReasons);

        btnWhy = root.findViewById(R.id.btnWhy);
        tvWhyReason = root.findViewById(R.id.tvWhyReason);

        tvSuggestionsLabel = root.findViewById(R.id.tvSuggestionsLabel);
        llSuggestions = root.findViewById(R.id.llSuggestions);
    }

    // ===================== TOP BAR: FONT SIZE CONTROLS =====================

    private View buildFontControls() {
        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);

        int btnSize = dp(32);
        int gap = dp(4);

        ImageButton btnMinus = new ImageButton(requireContext());
        btnMinus.setLayoutParams(new LinearLayout.LayoutParams(btnSize, btnSize));
        btnMinus.setBackgroundResource(R.drawable.bg_icon_button);
        btnMinus.setImageResource(R.drawable.ic_minus);
        btnMinus.setPadding(dp(9), dp(9), dp(9), dp(9));
        btnMinus.setOnClickListener(v -> {
            fontStep = Math.max(fontStep - 1, -1);
            applyFontSize();
        });

        ImageButton btnPlus = new ImageButton(requireContext());
        LinearLayout.LayoutParams plusLp = new LinearLayout.LayoutParams(btnSize, btnSize);
        plusLp.setMarginStart(gap);
        btnPlus.setLayoutParams(plusLp);
        btnPlus.setBackgroundResource(R.drawable.bg_icon_button);
        btnPlus.setImageResource(R.drawable.ic_plus);
        btnPlus.setPadding(dp(9), dp(9), dp(9), dp(9));
        btnPlus.setOnClickListener(v -> {
            fontStep = Math.min(fontStep + 1, 2);
            applyFontSize();
        });

        container.addView(btnMinus);
        container.addView(btnPlus);
        return container;
    }

    private void applyFontSize() {
        float sizeSp = FONT_SIZES_SP[fontStep + 1];
        for (int i = 0; i < llBody.getChildCount(); i++) {
            ((TextView) llBody.getChildAt(i)).setTextSize(sizeSp);
        }
    }

    // ===================== HERO / HEADER / BODY =====================

    private void setupHero() {
        View includeHeroArt = rootView.findViewById(R.id.includeHeroArt);
        ArticleArtBinder.bind(includeHeroArt, article.category, 130);

        CategoryStyle style = ArticleRepository.styleFor(article.category);
        int accentColor = getResources().getColor(style.accentColorRes);
        updateSaveIcon(accentColor);

        btnSave.setOnClickListener(v -> {
            saved = !saved;
            updateSaveIcon(accentColor);
        });
    }

    private void updateSaveIcon(int accentColor) {
        btnSave.setImageResource(saved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
        btnSave.setColorFilter(accentColor);
    }

    private void setupHeader() {
        tvTag.setText(article.tagEn);
        tvTime.setText(article.time);
        tvTitle.setText(article.title);

        if (article.author != null && !article.author.isEmpty()) {
            tvAuthor.setVisibility(View.VISIBLE);
            tvAuthor.setText(getString(R.string.by_author_format, article.author));
        } else {
            tvAuthor.setVisibility(View.GONE);
        }
    }

    private void setupBody() {
        llBody.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        for (String paragraph : article.body) {
            TextView tv = new TextView(requireContext());
            tv.setText(paragraph);
            tv.setTextColor(getResources().getColor(R.color.text_main));
            tv.setTextSize(FONT_SIZES_SP[fontStep + 1]);
            tv.setLineSpacing(0f, 1.9f);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = (int) (16 * density);
            tv.setLayoutParams(lp);
            llBody.addView(tv);
        }
    }

    private void setupRelatedExercise() {
        if (article.relatedExercise == null) {
            groupRelatedExercise.setVisibility(View.GONE);
            return;
        }
        groupRelatedExercise.setVisibility(View.VISIBLE);
        ivRelatedIcon.setImageResource(article.relatedExercise.iconRes);
        tvRelatedLabel.setText(article.relatedExercise.label);
    }

    // ===================== FEEDBACK =====================

    private void setupFeedback() {
        renderFeedbackButtons();

        btnThumbUp.setOnClickListener(v -> {
            feedback = "up";
            showReasons = false;
            renderFeedbackButtons();
            renderReasonsPanel(false);
            toastController.show(getString(R.string.toast_liked));
        });

        btnThumbDown.setOnClickListener(v -> {
            feedback = "down";
            showReasons = true;
            renderFeedbackButtons();
            renderReasonsPanel(true);
        });

        buildReasonChips();
    }

    private void renderFeedbackButtons() {
        boolean up = "up".equals(feedback);
        boolean down = "down".equals(feedback);

        btnThumbUp.setBackgroundResource(up ? R.drawable.bg_reaction_like : R.drawable.bg_reaction_neutral);
        btnThumbUp.setImageResource(up ? R.drawable.ic_thumb_up_filled : R.drawable.ic_thumb_up_outline);
        if (!up) btnThumbUp.setColorFilter(getResources().getColor(R.color.text_soft));
        else btnThumbUp.clearColorFilter();

        btnThumbDown.setBackgroundResource(down ? R.drawable.bg_reaction_dislike : R.drawable.bg_reaction_neutral);
        btnThumbDown.setImageResource(down ? R.drawable.ic_thumb_down_filled : R.drawable.ic_thumb_down_outline);
        if (!down) btnThumbDown.setColorFilter(getResources().getColor(R.color.text_soft));
        else btnThumbDown.clearColorFilter();
    }

    private void buildReasonChips() {
        flexReasons.removeAllViews();
        for (String reason : ArticleRepository.DISLIKE_REASONS) {
            TextView chip = (TextView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_reason_chip, flexReasons, false);
            chip.setText(reason);
            applyReasonChipStyle(chip, reason);
            chip.setOnClickListener(v -> {
                selectedReason = reason;
                showReasons = false;
                renderReasonsPanel(false);
                refreshReasonChipStyles();
                toastController.show(getString(R.string.toast_reason_thanks));
            });
            flexReasons.addView(chip);
        }
    }

    private void refreshReasonChipStyles() {
        for (int i = 0; i < flexReasons.getChildCount(); i++) {
            TextView chip = (TextView) flexReasons.getChildAt(i);
            applyReasonChipStyle(chip, chip.getText().toString());
        }
    }

    private void applyReasonChipStyle(TextView chip, String reason) {
        boolean selected = reason.equals(selectedReason);
        chip.setBackgroundResource(selected ? R.drawable.bg_reason_chip_selected : R.drawable.bg_reason_chip);
        chip.setTextColor(getResources().getColor(selected ? R.color.white : R.color.text_soft));
    }

    private void renderReasonsPanel(boolean animate) {
        groupReasons.setVisibility(showReasons ? View.VISIBLE : View.GONE);
        if (showReasons && animate) {
            FadeUtils.reasonFade(groupReasons);
        }
    }

    // ===================== WHY SUGGESTED =====================

    private void setupWhy() {
        tvWhyReason.setText(article.reason);
        btnWhy.setOnClickListener(v -> {
            showWhy = !showWhy;
            tvWhyReason.setVisibility(showWhy ? View.VISIBLE : View.GONE);
        });
    }

    // ===================== SUGGESTIONS =====================

    private void setupSuggestions() {
        List<Article> suggestions = ArticleRepository.getSuggestions(article);
        boolean hasSuggestions = !suggestions.isEmpty();

        tvSuggestionsLabel.setVisibility(hasSuggestions ? View.VISIBLE : View.GONE);
        llSuggestions.setVisibility(hasSuggestions ? View.VISIBLE : View.GONE);

        LinearLayout container = (LinearLayout) llSuggestions;
        container.removeAllViews();

        int gutter = dp(6);
        for (Article suggestion : suggestions) {
            View card = ArticleCardBinder.create(LayoutInflater.from(requireContext()), container);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(gutter, 0, gutter, 0);
            card.setLayoutParams(lp);

            // Suggestions are read-only previews in the original (onSave={() => {}}),
            // so bookmarking here always reports "not saved" without persisting.
            ArticleCardBinder.bind(card, suggestion, false,
                    a -> {
                        if (getActivity() instanceof ArticlesActivity) {
                            ((ArticlesActivity) getActivity()).openReader(a);
                        }
                    },
                    id -> false);

            container.addView(card);
        }
    }

    // ===================== SCROLL PROGRESS =====================

    private void setupScrollProgress() {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View content = scrollView.getChildAt(0);
            if (content == null) return;
            int scrollableHeight = content.getHeight() - scrollView.getHeight();
            float pct = scrollableHeight > 0 ? (scrollY / (float) scrollableHeight) * 100f : 0f;
            pct = Math.min(100f, Math.max(0f, pct));
            updateProgressFill(pct);
        });
    }

    private void updateProgressFill(float pct) {
        progressTrack.post(() -> {
            ViewGroup.LayoutParams lp = progressFill.getLayoutParams();
            lp.width = Math.round(progressTrack.getWidth() * (pct / 100f));
            progressFill.setLayoutParams(lp);
        });
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (toastController != null) toastController.clear();
        rootView = null;
    }
}
