package com.example.studypal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(Message message);

    @Query("SELECT * FROM messages")
    List<Message> getAllMessages();

    @Query("DELETE FROM messages")
    void clearMessages();
}
