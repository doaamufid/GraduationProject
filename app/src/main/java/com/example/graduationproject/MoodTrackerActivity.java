package com.example.graduationproject; // ◄◄ السطر الصحيح المفقود لحل مشكلة الـ Package

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

// استيراد مكتبات الرسم البياني
// استبدلي السطور الحمراء بهذه السطور بدقة:
// السطور الصحيحة والنهائية لملف الجافا (بحروف صغيرة philjay):
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;

public class MoodTrackerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_tracker);

        // 1. تعريف مراجع الرسم البياني من الـ XML
        BarChart barChart = findViewById(R.id.moodBarChart);

        if (barChart != null) {
            // 2. تجهيز البيانات (قيم المزاج الافتراضية من 0 إلى 100 لـ 7 أيام)
            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f, 65f)); // س
            entries.add(new BarEntry(1f, 85f)); // ح
            entries.add(new BarEntry(2f, 40f)); // ن (مزاج منخفض -> سيتحول لبرتقالي)
            entries.add(new BarEntry(3f, 95f)); // ث
            entries.add(new BarEntry(4f, 75f)); // ر
            entries.add(new BarEntry(5f, 88f)); // خ
            entries.add(new BarEntry(6f, 52f)); // ج

            // 3. التحكم الديناميكي بالألوان لتطابق الفيجما (برتقالي للمنخفض، أزرق للمستقر)
            ArrayList<Integer> colors = new ArrayList<>();
            for (BarEntry entry : entries) {
                if (entry.getY() < 50f) {
                    colors.add(Color.parseColor("#FCBB56")); // درجة اللون البرتقالي
                } else {
                    colors.add(Color.parseColor("#5D9CEC")); // درجة اللون الأزرق
                }
            }

            BarDataSet dataSet = new BarDataSet(entries, "Mood Data");
            dataSet.setColors(colors);
            dataSet.setDrawValues(false); // إخفاء الأرقام لتصميم راقٍ

            // 4. ضبط المحور الأفقي السفلي (X-Axis) لعرض الحروف العربية للأيام
            String[] days = {"س", "ح", "ن", "ث", "ر", "خ", "ج"};
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // نقل الأيام للأسفل
            xAxis.setDrawGridLines(false); // إخفاء خطوط الشبكة الطولية المزعجة
            xAxis.setDrawAxisLine(false); // إخفاء خط المحور الأساسي
            xAxis.setTextColor(Color.parseColor("#7F8C8D")); // لون نصوص الأيام
            xAxis.setGranularity(1f);

            // 5. ضبط المحور الرأسي الجانبي (Y-Axis)
            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setDrawGridLines(true); // إبقاء الخطوط الأفقية الخلفية خفيفة
            leftAxis.setGridColor(Color.parseColor("#E0E0E0"));
            leftAxis.setDrawAxisLine(false);
            leftAxis.setAxisMinimum(0f); // البداية من 0%
            leftAxis.setAxisMaximum(100f); // النهاية عند 100%
            leftAxis.setTextColor(Color.parseColor("#7F8C8D"));

            // إخفاء المحور الأيمن تماماً لأنه غير مستخدم في التصميم
            barChart.getAxisRight().setEnabled(false);

            // 6. تفعيل البيانات وإضافة الحركات اللمسية (Animation)
            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.5f); // ضبط عرض الأعمدة لتكون متناسقة

            barChart.setData(barData);
            barChart.getDescription().setEnabled(false); // إخفاء جملة الـ Description التلقائية
            barChart.getLegend().setEnabled(false); // إخفاء مربع الليجند الصغير بالأسفل
            barChart.setFitBars(true);
            barChart.animateY(1200); // تفعيل أنيميشن صعود الأعمدة الاحترافي
            barChart.invalidate(); // تحديث الرسم البياني
        }
    }
}