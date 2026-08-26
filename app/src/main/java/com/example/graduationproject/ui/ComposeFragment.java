package com.example.graduationproject.ui;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.graduationproject.MainActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.CategoryAdapter;
import com.example.graduationproject.adapters.EmojiAdapter;
import com.example.graduationproject.data.SeedData;
import com.example.graduationproject.util.CardColors;

public class ComposeFragment extends Fragment {

    private EditText messageInput;
    private String selectedCategory = "قوة";
    private int selectedColorIndex = 0;
    private String selectedEmoji = null;
    private Uri selectedImageUri = null;

    private LinearLayout colorPickerContainer;
    private FrameLayout imagePreviewWrap;
    private ImageView imagePreview;
    private View addImageBtn;
    
    private FrameLayout emojiFold;
    private boolean emojiOpen = false;
    private ImageView emojiChevron;
    private TextView emojiToggleLabel;
    private TextView emojiSelectedIcon;
    private ImageView emojiSmileIcon;

    private FrameLayout categoryFold;
    private boolean categoryOpen = false;
    private ImageView categoryChevron;
    private TextView categorySelectedIcon;
    
    private View submitBtn;

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                showImagePreview();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Host activity may be MainActivity or SalamCommunityActivity; avoid unsafe casts
        final android.app.Activity hostActivity = requireActivity();

