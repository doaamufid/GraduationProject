package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.R;
import com.example.graduationproject.VideoLibraryActivity;
import com.example.graduationproject.models.ContentItem;
import com.example.graduationproject.models.ContentRepository;

import java.util.List;

/**
 * Enhanced LibraryFragment with high-fidelity cards and staggered animations.
 */
public class LibraryFragment extends Fragment {

    private String selectedCategory = "الكل";
    private String searchQuery = "";
    private LinearLayout llCategories;
    private LinearLayout llItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_library, container, false);

        TopBarHelper.bind(root, getString(R.string.library_title), getString(R.string.library_sub),
                () -> {
                    if (getActivity() != null) getActivity().onBackPressed();
                }, null);

        // Adjust TopBar colors for light background
        TextView tvTitle = root.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) {
            tvTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_dark));
            tvTitle.setTextSize(20); // Make header slightly bigger
        }
        TextView tvSubtitle = root.findViewById(R.id.tvTopBarSubtitle);
        if (tvSubtitle != null) tvSubtitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_soft_alt2));
        View btnBack = root.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setBackgroundResource(R.drawable.bg_icon_button);
        }

        llCategories = root.findViewById(R.id.llCategories);
        llItems = root.findViewById(R.id.llItems);

        // Setup Search Bar
        View searchLayout = root.findViewById(R.id.layoutSearchBarIncluded);
        if (searchLayout != null) {
            android.widget.EditText etSearch = searchLayout.findViewById(R.id.etSearch);
            etSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchQuery = s.toString();
                    renderItems();
                }
                @Override public void afterTextChanged(android.text.Editable s) {}
            });
        }

        animateEntrance();
        buildCategoryChips();
        renderItems();

        return root;
    }

    private void animateEntrance() {
        llCategories.setAlpha(0f);
        llCategories.setTranslationX(-50f);
        llCategories.animate().alpha(1f).translationX(0f).setDuration(600).setStartDelay(200).setInterpolator(new DecelerateInterpolator()).start();
        
        View searchLayout = getView() != null ? getView().findViewById(R.id.layoutSearchBarIncluded) : null;
        if (searchLayout != null) {
            searchLayout.setAlpha(0f);
            searchLayout.setScaleX(0.9f);
            searchLayout.animate().alpha(1f).scaleX(1f).setDuration(600).setStartDelay(100).start();
        }
    }

    private void buildCategoryChips() {
        llCategories.removeAllViews();
        for (String category : ContentRepository.CATEGORIES) {
            TextView chip = (TextView) LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_category_chip, llCategories, false);
            chip.setText(category);
            chip.setOnClickListener(v -> {
                selectedCategory = category;
                renderCategoryChips();
                renderItems();
            });
            llCategories.addView(chip);
        }
        renderCategoryChips();
    }

    private void renderCategoryChips() {
        for (int i = 0; i < llCategories.getChildCount(); i++) {
            TextView chip = (TextView) llCategories.getChildAt(i);
            boolean selected = chip.getText().toString().equals(selectedCategory);
            chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
            int textColor = ContextCompat.getColor(requireContext(),
                    selected ? R.color.white : R.color.text_soft_alt2);
            chip.setTextColor(textColor);
        }
    }

    private void renderItems() {
        llItems.removeAllViews();
        List<ContentItem> filtered = ContentRepository.filterByCategory(selectedCategory);
        
        // Secondary filtering by search query
        List<ContentItem> finalFiltered = new java.util.ArrayList<>();
        for (ContentItem item : filtered) {
            if (searchQuery.isEmpty() || item.title.toLowerCase().contains(searchQuery.toLowerCase())
                    || item.src.toLowerCase().contains(searchQuery.toLowerCase())) {
                finalFiltered.add(item);
            }
        }

        for (int i = 0; i < finalFiltered.size(); i++) {
            ContentItem item = finalFiltered.get(i);
            View card = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_content_card, llItems, false);

            View thumbGradient = card.findViewById(R.id.thumbGradient);
            ImageView ivTypeIcon = card.findViewById(R.id.ivTypeIcon);
            TextView tvDuration = card.findViewById(R.id.tvDuration);
            TextView tvTitle = card.findViewById(R.id.tvTitle);
            TextView tvSrc = card.findViewById(R.id.tvSrc);
            View btnWatch = card.findViewById(R.id.btnWatch);

            applyGradient(thumbGradient, item.gradStart, item.gradEnd);
            ivTypeIcon.setImageResource(item.isVideo ? R.drawable.ic_play : R.drawable.ic_headphones);
            tvDuration.setText(item.duration);
            tvTitle.setText(item.title);
            tvSrc.setText(item.src + " | " + item.type);

            card.setOnClickListener(v -> {
                if (getActivity() instanceof VideoLibraryActivity) {
                    ((VideoLibraryActivity) getActivity()).openPlayer(item);
                }
            });
            
            if (btnWatch != null) {
                btnWatch.setOnClickListener(v -> card.performClick());
            }

            // Staggered Entrance Animation
            card.setAlpha(0f);
            card.setTranslationY(60f);
            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .setStartDelay(300 + (i * 120L))
                    .setInterpolator(new OvershootInterpolator(0.8f))
                    .start();

            llItems.addView(card);
        }
    }

    private void applyGradient(View view, int startColor, int endColor) {
        android.graphics.drawable.GradientDrawable gradient = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TL_BR,
                new int[]{startColor, endColor});
        gradient.setCornerRadius(12 * getResources().getDisplayMetrics().density);
        view.setBackground(gradient);
    }
}
