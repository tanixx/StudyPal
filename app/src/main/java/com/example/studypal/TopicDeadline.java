package com.example.studypal;

import java.util.Date;

public class TopicDeadline {
    private String title;
    private Date deadline;

    public TopicDeadline(String title, Date deadline) {
        this.title = title;
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public Date getDeadline() {
        return deadline;
    }
}
