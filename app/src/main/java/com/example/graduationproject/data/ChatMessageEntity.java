package com.example.graduationproject.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_messages")
public class ChatMessageEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public boolean fromUser;
    public String text;          // null للرسائل الصوتية
    public String time;          // نفس النص المعروض بالفقاعة
    public long timestamp;       // millis - يستخدم لحذف القديم
    public String cardType;      // "breathing" | "dhikr" | "article" | null
    public String audioPath;     // null للرسائل النصية
    public int audioDurationSec;
}