package com.example.graduationproject;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.adapters.AdultChatChatAdapter;
import com.example.graduationproject.data.ChatDatabase;
import com.example.graduationproject.data.ChatMessageDao;
import com.example.graduationproject.data.ChatMessageEntity;
import com.example.graduationproject.data.SalamGeminiService;
import com.example.graduationproject.models.AdultChatCardData;
import com.example.graduationproject.models.AdultChatChatMessage;
import com.example.graduationproject.models.AdultChatReply;
import com.example.graduationproject.models.AdultChatScript;
import com.example.graduationproject.models.AdultChatScriptNode;
import com.example.graduationproject.ui.AdultChatOrbView;
import com.example.graduationproject.util.AdultChatHapticUtil;
import com.example.graduationproject.util.AdultChatIdGen;
import com.example.graduationproject.util.AdultChatStreakPrefs;
import com.example.graduationproject.util.AdultChatTimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AdultChatMainActivity extends AppCompatActivity implements AdultChatChatAdapter.Listener {

    // screens
    private View welcomeScreen, chatScreen;
    private AdultChatOrbView orbWelcome, orbHeader;
    private FrameLayout orbFlyContainer, btnOrbHeader, screenContainer;
    private View topbarTitle;
    private TextView txtCompanionName, txtCompanionStatus, txtStreak;
    private LinearLayout welcomeChipsGrid, chatChipsGrid;
    private RecyclerView recyclerMessages;
    private AdultChatChatAdapter adapter;

    // input bar
    private View contextBar;
    private TextView txtContextLabel, txtContextSnippet;
    private ImageView btnContextClose;
    private View emojiGrid;
    private EditText editInput;
    private FrameLayout btnMicSend;
    private ImageView iconMic, iconSend;
    private ImageView btnEmojiToggle;

    // recording overlay
    private View recordingOverlay;
    private TextView txtRecTimer, txtRecLocked, txtRecCaption, txtDragCancelHint;
    private View sonarRing1, sonarRing2, sonarRing3;
    private AdultChatOrbView orbRecording;
    private View micIndicator, micIndicatorWrap, dragHints;
    private View btnRecClose, btnRecCancelRound, btnRecSendRound;

    // calm overlay
    private View calmOverlay;
    private TextView txtCalmCycles, txtCalmPhase, btnCalmFinish;
    private AdultChatOrbView orbCalm;
    private View btnCalmClose;
    private FrameLayout calmOrbScaleContainer;

    private TextView toastView;

    private ChatMessageDao chatDao;
    private SalamGeminiService geminiService;
    private final Executor dbExecutor = Executors.newSingleThreadExecutor();
    private static final long RETENTION_MILLIS = 7L * 24 * 60 * 60 * 1000;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private String currentNode = "start";
    private String companionName = "الرفيق";
    private String tone = "supportive";
    private boolean screenIsChat = false;

    private Long replyToId = null;
    private boolean replyToFromUser = false;
    private String replyToSnippet = null;
    private Long editingId = null;

    private boolean hasExercised = false;
    private final List<Runnable> pendingRingAnimators = new ArrayList<>();

    private static final String[] EMOJIS = {
            "😊", "🙂", "😍", "😂", "🥹", "😢", "😔", "😴",
            "🤝", "🙏", "💪", "🌿", "🌸", "☀️", "🌙", "✨",
            "❤️", "💜", "👍", "👏", "🤔", "😌", "🥰", "😅"
    };

    // ---------- recording gesture state ----------
    private boolean recording = false;
    private String dragState = "hold"; // hold | locked | cancelling
    private float dragStartX, dragStartY;
    private int recSeconds = 0;
    private Runnable recTimerTask;
    private Runnable recAmpTask;

    // ---------- calm phases ----------
    private static final String[] CALM_LABELS = {
            "شهيق ببطء من الأنف", "احبس نفسك", "أخرجه ببطء من الفم", "استرح"
    };
    private static final int[] CALM_SECONDS = {4, 4, 4, 2};
    private static final float[] CALM_SCALE = {1.35f, 1.35f, 0.85f, 0.85f};
    private int calmPhaseIndex = 0;
    private int calmCycles = 0;
    private Runnable calmTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adult_chat_activity_main);
        bindViews();
        setupAmbient();
        setupChat();
        setupWelcome();
        setupInputBar();
        setupRecordingOverlay();
        setupCalmOverlay();
        setupStreak();
        setupCompanionPopups();

        chatDao = ChatDatabase.getInstance(this).chatMessageDao();
        geminiService = new SalamGeminiService(this);
        loadMessagesFromDb();
    }

    private void bindViews() {
        welcomeScreen = findViewById(R.id.welcomeScreen);
        chatScreen = findViewById(R.id.chatScreen);
        screenContainer = findViewById(R.id.screenContainer);
        orbWelcome = welcomeScreen.findViewById(R.id.orbWelcome);
        orbFlyContainer = welcomeScreen.findViewById(R.id.orbFlyContainer);
        welcomeChipsGrid = welcomeScreen.findViewById(R.id.welcomeChipsGrid);

        orbHeader = findViewById(R.id.orbHeader);
        btnOrbHeader = findViewById(R.id.btnOrbHeader);
        topbarTitle = findViewById(R.id.topbarTitle);
        txtCompanionName = findViewById(R.id.txtCompanionName);
        txtCompanionStatus = findViewById(R.id.txtCompanionStatus);
        txtStreak = findViewById(R.id.txtStreak);

        recyclerMessages = chatScreen.findViewById(R.id.recyclerMessages);
        chatChipsGrid = chatScreen.findViewById(R.id.chatChipsGrid);

        View inputBar = findViewById(R.id.inputBar);
        contextBar = inputBar.findViewById(R.id.contextBar);
        txtContextLabel = inputBar.findViewById(R.id.txtContextLabel);
        txtContextSnippet = inputBar.findViewById(R.id.txtContextSnippet);
        btnContextClose = inputBar.findViewById(R.id.btnContextClose);
        emojiGrid = inputBar.findViewById(R.id.emojiGrid);
        editInput = inputBar.findViewById(R.id.editInput);
        btnMicSend = inputBar.findViewById(R.id.btnMicSend);
        iconMic = inputBar.findViewById(R.id.iconMic);
        iconSend = inputBar.findViewById(R.id.iconSend);
        btnEmojiToggle = inputBar.findViewById(R.id.btnEmojiToggle);

        recordingOverlay = findViewById(R.id.recordingOverlay);
        txtRecTimer = recordingOverlay.findViewById(R.id.txtRecTimer);
        txtRecLocked = recordingOverlay.findViewById(R.id.txtRecLocked);
        txtRecCaption = recordingOverlay.findViewById(R.id.txtRecCaption);
        txtDragCancelHint = recordingOverlay.findViewById(R.id.txtDragCancelHint);
        sonarRing1 = recordingOverlay.findViewById(R.id.sonarRing1);
        sonarRing2 = recordingOverlay.findViewById(R.id.sonarRing2);
        sonarRing3 = recordingOverlay.findViewById(R.id.sonarRing3);
        orbRecording = recordingOverlay.findViewById(R.id.orbRecording);
        micIndicator = recordingOverlay.findViewById(R.id.micIndicator);
        micIndicatorWrap = recordingOverlay.findViewById(R.id.micIndicatorWrap);
        dragHints = recordingOverlay.findViewById(R.id.dragHints);
        btnRecClose = recordingOverlay.findViewById(R.id.btnRecClose);
        btnRecCancelRound = recordingOverlay.findViewById(R.id.btnRecCancelRound);
        btnRecSendRound = recordingOverlay.findViewById(R.id.btnRecSendRound);

        calmOverlay = findViewById(R.id.calmOverlay);
        txtCalmCycles = calmOverlay.findViewById(R.id.txtCalmCycles);
        txtCalmPhase = calmOverlay.findViewById(R.id.txtCalmPhase);
        btnCalmFinish = calmOverlay.findViewById(R.id.btnCalmFinish);
        orbCalm = calmOverlay.findViewById(R.id.orbCalm);
        btnCalmClose = calmOverlay.findViewById(R.id.btnCalmClose);
        calmOrbScaleContainer = calmOverlay.findViewById(R.id.calmOrbScaleContainer);

        toastView = findViewById(R.id.toastView);

        findViewById(R.id.btnBack).setOnClickListener(v -> AdultChatHapticUtil.vibrate(this));
    }

    // ================= ambient background: aurora blobs + dust motes =================
    private void setupAmbient() {
        driftBlob(findViewById(R.id.blob1), 40, 60, 34000);
        driftBlob(findViewById(R.id.blob2), -50, -40, 40000);
        driftBlob(findViewById(R.id.blob3), 30, -50, 46000);

        FrameLayout dustLayer = findViewById(R.id.dustLayer);
        dustLayer.post(() -> {
            int w = dustLayer.getWidth();
            int h = dustLayer.getHeight();
            if (w <= 0 || h <= 0) return;
            for (int i = 0; i < 14; i++) {
                addDustMote(dustLayer, w, h, i);
            }
        });
    }

    private void driftBlob(View blob, float dx, float dy, long duration) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(duration);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.addUpdateListener(a -> {
            float t = (float) a.getAnimatedValue();
            blob.setTranslationX(dx * t);
            blob.setTranslationY(dy * t);
        });
        anim.start();
    }

    private void addDustMote(FrameLayout layer, int w, int h, int i) {
        View mote = new View(this);
        int size = (int) ((1.5f + (i * 7) % 3) * getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);
        lp.leftMargin = (int) (w * ((i * 37) % 100) / 100f);
        lp.topMargin = h;
        mote.setLayoutParams(lp);
        mote.setBackgroundResource(R.drawable.dust_mote);
        layer.addView(mote);

        long duration = (16 + (i * 5) % 10) * 1000L;
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(duration);
        anim.setStartDelay((i * 900) % 6000);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.addUpdateListener(a -> {
            float t = (float) a.getAnimatedValue();
            mote.setTranslationY(-h * t - 10);
            mote.setTranslationX(14f * t);
            float alpha = t < 0.1f ? t / 0.1f : t > 0.9f ? (1f - t) / 0.1f : 1f;
            mote.setAlpha(Math.max(0f, Math.min(0.7f, alpha)));
        });
        anim.start();
    }

    // ================= welcome screen =================
    private void setupWelcome() {
        AdultChatScriptNode start = AdultChatScript.get("start");
        renderChips(welcomeChipsGrid, start.replies, this::onReplyChosen);
    }

    // ================= chat setup =================
    private void setupChat() {
        adapter = new AdultChatChatAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setAdapter(adapter);
    }

    private void loadMessagesFromDb() {
        dbExecutor.execute(() -> {
            long threshold = System.currentTimeMillis() - RETENTION_MILLIS;
            chatDao.deleteOlderThan(threshold);
            List<ChatMessageEntity> saved = chatDao.getAll();

            long maxId = 0;
            for (ChatMessageEntity e : saved) {
                if (e.id > maxId) maxId = e.id;
            }
            AdultChatIdGen.setStart(maxId);

            mainHandler.post(() -> {
                if (saved.isEmpty()) {
                    AdultChatChatMessage first = new AdultChatChatMessage(AdultChatIdGen.next(), AdultChatChatMessage.Kind.BOT, AdultChatScript.get("start").bot, AdultChatTimeUtil.nowTime());
                    adapter.submit(first);
                    persistMessage(first);
                } else {
                    for (ChatMessageEntity e : saved) {
                        adapter.submit(entityToAdultChatMessage(e));
                    }
                    scrollToBottom();
                }
            });
        });
    }

    private AdultChatChatMessage entityToAdultChatMessage(ChatMessageEntity e) {
        AdultChatChatMessage.Kind kind = e.fromUser ? AdultChatChatMessage.Kind.USER : AdultChatChatMessage.Kind.BOT;
        AdultChatChatMessage m = new AdultChatChatMessage(e.id, kind, e.text, e.time);
        if (e.audioPath != null) {
            m.voice = true;
            m.voiceDuration = AdultChatTimeUtil.mmss(e.audioDurationSec);
        }
        if (e.cardType != null) {
            m.card = mapExerciseTypeToCard(e.cardType);
        }
        return m;
    }

    private void persistMessage(AdultChatChatMessage msg) {
        dbExecutor.execute(() -> {
            ChatMessageEntity e = new ChatMessageEntity();
            e.id = msg.id; // use the same ID
            e.fromUser = msg.kind == AdultChatChatMessage.Kind.USER;
            e.text = msg.text;
            e.time = msg.time;
            e.timestamp = System.currentTimeMillis();
            if (msg.card != null) {
                e.cardType = msg.card.type.name();
            }
            if (msg.voice) {
                e.audioDurationSec = 0; // should probably pass seconds here
            }
            chatDao.insert(e);
        });
    }

    private AdultChatCardData mapExerciseTypeToCard(String type) {
        if (type == null) return null;
        switch (type.toUpperCase()) {
            case "BREATHING":
                return AdultChatCardData.exercise("تنفّس الصندوق", "BREATHING EXERCISE", "٤ دقائق",
                        "تمرين ٤ دقائق لإعادة ضبط جهازك العصبي بإيقاع بسيط.");
            case "GROUNDING":
                return AdultChatCardData.exercise("تمرين ٥-٤-٣-٢-١", "GROUNDING EXERCISE", "٥ دقائق",
                        "عد للحظة الحاضرة بالتركيز على حواسك الخمس.");
            case "CBT_REFRAME":
                return AdultChatCardData.exercise("تغيير زاوية الرؤية", "REFRAMING", "٣ دقائق",
                        "لنفحص هذه الفكرة معاً ونبحث عن نظرة أكثر توازناً.");
            case "BODY_MAP":
                return AdultChatCardData.exercise("استمع لجسدك", "BODY MAP", "٥ دقائق",
                        "حدد مكان التوتر في جسدك وحاول تحريره.");
            case "FUTURE_LETTER":
                return AdultChatCardData.exercise("رسالة إلى نفسي القادمة", "FUTURE LETTER", "٣ دقائق",
                        "اكتب لنفسك كلمات تشجيع ستصلك في الوقت الذي تختاره.");
            case "SOS":
                return AdultChatCardData.sos();
            default:
                return null;
        }
    }

    // ================= streak =================
    private void setupStreak() {
        new Thread(() -> {
            int streak = AdultChatStreakPrefs.loadAndBump(this);
            mainHandler.post(() -> {
                if (streak > 0) {
                    txtStreak.setText("🔥 " + streak);
                    txtStreak.setVisibility(View.VISIBLE);
                    txtStreak.setScaleX(0.6f); txtStreak.setScaleY(0.6f); txtStreak.setAlpha(0f);
                    txtStreak.animate().scaleX(1f).scaleY(1f).alpha(1f)
                            .setInterpolator(new OvershootInterpolator(1.6f)).setDuration(400).start();
                }
                if (streak == 3 || streak == 7 || streak == 14 || streak == 30) {
                    adapter.submit(AdultChatChatMessage.system(AdultChatIdGen.next(), "🔥 " + streak + " أيام متتالية معنا — استمراريتك ملهمة!"));
                    scrollToBottom();
                }
            });
        }).start();
    }

    // ================= quick reply chips =================
    private interface ChipCallback { void onPick(AdultChatReply r); }

    private void renderChips(LinearLayout container, List<AdultChatReply> replies, ChipCallback cb) {
        container.removeAllViews();
        for (int i = 0; i < replies.size(); i += 2) {
            View row = LayoutInflater.from(this).inflate(R.layout.adult_chat_row_two_chips, container, false);
            TextView chip1 = row.findViewById(R.id.chip1);
            TextView chip2 = row.findViewById(R.id.chip2);
            AdultChatReply r1 = replies.get(i);
            chip1.setText(r1.label);
            chip1.setOnClickListener(v -> cb.onPick(r1));
            staggerIn(chip1, i * 70L);

            if (i + 1 < replies.size()) {
                AdultChatReply r2 = replies.get(i + 1);
                chip2.setVisibility(View.VISIBLE);
                chip2.setText(r2.label);
                chip2.setOnClickListener(v -> cb.onPick(r2));
                staggerIn(chip2, (i + 1) * 70L);
            } else {
                chip2.setVisibility(View.INVISIBLE);
            }
            container.addView(row);
        }
    }

    private void staggerIn(View v, long delay) {
        v.setAlpha(0f); v.setTranslationY(10f); v.setScaleX(0.94f); v.setScaleY(0.94f);
        v.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f)
                .setStartDelay(delay).setDuration(350).start();
    }

    private void onReplyChosen(AdultChatReply r) {
        AdultChatHapticUtil.vibrate(this);
        clearReplyEditState();
        goToChat();
        addUserMessage(r.label, null);
        pushBot(r.next);
    }

    // ================= welcome -> chat transition =================
    private void goToChat() {
        if (screenIsChat) return;
        screenIsChat = true;

        orbFlyContainer.animate()
                .translationX(118 * getResources().getDisplayMetrics().density)
                .translationY(-300 * getResources().getDisplayMetrics().density)
                .scaleX(0.16f).scaleY(0.16f).alpha(0f)
                .setDuration(600)
                .withEndAction(() -> {
                    welcomeScreen.setVisibility(View.GONE);
                    chatScreen.setVisibility(View.VISIBLE);
                    chatScreen.setAlpha(0f); chatScreen.setTranslationY(10f);
                    chatScreen.animate().alpha(1f).translationY(0f).setDuration(400).start();

                    topbarTitle.setVisibility(View.VISIBLE);
                    topbarTitle.setAlpha(0f); topbarTitle.setScaleX(0.4f); topbarTitle.setScaleY(0.4f);
                    topbarTitle.animate().alpha(1f).scaleX(1f).scaleY(1f)
                            .setInterpolator(new OvershootInterpolator(1.6f)).setDuration(350).start();
                }).start();
    }

    // ================= bot / user message flow =================
    private void pushBot(String node) {
        adapter.setTyping(true);
        scrollToBottom();
        long delay = 1100 + random.nextInt(500);
        mainHandler.postDelayed(() -> {
            adapter.setTyping(false);
            AdultChatScriptNode def = AdultChatScript.get(node);
            AdultChatChatMessage msg = new AdultChatChatMessage(AdultChatIdGen.next(), AdultChatChatMessage.Kind.BOT, def.bot, AdultChatTimeUtil.nowTime());
            msg.card = def.card;
            adapter.submit(msg);
            persistMessage(msg);
            currentNode = node;
            orbHeader.pulse();
            if ("closing".equals(node)) orbHeader.celebrate();
            scrollToBottom();
            renderCurrentQuickReplies();
        }, delay);
    }

    private void pushBotFromGemini(String userText) {
        adapter.setTyping(true);
        scrollToBottom();

        dbExecutor.execute(() -> {
            List<ChatMessageEntity> history = chatDao.getAll();
            geminiService.sendMessage(history, userText, new SalamGeminiService.GeminiCallback() {
                @Override public void onSuccess(String reply) {
                    mainHandler.post(() -> finishGeminiReply(reply));
                }
                @Override public void onError(String error) {
                    mainHandler.post(() -> finishGeminiReply(error));
                }
            });
        });
    }

    private void finishGeminiReply(String jsonString) {
        adapter.setTyping(false);
        String text = jsonString;
        AdultChatCardData suggestedCard = null;

        android.content.SharedPreferences prefs = getSharedPreferences("ChatPrefs", MODE_PRIVATE);
        int messagesSinceLastExercise = prefs.getInt("msgs_since_exercise", 0);

        try {
            if (jsonString.startsWith("{")) {
                org.json.JSONObject json = new org.json.JSONObject(jsonString);
                text = json.optString("replyText", jsonString);
                String rawExercise = json.optString("suggestedExerciseType", "NONE");

                java.util.List<String> validExercises = java.util.Arrays.asList("BREATHING", "GROUNDING", "CBT_REFRAME", "BODY_MAP", "FUTURE_LETTER");
                if (validExercises.contains(rawExercise) && messagesSinceLastExercise >= 3) {
                    suggestedCard = mapExerciseTypeToCard(rawExercise);
                    messagesSinceLastExercise = 0;
                } else {
                    messagesSinceLastExercise++;
                }
            }
        } catch (Exception ignored) {}

        prefs.edit().putInt("msgs_since_exercise", messagesSinceLastExercise).apply();

        AdultChatChatMessage botMsg = new AdultChatChatMessage(AdultChatIdGen.next(), AdultChatChatMessage.Kind.BOT, text, AdultChatTimeUtil.nowTime());
        botMsg.card = suggestedCard;
        adapter.submit(botMsg);
        persistMessage(botMsg);
        orbHeader.pulse();
        scrollToBottom();
    }

    private void renderCurrentQuickReplies() {
        AdultChatScriptNode node = AdultChatScript.get(currentNode);
        if (node != null && !node.replies.isEmpty()) {
            renderChips(chatChipsGrid, node.replies, this::onReplyChosen);
        } else {
            chatChipsGrid.removeAllViews();
        }
    }

    private long addUserMessage(String text, ReplyToInfo replyTo) {
        long id = AdultChatIdGen.next();
        AdultChatChatMessage msg = new AdultChatChatMessage(id, AdultChatChatMessage.Kind.USER, text, AdultChatTimeUtil.nowTime());
        if (replyTo != null) {
            msg.hasReplyTo = true;
            msg.replyToFromUser = replyTo.fromUser;
            msg.replyToSnippet = replyTo.snippet;
        }
        adapter.submit(msg);
        persistMessage(msg);
        scrollToBottom();
        if (text != null && text.length() > 40) {
            mainHandler.postDelayed(() -> {
                msg.seen = true;
                adapter.updateMessage(id);
            }, 1200 + random.nextInt(500));
        }
        return id;
    }

    private void addUserVoiceMessage(int seconds) {
        AdultChatChatMessage msg = new AdultChatChatMessage(AdultChatIdGen.next(), AdultChatChatMessage.Kind.USER, null, AdultChatTimeUtil.nowTime());
        msg.voice = true;
        msg.voiceDuration = AdultChatTimeUtil.mmss(Math.max(seconds, 1));
        adapter.submit(msg);
        persistMessage(msg);
        scrollToBottom();
    }

    private void scrollToBottom() {
        recyclerMessages.post(() -> recyclerMessages.smoothScrollToPosition(adapter.getItemCount() - 1));
    }

    private static class ReplyToInfo {
        boolean fromUser; String snippet;
        ReplyToInfo(boolean fromUser, String snippet) { this.fromUser = fromUser; this.snippet = snippet; }
    }

    // ================= AdultChatChatAdapter.Listener =================
    @Override public void onReply(AdultChatChatMessage msg) {
        AdultChatHapticUtil.vibrate(this);
        replyToId = msg.id;
        replyToFromUser = msg.kind == AdultChatChatMessage.Kind.USER;
        replyToSnippet = msg.voice ? "رسالة صوتية" : msg.text;
        editingId = null;
        showContextBar(false);
    }

    @Override public void onEdit(AdultChatChatMessage msg) {
        AdultChatHapticUtil.vibrate(this);
        editingId = msg.id;
        editInput.setText(msg.text == null ? "" : msg.text);
        editInput.setSelection(editInput.getText().length());
        replyToId = null;
        showContextBar(true);
    }

    @Override public void onDelete(long id) {
        AdultChatHapticUtil.vibrate(this);
        adapter.deleteMessage(id);
        dbExecutor.execute(() -> {
            // we don't have a soft delete in DB schema yet, 
            // but we can delete it entirely or leave it.
            // ChatMainActivity doesn't delete from DB on UI delete.
        });
    }

    @Override public void onRephrase(AdultChatChatMessage msg) {
        AdultChatHapticUtil.vibrate(this);
        adapter.setTyping(true);
        scrollToBottom();
        mainHandler.postDelayed(() -> {
            adapter.setTyping(false);
            AdultChatChatMessage m = new AdultChatChatMessage(AdultChatIdGen.next(), AdultChatChatMessage.Kind.BOT, "بمعنى أبسط: " + msg.text, AdultChatTimeUtil.nowTime());
            adapter.submit(m);
            orbHeader.pulse();
            scrollToBottom();
        }, 900 + random.nextInt(400));
    }

    @Override public void onExplainMore(AdultChatChatMessage msg) {
        AdultChatHapticUtil.vibrate(this);
        adapter.setTyping(true);
        scrollToBottom();
        mainHandler.postDelayed(() -> {
            adapter.setTyping(false);
            String extra = msg.text + "\n\nلتوضيح أكثر: خطوة صغيرة بكل مرة كافية — ما لازم تتعامل مع الموضوع دفعة وحدة.";
            AdultChatChatMessage m = new AdultChatChatMessage(AdultChatIdGen.next(), AdultChatChatMessage.Kind.BOT, extra, AdultChatTimeUtil.nowTime());
            adapter.submit(m);
            orbHeader.pulse();
            scrollToBottom();
        }, 900 + random.nextInt(400));
    }

    @Override public void onRate(long id, String rating) {
        AdultChatHapticUtil.vibrate(this);
        showToast("up".equals(rating) ? "شكراً لتقييمك 🙏" : "رح ناخد ملاحظتك بعين الاعتبار");
    }

    @Override public void onCardAction(String type) {
        AdultChatHapticUtil.vibrate(this);
        switch (type) {
            case "sos":
                openCalmOverlay();
                break;
            case "exercise":
                showToast("جارٍ فتح تمرين التنفّس 🌬️");
                if (!hasExercised) {
                    hasExercised = true;
                    adapter.submit(AdultChatChatMessage.system(AdultChatIdGen.next(), "🎉 أول تمرين تنفّس! خطوة رائعة 🌿"));
                    scrollToBottom();
                }
                break;
            case "dhikr": showToast("جارٍ فتح الأذكار 📿"); break;
            case "article": showToast("جارٍ فتح المقال 📖"); break;
            case "video": showToast("جارٍ تشغيل الفيديو ▶️"); break;
            case "moment": showToast("جارٍ فتح لحظة الهدوء 🌙"); break;
            default: break;
        }
    }

    private void showContextBar(boolean editing) {
        contextBar.setVisibility(View.VISIBLE);
        contextBar.setAlpha(0f); contextBar.setTranslationY(8f);
        contextBar.animate().alpha(1f).translationY(0f).setDuration(200).start();
        if (editing) {
            txtContextLabel.setText("تعديل رسالة");
            txtContextSnippet.setVisibility(View.GONE);
        } else {
            txtContextLabel.setText("الرد على " + (replyToFromUser ? "رسالتك" : companionName));
            txtContextSnippet.setVisibility(View.VISIBLE);
            txtContextSnippet.setText(replyToSnippet == null ? "رسالة صوتية" : replyToSnippet);
        }
    }

    private void clearReplyEditState() {
        replyToId = null; editingId = null;
        contextBar.setVisibility(View.GONE);
    }

    // ================= input bar =================
    private void setupInputBar() {
        editInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                boolean hasText = s.toString().trim().length() > 0;
                iconMic.setVisibility(hasText ? View.GONE : View.VISIBLE);
                iconSend.setVisibility(hasText ? View.VISIBLE : View.GONE);
                orbHeader.setListening(hasText);
                if (screenIsChat) {
                    txtCompanionStatus.setText(hasText ? "يقرأ رسالتك..." : "متصل الآن");
                }
            }
        });

        btnMicSend.setOnClickListener(v -> {
            if (editInput.getText().toString().trim().length() > 0) handleSend();
        });

        btnMicSend.setOnTouchListener((v, event) -> {
            if (editInput.getText().toString().trim().length() > 0) return false; // click handles send
            return handleMicTouch(event);
        });

        btnEmojiToggle.setOnClickListener(v -> {
            boolean show = emojiGrid.getVisibility() != View.VISIBLE;
            emojiGrid.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show) populateEmojiGrid();
        });

        btnContextClose.setOnClickListener(v -> {
            clearReplyEditState();
            editInput.setText("");
        });

        editInput.setOnEditorActionListener((v, actionId, event) -> {
            handleSend();
            return true;
        });
    }

    private void populateEmojiGrid() {
        android.widget.GridLayout gl = (android.widget.GridLayout) emojiGrid;
        if (gl.getChildCount() > 0) return;
        for (String e : EMOJIS) {
            TextView tv = new TextView(this);
            tv.setText(e);
            tv.setTextSize(18);
            tv.setPadding(10, 10, 10, 10);
            tv.setOnClickListener(v -> editInput.setText(editInput.getText() + e));
            gl.addView(tv);
        }
    }

    private void handleSend() {
        String text = editInput.getText().toString().trim();
        if (text.isEmpty()) return;
        AdultChatHapticUtil.vibrate(this);

        if (editingId != null) {
            adapter.editMessage(editingId, text);
            dbExecutor.execute(() -> {
                // update text in DB if we want full sync
            });
            editingId = null;
            editInput.setText("");
            emojiGrid.setVisibility(View.GONE);
            return;
        }

        goToChat();
        ReplyToInfo replyTo = null;
        if (replyToId != null) {
            replyTo = new ReplyToInfo(replyToFromUser, replyToSnippet == null ? "رسالة صوتية" : shorten(replyToSnippet, 60));
        }
        addUserMessage(text, replyTo);
        clearReplyEditState();
        editInput.setText("");
        emojiGrid.setVisibility(View.GONE);
        pushBotFromGemini(text);
    }

    private String shorten(String s, int max) {
        return s.length() > max ? s.substring(0, max) : s;
    }

    // ================= mic hold-to-record gesture =================
    private boolean handleMicTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                AdultChatHapticUtil.vibrate(this);
                emojiGrid.setVisibility(View.GONE);
                dragState = "hold";
                dragStartX = event.getRawX();
                dragStartY = event.getRawY();
                startRecordingUI();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!recording || "locked".equals(dragState)) return true;
                float dx = event.getRawX() - dragStartX;
                float dy = event.getRawY() - dragStartY;
                float density = getResources().getDisplayMetrics().density;
                if (dy < -70 * density) {
                    AdultChatHapticUtil.vibrate(this, 18);
                    lockRecording();
                    return true;
                }
                float clampedX = Math.min(Math.max(dx, -70 * density), 0);
                float clampedY = Math.min(Math.max(dy, -50 * density), 0);
                micIndicatorWrap.setTranslationX(clampedX);
                micIndicatorWrap.setTranslationY(clampedY);
                boolean cancelling = dx < -90 * density;
                setCancellingState(cancelling);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if ("locked".equals(dragState)) return true; // stays open, on-screen buttons take over
                if ("cancelling".equals(dragState)) cancelRecording();
                else sendRecording();
                return true;
            default:
                return true;
        }
    }

    private void setCancellingState(boolean cancelling) {
        dragState = cancelling ? "cancelling" : "hold";
        micIndicator.setBackgroundResource(cancelling ? R.drawable.adult_chat_bg_rec_mic_cancelling : R.drawable.adult_chat_bg_rec_mic_indicator);
        txtDragCancelHint.setText(cancelling ? "حرّر للإلغاء ⟵" : "اسحب لليسار للإلغاء ⟵");
        txtRecCaption.setText(cancelling ? "سيتم إلغاء التسجيل..." : "جاري الاستماع إليك... تحدّث بحريّة");
    }

    private void lockRecording() {
        dragState = "locked";
        micIndicatorWrap.setTranslationX(0); micIndicatorWrap.setTranslationY(0);
        micIndicator.setBackgroundResource(R.drawable.adult_chat_bg_rec_mic_locked);
        txtRecLocked.setVisibility(View.VISIBLE);
        dragHints.setVisibility(View.GONE);
        txtRecCaption.setText("التسجيل مستمر، اضغطي إرسال متى انتهيتِ");
    }

    // ================= recording overlay =================
    private void setupRecordingOverlay() {
        pulseSonarRing(sonarRing1, 0);
        pulseSonarRing(sonarRing2, 800);
        pulseSonarRing(sonarRing3, 1600);
        micPulseLoop();

        btnRecClose.setOnClickListener(v -> cancelRecording());
        btnRecCancelRound.setOnClickListener(v -> cancelRecording());
        btnRecSendRound.setOnClickListener(v -> sendRecording());
    }

    private void pulseSonarRing(View ring, long delay) {
        ring.setScaleX(1f); ring.setScaleY(1f); ring.setAlpha(0.65f);
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(2400);
        anim.setStartDelay(delay);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.addUpdateListener(a -> {
            float t = (float) a.getAnimatedValue();
            float s = 1f + t * 0.55f;
            ring.setScaleX(s); ring.setScaleY(s);
            ring.setAlpha(0.65f * (1f - t));
        });
        anim.start();
    }

    private void micPulseLoop() {
        ValueAnimator anim = ValueAnimator.ofFloat(1f, 1.08f, 1f);
        anim.setDuration(1400);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.addUpdateListener(a -> {
            if ("locked".equals(dragState)) return;
            float s = (float) a.getAnimatedValue();
            micIndicator.setScaleX(s); micIndicator.setScaleY(s);
        });
        anim.start();
    }

    private void startRecordingUI() {
        recording = true;
        recSeconds = 0;
        dragState = "hold";
        txtRecLocked.setVisibility(View.INVISIBLE);
        dragHints.setVisibility(View.VISIBLE);
        micIndicator.setBackgroundResource(R.drawable.adult_chat_bg_rec_mic_indicator);
        micIndicatorWrap.setTranslationX(0); micIndicatorWrap.setTranslationY(0);
        txtRecTimer.setText("00:00");
        txtRecCaption.setText("جاري الاستماع إليك... تحدّث بحريّة");
        recordingOverlay.setVisibility(View.VISIBLE);
        recordingOverlay.setAlpha(0f); recordingOverlay.setScaleX(1.03f); recordingOverlay.setScaleY(1.03f);
        recordingOverlay.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(350).start();

        recTimerTask = new Runnable() {
            @Override public void run() {
                if (!recording) return;
                recSeconds++;
                txtRecTimer.setText(AdultChatTimeUtil.mmss(recSeconds));
                mainHandler.postDelayed(this, 1000);
            }
        };
        mainHandler.postDelayed(recTimerTask, 1000);

        recAmpTask = new Runnable() {
            @Override public void run() {
                if (!recording) return;
                float amp = 0.9f + random.nextFloat() * 0.35f;
                orbRecording.animate().scaleX(amp).scaleY(amp).setDuration(160).start();
                mainHandler.postDelayed(this, 160);
            }
        };
        mainHandler.postDelayed(recAmpTask, 160);
    }

    private void closeRecordingUI() {
        recording = false;
        dragState = "hold";
        recordingOverlay.animate().alpha(0f).setDuration(200)
                .withEndAction(() -> recordingOverlay.setVisibility(View.GONE)).start();
    }

    private void cancelRecording() {
        AdultChatHapticUtil.vibrate(this);
        closeRecordingUI();
    }

    private void sendRecording() {
        AdultChatHapticUtil.vibrate(this);
        int seconds = recSeconds;
        closeRecordingUI();
        goToChat();
        addUserVoiceMessage(seconds);
        pushBot("freeReply");
    }

    // ================= one click calm overlay =================
    private void setupCalmOverlay() {
        btnCalmClose.setOnClickListener(v -> { AdultChatHapticUtil.vibrate(this); closeCalmOverlay(); });
        btnCalmFinish.setOnClickListener(v -> { AdultChatHapticUtil.vibrate(this); closeCalmOverlay(); });
    }

    private void openCalmOverlay() {
        calmPhaseIndex = 0;
        calmCycles = 0;
        calmOverlay.setVisibility(View.VISIBLE);
        calmOverlay.setAlpha(0f); calmOverlay.setScaleX(1.03f); calmOverlay.setScaleY(1.03f);
        calmOverlay.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(350).start();
        txtCalmCycles.setText("لنبدأ");
        runCalmPhase();
    }

    private void runCalmPhase() {
        String label = CALM_LABELS[calmPhaseIndex];
        float scale = CALM_SCALE[calmPhaseIndex];
        int seconds = CALM_SECONDS[calmPhaseIndex];

        txtCalmPhase.setAlpha(0f); txtCalmPhase.setTranslationY(6f);
        txtCalmPhase.setText(label);
        txtCalmPhase.animate().alpha(1f).translationY(0f).setDuration(300).start();

        calmOrbScaleContainer.animate().scaleX(scale).scaleY(scale).setDuration(seconds * 1000L).start();

        calmTask = () -> {
            if (calmOverlay.getVisibility() != View.VISIBLE) return;
            int next = (calmPhaseIndex + 1) % CALM_LABELS.length;
            calmPhaseIndex = next;
            if (next == 0) {
                calmCycles++;
                txtCalmCycles.setText("الدورة " + (calmCycles + 1));
            }
            runCalmPhase();
        };
        mainHandler.postDelayed(calmTask, seconds * 1000L);
    }

    private void closeCalmOverlay() {
        if (calmTask != null) mainHandler.removeCallbacks(calmTask);
        calmOverlay.animate().alpha(0f).setDuration(200)
                .withEndAction(() -> calmOverlay.setVisibility(View.GONE)).start();
    }

    // ================= toast =================
    private Runnable toastHideTask;
    private final Handler toastHandler = new Handler(Looper.getMainLooper());

    private void showToast(String message) {
        if (toastHideTask != null) toastHandler.removeCallbacks(toastHideTask);
        toastView.setText(message);
        toastView.setVisibility(View.VISIBLE);
        toastView.setAlpha(0f); toastView.setTranslationY(8f);
        toastView.animate().alpha(1f).translationY(0f).setDuration(220).start();
        toastHideTask = () -> toastView.animate().alpha(0f).setDuration(200)
                .withEndAction(() -> toastView.setVisibility(View.GONE)).start();
        toastHandler.postDelayed(toastHideTask, 2200);
    }

    // ================= companion rename + tone popups =================
    private void setupCompanionPopups() {
        txtCompanionName.setOnClickListener(v -> showRenamePopup());
        btnOrbHeader.setOnClickListener(v -> showTonePopup());
    }

    private void showRenamePopup() {
        AdultChatHapticUtil.vibrate(this);
        View content = LayoutInflater.from(this).inflate(R.layout.adult_chat_popup_rename, null);
        EditText editRename = content.findViewById(R.id.editRename);
        TextView btnConfirm = content.findViewById(R.id.btnRenameConfirm);
        editRename.setText(companionName);
        editRename.setSelection(editRename.getText().length());

        PopupWindow popup = new PopupWindow(content, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setOutsideTouchable(true);

        Runnable confirm = () -> {
            String name = editRename.getText().toString().trim();
            if (!name.isEmpty()) {
                companionName = name;
                txtCompanionName.setText(name);
                adapter.setCompanionName(name);
            }
            popup.dismiss();
        };
        btnConfirm.setOnClickListener(v -> confirm.run());
        popup.showAsDropDown(topbarTitle, 0, 8);
    }

    private void showTonePopup() {
        AdultChatHapticUtil.vibrate(this);
        View content = LayoutInflater.from(this).inflate(R.layout.adult_chat_popup_tone, null);
        TextView optSupportive = content.findViewById(R.id.optSupportive);
        TextView optDirect = content.findViewById(R.id.optDirect);
        TextView optOptimistic = content.findViewById(R.id.optOptimistic);

        PopupWindow popup = new PopupWindow(content, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setOutsideTouchable(true);

        View.OnClickListener pick = v -> {
            AdultChatHapticUtil.vibrate(this);
            optSupportive.setSelected(v == optSupportive);
            optDirect.setSelected(v == optDirect);
            optOptimistic.setSelected(v == optOptimistic);
            tone = v == optDirect ? "direct" : v == optOptimistic ? "optimistic" : "supportive";
            adapter.setTone(tone);
            adapter.notifyDataSetChanged();
            popup.dismiss();
        };
        optSupportive.setOnClickListener(pick);
        optDirect.setOnClickListener(pick);
        optOptimistic.setOnClickListener(pick);
        optSupportive.setSelected("supportive".equals(tone));
        optDirect.setSelected("direct".equals(tone));
        optOptimistic.setSelected("optimistic".equals(tone));

        popup.showAsDropDown(topbarTitle, 0, 8);
    }
}
