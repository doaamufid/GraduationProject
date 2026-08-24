package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.bottomNavFragments.ExercisesFragment;
import com.example.graduationproject.bottomNavFragments.HomeFragment;
import com.example.graduationproject.Fragments.CrisisModeFragment;
import com.example.graduationproject.databinding.ActivityMainBinding;
import com.example.graduationproject.models.Message;
import com.example.graduationproject.ui.AnalyzingFragment;
import com.example.graduationproject.ui.ApprovedFragment;
import com.example.graduationproject.ui.ComposeFragment;
import com.example.graduationproject.ui.ListFragment;
import com.example.graduationproject.ui.RejectedFragment;
import com.example.graduationproject.ui.WallFragment;
import com.example.graduationproject.util.CardHost;
import android.widget.FrameLayout;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements CardHost {
    private final int defaultBottomNavigationItem = R.id.nav_home;
    ActivityMainBinding binding;

    @Override
    public FrameLayout getToastOverlay() {
        // Return a FrameLayout that can be used for custom toasts if available, 
        // or just a container from your layout.
        return findViewById(R.id.frameLayout); 
    }

    @Override
    public void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "تم النسخ!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPinnedCountChanged() {
        // Update UI if needed when pinned count changes
    }

    public void showAnalyzing() {
        replaceFragment(new AnalyzingFragment());
    }

    public void showApproved(Message msg) {
        replaceFragment(ApprovedFragment.newInstance(msg.id));
    }

    public void showRejected(String reason, boolean crisis) {
        replaceFragment(RejectedFragment.newInstance(reason, crisis));
    }

    public void showWall(boolean addToBackstack) {
        replaceFragment(new WallFragment(), addToBackstack);
    }

    public void showCompose() {
        replaceFragment(new ComposeFragment());
    }

    public void showPinned() {
        replaceFragment(ListFragment.newInstance(ListFragment.MODE_PINNED));
    }

    public void showMine() {
        replaceFragment(ListFragment.newInstance(ListFragment.MODE_MINE));
    }

    private void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, true);
    }

    private void replaceFragment(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment);
        if (addToBackstack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    /** Opens the full-screen crisis-mode overlay. */
    public void openCrisisMode() {
        CrisisModeFragment fragment = new CrisisModeFragment();
        fragment.show(getSupportFragmentManager(), "crisis_mode");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.loadLibrary("rive-android");
        // ضبط لون شريط الحالة ليتناسق مع واجهة الرئيسية (اللون الأزرق الفاتح)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        // 📝 قراءة نوع الحساب المفعل حالياً لتحديد شكل الواجهة
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userType = prefs.getString("user_type", "adult"); // القيمة الافتراضية بالغ أماناً للتطبيق

        if (userType.equals("adult")) {
            // 🧑 وضع البالغين (شغلكِ الحالي): نقوم بتهيئة وعرض الفراقمنتات الطبيعية للبالغين
            setupAdultNavigation();
        } else if (userType.equals("kid")) {
            Intent intent = new Intent(MainActivity.this, com.example.graduationproject.Kids.ChildProfilesActivity.class);
            startActivity(intent);
            finish();
        }

        // هندلة زر الرجوع لمنع العودة للخلف بالخطأ وإغلاق التطبيق بنظافة
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });
    }

    // دالة مستقلة تحتوي على منطق البالغين ليكون الكود مرتباً ومنفصلاً
    private void setupAdultNavigation() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new HomeFragment())
                .commit();

        binding.bottomNavigation.setSelectedItemId(defaultBottomNavigationItem);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new HomeFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_exercises) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new ExercisesFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_friend) {
                // فتح صفحة رفيقي (ChatMainActivity) كـ Activity منفصلة
                startActivity(new Intent(this, ChatMainActivity.class));
                return false; // نرجع false لكي لا يتم اختيار العنصر بصرياً في الشريط السفلي إذا كنت تفضل ذلك، أو true إذا أردت بقاء الاختيار عليه
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, AdultProfileActivity.class));
                return true;
            }
            return true;
        });
    }
}