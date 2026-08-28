package com.example.graduationproject.data;

import com.example.graduationproject.models.Message;


import java.util.ArrayList;
import java.util.List;

public class SeedData {

    // Same category list as CATEGORIES (index 0 = "الكل" filter, used only in wall chips)
    public static final String[] CATEGORIES = new String[]{
            "الأكثر إلهاما", "الكل", "قوة", "أمل", "صبر", "شكر", "سلام", "ثقة", "شجاعة",
            "تعافي", "احتواء", "تفاؤل", "راحة", "امتنان", "دعم", "تقبل", "نمو"
    };

    // gentle, non-suggestive emoji only
    public static final String[] MOOD_EMOJIS = new String[]{
            "🌸", "🌹", "🌻", "🌼", "🌷", "🪷", "🌞", "🌙", "⭐", "✨",
            "🌈", "🦋", "🕊️", "🍃", "🌿", "☁️", "😊", "🙂", "😌", "🥰", "🤍", "💫", "🎈", "🧸"
    };

    public static final String[] AVATARS = new String[]{"🌤️", "🌿", "🌊", "⭐", "🕊️", "🌸", "☀️", "🍃"};

    public static String getCategoryEmoji(String cat) {
        switch (cat) {
            case "الكل": return "⭐";
            case "الأكثر إلهاما": return "✨";
            case "قوة": return "💪";
            case "أمل": return "🌈";
            case "صبر": return "⏳";
            case "شكر": return "🙏";
            case "سلام": return "🕊️";
            case "ثقة": return "🤝";
            case "شجاعة": return "🦁";
            case "تعافي": return "🌱";
            case "احتواء": return "🤗";
            case "تفاؤل": return "☀️";
            case "راحة": return "☁️";
            case "امتنان": return "💖";
            case "دعم": return "🫂";
            case "تقبل": return "🍃";
            case "نمو": return "🌳";
            default: return "✨";
        }
    }

    public static List<Message> seed() {
        List<Message> list = new ArrayList<>();
        list.add(new Message(1, "لو حسيت اليوم إنه ثقيل، تذكر إن كل يوم صعب مررت فيه قبل، خلّصته. أنت أقوى مما تتخيل.", "قوة", 142, null, "🌞", false));
        list.add(new Message(2, "الأمل مو إنك تحس إن كل شي بخير، هو إنك تكمل حتى لو مو كل شي بخير.", "أمل", 268, "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=500&q=60", "🌈", true));
        list.add(new Message(3, "خذ وقتك. ما يلزم تتعافى بسرعة أحد ثاني، تعافيك بوقتك أنت.", "صبر", 210, null, "🍃", false));
        list.add(new Message(4, "اليوم امتنيت لحاجة بسيطة: كوب شاي دافي وأنا قاعد لحالي بهدوء. الأشياء الصغيرة تكفي أحياناً.", "شكر", 76, null, null, false));
        list.add(new Message(5, "لو حد قالك إنك كثير حساس، تذكر إن حساسيتك هذي هي اللي تخليك إنسان طيب.", "قوة", 189, "https://images.unsplash.com/photo-1500673922987-e212871fec22?w=500&q=60", "🌸", false));
        list.add(new Message(6, "مرّيت بشي شبه اللي تمر فيه الحين، وطلعت منه. راح تطلع أنت كمان.", "أمل", 234, null, "✨", false));
        list.add(new Message(7, "سلام قلبك أهم من أي شي ثاني اليوم. خذ نفس عميق وكمل بلطف مع نفسك.", "سلام", 197, "https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?w=500&q=60", "🕊️", false));
        list.add(new Message(8, "ثقي إنك تستاهلين كل خير جاي، حتى لو الطريق طويل شوي.", "ثقة", 158, null, "🌷", false));
        list.add(new Message(9, "أحياناً العطاء الحقيقي هو إنك تعطي نفسك فرصة لراحة حقيقية بدون تأنيب ضمير.", "راحة", 312, "https://images.unsplash.com/photo-1499728603263-13726abce5fd?w=500&q=60", "☁️", false));
        list.add(new Message(10, "كلمة منك ممكن تغير يوم كامل لشخص غريب. لا تستهين بجمال روحك.", "دعم", 45, null, "🫂", false));
        list.add(new Message(11, "الحياة مو سباق، الحياة رحلة. استمتع بكل خطوة حتى لو كانت بطيئة.", "نمو", 215, "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=500&q=60", "🌳", false));
        list.add(new Message(12, "الامتنان هو المغناطيس اللي يسحب كل الأشياء الجميلة لحياتك.", "امتنان", 188, null, "💖", false));
        list.add(new Message(13, "كونك حقيقي هو أجمل شيء ممكن تقدمه لنفسك وللعالم.", "تقبل", 92, "https://images.unsplash.com/photo-1528605248644-14dd04022da1?w=500&q=60", "🍃", false));
        list.add(new Message(14, "كل بداية جديدة محتاجة شجاعة. وأنت شجاع لأنك لسا بتحاول.", "شجاعة", 276, null, "🦁", false));
        list.add(new Message(15, "خلي قلبك مثل الورد، يعطر حتى اللي كسره.", "سلام", 143, "https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=500&q=60", "🌹", false));
        list.add(new Message(16, "ابتسم، لأن الابتسامة هي لغة لا تحتاج لترجمة.", "تفاؤل", 67, null, "😊", false));
        return list;
    }
}