        // Intro animations
        view.findViewById(R.id.backBtn).startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in));
        view.findViewById(R.id.messageInput).startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_fade));

        view.findViewById(R.id.backBtn).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        messageInput = view.findViewById(R.id.messageInput);
        imagePreviewWrap = view.findViewById(R.id.imagePreviewWrap);
        imagePreview = view.findViewById(R.id.imagePreview);
        addImageBtn = view.findViewById(R.id.addImageBtn);
        
        emojiFold = view.findViewById(R.id.emojiFold);
        emojiChevron = view.findViewById(R.id.emojiChevron);
        emojiToggleLabel = view.findViewById(R.id.emojiToggleLabel);
        emojiSelectedIcon = view.findViewById(R.id.emojiSelectedIcon);
        emojiSmileIcon = view.findViewById(R.id.emojiSmileIcon);

        categoryFold = view.findViewById(R.id.categoryFold);
        categoryChevron = view.findViewById(R.id.categoryChevron);
        categorySelectedIcon = view.findViewById(R.id.categorySelectedIcon);
        
        submitBtn = view.findViewById(R.id.submitBtn);
        colorPickerContainer = view.findViewById(R.id.colorPickerContainer);

        setupCategoryPicker(view);

        addImageBtn.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        view.findViewById(R.id.removeImageBtn).setOnClickListener(v -> {
            selectedImageUri = null;
            imagePreviewWrap.setVisibility(View.GONE);
            addImageBtn.setVisibility(View.VISIBLE);
        });

        RecyclerView emojiRecycler = view.findViewById(R.id.emojiRecycler);
        emojiRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 6));
        EmojiAdapter emojiAdapter = new EmojiAdapter(SeedData.MOOD_EMOJIS, emoji -> {
            selectedEmoji = selectedEmoji != null && selectedEmoji.equals(emoji) ? null : emoji;
            refreshEmojiToggleLabel();
        });
        emojiRecycler.setAdapter(emojiAdapter);

        view.findViewById(R.id.emojiToggle).setOnClickListener(v -> toggleEmojiFold());
        view.findViewById(R.id.categoryToggle).setOnClickListener(v -> toggleCategoryFold());

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshSubmitState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        submitBtn.setOnClickListener(v -> {
            String text = messageInput.getText().toString();
            if (text.trim().isEmpty()) return;
            // Safely call host's showAnalyzing() if available
            if (hostActivity instanceof MainActivity) {
                ((MainActivity) hostActivity).showAnalyzing();
            } else if (hostActivity instanceof com.example.graduationproject.SalamCommunityActivity) {
                ((com.example.graduationproject.SalamCommunityActivity) hostActivity).showAnalyzing();
            } else {
                // Unknown host - cannot navigate to analyzing; fail gracefully
                return;
            }
            // hand the draft off via a static holder so AnalyzingFragment can moderate it
            DraftHolder.text = text;
            DraftHolder.cat = selectedCategory;
            DraftHolder.emoji = selectedEmoji;
            DraftHolder.img = selectedImageUri != null ? selectedImageUri.toString() : null;
            DraftHolder.colorIndex = selectedColorIndex;
        });

        buildColorPicker();
        refreshSubmitState();
        refreshCategoryToggle();
    }

    private void setupCategoryPicker(View view) {
        RecyclerView rv = view.findViewById(R.id.categoryRecyclerCompose);
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setNestedScrollingEnabled(false);
        rv.setClipToPadding(false);
        rv.setHasFixedSize(false);

        // Remove "الكل" for composing and keep the same category chip design as SalamCommunityActivity
        String[] cats = new String[SeedData.CATEGORIES.length - 1];
        System.arraycopy(SeedData.CATEGORIES, 1, cats, 0, cats.length);

        CategoryAdapter adapter = new CategoryAdapter(cats, selectedCategory, cat -> {
            selectedCategory = cat;
            refreshCategoryToggle();
        });
        rv.setAdapter(adapter);
    }

    private void refreshCategoryToggle() {
        categorySelectedIcon.setText(SeedData.getCategoryEmoji(selectedCategory));
    }

    private void toggleCategoryFold() {
        categoryOpen = !categoryOpen;
        categoryChevron.animate().rotation(categoryOpen ? 180f : 0f).setDuration(300).start();

        int targetHeight = categoryOpen ? dpToPx(132) : 0;
        if (categoryOpen) {
            categoryFold.getChildAt(0).startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right));
        }
        animateFold(categoryFold, targetHeight);
    }

    private void toggleEmojiFold() {
        emojiOpen = !emojiOpen;
        emojiChevron.animate().rotation(emojiOpen ? 180f : 0f).setDuration(300).start();

        int targetHeight = emojiOpen ? dpToPx(170) : 0;
        if (emojiOpen) {
            emojiFold.getChildAt(0).startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_fade));
        }
        animateFold(emojiFold, targetHeight);
    }

    private void animateFold(FrameLayout fold, int targetHeight) {
        int startHeight = fold.getHeight();
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, targetHeight);
        anim.setDuration(320);
        anim.addUpdateListener(a -> {
            ViewGroup.LayoutParams lp = fold.getLayoutParams();
            lp.height = (int) a.getAnimatedValue();
            fold.setLayoutParams(lp);
        });
        anim.start();
    }

    private void buildColorPicker() {
        colorPickerContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (int i = 0; i < 5; i++) {
            final int index = i;
            View colorItem = inflater.inflate(R.layout.item_color_option, colorPickerContainer, false);
            View circle = colorItem.findViewById(R.id.colorCircle);
            View ring = colorItem.findViewById(R.id.colorSelectionRing);

            CardColors.Pair pair = CardColors.forIndex(i);
            circle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), pair.bgRes)));

            ring.setVisibility(i == selectedColorIndex ? View.VISIBLE : View.GONE);

            colorItem.setOnClickListener(v -> {
                selectedColorIndex = index;
                for (int j = 0; j < colorPickerContainer.getChildCount(); j++) {
                    colorPickerContainer.getChildAt(j).findViewById(R.id.colorSelectionRing)
                            .setVisibility(j == selectedColorIndex ? View.VISIBLE : View.GONE);
                }
            });

            colorPickerContainer.addView(colorItem);
        }
    }

    private void showImagePreview() {
        imagePreviewWrap.setVisibility(View.VISIBLE);
        addImageBtn.setVisibility(View.GONE);
        Glide.with(this).load(selectedImageUri).centerCrop().into(imagePreview);
    }

    private void refreshEmojiToggleLabel() {
        if (selectedEmoji != null) {
            emojiSelectedIcon.setVisibility(View.VISIBLE);
            emojiSelectedIcon.setText(selectedEmoji);
            emojiSmileIcon.setVisibility(View.GONE);
            emojiToggleLabel.setText(R.string.compose_emoji_added);
        } else {
            emojiSelectedIcon.setVisibility(View.GONE);
            emojiSmileIcon.setVisibility(View.VISIBLE);
            emojiToggleLabel.setText(R.string.compose_emoji_add);
        }
    }

    private void refreshSubmitState() {
        boolean hasText = !messageInput.getText().toString().trim().isEmpty();
        submitBtn.setEnabled(hasText);
        submitBtn.setAlpha(hasText ? 1f : 0.4f);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    /** simple static hand-off for the pending draft between Compose -> Analyzing */
    public static class DraftHolder {
        public static String text;
        public static String cat;
        public static String emoji;
        public static String img;
        public static int colorIndex;
    }
}
