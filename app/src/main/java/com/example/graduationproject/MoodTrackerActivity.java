package com.example.graduationproject;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


import com.example.graduationproject.databinding.ActivityMoodTrackerBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;

public class MoodTrackerActivity extends AppCompatActivity {
ActivityMoodTrackerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding=   ActivityMoodTrackerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (binding.moodBarChart != null) {
            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f, 65f));
            entries.add(new BarEntry(1f, 85f));
            entries.add(new BarEntry(2f, 40f));
            entries.add(new BarEntry(3f, 95f));
            entries.add(new BarEntry(4f, 75f));
            entries.add(new BarEntry(5f, 88f));
            entries.add(new BarEntry(6f, 52f));

            ArrayList<Integer> colors = new ArrayList<>();
            for (BarEntry entry : entries) {
                if (entry.getY() < 50f) {
                    colors.add(Color.parseColor("#FCBB56"));
                } else {
                    colors.add(Color.parseColor("#5D9CEC"));
                }
            }

            BarDataSet dataSet = new BarDataSet(entries, "Mood Data");
            dataSet.setColors(colors);
            dataSet.setDrawValues(false);

            String[] days = {"س", "ح", "ن", "ث", "ر", "خ", "ج"};
            XAxis xAxis = binding.moodBarChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setTextColor(Color.parseColor("#7F8C8D"));
            xAxis.setGranularity(1f);

            YAxis leftAxis = binding.moodBarChart.getAxisLeft();
            leftAxis.setDrawGridLines(true);
            leftAxis.setGridColor(Color.parseColor("#E0E0E0"));
            leftAxis.setDrawAxisLine(false);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setAxisMaximum(100f);
            leftAxis.setTextColor(Color.parseColor("#7F8C8D"));

            binding.moodBarChart.getAxisRight().setEnabled(false);

            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.5f);

            binding.moodBarChart.setData(barData);
            binding.moodBarChart.getDescription().setEnabled(false);
            binding.moodBarChart.getLegend().setEnabled(false);
            binding.moodBarChart.setFitBars(true);
            binding.moodBarChart.animateY(1200);
            binding.moodBarChart.invalidate();
        }
    }
}