package com.example.graduationproject.models.profile;

import java.util.Date;

public class FutureMessage {
    public final long id;
    public final String text;
    public final Date targetDate;
    public final boolean arrived;

    public FutureMessage(long id, String text, Date targetDate, boolean arrived) {
        this.id = id;
        this.text = text;
        this.targetDate = targetDate;
        this.arrived = arrived;
    }
}
