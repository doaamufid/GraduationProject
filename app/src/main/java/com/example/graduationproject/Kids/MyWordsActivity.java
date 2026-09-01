package com.example.graduationproject.Kids;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.RecordingsAdapter;
import com.example.graduationproject.data.RecordingStorage;
import com.example.graduationproject.models.Recording;


import java.util.List;

/**
 * شاشة "كلماتي الحلوة" - تعرض كل التسجيلات اللي حفظها الطفل، مع إمكانية سماعها.
 */
public class MyWordsActivity extends AppCompatActivity {

    private RecordingsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_words);

        findViewById(R.id.closeButton).setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recordingsRecyclerView);
        View emptyText = findViewById(R.id.emptyText);

        List<Recording> recordings = new RecordingStorage(this).getAllRecordings();
        emptyText.setVisibility(recordings.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(recordings.isEmpty() ? View.GONE : View.VISIBLE);

        adapter = new RecordingsAdapter(recordings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnRecordNew).setOnClickListener(v -> {
            Intent intent = new Intent(this, WordOfWeekActivity.class);
            // Pass the child ID if needed
            long childId = getSharedPreferences("KidsApp", MODE_PRIVATE).getLong("current_child_id", -1L);
            intent.putExtra("CHILD_ID", childId);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter.releasePlayer();
        }
        super.onDestroy();
    }
}