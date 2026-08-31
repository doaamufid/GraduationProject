package com.example.graduationproject.models;

import java.util.HashMap;
import java.util.Map;

/** 1:1 port of the JS SCRIPT conversation graph. */
public class AdultChatScript {

    public static final Map<String, AdultChatScriptNode> NODES = new HashMap<>();

    static {
        NODES.put("start", new AdultChatScriptNode("أهلاً بك 🌿 كيف تشعر اليوم؟")
                .reply("أشعر بعض القلق اليوم", "anxious")
                .reply("أشعر بضيق شديد ولا أستطيع التنفس جيداً", "distress")
                .reply("أنا بخير، فقط أريد الحديث", "fine")
                .reply("أحتاج فقط لمن يستمع لي", "listen"));

        NODES.put("listen", new AdultChatScriptNode(
                "أنا هنا لأستمع إليك دون حكم أو مقاطعة 💜 خذ وقتك، وشاركني بما يجول في خاطرك.")
                .freeText(true));

        NODES.put("anxious", new AdultChatScriptNode(
                "أتفهّم شعورك، القلق أمر طبيعي أحياناً. هل تحب أن نجرّب تمرين تنفّس بسيط الآن؟")
                .reply("نعم، لنجرّب", "breathingOffer")
                .reply("لاحقاً ربما", "later")
                .reply("أريد أن أشرح أكثر", "elaborate"));

        NODES.put("breathingOffer", new AdultChatScriptNode(
                "رائع 🌬️ خذ نفساً عميقاً من الأنف لمدة 4 ثوانٍ، احبسه لـ4 ثوانٍ، ثم أخرجه ببطء من الفم لـ4 ثوانٍ. كرّر ذلك 3 مرات.")
                .reply("شعرت بتحسّن، شكراً", "closing")
                .reply("أريد معرفة المزيد", "article")
                .card(AdultChatCardData.exercise("تنفّس الصندوق", "BREATHING EXERCISE", "٤ دقائق",
                        "تمرين ٤ دقائق لإعادة ضبط جهازك العصبي بإيقاع بسيط.")));

        NODES.put("article", new AdultChatScriptNode(
                "التنفّس العميق يساعد الجسم على تفعيل الجهاز العصبي الباراسمبثاوي، ما يقلّل هرمونات التوتّر ويهدّئ نبض القلب.")
                .reply("شكراً لك", "closing")
                .card(AdultChatCardData.article("CBT", "كيف تتعاملين مع نوبات القلق؟", "٣ دقائق قراءة · لأنك تشعرين بهذا اليوم",
                        "القلق استجابة طبيعية للجسم عند الشعور بالتهديد. في هذا المقال نستعرض ٣ خطوات عملية للتعامل معه لحظة بلحظة، بدءاً من ملاحظة الإحساس الجسدي وصولاً إلى إعادة توجيه الانتباه بهدوء.")));

        NODES.put("later", new AdultChatScriptNode("لا بأس، أنا هنا عندما تكون مستعداً 🌸")
                .reply("شكراً", "closing"));

        NODES.put("elaborate", new AdultChatScriptNode("تفضّل، أنا أستمع إليك باهتمام.")
                .freeText(true)
                .card(AdultChatCardData.video("٣ دقائق لتهدئة عقلك", "فيديو مهدّئ", "٣:١٠")));

        NODES.put("distress", new AdultChatScriptNode(
                "أنا آسف لسماع أنك تشعر بهذا الضيق. إذا كان الأمر صعباً الآن، يمكننا الدخول مباشرة في تهدئة فورية.")
                .reply("نعم، أحتاج مساعدة", "breathingOffer")
                .reply("ربما، لست متأكداً", "dhikrOffer")
                .reply("لا، أنا بخير الآن", "later")
                .card(AdultChatCardData.sos()));

        NODES.put("dhikrOffer", new AdultChatScriptNode(
                "جرّب أن تردّد بهدوء: «حسبي الله ونعم الوكيل»، وركّز على معناها العميق. خذ وقتك.")
                .reply("شكراً، شعرت بسكينة", "closing")
                .card(AdultChatCardData.dhikr("ذكر مهدّئ", "«حسبي الله ونعم الوكيل» — كرّرها ببطء مع كل زفير.")));

        NODES.put("fine", new AdultChatScriptNode("يسعدني ذلك 😊 أخبرني بما يدور في ذهنك.")
                .freeText(true));

        NODES.put("closing", new AdultChatScriptNode("أنا هنا دائماً إن احتجت للحديث. اعتنِ بنفسك 🌿")
                .card(AdultChatCardData.moment("تنفّس · تأريض · ذكر")));

        NODES.put("freeReply", new AdultChatScriptNode(
                "شكراً لمشاركتي هذا. هل ترغب أن نجرّب تمرين تنفّس يساعدك على الاسترخاء؟")
                .reply("نعم من فضلك", "breathingOffer")
                .reply("لا شكراً، أنا بخير", "closing"));
    }

    public static AdultChatScriptNode get(String node) {
        return NODES.get(node);
    }
}
