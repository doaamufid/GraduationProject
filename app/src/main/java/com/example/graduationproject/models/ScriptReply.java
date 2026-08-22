package com.example.graduationproject.models;

/** Mirrors one entry of a node's `replies` array in SCRIPT. */
public class ScriptReply {
    public final int labelResId;
    public final String next;

    public ScriptReply(int labelResId, String next) {
        this.labelResId = labelResId;
        this.next = next;
    }
}
