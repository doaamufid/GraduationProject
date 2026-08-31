package com.example.graduationproject.models;

/** A quick-reply chip option: label shown to the user + the script node it leads to. */
public class AdultChatReply {
    public final String label;
    public final String next;

    public AdultChatReply(String label, String next) {
        this.label = label;
        this.next = next;
    }
}
