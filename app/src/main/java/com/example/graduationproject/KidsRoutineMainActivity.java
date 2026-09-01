package com.example.graduationproject;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.graduationproject.dialogs.KidsRoutineCelebrationDialog;
import com.example.graduationproject.models.KidsRoutineRoutineItem;
import com.example.graduationproject.ui.KidsRoutineAddItemBottomSheet;
import com.example.graduationproject.util.KidsRoutineColorUtils;
import com.example.graduationproject.view.KidsRoutineSkyArcView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class KidsRoutineMainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageManager.wrapContext(newBase));
    }

    // ===== state (mirrors the useState hooks in the React component) =====
    private final List<KidsRoutineRoutineItem> items = new ArrayList<>();
    private int streak = 3;
    private final List<String> stickers = new ArrayList<>();
    private boolean hasCelebratedToday = false;
    private boolean editMode = false;

    private static final String[] STICKER_POOL = {"🌟", "🏆", "🦋", "🌈", "🎈", "🐣", "🍀", "🪁"};
    private final Random random = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());

    // ===== views =====
    private FrameLayout headerContainer;
    private KidsRoutineSkyArcView skyArcView;
    private TextView tvGreeting, tvProgress, tvStreak;
    private View mascotAvatar, cloud1, cloud2;
    private LinearLayout stickerShelf, stickerRow, routineListContainer;
    private View emptyState;
    private ImageButton btnEdit, btnReset, btnAdd, btnBack;

    private final GradientDrawable headerGradient = new GradientDrawable();
    private final Map<String, View> cardViewsById = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kids_routine_activity_main);

        bindViews();
        setupHeaderGradient();
        seedDefaultData();
        setupListeners();
        startAmbientAnimations();

        renderList();
        updateHeader(false);
        
        // Initial status bar color sync
        updateStatusBar(currentTopColor != 0 ? currentTopColor : KidsRoutineColorUtils.getSky(0).top);
        updateNavigationBar();
    }

    private void updateNavigationBar() {
        Window window = getWindow();
        int navColor = getColor(R.color.kids_routine_phone_bg);
        window.setNavigationBarColor(navColor);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
        double luminance = (0.299 * android.graphics.Color.red(navColor) +
                0.587 * android.graphics.Color.green(navColor) +
                0.114 * android.graphics.Color.blue(navColor)) / 255.0;
        controller.setAppearanceLightNavigationBars(luminance > 0.5);
    }

    // ------------------------------------------------------------------
    // Setup
    // ------------------------------------------------------------------

    private void bindViews() {
        headerContainer = findViewById(R.id.headerContainer);
        skyArcView = findViewById(R.id.skyArcView);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvProgress = findViewById(R.id.tvProgress);
        tvStreak = findViewById(R.id.tvStreak);
        mascotAvatar = findViewById(R.id.mascotAvatar);
        cloud1 = findViewById(R.id.cloud1);
        cloud2 = findViewById(R.id.cloud2);
        stickerShelf = findViewById(R.id.stickerShelf);
        stickerRow = findViewById(R.id.stickerRow);
        routineListContainer = findViewById(R.id.routineListContainer);
        emptyState = findViewById(R.id.emptyState);
        btnEdit = findViewById(R.id.btnEdit);
        btnReset = findViewById(R.id.btnReset);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupHeaderGradient() {
        headerGradient.setShape(GradientDrawable.RECTANGLE);
        headerGradient.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        float r = dp(28);
        headerGradient.setCornerRadii(new float[]{0, 0, 0, 0, r, r, r, r});
        headerContainer.setBackground(headerGradient);
    }

    private void seedDefaultData() {
        items.add(new KidsRoutineRoutineItem("i1", "🌞", getString(R.string.kids_routine_static_item_wake_up), KidsRoutineRoutineItem.PERIOD_MORNING, false));
        items.add(new KidsRoutineRoutineItem("i2", "🦷", getString(R.string.kids_routine_static_item_brush_teeth), KidsRoutineRoutineItem.PERIOD_MORNING, false));
        items.add(new KidsRoutineRoutineItem("i3", "🍳", getString(R.string.kids_routine_static_item_breakfast), KidsRoutineRoutineItem.PERIOD_MORNING, false));
        items.add(new KidsRoutineRoutineItem("i4", "👕", getString(R.string.kids_routine_static_item_get_dressed), KidsRoutineRoutineItem.PERIOD_MORNING, false));
        items.add(new KidsRoutineRoutineItem("i5", "🎨", getString(R.string.kids_routine_static_item_play_draw), KidsRoutineRoutineItem.PERIOD_NOON, false));
        items.add(new KidsRoutineRoutineItem("i6", "🍽️", getString(R.string.kids_routine_static_item_lunch), KidsRoutineRoutineItem.PERIOD_NOON, false));
        items.add(new KidsRoutineRoutineItem("i7", "🛁", getString(R.string.kids_routine_static_item_shower), KidsRoutineRoutineItem.PERIOD_EVENING, false));
        items.add(new KidsRoutineRoutineItem("i8", "📖", getString(R.string.kids_routine_static_item_story), KidsRoutineRoutineItem.PERIOD_EVENING, false));
        items.add(new KidsRoutineRoutineItem("i9", "🌙", getString(R.string.kids_routine_static_item_sleep), KidsRoutineRoutineItem.PERIOD_EVENING, false));

        stickers.add("🌟");
        stickers.add("🏆");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            editMode = !editMode;
            btnEdit.setBackgroundResource(editMode ? R.drawable.kids_routine_bg_small_fab_active : R.drawable.kids_routine_bg_small_fab);
            btnEdit.setColorFilter(editMode
                    ? getColor(R.color.kids_routine_card_white)
                    : getColor(R.color.kids_routine_ink_purple));
            renderList();
        });

        btnReset.setOnClickListener(v -> {
            for (KidsRoutineRoutineItem item : items) item.setDone(false);
            hasCelebratedToday = false;
            renderList();
            updateHeader(true);
        });

        btnAdd.setOnClickListener(v -> {
            KidsRoutineAddItemBottomSheet sheet = new KidsRoutineAddItemBottomSheet();
            sheet.setOnAddListener((emoji, label, period) -> {
                items.add(new KidsRoutineRoutineItem("custom-" + System.currentTimeMillis(), emoji, label, period, false));
                renderList();
                updateHeader(true);
            });
            sheet.show(getSupportFragmentManager(), "add_item");
        });
    }

    /** Loops matching the CSS `floatY` (mascot/clouds) keyframes. */
    private void startAmbientAnimations() {
        loopFloat(mascotAvatar, dp(-6), 1600);
        loopFloat(cloud1, dp(-5), 2500);
        handler.postDelayed(() -> loopFloat(cloud2, dp(-5), 3000), 400);
    }

    private void loopFloat(View view, float distance, long halfDuration) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, distance);
        anim.setDuration(halfDuration);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    // ------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------

    private void renderList() {
        routineListContainer.removeAllViews();
        cardViewsById.clear();

        String[][] periods = {
                {KidsRoutineRoutineItem.PERIOD_MORNING, getString(R.string.kids_routine_period_morning), "🌅"},
                {KidsRoutineRoutineItem.PERIOD_NOON, getString(R.string.kids_routine_period_noon), "☀️"},
                {KidsRoutineRoutineItem.PERIOD_EVENING, getString(R.string.kids_routine_period_evening), "🌙"},
        };

        LayoutInflater inflater = LayoutInflater.from(this);
        boolean anyItems = !items.isEmpty();

        for (String[] period : periods) {
            List<KidsRoutineRoutineItem> group = new ArrayList<>();
            for (KidsRoutineRoutineItem it : items) if (it.getPeriod().equals(period[0])) group.add(it);
            if (group.isEmpty()) continue;

            View header = inflater.inflate(R.layout.kids_routine_view_period_header, routineListContainer, false);
            ((TextView) header.findViewById(R.id.tvPeriodEmoji)).setText(period[2]);
            ((TextView) header.findViewById(R.id.tvPeriodLabel)).setText(period[1]);
            routineListContainer.addView(header);

            for (KidsRoutineRoutineItem item : group) {
                View card = inflater.inflate(R.layout.kids_routine_view_routine_card, routineListContainer, false);
                bindCard(card, item);
                routineListContainer.addView(card);
                cardViewsById.put(item.getId(), card);
            }

            View spacer = new View(this);
            spacer.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpInt(10)));
            routineListContainer.addView(spacer);
        }

        emptyState.setVisibility(anyItems ? View.GONE : View.VISIBLE);
        renderStickers();
    }

    private void renderStickers() {
        stickerRow.removeAllViews();
        if (stickers.isEmpty()) {
            stickerShelf.setVisibility(View.GONE);
            return;
        }
        stickerShelf.setVisibility(View.VISIBLE);
        for (String s : stickers) {
            TextView tv = new TextView(this);
            tv.setText(s);
            tv.setTextSize(18f);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dpInt(4));
            tv.setLayoutParams(lp);
            stickerRow.addView(tv);
        }
    }

    private void bindCard(View card, KidsRoutineRoutineItem item) {
        View accentStrip = card.findViewById(R.id.accentStrip);
        FrameLayout emojiCircle = card.findViewById(R.id.emojiCircle);
        TextView tvEmoji = card.findViewById(R.id.tvItemEmoji);
        TextView tvLabel = card.findViewById(R.id.tvItemLabel);
        FrameLayout checkCircle = card.findViewById(R.id.checkCircle);
        ImageView ivCheck = card.findViewById(R.id.ivCheck);
        ImageButton btnDelete = card.findViewById(R.id.btnDeleteItem);

        accentStrip.setBackgroundColor(periodAccent(item.getPeriod()));
        tvEmoji.setText(item.getEmoji());
        tvLabel.setText(item.getLabel());

        applyDoneVisuals(card, emojiCircle, checkCircle, ivCheck, item.isDone());

        btnDelete.setVisibility(editMode ? View.VISIBLE : View.GONE);
        btnDelete.setOnClickListener(v -> removeItem(item.getId()));

        card.setOnClickListener(v -> toggleItem(item.getId()));
    }

    private void applyDoneVisuals(View card, FrameLayout emojiCircle, FrameLayout checkCircle,
                                   ImageView ivCheck, boolean done) {
        card.setAlpha(done ? 0.62f : 1f);
        emojiCircle.setBackgroundResource(done ? R.drawable.kids_routine_bg_emoji_circle_done : R.drawable.kids_routine_bg_emoji_circle_undone);
        checkCircle.setBackgroundResource(done ? R.drawable.kids_routine_bg_check_done : R.drawable.kids_routine_bg_check_unchecked);
        ivCheck.setVisibility(done ? View.VISIBLE : View.INVISIBLE);
    }

    private int periodAccent(String period) {
        switch (period) {
            case KidsRoutineRoutineItem.PERIOD_NOON: return getColor(R.color.kids_routine_period_noon);
            case KidsRoutineRoutineItem.PERIOD_EVENING: return getColor(R.color.kids_routine_period_evening);
            default: return getColor(R.color.kids_routine_period_morning);
        }
    }

    // ------------------------------------------------------------------
    // Interactions
    // ------------------------------------------------------------------

    private void toggleItem(String id) {
        KidsRoutineRoutineItem item = findItem(id);
        if (item == null) return;

        boolean nowDone = !item.isDone();
        item.setDone(nowDone);

        View card = cardViewsById.get(id);
        if (card != null) {
            FrameLayout emojiCircle = card.findViewById(R.id.emojiCircle);
            FrameLayout checkCircle = card.findViewById(R.id.checkCircle);
            ImageView ivCheck = card.findViewById(R.id.ivCheck);
            applyDoneVisuals(card, emojiCircle, checkCircle, ivCheck, nowDone);

            if (nowDone) {
                playCardBounce(card);
                playStarBurst(card);
            }
        }

        updateHeader(true);

        int total = items.size();
        long done = countDone();
        if (total > 0 && done == total && !hasCelebratedToday) {
            handler.postDelayed(this::celebrate, 450);
        }
    }

    private void removeItem(String id) {
        KidsRoutineRoutineItem item = findItem(id);
        if (item != null) items.remove(item);
        renderList();
        updateHeader(true);
    }

    private KidsRoutineRoutineItem findItem(String id) {
        for (KidsRoutineRoutineItem it : items) if (it.getId().equals(id)) return it;
        return null;
    }

    private long countDone() {
        long c = 0;
        for (KidsRoutineRoutineItem it : items) if (it.isDone()) c++;
        return c;
    }

    private void celebrate() {
        hasCelebratedToday = true;
        streak += 1;
        String newSticker = STICKER_POOL[random.nextInt(STICKER_POOL.length)];
        stickers.add(newSticker);
        renderStickers();
        tvStreak.setText(String.valueOf(streak));

        KidsRoutineCelebrationDialog dialog = new KidsRoutineCelebrationDialog(this, newSticker, streak);
        dialog.show();
    }

    // ------------------------------------------------------------------
    // Card micro-animations: bounce (cardBounce) + star burst (starBurst)
    // ------------------------------------------------------------------

    private void playCardBounce(View card) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, View.SCALE_X, 1f, 1.035f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, View.SCALE_Y, 1f, 1.035f, 1f);
        scaleX.setDuration(400);
        scaleY.setDuration(400);
        scaleX.start();
        scaleY.start();
    }

    private void playStarBurst(View card) {
        FrameLayout burstContainer = card.findViewById(R.id.burstContainer);
        if (burstContainer == null) return;

        String[] particles = {"✨", "⭐", "🌟"};
        float[][] deltas = {{-dp(26), -dp(22)}, {dp(26), -dp(18)}, {0, -dp(30)}};
        long[] delays = {0, 50, 100};

        for (int i = 0; i < particles.length; i++) {
            TextView star = new TextView(this);
            star.setText(particles[i]);
            star.setTextSize(16f);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = android.view.Gravity.START | android.view.Gravity.CENTER_VERTICAL;
            lp.setMarginStart(dpInt(60));
            star.setLayoutParams(lp);
            burstContainer.addView(star);

            float dx = deltas[i][0];
            float dy = deltas[i][1];

            ObjectAnimator tx = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 0f, dx);
            ObjectAnimator ty = ObjectAnimator.ofFloat(star, View.TRANSLATION_Y, 0f, dy);
            ObjectAnimator sc = ObjectAnimator.ofFloat(star, View.SCALE_X, 0.6f, 1.1f);
            ObjectAnimator sc2 = ObjectAnimator.ofFloat(star, View.SCALE_Y, 0.6f, 1.1f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(star, View.ALPHA, 1f, 0f);

            for (ObjectAnimator a : new ObjectAnimator[]{tx, ty, sc, sc2, alpha}) {
                a.setDuration(700);
                a.setStartDelay(delays[i]);
                a.setInterpolator(new OvershootInterpolator(0.4f));
                a.start();
            }

            burstContainer.postDelayed(() -> burstContainer.removeView(star), delays[i] + 750);
        }
    }

    // ------------------------------------------------------------------
    // Header (sky, progress, greeting, streak)
    // ------------------------------------------------------------------

    private void updateHeader(boolean animate) {
        int total = items.size();
        long done = countDone();
        float progress = total == 0 ? 0f : (float) done / (float) total;

        String progressText = getString(R.string.kids_routine_progress_fmt, (int) done, total);
        if (total > 0 && done == total) progressText += getString(R.string.kids_routine_progress_done_suffix);
        tvProgress.setText(progressText);

        tvGreeting.setText(KidsRoutineColorUtils.greeting(progress, total, this));
        tvStreak.setText(String.valueOf(streak));

        skyArcView.setProgress(progress, animate);
        animateHeaderGradient(progress, animate);
    }

    private int currentTopColor = 0;
    private int currentBottomColor = 0;

    private void animateHeaderGradient(float progress, boolean animate) {
        KidsRoutineColorUtils.SkyColors sky = KidsRoutineColorUtils.getSky(progress);

        if (!animate || currentTopColor == 0) {
            currentTopColor = sky.top;
            currentBottomColor = sky.bottom;
            headerGradient.setColors(new int[]{currentTopColor, currentBottomColor});
            updateStatusBar(currentTopColor);
            return;
        }

        int fromTop = currentTopColor;
        int fromBottom = currentBottomColor;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(800);
        ArgbEvaluator evaluator = new ArgbEvaluator();
        animator.addUpdateListener(a -> {
            float t = (float) a.getAnimatedValue();
            int top = (int) evaluator.evaluate(t, fromTop, sky.top);
            int bottom = (int) evaluator.evaluate(t, fromBottom, sky.bottom);
            headerGradient.setColors(new int[]{top, bottom});
            updateStatusBar(top);
        });
        animator.start();
        currentTopColor = sky.top;
        currentBottomColor = sky.bottom;
    }

    private void updateStatusBar(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(color);

        // Toggle light/dark status bar icons based on luminance
        double luminance = (0.299 * android.graphics.Color.red(color) +
                0.587 * android.graphics.Color.green(color) +
                0.114 * android.graphics.Color.blue(color)) / 255.0;

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(luminance > 0.5);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private int dpInt(float value) {
        return Math.round(dp(value));
    }
}
