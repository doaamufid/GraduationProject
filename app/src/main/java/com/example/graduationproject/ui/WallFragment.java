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
    private ViewPager2 textSlider;
    private ViewPager2 imageSlider;
    private LinearLayout textDotsContainer;
    private LinearLayout imageDotsContainer;
    private LinearLayout gridColumnRight;
    private LinearLayout gridColumnLeft;
    private LinearLayout gridContainer;
    private TextView pinnedBadge;
    private ImageView pinnedBtnIcon;

    private final Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private Runnable textAutoScrollRunnable;
    private Runnable imageAutoScrollRunnable;
    private boolean autoScrollPaused = false;
    private List<Message> textSlides;
    private List<Message> imageSlides;

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

        view.findViewById(R.id.textSliderContainer).startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in));
        view.findViewById(R.id.imageSliderContainer).startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in));

        textSlider = view.findViewById(R.id.textSlider);
        imageSlider = view.findViewById(R.id.imageSlider);
        textDotsContainer = view.findViewById(R.id.textDotsContainer);
        imageDotsContainer = view.findViewById(R.id.imageDotsContainer);
        gridColumnRight = view.findViewById(R.id.gridColumnRight);
        gridColumnLeft = view.findViewById(R.id.gridColumnLeft);
        gridContainer = view.findViewById(R.id.gridContainer);
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
        setupSliders(activity);
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

    private void setupSliders(SalamCommunityActivity activity) {
        List<Message> allTop = Repository.get().getTopSlides();
        textSlides = new java.util.ArrayList<>();
        imageSlides = new java.util.ArrayList<>();

        for (Message m : allTop) {
            if (m.img == null || m.img.isEmpty()) {
                textSlides.add(m);
            } else {
                imageSlides.add(m);
            }
        }

        if (textSlides != null && !textSlides.isEmpty()) {
            setupIndividualSlider(textSlider, textDotsContainer, textSlides, activity, true);
        }

        if (imageSlides != null && !imageSlides.isEmpty()) {
            setupIndividualSlider(imageSlider, imageDotsContainer, imageSlides, activity, false);
        }
        
        updateContentVisibility();
    }

    private void setupIndividualSlider(ViewPager2 slider, LinearLayout dots, List<Message> slides, SalamCommunityActivity activity, boolean isText) {
        SliderAdapter adapter = new SliderAdapter(slides, activity);
        slider.setAdapter(adapter);
        slider.setOffscreenPageLimit(3);
        slider.setPageTransformer((page, position) -> {
            page.setTranslationX(0);
            page.setScaleX(1f);
            page.setScaleY(1f);
            page.setAlpha(1f);
        });

        buildDots(dots, slides.size(), slider);

        slider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateActiveDot(dots, position);
            }
        });

        RecyclerView inner = (RecyclerView) slider.getChildAt(0);
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

        Runnable scroll = new Runnable() {
            @Override
            public void run() {
                if (!autoScrollPaused && slides.size() > 1 && isAdded()) {
                    int next = (slider.getCurrentItem() + 1) % slides.size();
                    slider.setCurrentItem(next, true);
                }
                autoScrollHandler.postDelayed(this, 4200);
            }
        };
        if (isText) textAutoScrollRunnable = scroll;
        else imageAutoScrollRunnable = scroll;

        autoScrollHandler.postDelayed(scroll, 4200);
    }

    private void buildDots(LinearLayout container, int count, ViewPager2 slider) {
        container.removeAllViews();
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
                slider.setCurrentItem(idx, true);
                autoScrollHandler.postDelayed(() -> autoScrollPaused = false, 4500);
            });
            container.addView(dot);
        }
    }

    private void updateActiveDot(LinearLayout container, int activeIndex) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View dot = container.getChildAt(i);
            ViewGroup.LayoutParams lp = dot.getLayoutParams();
            boolean active = i == activeIndex;
            lp.width = dpToPx(active ? 18 : 6);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(active ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
        }
    }

    private void rebuildGrid() {
        updateContentVisibility();
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

    private void updateContentVisibility() {
        String current = Repository.get().getCurrentCategory();
        boolean isMostInspiring = current.equals("الأكثر إلهاما");

        View root = getView();
        if (root == null) return;

        View textSliderContainer = root.findViewById(R.id.textSliderContainer);
        View imageSliderContainer = root.findViewById(R.id.imageSliderContainer);

        if (isMostInspiring) {
            textSliderContainer.setVisibility(textSlides != null && !textSlides.isEmpty() ? View.VISIBLE : View.GONE);
            imageSliderContainer.setVisibility(imageSlides != null && !imageSlides.isEmpty() ? View.VISIBLE : View.GONE);
            gridContainer.setVisibility(View.GONE);
        } else {
            textSliderContainer.setVisibility(View.GONE);
            imageSliderContainer.setVisibility(View.GONE);
            gridContainer.setVisibility(View.VISIBLE);
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
        if (textAutoScrollRunnable != null) autoScrollHandler.removeCallbacks(textAutoScrollRunnable);
        if (imageAutoScrollRunnable != null) autoScrollHandler.removeCallbacks(imageAutoScrollRunnable);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
