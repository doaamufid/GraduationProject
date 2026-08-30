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

/**
 * شاشة عرض كافة الأطفال المسجلين في قاعدة بيانات الجهاز (SQLCipher) باستخدام LinearLayout Container
 */
public class ChildProfilesFragment extends Fragment {

    private final List<ChildProfile> children = new ArrayList<>();
    private LinearLayout container;
    private ChildProfileStore dbStore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbStore = new ChildProfileStore(requireContext());
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
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> activity.showHome());
        }

        container = view.findViewById(R.id.children_container);

        // جلب البيانات المخزنة من قاعدة البيانات
        loadChildrenFromDb(activity);
    }

    /** 📥 قراءة جميع الأطفال من قاعدة البيانات في خيط خلفي Background Thread */
    private void loadChildrenFromDb(ProfileNavigator activity) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<ChildProfile> dbProfiles = dbStore.getProfiles();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    children.clear();
                    if (dbProfiles != null && !dbProfiles.isEmpty()) {
                        children.addAll(dbProfiles);
                    }
                    render(activity);
                });
            }
        });
    }

    /** 🎨 بناء وعرض صفوف الأطفال داخل الـ LinearLayout Container */
    private void render(ProfileNavigator activity) {
        if (container == null) return;
        container.removeAllViews();

        for (ChildProfile child : children) {
            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_child_row, container, false);

            TextView txtName = row.findViewById(R.id.txt_child_name);
            TextView txtAge = row.findViewById(R.id.txt_child_age);
            TextView txtAvatar = row.findViewById(R.id.txt_child_avatar);
            View btnOpen = row.findViewById(R.id.btn_open_child);
            View btnDelete = row.findViewById(R.id.btn_delete_child);

            if (txtName != null) txtName.setText(child.getName());
            if (txtAge != null) {
                txtAge.setText(getString(R.string.age_years_fmt, child.getAge()) + " · " + getString(R.string.tap_to_view_stats));
            }
            if (txtAvatar != null) txtAvatar.setText(child.getAvatar());

            // 1️⃣ جعل الصف بالكامل قابلاً للضغط للانتقال إلى شاشة التفاصيل
            row.setOnClickListener(v -> activity.showChildDetail((int) child.getId()));

            // 2️⃣ زر الفتح المخصص (إن وجد)
            if (btnOpen != null) {
                btnOpen.setOnClickListener(v -> activity.showChildDetail((int) child.getId()));
            }

            // 3️⃣ زر الحذف
            if (btnDelete != null) {
                btnDelete.setOnClickListener(v -> confirmDelete(activity, child));
            }

            container.addView(row);
        }
    }

    /** 🗑️ حذف الطفل من قاعدة البيانات وإعادة تحميل القائمة */
    private void confirmDelete(ProfileNavigator activity, ChildProfile child) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_child_title)
                .setMessage(R.string.delete_child_message)
                .setPositiveButton(R.string.answer_yes, (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        // الحذف الفعلي من قاعدة بيانات SQLite/SQLCipher
                        dbStore.getWritableDatabase(ChildProfileStore.DATABASE_PASSPHRASE)
                                .delete("child_profiles", "id = ?", new String[]{String.valueOf(child.getId())});

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> loadChildrenFromDb(activity));
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbStore != null) {
            dbStore.close();
        }
    }
}