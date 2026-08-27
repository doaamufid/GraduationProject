package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageManager.wrapContext(newBase));
    }

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

        // ضبط لون شريط الحالة والأسفل ليتناسق مع واجهة الرئيسية (اللون البيج الفاتح)
        int navColor = android.graphics.Color.parseColor("#FDFCF9");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(navColor);
            getWindow().setNavigationBarColor(navColor);
            
            WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        binding.getRoot().setLayoutDirection(AppLanguageManager.getLayoutDirection(this));

        // Remove old BottomNavigationView setup and replace with custom one
        setupCustomNavigation();

        // Handle window insets for bottom nav
        View navContainer = findViewById(R.id.bottom_navigation_container);
        ViewCompat.setOnApplyWindowInsetsListener(navContainer, (v, insets) -> {
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
        // Handled by setupCustomNavigation()
    }

    private void setupCustomNavigation() {
        View navHome = findViewById(R.id.nav_home);
        View navExercises = findViewById(R.id.nav_exercises);
        View navFriend = findViewById(R.id.nav_friend);
        View navProfile = findViewById(R.id.nav_profile);

        navHome.setOnClickListener(v -> selectNavItem(R.id.nav_home));
        navExercises.setOnClickListener(v -> selectNavItem(R.id.nav_exercises));
        navFriend.setOnClickListener(v -> selectNavItem(R.id.nav_friend));
        navProfile.setOnClickListener(v -> selectNavItem(R.id.nav_profile));

        // Default selection
        selectNavItem(R.id.nav_home);
    }

    private void selectNavItem(int itemId) {
        // Reset all
        resetNavItems();

        if (itemId == R.id.nav_home) {
            findViewById(R.id.nav_home).setSelected(true);
            findViewById(R.id.nav_home_bg).setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new HomeFragment())
                    .commit();
        } else if (itemId == R.id.nav_exercises) {
            findViewById(R.id.nav_exercises).setSelected(true);
            findViewById(R.id.nav_exercises_bg).setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new ExercisesFragment())
                    .commit();
        } else if (itemId == R.id.nav_friend) {
            findViewById(R.id.nav_friend).setSelected(true);
            findViewById(R.id.nav_friend_bg).setVisibility(View.VISIBLE);
            startActivity(new Intent(this, ChatMainActivity.class));
        } else if (itemId == R.id.nav_profile) {
            findViewById(R.id.nav_profile).setSelected(true);
            findViewById(R.id.nav_profile_bg).setVisibility(View.VISIBLE);
            startActivity(new Intent(this, AdultProfileActivity.class));
        }
    }

    private void resetNavItems() {
        int[] ids = {R.id.nav_home, R.id.nav_exercises, R.id.nav_friend, R.id.nav_profile};
        int[] bgs = {R.id.nav_home_bg, R.id.nav_exercises_bg, R.id.nav_friend_bg, R.id.nav_profile_bg};
        
        for (int i = 0; i < ids.length; i++) {
            findViewById(ids[i]).setSelected(false);
            findViewById(bgs[i]).setVisibility(View.INVISIBLE);
        }
    }
}