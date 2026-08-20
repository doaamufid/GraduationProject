package com.example.graduationproject.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.graduationproject.ArticlesActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.data.AppState;
import com.example.graduationproject.models.Article;
import com.example.graduationproject.models.ArticleCategory;
import com.example.graduationproject.models.Highlight;
import com.example.graduationproject.models.SavedQuote;
import com.example.graduationproject.ui.WaveArtView;

import java.util.List;

/**
 * Redesigned Article Details / Reader page following the provided image.
 * Features custom text selection toolbar for highlighting and saving quotes.
 */
public class ReaderFragment extends Fragment {

    private static final String ARG_ARTICLE_ID = "arg_article_id";
    private static final int[] HL_COLORS = {0xFF8FDDB0, 0xFF7FC8F0, 0xFFB69CE8, 0xFFFF9EC4, 0xFFFFE066};

    private Article article;
    private final ReaderSettings settings = new ReaderSettings();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private String feedback = null; // null | "up" | "down"

    private View root;
    private View heroContainer, heroFade;
    private ImageButton btnFavorite, btnBookmark, btnThumbUp, btnThumbDown;
    private View groupReasons, brightnessOverlay;
    private ChipGroup chipGroupReasons;
    private LinearLayout bodyContainer;
    private TextView badgeCount, toastView;
    private TextView[] paragraphViews;
    private Runnable toastDismiss;

    private PopupWindow selectionToolbar;

    public ReaderFragment() {
        // Required empty public constructor
    }

    public static ReaderFragment newInstance(int articleId) {
        ReaderFragment f = new ReaderFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_ARTICLE_ID, articleId);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.articles_fragment_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view;
        int articleId = requireArguments().getInt(ARG_ARTICLE_ID);
        article = com.example.graduationproject.data.ArticleRepository.getById(articleId);
        if (article == null) {
            requireActivity().onBackPressed();
            return;
        }

        bindViews(view);
        setupHero(view);
        setupMeta(view);
        setupBody();
        setupRelatedExercise(view);
        setupFeedback();
        
