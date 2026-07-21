package com.example.graduationproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new HomeFragment())
                .commit();

        binding.bottomNavigation.setSelectedItemId(defaultBottomNavigationItem);// هان خليت أول أيقونة يختارها تلقائي من البوتوم ناف هي أيقونة الهوم

        binding.bottomNavigation.setOnItemSelectedListener(item -> {//هان خليت أول فراقمنت يظهر أول شي تلقائي الهوم فراقمنت
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new HomeFragment())
                        .commit();
            }
//            } else if (itemId == R.id.nav_profile) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.frameLayout, new OffersFragment())
//                        .commit();
//            } else if (itemId == R.id.icon_saved_cars) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.frameLayout, new SavedCarsFragment())
//                        .commit();
//            } else if (itemId == R.id.icon_profile) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.frameLayout, new ProfileFragment())
//                        .commit();
//            }
            return true;
        });
    }
}