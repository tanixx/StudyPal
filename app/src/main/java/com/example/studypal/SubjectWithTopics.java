package com.example.studypal;

import com.amplifyframework.datastore.generated.model.Subject;
import com.amplifyframework.datastore.generated.model.Topic;

import java.util.List;

public class SubjectWithTopics {
    Subject subject;
    List<Topic> topicList;

    public SubjectWithTopics(Subject subject, List<Topic> topicList) {
        this.subject = subject;
        this.topicList = topicList;
    }
    public int getTotalTopics() {
        return topicList.size();
    }
    public int getCompletedTopics() {
        int count = 0;
        for (Topic topic : topicList) {
            if (topic.getProgress() != null && topic.getProgress().equalsIgnoreCase("completed")) {
                count++;
            }
        }
        return count;
    }
    public Subject getSubject() {
        return subject;
    }

    public List<Topic> getTopicList() {
        return topicList;
    }
}
