package com.example.graduationproject.Kids;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityMessagesBinding;

import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    public static final String EXTRA_CHILD_ID = "CHILD_ID";

    private ActivityMessagesBinding binding;
    private ChildProfileStore childProfileStore;
    private long currentChildId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        childProfileStore = new ChildProfileStore(this);
        currentChildId = getIntent().getLongExtra(EXTRA_CHILD_ID, -1);

        binding.btnBack.setOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );
        loadMessages();
    }

    private void loadMessages() {
        if (currentChildId == -1) {
            Toast.makeText(this, "لم يتم تحديد الطفل", Toast.LENGTH_SHORT).show();
            return;
        }

        List<BotMessage> messages = childProfileStore.getBotMessages(currentChildId);

        if (messages.isEmpty()) {
            binding.recyclerMessages.setVisibility(View.GONE);
            binding.textEmptyState.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerMessages.setVisibility(View.VISIBLE);
            binding.textEmptyState.setVisibility(View.GONE);

            BotMessageAdapter adapter = new BotMessageAdapter(messages);
            binding.recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerMessages.setAdapter(adapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}