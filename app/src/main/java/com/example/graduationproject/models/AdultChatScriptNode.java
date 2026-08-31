package com.example.graduationproject.models;

import java.util.ArrayList;
import java.util.List;

/** One node of the conversation graph: bot text + optional quick replies/card/free-text flag. */
public class AdultChatScriptNode {
    public final String bot;
    public final List<AdultChatReply> replies = new ArrayList<>();
    public boolean freeText = false;
    public AdultChatCardData card;

    public AdultChatScriptNode(String bot) {
        this.bot = bot;
    }

    public AdultChatScriptNode reply(String label, String next) {
        replies.add(new AdultChatReply(label, next));
        return this;
    }

    public AdultChatScriptNode freeText(boolean v) {
        this.freeText = v;
        return this;
    }

    public AdultChatScriptNode card(AdultChatCardData c) {
        this.card = c;
        return this;
    }
}
