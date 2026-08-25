package com.example.graduationproject.Fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.ProfileNavigator;
import com.example.graduationproject.R;
import com.example.graduationproject.data.profile.SeedData;
import com.example.graduationproject.models.profile.ChildProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Mirrors <ChildProfilesScreen/>. Deleting a child profile asks for confirmation
 * via a "Yes" / "Cancel" dialog before the profile is removed.
 */
public class ChildProfilesFragment extends Fragment {

    private List<ChildProfile> children;
    private LinearLayout container;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        children = new ArrayList<>(SeedData.getInitialChildren(requireContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_profiles, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileNavigator activity = (ProfileNavigator) requireActivity();

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> activity.showHome());

        container = view.findViewById(R.id.children_container);
        render(activity);
    }

    private void render(ProfileNavigator activity) {
        container.removeAllViews();
        for (ChildProfile child : children) {
            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_child_row, container, false);
            ((TextView) row.findViewById(R.id.txt_child_name)).setText(child.name);
            ((TextView) row.findViewById(R.id.txt_child_age))
                    .setText(getString(R.string.age_years_fmt, child.age) + " · " + getString(R.string.tap_to_view_stats));
            ((TextView) row.findViewById(R.id.txt_child_avatar)).setText(child.avatarEmoji);

            row.findViewById(R.id.btn_open_child).setOnClickListener(v -> activity.showChildDetail(child.id));

            row.findViewById(R.id.btn_delete_child).setOnClickListener(v -> confirmDelete(activity, child));

            container.addView(row);
        }
    }

    /** Shows a confirmation dialog ("Yes" / "Cancel") before deleting a child profile. */
    private void confirmDelete(ProfileNavigator activity, ChildProfile child) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_child_title)
                .setMessage(R.string.delete_child_message)
                .setPositiveButton(R.string.answer_yes, (dialog, which) -> {
                    children.removeIf(c -> c.id == child.id);
                    render(activity);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
