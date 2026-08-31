package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/** Port of the default export `Hub()` gallery screen. */
public class HubActivity extends AppCompatActivity {

    private static class Item {
        String emoji, title, desc;
        Class<?> target;
        Item(String emoji, String title, String desc, Class<?> target) {
            this.emoji = emoji; this.title = title; this.desc = desc; this.target = target;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        Item[] items = new Item[]{
                new Item("🦔", getString(R.string.item_daily_title), getString(R.string.item_daily_desc), KidsHabitProgDailyCardActivity.class),
                new Item("⛰️", getString(R.string.item_adult_title), getString(R.string.item_adult_desc), AdultHabitProgAdultCardActivity.class),
                new Item("🌿", getString(R.string.item_breathe_title), getString(R.string.item_breathe_desc), BreathingFeatBreatheRoutineActivity.class),
                new Item("🧘", "شاشة جلسة تنفس", "الشاشة الأصلية وحدها، بدون الروتين", BreathingFeatBreatheSessionActivity.class),
        };

        LinearLayout list = findViewById(R.id.hubList);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Item item : items) {
            View row = inflater.inflate(R.layout.item_hub_card, list, false);
            ((TextView) row.findViewById(R.id.itemEmoji)).setText(item.emoji);
            ((TextView) row.findViewById(R.id.itemTitle)).setText(item.title);
            ((TextView) row.findViewById(R.id.itemDesc)).setText(item.desc);
            row.setOnClickListener(v -> startActivity(new Intent(HubActivity.this, item.target)));
            list.addView(row);
        }
    }
}
