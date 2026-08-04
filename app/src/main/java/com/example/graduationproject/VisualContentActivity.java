package com.example.graduationproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.adapters.CategoryAdapter;
import com.example.graduationproject.adapters.VisualContentAdapter;
import com.example.graduationproject.models.Category;
import com.example.graduationproject.models.VisualContent;

import java.util.ArrayList;
import java.util.List;

public class VisualContentActivity extends AppCompatActivity {

    // عناصر الواجهة
    private ImageView btnBack;
    private RecyclerView rvCategories, rvVisualContent;

    // الأدابترز والقوائم
    private CategoryAdapter categoryAdapter;
    private VisualContentAdapter contentAdapter;

    private List<Category> categoryList;
    private List<VisualContent> allContentList; // تحتوي على كل الفيديوهات
    private List<VisualContent> filteredContentList; // القائمة المفلترة التي تُعرض حالياً

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_content);

        // 1. تعريف العناصر
        btnBack = findViewById(R.id.btnBack);
        rvCategories = findViewById(R.id.rvCategories);
        rvVisualContent = findViewById(R.id.rvVisualContent);

        // زر الرجوع
        btnBack.setOnClickListener(v -> finish());

        // 2. تحضير البيانات الافتراضية (Dummy Data)
        prepareDummyData();

        // 3. إعداد الـ RecyclerView الخاص بالتصنيفات الأفقية
        setupCategoriesRecyclerView();

        // 4. إعداد الـ RecyclerView الخاص بالفيديوهات الرأسية
        setupContentRecyclerView();

        // 5. تفعيل الفلترة الافتراضية عند فتح الشاشة (تبويب "موصى لك")
        filterContentByCategory("RECOMMENDED");
    }

    private void prepareDummyData() {
        // أ. تجهيز قائمة التصنيفات (التبويب الأول محدد تلقائياً true)
        categoryList = new ArrayList<>();
        categoryList.add(new Category(1, "موصى لك", true));
        categoryList.add(new Category(2, "تنفس", false));
        categoryList.add(new Category(3, "نوم", false));
        categoryList.add(new Category(4, "تركيز", false));

        // ب. تجهيز بنك الفيديوهات بالكامل (كل فيديو محدد بنوع تصنيف categoryType)
        // ⚠️ استبدلي R.drawable.video بالصور الحقيقية المصغرة للفيديوهات لاحقاً
        allContentList = new ArrayList<>();

        // فيديوهات تبويب "موصى لك" (التي يقترحها الـ AI بناءً على الشات أو المزاج)
        allContentList.add(new VisualContent(1, "تأمل خاص لتخفيف ضغط اليوم المكتشف من محادثتك", "⏱️ 8 دقائق", R.drawable.video, "RECOMMENDED", "url_1"));
        allContentList.add(new VisualContent(2, "تمارين مخصصة لتحسين المزاج الحالي", "⏱️ 5 دقائق", R.drawable.video, "RECOMMENDED", "url_2"));

        // فيديوهات تبويب "تنفس"
        allContentList.add(new VisualContent(3, "تنفس بطني عميق لتهدئة ضربات القلب", "⏱️ 4 دقائق", R.drawable.video, "RESPIRATION", "url_3"));
        allContentList.add(new VisualContent(4, "تقنية التنفس المربع (Box Breathing) للتركيز", "⏱️ 6 دقائق", R.drawable.video, "RESPIRATION", "url_4"));

        // فيديوهات تبويب "نوم"
        allContentList.add(new VisualContent(5, "استرخاء عضلي تدريجي للنوم العميق", "⏱️ 15 دقيقة", R.drawable.video, "SLEEP", "url_5"));
        allContentList.add(new VisualContent(6, "رحلة بصرية موجهة وادي الضباب للنوم", "⏱️ 20 دقيقة", R.drawable.video, "SLEEP", "url_6"));

        // فيديوهات تبويب "تركيز"
        allContentList.add(new VisualContent(7, "موجات ألفا السمعية والبصرية للدراسة", "⏱️ 30 دقيقة", R.drawable.video, "FOCUS", "url_7"));

        // تهيئة القائمة المفلترة لتكون فارغة في البداية
        filteredContentList = new ArrayList<>();
    }

    private void setupCategoriesRecyclerView() {
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            // منطق الفلترة عند الضغط على أي تصنيف
            switch (category.getId()) {
                case 1:
                    filterContentByCategory("RECOMMENDED");
                    break;
                case 2:
                    filterContentByCategory("RESPIRATION");
                    break;
                case 3:
                    filterContentByCategory("SLEEP");
                    break;
                case 4:
                    filterContentByCategory("FOCUS");
                    break;
            }
        });

        // ضبط القائمة لتكون أفقية
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupContentRecyclerView() {
        contentAdapter = new VisualContentAdapter(filteredContentList, content -> {
            // هنا يتم برمجة تشغيل الفيديو عند الضغط على الكارت
            // يمكنك فتح Intent جديد يفتح كلاس مشغل الفيديو VideoPlayerActivity
            Toast.makeText(this, "جاري تشغيل: " + content.getTitle(), Toast.LENGTH_SHORT).show();
        });

        // ضبط قائمة الفيديوهات لتكون رأسية تقليدية
        rvVisualContent.setLayoutManager(new LinearLayoutManager(this));
        rvVisualContent.setAdapter(contentAdapter);
    }

    /**
     * دالة الفلترة: تقوم بفحص بنك الفيديوهات وتصفية العناصر وتحديث الأدابتر فوراً
     */
    private void filterContentByCategory(String categoryType) {
        filteredContentList.clear();

        for (VisualContent item : allContentList) {
            if (item.getCategoryType().equalsIgnoreCase(categoryType)) {
                filteredContentList.add(item);
            }
        }

        // 💡 حل مشكلة البداية الباردة (Cold Start):
        // إذا كان تبويب "موصى لك" فارغاً (المستخدم لم يتحدث مع الشات أو يحدد مزاج)
        // نقوم بملئه بفيديوهات عشوائية أو الأكثر مشاهدة لكي لا تظهر الشاشة بيضاء
        if (categoryType.equals("RECOMMENDED") && filteredContentList.isEmpty()) {
            // نعرض له أول 3 فيديوهات من التطبيق كعرض افتراضي
            if (allContentList.size() >= 3) {
                filteredContentList.add(allContentList.get(2)); // فيديو تنفس
                filteredContentList.add(allContentList.get(4)); // فيديو نوم
            } else {
                filteredContentList.addAll(allContentList);
            }
        }

        // تحديث الأدابتر بالبيانات الجديدة المفلترة
        contentAdapter.updateData(filteredContentList);
    }
}