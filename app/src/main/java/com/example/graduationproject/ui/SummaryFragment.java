package com.example.graduationproject.ui;
 
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.R;
import com.example.graduationproject.MainActivity;
import com.example.graduationproject.SurvivalBoxActivity;
import com.example.graduationproject.models.SurvivalBoxRepository;
import com.example.graduationproject.Fragments.BrowseAudioFragment;
import com.example.graduationproject.Fragments.BrowseDhikrFragment;
import com.example.graduationproject.Fragments.BrowseLoveFragment;
import com.example.graduationproject.Fragments.BrowsePhotosFragment;
import com.example.graduationproject.dialogs.AddAudioDialogFragment;
import com.example.graduationproject.dialogs.AddDhikrDialogFragment;
import com.example.graduationproject.dialogs.AddLoveDialogFragment;
import com.example.graduationproject.dialogs.AddPhotoDialogFragment;

import java.util.List;

/**
 * Enhanced SummaryFragment with light theme and full animations.
 */
public class SummaryFragment extends Fragment {

    private final SurvivalBoxRepository repo = SurvivalBoxRepository.getInstance();
    private LinearLayout llCategories;
    private View btnOpenBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_summary, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        TopBarHelper.bind(root, getString(R.string.summary_title), () -> {
            if (getActivity() != null) getActivity().finish();
        }, null);

        // Adjust top bar for light theme
        TextView tvTopBarTitle = root.findViewById(R.id.tvTopBarTitle);
        if (tvTopBarTitle != null) tvTopBarTitle.setTextColor(Color.parseColor("#2D5B7B"));
        View btnBack = root.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setBackgroundResource(R.drawable.bg_icon_button);

        llCategories = root.findViewById(R.id.llCategories);
        btnOpenBox = root.findViewById(R.id.btnOpenBox);

        btnOpenBox.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openCrisisMode();
            } else if (getActivity() instanceof SurvivalBoxActivity) {
                ((SurvivalBoxActivity) getActivity()).openCrisisMode();
            }
        });

        setupDialogListeners();
        renderCategories();
        startEntranceAnimations(root);
        
        return root;
    }

    private void setupDialogListeners() {
        getParentFragmentManager().setFragmentResultListener(
                AddAudioDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    repo.addAudio(bundle.getString(AddAudioDialogFragment.KEY_LABEL),
                            bundle.getInt(AddAudioDialogFragment.KEY_DURATION));
                    renderCategories();
                });
        getParentFragmentManager().setFragmentResultListener(
                AddPhotoDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    repo.addPhoto(bundle.getString(AddPhotoDialogFragment.KEY_URI),
                            bundle.getString(AddPhotoDialogFragment.KEY_CAPTION));
                    renderCategories();
                });
        getParentFragmentManager().setFragmentResultListener(
                AddLoveDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    repo.addLove(bundle.getString(AddLoveDialogFragment.KEY_TEXT),
                            bundle.getString(AddLoveDialogFragment.KEY_SOURCE));
                    renderCategories();
                });
        getParentFragmentManager().setFragmentResultListener(
                AddDhikrDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    repo.setDhikr(bundle.getStringArrayList(AddDhikrDialogFragment.KEY_TEXTS));
                    renderCategories();
                });
    }

    private void startEntranceAnimations(View root) {
        View tag = root.findViewById(R.id.tvSummaryTag);
        View headline = root.findViewById(R.id.tvSummaryHeadline);
        View sub = root.findViewById(R.id.tvSummarySub);

        if (tag == null || headline == null || sub == null) return;

        tag.setAlpha(0f);
        tag.setTranslationY(20f);
        headline.setAlpha(0f);
        headline.setTranslationY(30f);
        sub.setAlpha(0f);
        sub.setTranslationY(30f);
        btnOpenBox.setAlpha(0f);
        btnOpenBox.setTranslationY(100f);

        AnimatorSet headerSet = new AnimatorSet();
        headerSet.playTogether(
                ObjectAnimator.ofFloat(tag, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(tag, "translationY", 20f, 0f),
                ObjectAnimator.ofFloat(headline, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(headline, "translationY", 30f, 0f),
                ObjectAnimator.ofFloat(sub, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(sub, "translationY", 30f, 0f)
        );
        headerSet.setDuration(800);
        headerSet.setInterpolator(new DecelerateInterpolator());

        btnOpenBox.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(1000).setInterpolator(new OvershootInterpolator()).start();

        headerSet.start();
    }

    private void renderCategories() {
        llCategories.removeAllViews();

        addCategoryRow(
                getString(R.string.cat_audio_tag), getString(R.string.cat_audio_title),
                R.drawable.audio, R.drawable.bg_cat_audio,
                () -> openBrowse(new BrowseAudioFragment())
        );

        addCategoryRow(
                getString(R.string.cat_photos_tag), getString(R.string.cat_photos_title),
                R.drawable.ic_image, R.drawable.bg_cat_photos,
                () -> openBrowse(new BrowsePhotosFragment())
        );

        addCategoryRow(
                getString(R.string.cat_love_tag), getString(R.string.cat_love_title),
                R.drawable.ic_heart_filled_white, R.drawable.bg_cat_love,
                () -> openBrowse(new BrowseLoveFragment())
        );

        addCategoryRow(
                getString(R.string.cat_dhikr_tag), getString(R.string.cat_dhikr_title),
                R.drawable.ic_sparkles, R.drawable.bg_cat_dhikr,
                () -> openBrowse(new BrowseDhikrFragment())
        );
    }

    private void addCategoryRow(String tag, String title, int iconResId, int bgResId, Runnable onOpen) {
        View row = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_category_row, llCategories, false);

        TextView tvTag = row.findViewById(R.id.tvTag);
        TextView tvTitle = row.findViewById(R.id.tvTitle);
        ImageView ivCategoryIconTrailing = row.findViewById(R.id.ivCategoryIconTrailing);
        View layoutInner = row.findViewById(R.id.layoutInner);
        View btnOpen = row.findViewById(R.id.btnOpen);

        tvTag.setText(tag);
        tvTitle.setText(title);
        if (ivCategoryIconTrailing != null) {
            ivCategoryIconTrailing.setImageResource(iconResId);
        }
        if (layoutInner != null) {
            layoutInner.setBackgroundResource(bgResId);
        }

        btnOpen.setOnClickListener(v -> onOpen.run());
        row.setOnClickListener(v -> onOpen.run());

        // Staggered entrance
        row.setAlpha(0f);
        row.setTranslationY(40f);
        row.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(400 + (llCategories.getChildCount() * 100L)).start();

        llCategories.addView(row);
    }

    private void openBrowse(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right, R.anim.slide_out_left,
                        android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }
}
