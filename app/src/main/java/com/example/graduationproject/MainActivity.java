package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.bottomNavFragments.ExercisesFragment;
import com.example.graduationproject.bottomNavFragments.HomeFragment;
import com.example.graduationproject.Fragments.CrisisModeFragment;
import com.example.graduationproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private final int defaultBottomNavigationItem = R.id.nav_home;
    ActivityMainBinding binding;

    /** Opens the full-screen crisis-mode overlay. */
    public void openCrisisMode() {
        CrisisModeFragment fragment = new CrisisModeFragment();
        fragment.show(getSupportFragmentManager(), "crisis_mode");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ضبط لون شريط الحالة ليتناسق مع واجهة الرئيسية (أزرق فاتح)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#E1F1FF"));
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
            // 👶 وضع الأطفال (مجهز ومستعد لزميلاتكِ):
            // هنا مستقبلاً سيقومون باستدعاء دالة خاصة بالأطفال setupKidNavigation()
            // حالياً سنعرض رسالة تنبيه بسيطة
            Toast.makeText(this, "مرحباً بك في وضع الأطفال (قيد التطوير)", Toast.LENGTH_LONG).show();
            setupAdultNavigation(); // مؤقتاً يعرض واجهتك لكي لا يتوقف التطبيق
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
                // فتح صفحة رفيقي (ChatActivity) كـ Activity منفصلة
                startActivity(new Intent(this, ChatActivity.class));
                return false; // نرجع false لكي لا يتم اختيار العنصر بصرياً في الشريط السفلي إذا كنت تفضل ذلك، أو true إذا أردت بقاء الاختيار عليه
            }
            return true;
        });
    }
}