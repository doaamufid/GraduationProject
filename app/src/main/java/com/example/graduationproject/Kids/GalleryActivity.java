package com.example.graduationproject.Kids;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.GalleryAdapter;
import com.example.graduationproject.data.LocalStorageHelper;
import com.example.graduationproject.models.DrawingResult;

import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView = findViewById(R.id.recyclerGallery);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<DrawingResult> results = LocalStorageHelper.getAllResults(this);
        recyclerView.setAdapter(new GalleryAdapter(results));
    }
}
