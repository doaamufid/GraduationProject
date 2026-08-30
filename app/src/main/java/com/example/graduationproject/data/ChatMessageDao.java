package com.example.graduationproject.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ChatMessageDao {

    @Insert
    long insert(ChatMessageEntity entity);

    @Query("SELECT * FROM chat_messages ORDER BY id ASC")
    List<ChatMessageEntity> getAll();

    @Query("DELETE FROM chat_messages WHERE timestamp < :thresholdMillis")
    void deleteOlderThan(long thresholdMillis);

    @Query("DELETE FROM chat_messages")
    void deleteAll();
}