package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Equivalent of <Library/>: category filter chips + the list of content
 * cards, filtered by the currently-selected category (mirrors the
 * `const [cat, setCat] = useState("الكل")` state).
 */
public class LibraryFragment extends Fragment {

    private String selectedCategory = "الكل";
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

        // ضبط ألوان الـ TopBar لتناسب الخلفية الفاتحة
        TextView tvTitle = root.findViewById(R.id.tvTopBarTitle);
        if (tvTitle != null) tvTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_dark));
        TextView tvSubtitle = root.findViewById(R.id.tvTopBarSubtitle);
        if (tvSubtitle != null) tvSubtitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_soft_alt2));
        View btnBack = root.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setBackgroundResource(R.drawable.bg_fav_circle); // استخدام نفس شكل الدائرة البيضاء
        }

        llCategories = root.findViewById(R.id.llCategories);
        llItems = root.findViewById(R.id.llItems);

        buildCategoryChips();
        renderItems();

        return root;
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

        for (ContentItem item : filtered) {
            View card = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_content_card, llItems, false);

            View thumbGradient = card.findViewById(R.id.thumbGradient);
            ImageView ivTypeIcon = card.findViewById(R.id.ivTypeIcon);
            TextView tvDuration = card.findViewById(R.id.tvDuration);
            TextView tvTitle = card.findViewById(R.id.tvTitle);
            TextView tvSrc = card.findViewById(R.id.tvSrc);
            TextView tvTypePill = card.findViewById(R.id.tvTypePill);
            ImageView ivFavorite = card.findViewById(R.id.ivFavorite);

            applyGradient(thumbGradient, item.gradStart, item.gradEnd);
            ivTypeIcon.setImageResource(item.isVideo ? R.drawable.ic_play : R.drawable.ic_headphones);
            tvDuration.setText(item.duration);
            tvTitle.setText(item.title);
            tvSrc.setText(item.src);
            tvTypePill.setText(item.type);

            ivFavorite.setOnClickListener(v -> {
                // تبديل أيقونة المفضلة (شكل بسيط للتفاعل)
                v.setSelected(!v.isSelected());
                ((ImageView)v).setImageResource(v.isSelected() ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
            });

            card.setOnClickListener(v -> {
                if (getActivity() instanceof VideoLibraryActivity) {
                    ((VideoLibraryActivity) getActivity()).openPlayer(item);
                }
            });

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
