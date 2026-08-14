package com.example.graduationproject.ui.profile.settings;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This file is currently disabled because it conflicts with com.example.graduationproject.Kids.ChildProfilesActivity
 * and has several resource ID mismatches with activity_child_profiles.xml.
 */
public class ChildProfilesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish(); // Just in case it's ever started

    }
}
