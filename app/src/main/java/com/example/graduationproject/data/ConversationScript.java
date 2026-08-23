package com.example.graduationproject.data;

import com.example.graduationproject.R;
import com.example.graduationproject.models.ScriptNode;
import com.example.graduationproject.models.ScriptReply;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Direct port of the SCRIPT object from the JSX - same node ids, same bot
 * text, same reply labels/targets, same card assignments, same freeText
 * flags. Nothing about the conversation logic has been altered.
 */
public class ConversationScript {

    public static final String NODE_START = "start";
    public static final String NODE_FREE_REPLY = "freeReply";

    public static final Map<String, ScriptNode> NODES = new LinkedHashMap<>();

    static {
        NODES.put("start", new ScriptNode(
                R.string.chat_node_start_bot,
                null,
                Arrays.asList(
                        new ScriptReply(R.string.chat_reply_start_anxious, "anxious"),
                        new ScriptReply(R.string.chat_reply_start_distress, "distress"),
                        new ScriptReply(R.string.chat_reply_start_fine, "fine")
                ),
                false
        ));

        NODES.put("anxious", new ScriptNode(
                R.string.chat_node_anxious_bot,
                null,
                Arrays.asList(
                        new ScriptReply(R.string.chat_reply_anxious_breathing, "breathingOffer"),
                        new ScriptReply(R.string.chat_reply_anxious_later, "later"),
                        new ScriptReply(R.string.chat_reply_anxious_elaborate, "elaborate")
                ),
                false
        ));

        NODES.put("breathingOffer", new ScriptNode(
                R.string.chat_node_breathingOffer_bot,
                "breathing",
                Arrays.asList(
                        new ScriptReply(R.string.chat_reply_breathingOffer_article, "article"),
                        new ScriptReply(R.string.chat_reply_breathingOffer_closing, "closing")
                ),
                false
        ));

        NODES.put("article", new ScriptNode(
                R.string.chat_node_article_bot,
                "article",
                Arrays.asList(new ScriptReply(R.string.chat_reply_article_closing, "closing")),
                false
        ));

        NODES.put("later", new ScriptNode(
                R.string.chat_node_later_bot,
                null,
                Arrays.asList(new ScriptReply(R.string.chat_reply_later_closing, "closing")),
                false
        ));

        NODES.put("elaborate", new ScriptNode(
                R.string.chat_node_elaborate_bot,
                null,
                null,
                true
        ));

        NODES.put("distress", new ScriptNode(
                R.string.chat_node_distress_bot,
                null,
                Arrays.asList(
                        new ScriptReply(R.string.chat_reply_distress_yes, "breathingOffer"),
                        new ScriptReply(R.string.chat_reply_distress_maybe, "dhikrOffer"),
                        new ScriptReply(R.string.chat_reply_distress_no, "later")
                ),
                false
        ));

        NODES.put("dhikrOffer", new ScriptNode(
                R.string.chat_node_dhikrOffer_bot,
                "dhikr",
                Arrays.asList(new ScriptReply(R.string.chat_reply_dhikrOffer_closing, "closing")),
                false
        ));

        NODES.put("fine", new ScriptNode(
                R.string.chat_node_fine_bot,
                null,
                null,
                true
        ));

        NODES.put("closing", new ScriptNode(
                R.string.chat_node_closing_bot,
                null,
                Arrays.asList(),
                false
        ));

        NODES.put("freeReply", new ScriptNode(
                R.string.chat_node_freeReply_bot,
                null,
                Arrays.asList(
                        new ScriptReply(R.string.chat_reply_freeReply_breathing, "breathingOffer"),
                        new ScriptReply(R.string.chat_reply_freeReply_closing, "closing")
                ),
                false
        ));
    }
}
