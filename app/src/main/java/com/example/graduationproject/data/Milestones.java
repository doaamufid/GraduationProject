package com.example.graduationproject.data;

public class Milestones {

    public static class Milestone {
        public final int min;
        public final String label;
        public final String icon;

        Milestone(int min, String label, String icon) {
            this.min = min;
            this.label = label;
            this.icon = icon;
        }
    }

    private static final Milestone[] ALL = new Milestone[]{
            new Milestone(20, "٢٠ قلب", "🌱"),
            new Milestone(50, "٥٠ قلب", "🌟"),
            new Milestone(100, "١٠٠ قلب", "🏅"),
            new Milestone(200, "٢٠٠ قلب", "💎"),
    };

    /** Returns the highest reached milestone, or null if none reached yet. */
    public static Milestone getMilestone(int hearts) {
        Milestone result = null;
        for (Milestone m : ALL) {
            if (hearts >= m.min) result = m;
        }
        return result;
    }
}
