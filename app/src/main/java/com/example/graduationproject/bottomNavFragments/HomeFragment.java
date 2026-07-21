package com.example.graduationproject.bottomNavFragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.ChatActivity;
import com.example.graduationproject.DailyHabitsActivity;
import com.example.graduationproject.R;
import com.example.graduationproject.SurvivalBoxActivity;
import com.example.graduationproject.adapters.HomeAdapter;
import com.example.graduationproject.models.HomeItem;

import java.util.ArrayList;
import java.util.List;


import android.content.Intent;

public class HomeFragment extends Fragment {

    private RecyclerView rvDashboard;
    private HomeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvDashboard = view.findViewById(R.id.rvDashboard);

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        // 1. تجهيز قائمة البيانات
        List<HomeItem> items = new ArrayList<>();
        items.add(new HomeItem(1, "رفيق سلام\nمحادثة - CHAT", R.drawable.ic_drop));
        items.add(new HomeItem(2, "سجل المزاج\nتتبع - MOOD", R.drawable.home));
        items.add(new HomeItem(3, "شجرة التعافي\nنمو - GROWTH", R.drawable.home));
        items.add(new HomeItem(4, "رسالة للمستقبل\nرسالة - FUTURE", R.drawable.home));
        items.add(new HomeItem(8, "العادات اليومية\nDaily Habits", R.drawable.home));

        items.add(new HomeItem(6, "تمارين التأريض\nGROUNDING", R.drawable.home));

        // 2. إعداد الـ Adapter والتعامل مع التنقل عبر الـ Intent
        adapter = new HomeAdapter(items, item -> {
            Intent intent;
            switch (item.getId()) {
                case 1:
                    // الانتقال إلى Activity المحادثة
                    intent = new Intent(requireActivity(), ChatActivity.class);
                    startActivity(intent);
                    break;
                    case 8:
                    intent = new Intent(requireActivity(), DailyHabitsActivity.class);
                    startActivity(intent);
                    break;
//                case 2:
//                    // الانتقال إلى Activity سجل المزاج
//                    intent = new Intent(requireActivity(), MoodLogActivity.class);
//                    startActivity(intent);
//                    break;
//                case 3:
//                    // الانتقال إلى Activity شجرة التعافي
//                    intent = new Intent(requireActivity(), GrowthActivity.class);
//                    startActivity(intent);
//                    break;
                case 7:
                    // الانتقال إلى Activity رسالة للمستقبل
                    intent = new Intent(requireActivity(), FutureActivity.class);
                    startActivity(intent);
                    break;
            }
        });

        // 3. ضبط الـ LayoutManager ليعرض العناصر كشبكة (Grid) من عمودين
        rvDashboard.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvDashboard.setAdapter(adapter);
    }
}
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.graduationproject.R;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link HomeFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class HomeFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public HomeFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment HomeFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static HomeFragment newInstance(String param1, String param2) {
//        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false);
//    }
//}