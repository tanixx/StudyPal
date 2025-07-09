package com.example.studypal;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amplifyframework.datastore.generated.model.Topic;

import com.amplifyframework.core.Amplify;
import com.example.studypal.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Deadline extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TopicAdapter adapter;
    private List<TopicDeadline> topicDeadlineList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline);

        recyclerView = findViewById(R.id.recyclerViewDeadlines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TopicAdapter(topicDeadlineList);
        recyclerView.setAdapter(adapter);

        fetchTopicsFromDataStore();
    }

    private void fetchTopicsFromDataStore() {
        Amplify.DataStore.query(Topic.class,
                topics -> {
                    while (topics.hasNext()) {
                        Topic topic = topics.next();
                        if (topic.getDeadline() != null && !topic.getDeadline().isEmpty()) {
                            Date deadline = parseDeadline(topic.getDeadline());
                            if (deadline != null) {
                                TopicDeadline td = new TopicDeadline(topic.getTopicName(), deadline);
                                runOnUiThread(() -> {
                                    topicDeadlineList.add(td);
                                    adapter.notifyItemInserted(topicDeadlineList.size() - 1);
                                });
                            }
                        }
                    }
                },
                failure -> runOnUiThread(() -> Toast.makeText(this, "Failed to fetch topics", Toast.LENGTH_SHORT).show())
        );
    }

    private Date parseDeadline(String deadlineStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            return sdf.parse(deadlineStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
