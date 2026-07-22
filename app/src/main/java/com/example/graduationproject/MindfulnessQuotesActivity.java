package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityMindfulnessQuotesBinding;
import java.util.Random;

public class MindfulnessQuotesActivity extends AppCompatActivity {

    private ActivityMindfulnessQuotesBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable quoteSwitcherRunnable;
    private Runnable navigateTask;

    // مصفوفة عبارات البالغين الموسعة (أكثر عمقاً ودعماً)
    private final String[] adultQuotes = {
            "هذه اللحظة ستمر وتبقى أنت بسلامك...",
            "أنت آمن هنا، خذ شهيقاً عميقاً وأطلق قلقك.",
            "لا بأس بألا تكون على ما يرام الآن، امنح نفسك وقتاً.",
            "أفكارك مجرد سحب عابرة، وأنت السماء الثابتة.",
            "كل شيء سيكون بخير، ابدأ خطوة بخطوة.",
            "تنفّس.. لستَ مضطراً لحل كل شيء في هذه اللحظة.",
            "أنت تبذل قصارى جهدك، وهذا يكفي جداً."
    };

    // مصفوفة عبارات الأطفال الموسعة
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

        // 1. عرض أول مقولة فوراً
        displayNextRandomQuote(userType);

        // 2. تفعيل التبديل التلقائي للمقولات بحركة لطيفة كل 2.5 ثانية
        quoteSwitcherRunnable = new Runnable() {
            public void mainRun() {
                // نعمل تراجع واختفاء للنص القديم
                Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                fadeOut.setDuration(500);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // عند انتهاء الاختفاء، نغير النص ونظهره مجدداً
                        displayNextRandomQuote(userType);
                    }
                });
                binding.tvQuote.startAnimation(fadeOut);
                handler.postDelayed(this, 2500);
            }

            // سطر أمان لتجنب أي مشاكل في الفئات الداخيلة
            public void run() { mainRun(); }
        };
        handler.postDelayed(quoteSwitcherRunnable, 2500);

        // 3. الانتقال التلقائي للرئيسية بعد 6.5 ثوانٍ ليعيش المستخدم التجربة كاملة
        navigateTask = this::navigateToHome;
        handler.postDelayed(navigateTask, 6500);

        // 4. زر التخطي للانتقال الفوري
        binding.btnSkip.setOnClickListener(v -> {
            cleanupHandlerTasks();
            navigateToHome();
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

        // حركة ظهور (Fade In) لطيفة للنص الجديد
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(600);
        binding.tvQuote.startAnimation(fadeIn);
    }

    private void navigateToHome() {
        Intent intent = new Intent(MindfulnessQuotesActivity.this, MainActivity.class);
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
        cleanupHandlerTasks(); // حماية الذاكرة من أي تسريب (Memory Leak) عند إغلاق الشاشة
    }
}