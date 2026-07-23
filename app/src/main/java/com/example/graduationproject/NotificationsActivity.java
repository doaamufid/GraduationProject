package com.example.graduationproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.adapters.NotificationsAdapter;
import com.example.graduationproject.databinding.ActivityNotificationsBinding;
import com.example.graduationproject.models.NotificationItem;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> onBackPressed());

        setupNotificationsRecyclerView();
    }

    private void setupNotificationsRecyclerView() {
        List<NotificationItem> list = new ArrayList<>();

        // إضافة البيانات الحقيقية من تصميم الفيجما الخاص بكِ
        list.add(new NotificationItem("اقتباس اليوم", "اقتباس مخصص لك بناء على مزاجك", "قبل ساعتين", R.drawable.baseline_more_vert_24));
        list.add(new NotificationItem("وقت تمرين التنفس", "خصص 3 دقائق لنفسك الآن", "قبل 5 ساعات", R.drawable.outline_favorite_24));
        list.add(new NotificationItem("تحديث بخصوص طفلك", "لحظتنا مفيدة جداً وسلوك طفلك بانتظارك", "قبل 6 ساعات", R.drawable.outline_mic_24));
        list.add(new NotificationItem("تقريرك الشهري جاهز", "تقرير مفصل عن أنماط مزاجك الشهرية", "أمس", R.drawable.outline_favorite_24));
        list.add(new NotificationItem("ذكر المساء", "حان وقت ذكر المساء لتهدئة عقلانية", "اليوم", R.drawable.baseline_more_vert_24));

        NotificationsAdapter adapter = new NotificationsAdapter(list);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(adapter);
    }
}