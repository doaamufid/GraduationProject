package com.example.graduationproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.adapters.ChatAdapter;
import com.example.graduationproject.data.ConversationScript;
import com.example.graduationproject.models.ChatMessage;
import com.example.graduationproject.models.ScriptNode;
import com.example.graduationproject.models.ScriptReply;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Java/XML port of CompanionChatFlow (JSX), extended with working voice
 * messages: the mic records the user's voice, sends it into the chat as a
 * voice bubble, and bubble playback uses MediaPlayer.
 *
 * State mirrored from the original useState hooks:
 *   messages     -> `messages` (List<ChatMessage>)
 *   currentNode  -> `currentNode` (String, key into ConversationScript.NODES)
 *   typing       -> `typing` (boolean, drives the typing-indicator row)
 *   input        -> the EditText's own text (read directly, no separate field needed)
 *   toast        -> handled by showToast()/hideToast()
 */
public class ChatMainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO = 200;
    private static final Random RANDOM = new Random();
    private static long uid = 0;
    private static long nextId() { return ++uid; }
    private String now() { return getString(R.string.chat_now); }

    private final List<ChatMessage> messages = new ArrayList<>();
    private String currentNode = ConversationScript.NODE_START;
    private boolean typing = false;

    private RecyclerView recyclerMessages;
    private ChatAdapter adapter;
    private HorizontalScrollView quickRepliesScroll;
    private LinearLayout quickRepliesContainer;
    private EditText editInput;
    private TextView txtToast;

    // --- Voice recording state ---
    private BottomSheetDialog voiceSheet;
    private TextView tvRecordTimer;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private int recordedSeconds = 0;
    private boolean recording = false;

    // --- Voice playback state ---
    private MediaPlayer mediaPlayer;
    private long playingMessageId = -1;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingToastHide;
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            recordedSeconds++;
            if (tvRecordTimer != null) {
                tvRecordTimer.setText(formatSeconds(recordedSeconds));
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppLanguageManager.applySavedLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_main);

        bindViews();
        setupRecycler();
        setupInputBar();

        // Mirrors: useState([{ id: nextId(), from:"bot", text: SCRIPT.start.bot, time: now() }])
        ScriptNode startNode = ConversationScript.NODES.get(ConversationScript.NODE_START);
        messages.add(ChatMessage.bot(nextId(), getString(startNode.botResId), now(), startNode.cardType));
        adapter.notifyItemInserted(0);
        renderQuickReplies();
    }

    private void bindViews() {
        recyclerMessages = findViewById(R.id.recycler_messages);
        quickRepliesScroll = findViewById(R.id.quick_replies_scroll);
        quickRepliesContainer = findViewById(R.id.quick_replies_container);
        editInput = findViewById(R.id.edit_input);
        txtToast = findViewById(R.id.txt_toast);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());

        ImageButton btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(v -> handleSend());

        ImageButton btnMic = findViewById(R.id.btn_mic);
        btnMic.setOnClickListener(v -> onMicClicked());
    }

    private void setupRecycler() {
        recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(this, messages, this::handleCardAction);
        adapter.setVoiceActionListener(this::onVoiceClicked);
        recyclerMessages.setAdapter(adapter);
    }

    private void setupInputBar() {
        editInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                handleSend();
                return true;
            }
            return false;
        });
    }

    // ---------------------------------------------------------------
    // Voice recording (mic button -> permission -> bottom sheet -> record)
    // ---------------------------------------------------------------
    private void onMicClicked() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
            return;
        }
        showVoiceRecordingSheet();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showVoiceRecordingSheet();
            } else {
                Toast.makeText(this, R.string.chat_voice_perm_denied, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showVoiceRecordingSheet() {
        voiceSheet = new BottomSheetDialog(this);
        View sheet = LayoutInflater.from(this).inflate(R.layout.layout_chat_voice_sheet, null);
        voiceSheet.setContentView(sheet);

        tvRecordTimer = sheet.findViewById(R.id.txt_record_timer);
        TextView btnCancel = sheet.findViewById(R.id.btn_voice_cancel);
        TextView btnSend = sheet.findViewById(R.id.btn_voice_send);

        tvRecordTimer.setText(formatSeconds(0));
        startRecording();

        btnCancel.setOnClickListener(v -> {
            stopRecording(false);
            voiceSheet.dismiss();
        });

        btnSend.setOnClickListener(v -> {
            stopRecording(true);
            voiceSheet.dismiss();
        });

        voiceSheet.setOnDismissListener(dialog -> stopRecording(false));
        voiceSheet.show();
    }

    private void startRecording() {
        File cacheDir = getExternalCacheDir();
        if (cacheDir == null) cacheDir = getCacheDir();
        audioFilePath = cacheDir.getAbsolutePath() + "/chat_voice_" + System.currentTimeMillis() + ".m4a";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioEncodingBitRate(128000);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            recording = true;
            recordedSeconds = 0;
            timerHandler.postDelayed(timerRunnable, 1000);
        } catch (IOException | RuntimeException e) {
            recording = false;
            Toast.makeText(this, R.string.chat_voice_perm_denied, Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording(boolean send) {
        timerHandler.removeCallbacks(timerRunnable);
        if (!recording || mediaRecorder == null) return;

        recording = false;
        try {
            mediaRecorder.stop();
        } catch (RuntimeException ignored) {
        } finally {
            mediaRecorder.release();
            mediaRecorder = null;
        }

        if (send && audioFilePath != null) {
            File f = new File(audioFilePath);
            if (f.exists() && f.length() > 0) {
                sendVoiceMessage(audioFilePath, recordedSeconds);
            }
        }
    }

    /** Adds the recorded voice into the chat and triggers the bot's reply. */
    private void sendVoiceMessage(String path, int durationSec) {
        messages.add(ChatMessage.voice(nextId(), path, durationSec, now()));
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
        pushBot(ConversationScript.NODE_FREE_REPLY); // exactly like a text send
        showToast(getString(R.string.chat_voice_sent_toast));
    }

    // ---------------------------------------------------------------
    // Voice playback (tap on a voice bubble)
    // ---------------------------------------------------------------
    private void onVoiceClicked(ChatMessage msg) {
        if (msg.audioPath == null) return;
        if (mediaPlayer != null && playingMessageId == msg.id) {
            stopPlayback(true);
            return;
        }
        startPlayback(msg);
    }

    private void startPlayback(ChatMessage msg) {
        stopPlayback(false);
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(msg.audioPath);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                playingMessageId = msg.id;
                adapter.setPlayingMessageId(msg.id);
            });
            mediaPlayer.setOnCompletionListener(mp -> stopPlayback(true));
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                stopPlayback(true);
                return true;
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            stopPlayback(true);
        }
    }

    private void stopPlayback(boolean notifyAdapter) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException ignored) {
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        playingMessageId = -1;
        if (notifyAdapter && adapter != null) {
            adapter.setPlayingMessageId(-1);
        }
    }

    // ---------------------------------------------------------------
    // Card actions (mirrors onStart/onOpen/onRead -> setToast(...))
    // ---------------------------------------------------------------
    private void handleCardAction(String cardType) {
        switch (cardType) {
            case "breathing": showToast(getString(R.string.chat_breathing_toast)); break;
            case "dhikr": showToast(getString(R.string.chat_dhikr_toast)); break;
            case "article": showToast(getString(R.string.chat_article_toast)); break;
        }
    }

    private void showToast(String message) {
        if (pendingToastHide != null) handler.removeCallbacks(pendingToastHide);

        txtToast.setText(message);
        txtToast.setVisibility(View.VISIBLE);
        txtToast.setAlpha(0f);
        txtToast.setTranslationY(8 * getResources().getDisplayMetrics().density);
        txtToast.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start();

        pendingToastHide = () -> txtToast.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> txtToast.setVisibility(View.GONE))
                .start();
        handler.postDelayed(pendingToastHide, 2200);
    }

    // ---------------------------------------------------------------
    // Reply / send handlers (mirrors handleReply / handleSend)
    // ---------------------------------------------------------------
    private void handleReply(String label, String next) {
        addUserMessage(label);
        pushBot(next);
    }

    private void handleSend() {
        String text = editInput.getText().toString();
        if (text.trim().isEmpty()) return;
        addUserMessage(text);
        editInput.setText("");
        pushBot(ConversationScript.NODE_FREE_REPLY); // always freeReply, exactly like the JSX
    }

    private void addUserMessage(String text) {
        messages.add(ChatMessage.user(nextId(), text, now()));
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }

    /** Mirrors pushBot(node): shows typing for 1100-1600ms, then appends the bot message. */
    private void pushBot(String node) {
        typing = true;
        setTypingIndicatorVisible(true);
        renderQuickReplies(); // hides quick replies immediately while typing (node?.replies check)

        long delay = 1100 + (long) (RANDOM.nextDouble() * 500);
        handler.postDelayed(() -> {
            typing = false;
            setTypingIndicatorVisible(false);

            ScriptNode def = ConversationScript.NODES.get(node);
            messages.add(ChatMessage.bot(nextId(), getString(def.botResId), now(), def.cardType));
            adapter.notifyItemInserted(messages.size() - 1);
            scrollToBottom();

            currentNode = node;
            renderQuickReplies();
        }, delay);
    }

    // ---------------------------------------------------------------
    // Typing indicator (transient item appended to the message list)
    // ---------------------------------------------------------------
    private ChatMessage typingItem;

    private void setTypingIndicatorVisible(boolean visible) {
        if (visible) {
            typingItem = ChatMessage.typingIndicator(nextId());
            messages.add(typingItem);
            adapter.notifyItemInserted(messages.size() - 1);
        } else if (typingItem != null) {
            int index = messages.indexOf(typingItem);
            if (index >= 0) {
                messages.remove(index);
                adapter.notifyItemRemoved(index);
            }
            typingItem = null;
        }
        scrollToBottom();
    }

    private void scrollToBottom() {
        recyclerMessages.post(() -> {
            if (adapter.getItemCount() > 0) {
                recyclerMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    // ---------------------------------------------------------------
    // Quick replies (mirrors: {!typing && node?.replies?.length > 0 && (...)})
    // ---------------------------------------------------------------
    private void renderQuickReplies() {
        quickRepliesContainer.removeAllViews();

        ScriptNode node = ConversationScript.NODES.get(currentNode);
        boolean show = !typing && node != null && !node.replies.isEmpty();

        if (!show) {
            quickRepliesScroll.setVisibility(View.GONE);
            return;
        }

        quickRepliesScroll.setVisibility(View.VISIBLE);
        // Mirrors .quick-in { animation: qin .25s ease } (simple fade-in)
        quickRepliesScroll.setAlpha(0f);
        quickRepliesScroll.animate().alpha(1f).setDuration(250).start();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (ScriptReply reply : node.replies) {
            TextView chip = (TextView) inflater.inflate(R.layout.chat_item_quick_reply_chip, quickRepliesContainer, false);
            String label = getString(reply.labelResId);
            chip.setText(label);
            chip.setOnClickListener(v -> handleReply(label, reply.next));
            quickRepliesContainer.addView(chip);
        }
    }

    /** Formats seconds as mm:ss. */
    private String formatSeconds(int totalSec) {
        int min = totalSec / 60;
        int sec = totalSec % 60;
        return String.format(Locale.US, "%02d:%02d", min, sec);
    }

    @Override
    protected void onDestroy() {
        timerHandler.removeCallbacks(timerRunnable);
        handler.removeCallbacksAndMessages(null);
        if (recording && mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (RuntimeException ignored) {
            }
            mediaRecorder.release();
            mediaRecorder = null;
        }
        stopPlayback(false);
        super.onDestroy();
    }
}
