package com.example.graduationproject.ui;

import com.example.graduationproject.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.graduationproject.SalamCommunityActivity;
import com.example.graduationproject.models.Message;
import com.example.graduationproject.adapters.CategoryAdapter;
import com.example.graduationproject.adapters.SliderAdapter;
import com.example.graduationproject.data.Repository;
import com.example.graduationproject.data.SeedData;
import com.example.graduationproject.util.CardBinder;

import java.util.List;

public class WallFragment extends Fragment {

    private RecyclerView categoryRecycler;
    private ViewPager2 topSlider;
    private LinearLayout dotsContainer;
    private LinearLayout gridColumnRight;
    private LinearLayout gridColumnLeft;
    private TextView pinnedBadge;
    private ImageView pinnedBtnIcon;

    private final Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private Runnable autoScrollRunnable;
    private boolean autoScrollPaused = false;
    private SliderAdapter sliderAdapter;
    private List<Message> topSlides;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wall, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SalamCommunityActivity activity = (SalamCommunityActivity) requireActivity();

        // Animate elements
        view.findViewById(R.id.topBar).startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_fade));
        
        categoryRecycler = view.findViewById(R.id.categoryRecycler);
        categoryRecycler.startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right));

        view.findViewById(R.id.sliderContainer).startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in));

        topSlider = view.findViewById(R.id.topSlider);
        dotsContainer = view.findViewById(R.id.dotsContainer);
        gridColumnRight = view.findViewById(R.id.gridColumnRight);
        gridColumnLeft = view.findViewById(R.id.gridColumnLeft);
        pinnedBadge = view.findViewById(R.id.pinnedBadge);
        pinnedBtnIcon = view.findViewById(R.id.pinnedBtnIcon);

        view.findViewById(R.id.pinnedBtn).setOnClickListener(v -> activity.showPinned());
        view.findViewById(R.id.mineBtn).setOnClickListener(v -> activity.showMine());

        View shuffleBtn = view.findViewById(R.id.shuffleBtn);
        ImageView shuffleIcon = view.findViewById(R.id.shuffleIcon);
        shuffleBtn.setOnClickListener(v -> {
            shuffleIcon.animate().rotationBy(180f).setDuration(300).start();
            Repository.get().shuffleOrder();
            rebuildGrid();
        });

        setupCategoryChips(activity);
        setupSlider(activity);
        rebuildGrid();
        refreshPinnedBadge();

        view.findViewById(R.id.fabCompose).setOnClickListener(v -> activity.showCompose());
    }

    private void setupCategoryChips(SalamCommunityActivity activity) {
        categoryRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        CategoryAdapter adapter = new CategoryAdapter(SeedData.CATEGORIES, Repository.get().getCurrentCategory(), cat -> {
            Repository.get().setCurrentCategory(cat);
            rebuildGrid();
        });
        categoryRecycler.setAdapter(adapter);
    }

    private void setupSlider(SalamCommunityActivity activity) {
        topSlides = Repository.get().getTopSlides();
        sliderAdapter = new SliderAdapter(topSlides, activity);
        topSlider.setAdapter(sliderAdapter);
        topSlider.setOffscreenPageLimit(3);

        topSlider.setPageTransformer((page, position) -> {
            // Standard sliding animation without scaling or overlapping gaps
            page.setTranslationX(0);
            page.setScaleX(1f);
            page.setScaleY(1f);
            page.setAlpha(1f);
        });

        buildDots(topSlides.size());

        topSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateActiveDot(position);
            }
        });

        // pause autoscroll while the user is interacting, resume shortly after release
        RecyclerView inner = (RecyclerView) topSlider.getChildAt(0);
        if (inner != null) {
            inner.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    autoScrollPaused = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    autoScrollHandler.postDelayed(() -> autoScrollPaused = false, 4500);
                }
                return false;
            });
        }

        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (!autoScrollPaused && topSlides.size() > 1 && isAdded()) {
                    int next = (topSlider.getCurrentItem() + 1) % topSlides.size();
                    topSlider.setCurrentItem(next, true);
                }
                autoScrollHandler.postDelayed(this, 4200);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, 4200);
    }

    private void buildDots(int count) {
        dotsContainer.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(requireContext());
            int size = dpToPx(6);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMarginStart(dpToPx(3));
            lp.setMarginEnd(dpToPx(3));
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(i == 0 ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
            final int idx = i;
            dot.setOnClickListener(v -> {
                autoScrollPaused = true;
                topSlider.setCurrentItem(idx, true);
                autoScrollHandler.postDelayed(() -> autoScrollPaused = false, 4500);
            });
            dotsContainer.addView(dot);
        }
    }

    private void updateActiveDot(int activeIndex) {
        for (int i = 0; i < dotsContainer.getChildCount(); i++) {
            View dot = dotsContainer.getChildAt(i);
            ViewGroup.LayoutParams lp = dot.getLayoutParams();
            boolean active = i == activeIndex;
            lp.width = dpToPx(active ? 18 : 6);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(active ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
        }
    }

    private void rebuildGrid() {
        gridColumnRight.removeAllViews();
        gridColumnLeft.removeAllViews();
        List<Message> visible = Repository.get().getVisibleGrid();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        SalamCommunityActivity activity = (SalamCommunityActivity) requireActivity();

        for (int i = 0; i < visible.size(); i++) {
            Message msg = visible.get(i);
            LinearLayout target = (i % 2 == 0) ? gridColumnRight : gridColumnLeft;
            View card = CardBinder.bind(requireContext(), inflater, target, msg, i, activity);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = dpToPx(12);
            card.setLayoutParams(lp);
            card.startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.card_in));
            target.addView(card);
        }
    }

    public void refreshPinnedBadge() {
        int count = Repository.get().pinnedCount();
        if (count > 0) {
            pinnedBadge.setVisibility(View.VISIBLE);
            pinnedBadge.setText(String.valueOf(count));
            pinnedBtnIcon.setImageResource(R.drawable.ic_bookmark_filled);
        } else {
            pinnedBadge.setVisibility(View.GONE);
            pinnedBtnIcon.setImageResource(R.drawable.ic_bookmark_outline);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (autoScrollRunnable != null) autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
