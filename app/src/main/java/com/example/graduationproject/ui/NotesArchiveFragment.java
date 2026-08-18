package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.R;
import com.example.graduationproject.data.AppState;
import com.example.graduationproject.models.Highlight;
import com.example.graduationproject.models.SavedQuote;

import java.util.ArrayList;
import java.util.List;

/**
 * Java port of <NotesArchiveScreen/>: three tabs — favorites (saved quotes), notes
 * (highlights that carry a note) and highlights (every highlight, active or not).
 */
public class NotesArchiveFragment extends Fragment {

    private static final String TAB_FAVORITES = "favorites";
    private static final String TAB_NOTES = "notes";
    private static final String TAB_HIGHLIGHTS = "highlights";

    private String activeTab = TAB_FAVORITES;

    private LinearLayout tabContainer;
    private LinearLayout favPanel;
    private LinearLayout favQuoteList;
    private LinearLayout plainList;
    private TextView tvFavCount;
    private TextView tvEmpty;

    public static NotesArchiveFragment newInstance() {
        return new NotesArchiveFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.articles_fragment_notes_archive, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View topBar = view.findViewById(R.id.topBar);
        ((TextView) topBar.findViewById(R.id.tvTopBarTitle)).setText(R.string.my_notes_title);
        topBar.findViewById(R.id.topBarRightSlot).setVisibility(View.INVISIBLE);
        topBar.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());

        tabContainer = view.findViewById(R.id.tabContainer);
        favPanel = view.findViewById(R.id.favPanel);
        favQuoteList = view.findViewById(R.id.favQuoteList);
        plainList = view.findViewById(R.id.plainList);
        tvFavCount = view.findViewById(R.id.tvFavCount);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        buildTabs();
        render();
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void buildTabs() {
        tabContainer.removeAllViews();
        String[][] tabs = {
                {TAB_FAVORITES, getString(R.string.tab_favorites)},
                {TAB_NOTES, getString(R.string.tab_notes)},
                {TAB_HIGHLIGHTS, getString(R.string.tab_highlights)}
        };
        for (String[] tab : tabs) {
            TextView chip = (TextView) LayoutInflater.from(getContext())
                    .inflate(R.layout.articles_item_tab_chip, tabContainer, false);
            chip.setText(tab[1]);
            styleTab(chip, tab[0].equals(activeTab));
            chip.setOnClickListener(v -> {
                activeTab = tab[0];
                buildTabs();
                render();
            });
            tabContainer.addView(chip);
        }
    }

    private void styleTab(TextView chip, boolean selected) {
        chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chip.setTextColor(getResources().getColor(selected ? R.color.white : R.color.textSoft));
    }

    private void render() {
        boolean isFav = TAB_FAVORITES.equals(activeTab);
        favPanel.setVisibility(isFav ? View.VISIBLE : View.GONE);
        plainList.setVisibility(isFav ? View.GONE : View.VISIBLE);

        if (isFav) {
            renderFavorites();
        } else {
            renderHighlightList();
        }
    }

    private void renderFavorites() {
        List<SavedQuote> quotes = AppState.get().getSavedQuotes();
        favQuoteList.removeAllViews();

        if (quotes.isEmpty()) {
            favPanel.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(R.string.empty_fav_quotes);
            return;
        }
        tvEmpty.setVisibility(View.GONE);
        favPanel.setVisibility(View.VISIBLE);
        tvFavCount.setText(String.valueOf(quotes.size()));

        for (SavedQuote q : quotes) {
            View card = LayoutInflater.from(getContext()).inflate(R.layout.articles_item_quote_card, favQuoteList, false);
            ((TextView) card.findViewById(R.id.tvArticleTitle)).setText(q.articleTitle);
            ((TextView) card.findViewById(R.id.tvQuoteText)).setText("\"" + q.text + "\"");
            ((TextView) card.findViewById(R.id.tvDate)).setText(q.date);
            card.findViewById(R.id.btnDelete).setOnClickListener(v -> {
                AppState.get().removeSavedQuote(q.id);
                renderFavorites();
            });
            favQuoteList.addView(card);
        }
    }

    private void renderHighlightList() {
        List<Highlight> source = new ArrayList<>();
        if (TAB_HIGHLIGHTS.equals(activeTab)) {
            source.addAll(AppState.get().getHighlights());
        } else {
            for (Highlight h : AppState.get().getHighlights()) {
                if (h.note != null && !h.note.trim().isEmpty()) source.add(h);
            }
        }

        plainList.removeAllViews();
        if (source.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(R.string.empty_generic);
            plainList.setVisibility(View.GONE);
            return;
        }
        tvEmpty.setVisibility(View.GONE);
        plainList.setVisibility(View.VISIBLE);

        for (Highlight h : source) {
            View card = LayoutInflater.from(getContext()).inflate(R.layout.articles_item_highlight_card, plainList, false);
            card.findViewById(R.id.accentBar).setBackgroundColor(h.color);
            ((TextView) card.findViewById(R.id.tvArticleTitle)).setText(h.articleTitle);
            ((TextView) card.findViewById(R.id.tvQuoteText)).setText("\"" + h.text + "\"");
            ((TextView) card.findViewById(R.id.tvDate)).setText(h.date);
            TextView tvNote = card.findViewById(R.id.tvNote);
            if (h.note != null && !h.note.trim().isEmpty()) {
                tvNote.setVisibility(View.VISIBLE);
                tvNote.setText(h.note);
            } else {
                tvNote.setVisibility(View.GONE);
            }
            card.findViewById(R.id.btnDelete).setOnClickListener(v -> {
                AppState.get().removeHighlight(h.id);
                renderHighlightList();
            });
            plainList.addView(card);
        }
    }
}
