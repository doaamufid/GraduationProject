package com.example.graduationproject.models;

import java.util.Date;

/** Equivalent of one message object inside the `messages` state array. */
public class FutureMessage {
    public long id;
    public String text;
    public String createdLabel;
    public Date targetDate;
    public boolean arrived;

    public FutureMessage(long id, String text, String createdLabel, Date targetDate, boolean arrived) {
        this.id = id;
        this.text = text;
        this.createdLabel = createdLabel;
        this.targetDate = targetDate;
        this.arrived = arrived;
    }
}
