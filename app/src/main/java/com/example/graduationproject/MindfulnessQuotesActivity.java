package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.Kids.ChildProfilesActivity;
import com.example.graduationproject.databinding.ActivityMindfulnessQuotesBinding;
import java.util.Random;

public class MindfulnessQuotesActivity extends AppCompatActivity {

    private ActivityMindfulnessQuotesBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable quoteSwitcherRunnable;
    private Runnable navigateTask;

    private final String[] adultQuotes = {
            "هذه اللحظة ستمر وتبقى أنت بسلامك...",
            "أنت آمن هنا، خذ شهيقاً عميقاً وأطلق قلقك.",
            "لا بأس بألا تكون على ما يرام الآن، امنح نفسك وقتاً.",
            "أفكارك مجرد سحب عابرة، وأنت السماء الثابتة.",
            "كل شيء سيكون بخير، ابدأ خطوة بخطوة.",
            "تنفّس.. لستَ مضطراً لحل كل شيء في هذه اللحظة.",
            "أنت تبذل قصارى جهدك، وهذا يكفي جداً."
    };

    private final String[] kidQuotes = {
            "أنت بطل خارق وشجاع جداً اليوم وكل يوم! ✨",
            "خذ نفساً عميقاً كأنك تشم وردة جميلة 🌸",
            "أنت قوي، ذكي، ومحبوب دائماً من الجميع.",
            "الغيوم تمطر ثم تشرق الشمس مجدداً لتبتسم لك ☀️",
            "قلبك الصغير مليء بالفرح والشجاعة!",
            "يوم جميل ينتظرك يا بطل! 🚀"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMindfulnessQuotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userType = prefs.getString("user_type", "adult");

        displayNextRandomQuote(userType);

        quoteSwitcherRunnable = new Runnable() {
            public void mainRun() {
                Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                fadeOut.setDuration(500);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        displayNextRandomQuote(userType);
                    }
                });
                binding.tvQuote.startAnimation(fadeOut);
                handler.postDelayed(this, 2500);
            }

            public void run() { mainRun(); }
        };
        handler.postDelayed(quoteSwitcherRunnable, 2500);

        navigateTask = this::navigateToNextScreen;
        handler.postDelayed(navigateTask, 6500);

        binding.btnSkip.setOnClickListener(v -> {
            cleanupHandlerTasks();
            navigateToNextScreen();
        });
    }

    private void displayNextRandomQuote(String userType) {
        Random random = new Random();
        String quote;
        if ("kid".equals(userType)) {
            quote = kidQuotes[random.nextInt(kidQuotes.length)];
        } else {
            quote = adultQuotes[random.nextInt(adultQuotes.length)];
        }

        binding.tvQuote.setText(quote);

        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(600);
        binding.tvQuote.startAnimation(fadeIn);
    }

    private void navigateToNextScreen() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userType = prefs.getString("user_type", "adult");

        Intent intent;
        if ("kid".equals(userType)) {
            intent = new Intent(MindfulnessQuotesActivity.this, ChildProfilesActivity.class);
        } else {
            intent = new Intent(MindfulnessQuotesActivity.this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void cleanupHandlerTasks() {
        if (handler != null) {
            if (quoteSwitcherRunnable != null) handler.removeCallbacks(quoteSwitcherRunnable);
            if (navigateTask != null) handler.removeCallbacks(navigateTask);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupHandlerTasks();
    }
}