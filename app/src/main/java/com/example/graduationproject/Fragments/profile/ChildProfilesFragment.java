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
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.models.ChildProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ChildProfilesFragment extends Fragment {

    private List<ChildProfile> children = new ArrayList<>();
    private LinearLayout container;

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
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> activity.showHome());
        }

        container = view.findViewById(R.id.children_container);

        // 📥 تحميل قائمة الأطفال الحقيقية من قاعدة البيانات
        loadChildrenFromDatabase(activity);
    }

    private void loadChildrenFromDatabase(ProfileNavigator activity) {
        Executors.newSingleThreadExecutor().execute(() -> {
            ChildProfileStore store = ChildProfileStore.getInstance(requireContext());
            List<ChildProfile> realChildren = store.getProfiles();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    children.clear();
                    children.addAll(realChildren);
                    render(activity);
                });
            }
        });
    }

    private void render(ProfileNavigator activity) {
        container.removeAllViews();
        for (ChildProfile child : children) {
            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_child_row, container, false);

            ((TextView) row.findViewById(R.id.txt_child_name)).setText(child.getName());
            ((TextView) row.findViewById(R.id.txt_child_age))
                    .setText(getString(R.string.age_years_fmt, child.getAge()) + " · " + getString(R.string.tap_to_view_stats));
            ((TextView) row.findViewById(R.id.txt_child_avatar)).setText(child.getAvatar());

            row.findViewById(R.id.btn_open_child).setOnClickListener(v -> activity.showChildDetail(child.getId()));
            row.findViewById(R.id.btn_delete_child).setOnClickListener(v -> confirmDelete(activity, child));

            container.addView(row);
        }
    }

    private void confirmDelete(ProfileNavigator activity, ChildProfile child) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_child_title)
                .setMessage(R.string.delete_child_message)
                .setPositiveButton(R.string.answer_yes, (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        // يمكنك إضافة دالة حذف البروفايل من الداتابيز هنا إذا كانت معرفة لديك
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> loadChildrenFromDatabase(activity));
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }
}