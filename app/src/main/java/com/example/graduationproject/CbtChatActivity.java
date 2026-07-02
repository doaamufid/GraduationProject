package com.example.graduationproject;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.adapters.CbtChatAdapter;
import com.example.graduationproject.databinding.ActivityCbtChatBinding;
import com.example.graduationproject.models.ChatItem;

import java.util.ArrayList;
import java.util.List;

public class CbtChatActivity extends AppCompatActivity {

    private ActivityCbtChatBinding binding;
    private CbtChatAdapter adapter;
    private List<ChatItem> chatDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityCbtChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.rvCbtChatMessages.setLayoutManager(new LinearLayoutManager(this));

        chatDataList = new ArrayList<>();

        chatDataList.add(new ChatItem(ChatItem.TYPE_AI_MESSAGE,
                "\"بعد كل ليلة ثقيلة فجرٌ ينتظر. أنتِ لستِ وحدكِ - سلام هنا معكِ.\"",
                "9:40 ص ✓✓"));

        chatDataList.add(new ChatItem(ChatItem.TYPE_AI_SUGGESTION,
                "كيف تتعاملين مع الحزن بدون قمعه؟",
                "لأنكِ تشعرين بالحزن هذا الأسبوع",
                "9:41 ص"));

        chatDataList.add(new ChatItem(ChatItem.TYPE_AI_SUGGESTION,
                "تنفس • تأريض • ذكر",
                "لأنكِ تشعرين بالحزن هذا الأسبوع",
                "9:42 ص"));

        adapter = new CbtChatAdapter(chatDataList);
        binding.rvCbtChatMessages.setAdapter(adapter);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.ivSendMessage.setOnClickListener(v -> {
            String text = binding.etMessageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                chatDataList.add(new ChatItem(ChatItem.TYPE_AI_MESSAGE, text, "الآن ✓"));
                adapter.notifyItemInserted(chatDataList.size() - 1);
                binding.rvCbtChatMessages.scrollToPosition(chatDataList.size() - 1);
                binding.etMessageInput.setText("");
            }
        });
    }
}