        NestedScrollView scrollView = view.findViewById(R.id.scrollView);
        if (scrollView != null) {
            scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (heroContainer != null) {
                    heroContainer.setTranslationY(scrollY * 0.4f);
                    heroContainer.setAlpha(Math.max(0, 1.0f - (scrollY / 600f)));
                }
            });
        }

        brightnessOverlay = view.findViewById(R.id.brightnessOverlay);
        
        applyTheme();
        refreshFavBookmarkIcons();
        refreshBadge();
    }

    private void bindViews(View view) {
        heroContainer = view.findViewById(R.id.heroContainer);
        heroFade = view.findViewById(R.id.heroFade);
        view.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());

        btnFavorite = view.findViewById(R.id.btnFavorite);
        btnBookmark = view.findViewById(R.id.btnBookmark);
        badgeCount = view.findViewById(R.id.badgeCount);

        btnFavorite.setOnClickListener(v -> {
            AppState.get().toggleSaved(article.id);
            refreshFavBookmarkIcons();
        });
        btnBookmark.setOnClickListener(v -> {
            AppState.get().toggleBookmarked(article.id);
            refreshFavBookmarkIcons();
        });

        view.findViewById(R.id.btnNotes).setOnClickListener(v -> {
            if (getActivity() instanceof ArticlesActivity) {
                ((ArticlesActivity) getActivity()).openNotes();
            }
        });
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> 
                ReadingSettingsSheet.newInstance(settings, this::onSettingsChanged)
                        .show(getChildFragmentManager(), "reading_settings"));

        bodyContainer = view.findViewById(R.id.bodyContainer);
        groupReasons = view.findViewById(R.id.groupReasons);
        chipGroupReasons = view.findViewById(R.id.chipGroupReasons);
        btnThumbUp = view.findViewById(R.id.btnThumbUp);
        btnThumbDown = view.findViewById(R.id.btnThumbDown);
        toastView = view.findViewById(R.id.toastView);
    }

    private void setupHero(View view) {
        WaveArtView waveArt = view.findViewById(R.id.waveArt);
        if (waveArt != null) {
            waveArt.setColors(ArticleCategory.gradientColors(requireContext(), article.category));
        }
    }

    private void setupMeta(View view) {
        TextView tvCategoryIcon = view.findViewById(R.id.tvCategoryIcon);
        TextView tvCategoryPill = view.findViewById(R.id.tvCategoryPill);
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvAuthor = view.findViewById(R.id.tvAuthor);

        tvCategoryIcon.setText(ArticleCategory.getIcon(article.category));
        tvCategoryPill.setText(article.category);
        tvTime.setText(article.time);
        tvTitle.setText(article.title);
        tvAuthor.setText(getString(R.string.by_author, article.author));
    }

    private void setupBody() {
        bodyContainer.removeAllViews();
        paragraphViews = new TextView[article.body.length];
        float density = getResources().getDisplayMetrics().density;

        for (int i = 0; i < article.body.length; i++) {
            final int paragraphIndex = i;
            TextView tv = new TextView(getContext());
            tv.setTextIsSelectable(true);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setCustomSelectionActionModeCallback(new CustomSelectionCallback(tv, paragraphIndex));
            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = (int) (16 * density);
            bodyContainer.addView(tv, lp);
            paragraphViews[i] = tv;
        }
        renderAllParagraphs();
    }

    private void renderAllParagraphs() {
        if (paragraphViews == null) return;
        for (int i = 0; i < paragraphViews.length; i++) renderParagraph(i);
    }

    private void renderParagraph(int index) {
        if (paragraphViews == null || index >= paragraphViews.length) return;
        String paragraph = article.body[index];
        SpannableString spannable = new SpannableString(paragraph);
        List<Highlight> highlights = AppState.get().getHighlightsForArticle(article.id);

        int textColor;
        switch (settings.theme) {
            case ReaderSettings.THEME_TRADITIONAL:
                textColor = getResources().getColor(R.color.theme_traditional_text);
                break;
            case ReaderSettings.THEME_NIGHT:
                textColor = getResources().getColor(R.color.theme_night_text);
                break;
            case ReaderSettings.THEME_TYPEWRITER:
                textColor = getResources().getColor(R.color.theme_typewriter_text);
                break;
            default:
                textColor = getResources().getColor(R.color.text);
        }

        float density = getResources().getDisplayMetrics().density;

        for (Highlight h : highlights) {
            if (h.paragraphIndex != index || !h.active) continue;
            int start = paragraph.indexOf(h.text);
            if (start < 0) continue;
            int end = start + h.text.length();

            spannable.setSpan(new RoundedBackgroundSpan(h.color, textColor, 6 * density, 2 * density, 4 * density),
                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    openNoteComposer(h);
                }
                @Override
                public void updateDrawState(@NonNull android.text.TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        paragraphViews[index].setText(spannable);
        applyParagraphStyle(paragraphViews[index]);
    }

    private void applyParagraphStyle(TextView tv) {
        Typeface typeface = Typeface.DEFAULT;
        if (ReaderSettings.THEME_TYPEWRITER.equals(settings.theme)) {
            typeface = Typeface.MONOSPACE;
        }
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.fontSize);
        tv.setTypeface(typeface, ReaderSettings.WEIGHT_BOLD.equals(settings.weight) ? Typeface.BOLD : Typeface.NORMAL);
        tv.setLineSpacing(0, 1.6f);
        tv.setGravity(Gravity.START);
        
        int textColor;
        switch (settings.theme) {
            case ReaderSettings.THEME_TRADITIONAL: textColor = getResources().getColor(R.color.theme_traditional_text); break;
            case ReaderSettings.THEME_NIGHT: textColor = getResources().getColor(R.color.theme_night_text); break;
            case ReaderSettings.THEME_TYPEWRITER: textColor = getResources().getColor(R.color.theme_typewriter_text); break;
            default: textColor = getResources().getColor(R.color.text);
        }
        tv.setTextColor(textColor);
    }

    private void openNoteComposer(Highlight h) {
        NoteComposerSheet.newInstance(h, new NoteComposerSheet.Listener() {
            @Override public void onNoteChanged(Highlight highlight, String note) {
                highlight.note = note;
            }
            @Override public void onColorChanged(Highlight highlight, int color) {
                highlight.color = color;
                renderParagraph(highlight.paragraphIndex);
            }
            @Override public void onActiveToggled(Highlight highlight, boolean active) {
                highlight.active = active;
                renderParagraph(highlight.paragraphIndex);
            }
            @Override public void onDeleted(Highlight highlight) {
                AppState.get().removeHighlight(highlight.id);
                renderParagraph(highlight.paragraphIndex);
                refreshBadge();
            }
            @Override public void onToggleSavedQuote(Highlight highlight) {
                AppState.get().toggleSavedQuoteForHighlight(highlight);
            }
            @Override public boolean isQuoteSaved(Highlight highlight) {
                return AppState.get().isQuoteSaved(highlight.articleId, highlight.text);
            }
            @Override public void onClosed() {}
        }).show(getChildFragmentManager(), "note_composer");
    }

    private class CustomSelectionCallback implements ActionMode.Callback {
        private final TextView tv;
        private final int paragraphIndex;

        CustomSelectionCallback(TextView tv, int paragraphIndex) {
            this.tv = tv;
            this.paragraphIndex = paragraphIndex;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            showSelectionToolbar(tv, paragraphIndex, mode);
            return false; // We don't want the default Android selection menu
        }

        @Override public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }
        @Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) { return false; }
        @Override public void onDestroyActionMode(ActionMode mode) {}
    }

    private void showSelectionToolbar(TextView tv, int paragraphIndex, ActionMode mode) {
        if (selectionToolbar != null && selectionToolbar.isShowing()) selectionToolbar.dismiss();

        View toolbarView = LayoutInflater.from(getContext()).inflate(R.layout.articles_layout_selection_toolbar, null);
        selectionToolbar = new PopupWindow(toolbarView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        selectionToolbar.setOutsideTouchable(true);

        int start = tv.getSelectionStart();
        int end = tv.getSelectionEnd();
        if (start < 0 || end < 0 || start == end) return;
        String selectedText = tv.getText().toString().substring(Math.min(start, end), Math.max(start, end));

        toolbarView.findViewById(R.id.btnDismiss).setOnClickListener(v -> {
            selectionToolbar.dismiss();
            mode.finish();
        });
        toolbarView.findViewById(R.id.btnFav).setOnClickListener(v -> {
            boolean isSaved = AppState.get().isQuoteSaved(article.id, selectedText);
            if (isSaved) {
                // Find the ID to remove it
                for (SavedQuote q : AppState.get().getSavedQuotes()) {
                    if (q.articleId == article.id && q.text.equals(selectedText)) {
                        AppState.get().removeSavedQuote(q.id);
                        break;
                    }
                }
            } else {
                AppState.get().addSavedQuote(new SavedQuote(System.currentTimeMillis(), article.id, article.title, selectedText, "الآن"));
            }
            updateFavIcon((ImageButton) v, !isSaved);
        });

        // Initialize icon state
        updateFavIcon(toolbarView.findViewById(R.id.btnFav), AppState.get().isQuoteSaved(article.id, selectedText));
        toolbarView.findViewById(R.id.btnNote).setOnClickListener(v -> {
            Highlight h = createHighlight(paragraphIndex, selectedText, HL_COLORS[0]);
            openNoteComposer(h);
            selectionToolbar.dismiss();
            mode.finish();
        });

        int[] dotIds = {R.id.dotGreen, R.id.dotBlue, R.id.dotPurple, R.id.dotPink, R.id.dotYellow};
        for (int i = 0; i < dotIds.length; i++) {
            final int color = HL_COLORS[i];
            toolbarView.findViewById(dotIds[i]).setOnClickListener(v -> {
                createHighlight(paragraphIndex, selectedText, color);
                selectionToolbar.dismiss();
                mode.finish();
            });
        }

        // Position popup above selection
        tv.post(() -> {
            Rect rect = new Rect();
            tv.getGlobalVisibleRect(rect);
            int y = rect.top - 80; 
            selectionToolbar.showAtLocation(tv, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, y);
        });
    }

    private void updateFavIcon(ImageButton btn, boolean isSaved) {
        btn.setImageResource(isSaved ? R.drawable.ic_heart : R.drawable.ic_heart_outline);
        if (isSaved) {
            btn.setColorFilter(getResources().getColor(R.color.rose));
        } else {
            btn.setColorFilter(Color.WHITE);
        }
    }

    private Highlight createHighlight(int paragraphIndex, String text, int color) {
        Highlight h = new Highlight(System.currentTimeMillis(), article.id, article.title,
                paragraphIndex, text, color, "", true, "الآن");
        AppState.get().addHighlight(h);
        renderParagraph(paragraphIndex);
        refreshBadge();
        return h;
    }

    private void setupRelatedExercise(View view) {
        View group = view.findViewById(R.id.groupRelatedExercise);
        if (article.relatedExerciseLabel == null || article.relatedExerciseLabel.isEmpty()) {
            group.setVisibility(View.GONE);
            return;
        }
        group.setVisibility(View.VISIBLE);
        TextView tvLabel = view.findViewById(R.id.tvRelatedLabel);
        tvLabel.setText(article.relatedExerciseLabel);
    }

    private void setupFeedback() {
        btnThumbUp.setOnClickListener(v -> toggleFeedback("up"));
        btnThumbDown.setOnClickListener(v -> toggleFeedback("down"));

        String[] reasons = {"طويلة زيادة", "مو مناسبة لمزاجي الآن", "معلومات أعرفها أصلاً", "لغة صعبة", "غير ذلك"};
        chipGroupReasons.removeAllViews();
        for (String reason : reasons) {
            Chip chip = new Chip(requireContext());
            chip.setText(reason);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
            chip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.border)));
            chip.setChipStrokeWidth(1 * getResources().getDisplayMetrics().density);
            chip.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.textSoft)));
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            chip.setCloseIconVisible(false);
            chip.setCheckable(false);
            chipGroupReasons.addView(chip);
        }
    }

    private void toggleFeedback(String type) {
        if (type.equals(feedback)) {
            feedback = null;
        } else {
            feedback = type;
        }

        boolean up = "up".equals(feedback);
        boolean down = "down".equals(feedback);

        btnThumbUp.setBackgroundResource(up ? R.drawable.bg_circle_primary : R.drawable.bg_circle_surface);
        btnThumbUp.setColorFilter(up ? Color.WHITE : getResources().getColor(R.color.textSoft));

        btnThumbDown.setBackgroundResource(down ? R.drawable.bg_circle_rose : R.drawable.bg_circle_surface);
        btnThumbDown.setColorFilter(down ? Color.WHITE : getResources().getColor(R.color.textSoft));

        groupReasons.setVisibility(down ? View.VISIBLE : View.GONE);
        if (up) showToast(getString(R.string.toast_liked));
    }

    private void showToast(String message) {
        if (toastView == null) return;
        toastView.clearAnimation();
        if (toastDismiss != null) handler.removeCallbacks(toastDismiss);

        toastView.setText(message);
        toastView.setVisibility(View.VISIBLE);
        toastView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.toast_in));

        toastDismiss = () -> {
            toastView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.toast_out));
            toastView.postDelayed(() -> toastView.setVisibility(View.GONE), 220);
        };
        handler.postDelayed(toastDismiss, 2500);
    }

    private void onSettingsChanged(ReaderSettings s) {
        applyTheme();
    }

    private void applyTheme() {
        int bgColor, textColor;
        switch (settings.theme) {
            case ReaderSettings.THEME_TRADITIONAL:
                bgColor = getResources().getColor(R.color.theme_traditional_bg);
                textColor = getResources().getColor(R.color.theme_traditional_text);
                break;
            case ReaderSettings.THEME_NIGHT:
                bgColor = getResources().getColor(R.color.theme_night_bg);
                textColor = getResources().getColor(R.color.theme_night_text);
                break;
            case ReaderSettings.THEME_TYPEWRITER:
                bgColor = getResources().getColor(R.color.theme_typewriter_bg);
                textColor = getResources().getColor(R.color.theme_typewriter_text);
                break;
            default:
                bgColor = getResources().getColor(R.color.bg);
                textColor = getResources().getColor(R.color.text);
        }

        root.setBackgroundColor(bgColor);
        if (heroFade != null) {
            heroFade.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        }
        ((TextView) root.findViewById(R.id.tvTitle)).setTextColor(textColor);
        ((TextView) root.findViewById(R.id.tvAuthor)).setTextColor(textColor);
        ((TextView) root.findViewById(R.id.tvTime)).setTextColor(textColor);

        if (paragraphViews != null) {
            for (TextView tv : paragraphViews) applyParagraphStyle(tv);
        }

        float dim = Math.max(0f, (100 - settings.brightness) / 100f) * 0.7f;
        brightnessOverlay.setAlpha(dim);
        brightnessOverlay.setVisibility(dim > 0 ? View.VISIBLE : View.GONE);
    }

    private void refreshFavBookmarkIcons() {
        boolean saved = AppState.get().isSaved(article.id);
        boolean bookmarked = AppState.get().isBookmarked(article.id);
        btnFavorite.setImageResource(saved ? R.drawable.ic_heart : R.drawable.ic_heart_outline);
        btnFavorite.setBackgroundResource(saved ? R.drawable.bg_circle_rose : R.drawable.bg_circle_surface);
        btnBookmark.setImageResource(bookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark);
        btnBookmark.setBackgroundResource(bookmarked ? R.drawable.bg_circle_primary : R.drawable.bg_circle_surface);
    }

    private void refreshBadge() {
        int count = AppState.get().getHighlightsForArticle(article.id).size();
        badgeCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        badgeCount.setText(String.valueOf(count));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (paragraphViews != null) renderAllParagraphs();
        refreshBadge();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (selectionToolbar != null && selectionToolbar.isShowing()) selectionToolbar.dismiss();
        if (toastDismiss != null) handler.removeCallbacks(toastDismiss);
    }
}
