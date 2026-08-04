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

/**
 * Equivalent of the root component's default (non-browsing) render
 * branch: header + 4 <CategoryRow/> items + the "open box now" CTA.
 */
public class SummaryFragment extends Fragment {

    private final SurvivalBoxRepository repo = SurvivalBoxRepository.getInstance();
    private LinearLayout llCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_summary, container, false);

        TopBarHelper.bind(root, getString(R.string.summary_title), () -> {
            // matches `onBack={() => {}}` in the original - no-op on the root screen
        }, null);

        llCategories = root.findViewById(R.id.llCategories);

        root.findViewById(R.id.btnOpenBox).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openCrisisMode();
            } else if (getActivity() instanceof SurvivalBoxActivity) {
                ((SurvivalBoxActivity) getActivity()).openCrisisMode();
            }
        });

        // Listen for results reported by the "Add" dialogs launched directly
        // from this screen's + buttons (equivalent of onSave -> setData()).
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

        renderCategories();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh counts in case data changed while browsing a category.
        renderCategories();
    }

    private void renderCategories() {
        llCategories.removeAllViews();

        addCategoryRow(
                getString(R.string.cat_audio_tag), getString(R.string.cat_audio_title),
                repo.getAudio().isEmpty(),
                repo.getAudio().isEmpty() ? getString(R.string.empty_category)
                        : getString(R.string.count_messages, repo.getAudio().size()),
                () -> openBrowse(new BrowseAudioFragment()),
                () -> AddAudioDialogFragment.newInstance().show(getParentFragmentManager(), "add_audio")
        );

        addCategoryRow(
                getString(R.string.cat_photos_tag), getString(R.string.cat_photos_title),
                repo.getPhotos().isEmpty(),
                repo.getPhotos().isEmpty() ? getString(R.string.empty_category)
                        : getString(R.string.count_photos, repo.getPhotos().size()),
                () -> openBrowse(new BrowsePhotosFragment()),
                () -> AddPhotoDialogFragment.newInstance().show(getParentFragmentManager(), "add_photo")
        );

        addCategoryRow(
                getString(R.string.cat_love_tag), getString(R.string.cat_love_title),
                repo.getLove().isEmpty(),
                repo.getLove().isEmpty() ? getString(R.string.empty_category)
                        : getString(R.string.count_messages, repo.getLove().size()),
                () -> openBrowse(new BrowseLoveFragment()),
                () -> AddLoveDialogFragment.newInstance().show(getParentFragmentManager(), "add_love")
        );

        addCategoryRow(
                getString(R.string.cat_dhikr_tag), getString(R.string.cat_dhikr_title),
                repo.getDhikr().isEmpty(),
                repo.getDhikr().isEmpty() ? getString(R.string.empty_category)
                        : getString(R.string.count_dhikr, repo.getDhikr().size()),
                () -> openBrowse(new BrowseDhikrFragment()),
                () -> AddDhikrDialogFragment.newInstance().show(getParentFragmentManager(), "add_dhikr")
        );
    }

    private void addCategoryRow(String tag, String title, boolean empty, String countText,
                                Runnable onOpen, Runnable onAdd) {
        View row = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_category_row, llCategories, false);

        row.setBackgroundResource(empty ? R.drawable.bg_card_empty : R.drawable.bg_card);

        TextView tvTag = row.findViewById(R.id.tvTag);
        TextView tvTitle = row.findViewById(R.id.tvTitle);
        TextView tvCount = row.findViewById(R.id.tvCount);
        ImageView btnAdd = row.findViewById(R.id.btnAdd);
        View btnOpen = row.findViewById(R.id.btnOpen);

        tvTag.setText(tag);
        tvTitle.setText(title);
        tvCount.setText(countText);
        tvCount.setTextColor(getResources().getColor(empty ? R.color.text_soft : R.color.primary_light));

        btnOpen.setOnClickListener(v -> onOpen.run());
        btnAdd.setOnClickListener(v -> onAdd.run());

        llCategories.addView(row);
    }

    private void openBrowse(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }
}
