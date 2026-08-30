package com.example.graduationproject.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * المصدر الوحيد والموثوق لمعرفة "مين الطفل النشط حالياً" بالتطبيق.
 *
 * قبل هيك، كل شاشة كانت عندها نسخة يدوية مختلفة (وأحياناً بأسماء SharedPreferences
 * مختلفة زي "KidsApp" و "KidsAppPrefs")، وهاد السبب الرئيسي وراء ظهور بيانات
 * كل الأطفال مع بعض بدل بيانات الطفل الحالي بس.
 *
 * الاستخدام:
 * - عند اختيار/تبديل بروفايل الطفل (بشاشة اختيار البروفايل):
 *      ActiveChildManager.setActiveChildId(context, chosenChildId);
 * - بأي مكان محتاجة فيه تعرفي مين الطفل الحالي:
 *      long childId = ActiveChildManager.getActiveChildId(context);
 */
public class ActiveChildManager {

    private static final String PREFS_NAME = "KidsAppPrefs";
    private static final String KEY_ACTIVE_CHILD_ID = "active_child_id";

    public static final long NO_ACTIVE_CHILD = -1L;

    private ActiveChildManager() {
        // كلاس أدوات ثابتة، ما بدنا نسمح بإنشاء نسخة منه
    }

    /** تُنادى لما يتم اختيار/تبديل بروفايل الطفل النشط */
    public static void setActiveChildId(Context context, long childId) {
        getPrefs(context).edit()
                .putLong(KEY_ACTIVE_CHILD_ID, childId)
                .apply();
    }

    /** ترجع id الطفل النشط حالياً، أو NO_ACTIVE_CHILD (-1) لو ما في طفل محدد */
    public static long getActiveChildId(Context context) {
        return getPrefs(context).getLong(KEY_ACTIVE_CHILD_ID, NO_ACTIVE_CHILD);
    }

    public static void clearActiveChild(Context context) {
        getPrefs(context).edit()
                .remove(KEY_ACTIVE_CHILD_ID)
                .apply();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}