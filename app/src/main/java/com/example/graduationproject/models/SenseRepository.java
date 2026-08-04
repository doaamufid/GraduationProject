package com.example.graduationproject.models;

import java.util.Arrays;
import java.util.List;

/** Static data source - equivalent of the SENSES array constant. */
public final class SenseRepository {

    private SenseRepository() {
    }

    public static List<Sense> getSenses() {
        return Arrays.asList(
                new Sense("see", 5, "SEE · تشاهد", "٥ أشياء تراها",
                        "انظري حواليك والمسي بعينك ٥ أشياء — بس اضغطي، ما تحتاجين تكتبين", "\uD83D\uDC40"),
                new Sense("touch", 4, "TOUCH · تلمس", "٤ أشياء تلمسها",
                        "لاحظي ٤ أشياء تقدرين تلمسينها الآن", "\u270B"),
                new Sense("hear", 3, "HEAR · تسمع", "٣ أصوات تسمعها",
                        "أنصتي لـ٣ أصوات حواليك", "\uD83D\uDC42"),
                new Sense("smell", 2, "SMELL · تشم", "رائحتان",
                        "لاحظي رائحتين قريبتين منك", "\uD83D\uDC43"),
                new Sense("taste", 1, "TASTE · تتذوق", "طعم واحد",
                        "استحضري طعماً واحداً بفمك الآن", "\uD83D\uDC45")
        );
    }
}
