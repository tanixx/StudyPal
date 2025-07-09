package com.example.studypal;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {
    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT = 1;

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String text;
    private int type;

    // Constructor
    public Message(String text, int type) {
        this.text = text;
        this.type = type;
    }

    // Getters
    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    // Room requires setter for auto-generated id
    public void setId(int id) {
        this.id = id;
    }
}
