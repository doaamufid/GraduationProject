package com.example.graduationproject.models;

import java.util.Collections;
import java.util.List;

/** Mirrors one node object inside the SCRIPT map in the JSX. */
public class ScriptNode {
    public final int botResId;
    public final String cardType;      // "breathing" | "dhikr" | "article" | null
    public final List<ScriptReply> replies;
    public final boolean freeText;

    public ScriptNode(int botResId, String cardType, List<ScriptReply> replies, boolean freeText) {
        this.botResId = botResId;
        this.cardType = cardType;
        this.replies = replies == null ? Collections.emptyList() : replies;
        this.freeText = freeText;
    }
}
