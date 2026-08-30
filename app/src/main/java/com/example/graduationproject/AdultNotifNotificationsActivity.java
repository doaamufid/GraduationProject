package com.example.graduationproject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.adapters.AdultNotifNotificationAdapter;
import com.example.graduationproject.models.AdultNotifNotificationGroup;
import com.example.graduationproject.models.AdultNotifNotificationItem;
import com.example.graduationproject.models.AdultNotifNotificationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * الشاشة الرئيسية للإشعارات — تقابل AdultNotificationsScreen في الكود الأصلي (React).
 * تحافظ على نفس البيانات الأولية (INITIAL_GROUPS)، ونفس المنطق:
 *  - الضغط على إشعار يحدّده كمقروء (markRead)
 *  - قائمة "..." تحدد الكل كمقروء (markAllRead) وتُعطَّل عند عدم وجود إشعارات غير مقروءة
 */
public class AdultNotifNotificationsActivity extends AppCompatActivity implements AdultNotifNotificationAdapter.OnItemClickListener {

    private List<AdultNotifNotificationGroup> groups;
    private AdultNotifNotificationAdapter adapter;
    private PopupWindow optionsPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity edge-to-edge and set system bar colors
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.adult_notif_bg_top));
        window.setNavigationBarColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        setContentView(R.layout.adult_notif_activity_notifications);

        groups = buildInitialGroups();

        RecyclerView recyclerView = findViewById(R.id.adult_notif_recycler_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdultNotifNotificationAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.submitFlatList(flatten(groups));

        ImageButton btnBack = findViewById(R.id.adult_notif_btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());

        ImageButton btnOptions = findViewById(R.id.adult_notif_btn_options);
        btnOptions.setOnClickListener(this::showOptionsMenu);
    }

    /** يطابق مصفوفة INITIAL_GROUPS في أعلى الملف الأصلي بالضبط (نفس الترتيب والنصوص). */
    private List<AdultNotifNotificationGroup> buildInitialGroups() {
        List<AdultNotifNotificationGroup> result = new ArrayList<>();

        result.add(new AdultNotifNotificationGroup(
                getString(R.string.adult_notif_group_today),
                Arrays.asList(
                        new AdultNotifNotificationItem(1, AdultNotifNotificationType.QUOTE,
                                getString(R.string.adult_notif_n1_title), getString(R.string.adult_notif_n1_desc),
                                getString(R.string.adult_notif_n1_time), true),
                        new AdultNotifNotificationItem(2, AdultNotifNotificationType.BREATHING,
                                getString(R.string.adult_notif_n2_title), getString(R.string.adult_notif_n2_desc),
                                getString(R.string.adult_notif_n2_time), true),
                        new AdultNotifNotificationItem(3, AdultNotifNotificationType.CHILD,
                                getString(R.string.adult_notif_n3_title), getString(R.string.adult_notif_n3_desc),
                                getString(R.string.adult_notif_n3_time), false)
                )
        ));

        result.add(new AdultNotifNotificationGroup(
                getString(R.string.adult_notif_group_yesterday),
                Arrays.asList(
                        new AdultNotifNotificationItem(4, AdultNotifNotificationType.REPORT,
                                getString(R.string.adult_notif_n4_title), getString(R.string.adult_notif_n4_desc),
                                getString(R.string.adult_notif_n4_time), false),
                        new AdultNotifNotificationItem(5, AdultNotifNotificationType.QUOTE,
                                getString(R.string.adult_notif_n5_title), getString(R.string.adult_notif_n5_desc),
                                getString(R.string.adult_notif_n5_time), false)
                )
        ));

        result.add(new AdultNotifNotificationGroup(
                getString(R.string.adult_notif_group_this_week),
                Arrays.asList(
                        new AdultNotifNotificationItem(6, AdultNotifNotificationType.PARENT_REPORT,
                                getString(R.string.adult_notif_n6_title), getString(R.string.adult_notif_n6_desc),
                                getString(R.string.adult_notif_n6_time), false),
                        new AdultNotifNotificationItem(7, AdultNotifNotificationType.DHIKR,
                                getString(R.string.adult_notif_n7_title), getString(R.string.adult_notif_n7_desc),
                                getString(R.string.adult_notif_n7_time), false)
                )
        ));

        return result;
    }

    /** يحوّل قائمة المجموعات إلى قائمة مسطّحة (عنوان مجموعة + عناصرها) للأدابتر. */
    private List<Object> flatten(List<AdultNotifNotificationGroup> groups) {
        List<Object> flat = new ArrayList<>();
        for (AdultNotifNotificationGroup g : groups) {
            flat.add(g.label);
            flat.addAll(g.items);
        }
        return flat;
    }

    private boolean hasUnread() {
        for (AdultNotifNotificationGroup g : groups) {
            for (AdultNotifNotificationItem it : g.items) {
                if (it.unread) return true;
            }
        }
        return false;
    }

    // ===================== منطق التفاعل (يطابق markRead / markAllRead الأصليتين) =====================

    @Override
    public void onItemClick(AdultNotifNotificationItem item, int adapterPosition) {
        item.unread = false;
        adapter.notifyItemChanged(adapterPosition);
    }

    private void markAllRead() {
        for (AdultNotifNotificationGroup g : groups) {
            for (AdultNotifNotificationItem it : g.items) {
                it.unread = false;
            }
        }
        adapter.notifyDataSetChanged();
        if (optionsPopup != null) {
            optionsPopup.dismiss();
        }
    }

    // ===================== قائمة "..." المنبثقة =====================

    private void showOptionsMenu(View anchor) {
        View popupView = getLayoutInflater().inflate(R.layout.adult_notif_popup_menu_notifications, null);
        TextView tvMarkAllRead = popupView.findViewById(R.id.adult_notif_tv_mark_all_read);

        boolean unreadExists = hasUnread();
        tvMarkAllRead.setEnabled(unreadExists);
        tvMarkAllRead.setTextColor(ContextCompat.getColor(this,
                unreadExists ? R.color.adult_notif_ink : R.color.adult_notif_ink_faint));
        tvMarkAllRead.setOnClickListener(v -> markAllRead());

        optionsPopup = new PopupWindow(
                popupView,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        optionsPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        optionsPopup.setElevation(8f);
        optionsPopup.setOutsideTouchable(true);

        // يقابل "absolute left-0 top-11" في الأصل: القائمة تظهر أسفل الزر بمحاذاة حافته.
        optionsPopup.showAsDropDown(anchor, 0, 8, Gravity.END);
    }
}
