package com.example.graduationproject.models;

public class ChatItem {
    public static final int TYPE_AI_MESSAGE = 1;
    public static final int TYPE_AI_SUGGESTION = 2;

    private int viewType;
    private String title;
    private String subTitle;
    private String messageText;
    private String time;

    public ChatItem(int viewType, String messageText, String time) {
        this.viewType = viewType;
        this.messageText = messageText;
        this.time = time;
    }

    public ChatItem(int viewType, String title, String subTitle, String time) {
        this.viewType = viewType;
        this.title = title;
        this.subTitle = subTitle;
        this.time = time;
    }

    public int getViewType() { return viewType; }
    public String getTitle() { return title; }
    public String getSubTitle() { return subTitle; }
    public String getMessageText() { return messageText; }
    public String getTime() { return time; }
}
