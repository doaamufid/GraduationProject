package com.example.graduationproject;

import com.example.graduationproject.models.QouteFeatureQuoteEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Static list of quotes — exact content ported from the ENTRIES array
 * in the original React component (SalamWelcome.jsx).
 */
public class QouteFeatureQuoteRepository {

    public static List<QouteFeatureQuoteEntry> all() {
        List<QouteFeatureQuoteEntry> list = new ArrayList<>();

        list.add(new QouteFeatureQuoteEntry("q0",
                "https://images.unsplash.com/photo-1433086966358-54859d0ed716?w=1200&q=80&auto=format",
                "الألم اللي جوّاك مش نهاية القصة، هو بس فصل بتقدر تطويه.",
                "The pain you carry isn't the end of your story — it's just a page you're allowed to turn."));

        list.add(new QouteFeatureQuoteEntry("q1",
                "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=1200&q=80&auto=format",
                "خذ نفس عميق... أنت هون، أنت موجود، وهاد يكفي اليوم.",
                "Take a deep breath. You are here, you exist, and that is enough for today."));

        list.add(new QouteFeatureQuoteEntry("q2",
                "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=1200&q=80&auto=format",
                "مش لازم تكون بخير كل يوم عشان تستاهل الحب والراحة.",
                "You don't have to be okay every day to deserve love and rest."));

        list.add(new QouteFeatureQuoteEntry("q3",
                "https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?w=1200&q=80&auto=format",
                "كل شروق شمس هو دعوة جديدة تسامح فيها حالك وتبلش من جديد.",
                "Every sunrise is an invitation to forgive yourself and begin again."));

        list.add(new QouteFeatureQuoteEntry("q4",
                "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1200&q=80&auto=format",
                "قلبك أقوى مما تتخيل، حتى لو حسّيته تعبان هلق.",
                "Your heart is stronger than you think, even when it feels tired right now."));

        list.add(new QouteFeatureQuoteEntry("q5",
                "https://images.unsplash.com/photo-1465146344425-f00d5f5c8f07?w=1200&q=80&auto=format",
                "مسموح تبكي، مسموح تضعف، بس ممنوع تسيب حالك.",
                "It's okay to cry, it's okay to feel weak — just never let go of yourself."));

        list.add(new QouteFeatureQuoteEntry("q6",
                "https://images.unsplash.com/photo-1500534623283-312aade485b7?w=1200&q=80&auto=format",
                "الطريق للسلام الداخلي بيبلش من لحظة هدوء وحدة، متل هاي.",
                "The road to inner peace begins with one quiet moment, just like this one."));

        list.add(new QouteFeatureQuoteEntry("q7",
                "https://images.unsplash.com/photo-1447752875215-b2761acb3c5d?w=1200&q=80&auto=format",
                "أنت مش لحالك؛ في ناس بتحبك وبتنطر تشوفك بخير.",
                "You are not alone. There are people who love you and long to see you well."));

        list.add(new QouteFeatureQuoteEntry("q8",
                "https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=1200&q=80&auto=format",
                "بكرا رح يجيب أشياء ما بتتخيلها هلق، اعطِ حالك فرصة.",
                "Tomorrow will bring things you can't imagine yet — give yourself a chance."));

        list.add(new QouteFeatureQuoteEntry("q9",
                "https://images.unsplash.com/photo-1470770903676-69b98201ea1c?w=1200&q=80&auto=format",
                "راحة البال مش رفاهية... هي حق طبيعي إلك.",
                "Peace of mind isn't a luxury — it's your natural right."));

        return list;
    }
}
