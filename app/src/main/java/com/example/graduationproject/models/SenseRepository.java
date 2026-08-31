package com.example.graduationproject.models;

import com.example.graduationproject.R;
import java.util.Arrays;
import java.util.List;

/** Static data source - equivalent of the SENSES array constant. */
public final class SenseRepository {

    private SenseRepository() {
    }

    public static List<Sense> getSenses() {
        return Arrays.asList(
                new Sense("see", 5, R.string.grounding_ex_see_tag, R.string.grounding_ex_see_title,
                        R.string.grounding_ex_see_question, "\uD83D\uDC40"),
                new Sense("touch", 4, R.string.grounding_ex_touch_tag, R.string.grounding_ex_touch_title,
                        R.string.grounding_ex_touch_question, "\u270B"),
                new Sense("hear", 3, R.string.grounding_ex_hear_tag, R.string.grounding_ex_hear_title,
                        R.string.grounding_ex_hear_question, "\uD83D\uDC42"),
                new Sense("smell", 2, R.string.grounding_ex_smell_tag, R.string.grounding_ex_smell_title,
                        R.string.grounding_ex_smell_question, "\uD83D\uDC43"),
                new Sense("taste", 1, R.string.grounding_ex_taste_tag, R.string.grounding_ex_taste_title,
                        R.string.grounding_ex_taste_question, "\uD83D\uDC45")
        );
    }
}
