package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.MainActivity;
import com.example.graduationproject.adapters.ChildProfilesAdapter;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityChildProfilesBinding;
import com.example.graduationproject.models.ChildProfile;

import java.util.ArrayList;
import java.util.List;

public class ChildProfilesActivity extends AppCompatActivity {
    private ActivityChildProfilesBinding binding;
    private ChildProfilesAdapter adapter;
    private ChildProfileStore childProfileStore;
    private final List<ChildProfile> profiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChildProfilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        childProfileStore = new ChildProfileStore(this);
        childProfileStore.migrateFromSharedPreferencesIfNeeded(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnBack.setOnClickListener(v -> finish());
        setupProfilesList();
    }

    private void setupProfilesList() {
        adapter = new ChildProfilesAdapter(profiles, new ChildProfilesAdapter.OnChildProfileClickListener() {
            @Override
            public void onProfileClick(ChildProfile profile) {
                Intent intent = new Intent(ChildProfilesActivity.this, KidsAiChatActivity.class);
                intent.putExtra("child_id", profile.getId());
                intent.putExtra("child_name", profile.getName());
                startActivity(intent);
            }

            @Override
            public void onAddProfileClick() {
                openNewProfileScreen();
            }
        });
        binding.rvChildProfiles.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChildProfiles.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfiles();
    }

    @Override
    protected void onDestroy() {
        childProfileStore.close();
        super.onDestroy();
    }

    private void openNewProfileScreen() {
        startActivity(new Intent(this,NewChildProfileActivity.class));
    }

    private void loadProfiles() {
        profiles.clear();
        profiles.addAll(childProfileStore.getProfiles());
        adapter.notifyDataSetChanged();
    }

}
