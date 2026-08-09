package com.example.graduationproject;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.adapters.EnvironmentAdapter;
import com.example.graduationproject.models.Environment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HealingEnvironmentActivity extends AppCompatActivity {

    // 1. عناصر الواجهة الرئيسية
    private TextView tvTimer, tvCurrentTitle, tvCurrentSub;
    private TextView btnTime10, btnTime20, btnTime40;
    private FloatingActionButton btnPlayPause;
    private ImageView btnBack, btnPrevious, btnNext, imgCurrentBg;
    private Slider sliderVolume;

    // 2. عناصر قائمة البيئات (RecyclerView)
    private RecyclerView rvEnvironments;
    private EnvironmentAdapter adapter;
    private List<Environment> environmentList;
    private int currentPlayIndex = 0; // لمتابعة البيئة الحالية عند الضغط على التالي والسابق

    // 3. متغيرات العداد التنازلي (Timer)
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 1200000; // القيمة الافتراضية 20 دقيقة بالملي ثانية
    private boolean isTimerRunning = false;

    // 4. متغيرات الصوت والـ MediaPlayer الفعلي
    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healing_environment);

        // تهيئة وتعريف جميع العناصر من ملف الـ XML
        initViews();

        // إعداد قائمة البيئات الأفقية (RecyclerView)
        setupRecyclerView();

        // إعداد التحكم بمستوى الصوت (Volume Slider)
        setupVolumeControl();

        // إعداد مستمعات الضغط لأزرار المؤقت الثلاثة (10د - 20د - 40د)
        setupTimerButtons();

        // إعداد أزرار التحكم بالمشغل (Play, Next, Prev, Back)
        setupPlayerControls();

        // تحضير البيئة الافتراضية الأولى لتكون جاهزة للعمل فوراً
        if (!environmentList.isEmpty()) {
            prepareSoundOnly(environmentList.get(0).getSoundResId());
        }
    }

    private void initViews() {
        tvTimer = findViewById(R.id.tvTimer);
        tvCurrentTitle = findViewById(R.id.tvCurrentTitle);
        tvCurrentSub = findViewById(R.id.tvCurrentSub);
        imgCurrentBg = findViewById(R.id.imgCurrentBg);

        btnTime10 = findViewById(R.id.btnTime10);
        btnTime20 = findViewById(R.id.btnTime20);
        btnTime40 = findViewById(R.id.btnTime40);

        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnBack = findViewById(R.id.btnBack);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        sliderVolume = findViewById(R.id.sliderVolume);
        rvEnvironments = findViewById(R.id.rvEnvironments);
    }

    private void setupRecyclerView() {
        // تجهيز بيانات البيئات الافتراضية
        // ⚠️ تأكدي من تسمية ملفات الـ mp3 المتبقية بأحرف صغيرة في مجلد raw لاحقاً
        environmentList = new ArrayList<>();
        environmentList.add(new Environment(1, "غابة هادئة", "FOREST", R.drawable.video, R.raw.tranquil_forest));
        environmentList.add(new Environment(2, "صوت المطر", "RAIN", R.drawable.video, R.raw.tranquil_forest)); // مؤقتاً نفس الملف للتجربة
        environmentList.add(new Environment(3, "شاطئ البحر", "BEACH", R.drawable.video, R.raw.tranquil_forest));
        environmentList.add(new Environment(4, "نار دافئة", "FIREPLACE", R.drawable.video, R.raw.tranquil_forest));

        // إعداد الأدابتر وتحديث الشاشة الرئيسية عند الضغط على أي كرت بيئة
        adapter = new EnvironmentAdapter(environmentList, environment -> {
            updateMainPlayer(environment);
            // تحديث مؤشر العنصر الحالي لتسهيل التنقل عبر أزرار التالي والسابق
            for (int i = 0; i < environmentList.size(); i++) {
                if (environmentList.get(i).getId() == environment.getId()) {
                    currentPlayIndex = i;
                    break;
                }
            }
        });

        // ضبط الـ RecyclerView ليعرض بشكل أفقي
        rvEnvironments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvEnvironments.setAdapter(adapter);
    }

    private void updateMainPlayer(Environment environment) {
        tvCurrentTitle.setText(environment.getTitle());
        tvCurrentSub.setText(environment.getSubtitle());
        imgCurrentBg.setImageResource(environment.getImageResId());

        // إذا كان المشغل قيد العمل، نقوم بتشغيل الصوت الجديد فوراً، وإلا نكتفي بتهيئته
        if (isTimerRunning) {
            playNewSound(environment.getSoundResId());
        } else {
            prepareSoundOnly(environment.getSoundResId());
        }
    }

    // دالة لتشغيل الصوت بشكل فوري وتكراره تلقائياً
    private void playNewSound(int soundResId) {
        stopAndReleaseMediaPlayer();
        try {
            mediaPlayer = MediaPlayer.create(this, soundResId);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } else {
                Toast.makeText(this, "فشل تحميل ملف الصوت!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // دالة لتهيئة الصوت فقط بالخلفية دون تشغيله مباشرة
    private void prepareSoundOnly(int soundResId) {
        stopAndReleaseMediaPlayer();
        try {
            mediaPlayer = MediaPlayer.create(this, soundResId);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupVolumeControl() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        sliderVolume.setValueFrom(0);
        sliderVolume.setValueTo(maxVolume);
        sliderVolume.setValue(currentVolume);

        sliderVolume.addOnChangeListener((slider, value, fromUser) -> {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) value, 0);
        });
    }

    private void setupTimerButtons() {
        btnTime10.setOnClickListener(v -> selectTimerButton(btnTime10, 10));
        btnTime20.setOnClickListener(v -> selectTimerButton(btnTime20, 20));
        btnTime40.setOnClickListener(v -> selectTimerButton(btnTime40, 40));
    }

    private void selectTimerButton(TextView selectedButton, int minutes) {
        // 1. إعادة تعيين شكل جميع الأزرار لتصبح بيضاء غير محددة
        resetTimerButtonsStyle();

        // 2. تطبيق التصميم الأزرق المحدد على الزر المضغوط
        selectedButton.setBackgroundResource(R.drawable.bg_timer_chip_selected);
        selectedButton.setTextColor(getResources().getColor(android.R.color.white));

        // 3. تحديث الوقت وإيقاف المؤقت القديم إذا كان يعمل
        pauseTimer();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        timeLeftInMillis = minutes * 60 * 1000;
        updateCountDownText();
    }

    private void resetTimerButtonsStyle() {
        int primaryColor = android.graphics.Color.parseColor("#2D587B");

        btnTime10.setBackgroundResource(R.drawable.bg_timer_chip);
        btnTime10.setTextColor(primaryColor);

        btnTime20.setBackgroundResource(R.drawable.bg_timer_chip);
        btnTime20.setTextColor(primaryColor);

        btnTime40.setBackgroundResource(R.drawable.bg_timer_chip);
        btnTime40.setTextColor(primaryColor);
    }

    private void setupPlayerControls() {
        // زر الرجوع في الأعلى
        btnBack.setOnClickListener(v -> finish());

        // زر التشغيل والإيقاف المؤقت
        btnPlayPause.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            } else {
                startTimer();
                // إذا لم يتم تهيئة الـ MediaPlayer بعد، نقوم بتهيئته
                if (mediaPlayer == null) {
                    prepareSoundOnly(environmentList.get(currentPlayIndex).getSoundResId());
                }
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });

        // زر البيئة التالية
        btnNext.setOnClickListener(v -> {
            if (currentPlayIndex < environmentList.size() - 1) {
                currentPlayIndex++;
            } else {
                currentPlayIndex = 0; // العودة لأول بيئة إذا وصلنا للنهاية
            }
            updateMainPlayer(environmentList.get(currentPlayIndex));
        });

        // @@@ زر البيئة السابقة
        btnPrevious.setOnClickListener(v -> {
            if (currentPlayIndex > 0) {
                currentPlayIndex--;
            } else {
                currentPlayIndex = environmentList.size() - 1; // الذهاب لآخر بيئة إذا كنا في البداية
            }
            updateMainPlayer(environmentList.get(currentPlayIndex));
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                tvTimer.setText("انتهت جلستك بنجاح 🌸");

                // إيقاف تشغيل الصوت فور انتهاء الوقت المحدد للجلسة
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        }.start();

        isTimerRunning = true;
        btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "متبقي %02d:%02d د", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    // دالة مهمة جداً لتحرير موارد مشغل الصوت ومنع استهلاك الذاكرة (Memory Leak)
    private void stopAndReleaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // إيقاف العداد والمشغل فور الخروج من الشاشة
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopAndReleaseMediaPlayer();
    }
}