package com.example.studypal;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Subject;
import com.amplifyframework.datastore.generated.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class home_fragment extends Fragment {

    private static final Logger log = LoggerFactory.getLogger(home_fragment.class);

    Button addSubject, addTopic;
    EditText subjectName, topicName;
    Spinner spinner;
    DatePicker datePicker;
    RadioGroup radioProgress, radioImportance;
    RadioButton radioNotStarted, radioStarted, radioCompleted, radioLow, radioModerate, radioHigh;

    private String currentUserId = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addSubject = view.findViewById(R.id.add_subject);
        addTopic = view.findViewById(R.id.add_topic);
        subjectName = view.findViewById(R.id.subject_name);
        topicName = view.findViewById(R.id.topic_name);
        spinner = view.findViewById(R.id.spinner_subjects);
        datePicker = view.findViewById(R.id.deadline);
        radioProgress = view.findViewById(R.id.radioProgress);
        radioImportance = view.findViewById(R.id.radioImportance);
        radioNotStarted = view.findViewById(R.id.radio_Not_Started);
        radioStarted = view.findViewById(R.id.radio_Started);
        radioCompleted = view.findViewById(R.id.radio_Completed);
        radioLow = view.findViewById(R.id.radio_Low);
        radioModerate = view.findViewById(R.id.radio_Moderate);
        radioHigh = view.findViewById(R.id.radio_High);
        addSubject.setEnabled(false);
        loadSubjectsIntoSpinner();

        // Fetch the Cognito "sub" which is required for owner field
        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    for (AuthUserAttribute attr : attributes) {
                        if (attr.getKey().getKeyString().equals("sub")) {
                            currentUserId = attr.getValue();
                            Log.i("AUTH", "Current user sub: " + currentUserId);
                            break;
                        }
                    }
                    requireActivity().runOnUiThread(() -> addSubject.setEnabled(true));
                },
                error -> Log.e("AUTH", "Failed to fetch user attributes", error)
        );

        addSubject.setOnClickListener(v -> {
            String enteredSubjectName = subjectName.getText().toString().trim();
            if (enteredSubjectName.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter a subject name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentUserId == null) {
                Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
                return;
            }

            Subject subject = Subject.builder()
                    .name(enteredSubjectName)
                    .owner(currentUserId)
                    .build();

            Amplify.DataStore.save(subject,
                    success -> {
                        Log.i("SAVE", "Saved subject: " + success.item().getName());
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Subject saved", Toast.LENGTH_SHORT).show();
                            loadSubjectsIntoSpinner();
                        });
                    },
                    error -> Log.e("SAVE", "Error saving subject", error)
            );
        });

        addTopic.setOnClickListener(v -> {
            String topicText = topicName.getText().toString().trim();
            if (topicText.isEmpty()) {
                Toast.makeText(getActivity(), "Enter topic name", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedSubject = (String) spinner.getSelectedItem();
            if (selectedSubject == null) {
                Toast.makeText(getActivity(), "Select a subject", Toast.LENGTH_SHORT).show();
                return;
            }

            int progressId = radioProgress.getCheckedRadioButtonId();
            int importanceId = radioImportance.getCheckedRadioButtonId();

            if (progressId == -1 || importanceId == -1) {
                Toast.makeText(getActivity(), "Select progress and importance", Toast.LENGTH_SHORT).show();
                return;
            }

            String progress = ((RadioButton) radioProgress.findViewById(progressId)).getText().toString();
            String importance = ((RadioButton) radioImportance.findViewById(importanceId)).getText().toString();
            String deadline = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();

            if (currentUserId == null) {
                Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
                return;
            }

            Amplify.DataStore.query(
                    Subject.class,
                    Subject.NAME.eq(selectedSubject),
                    subjects -> {
                        if (subjects.hasNext()) {
                            Subject fullSubject = subjects.next();

                            Topic topic = Topic.builder()
                                    .subjectId(fullSubject.getId())
                                    .topicName(topicText)
                                    .owner(currentUserId)
                                    .deadline(deadline)
                                    .progress(progress)
                                    .importance(importance)
                                    .build();

                            Amplify.DataStore.save(
                                    topic,
                                    result -> {
                                        Log.i("SAVE", "Saved topic: " + result.item().getId());
                                        requireActivity().runOnUiThread(this::setNull);
                                        scheduleRepeatingTopicNotification(getContext(), topic);

                                    },
                                    error -> Log.e("SAVE", "Error saving topic", error)
                            );

                        } else {
                            Log.e("TOPIC", "Subject not found for: " + selectedSubject);
                        }
                    },
                    error -> Log.e("QUERY", "Failed to find subject", error)
            );



        });

    }
    private long getIntervalHoursFromImportance(String importance) {
        switch (importance.toLowerCase()) {
            case "high":
                return 8; // every 8 hours
            case "moderate":
                return 12; // every 12 hours
            case "low":
                return 24; // every day
            default:
                return 24;
        }
    }

    private String getMessageFromStatus(String status, String title) {
        switch (status.toLowerCase()) {
            case "not started":
                return "📌 You haven’t started \"" + title + "\" yet. Let’s begin!";
            case "started":
                return "🧠 Keep working on \"" + title + "\" — you’re doing great!";
            case "completed":
                return "🔁 Time to revise \"" + title + "\" and refresh your memory.";
            default:
                return "📚 Reminder for topic: \"" + title + "\".";
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    public void scheduleRepeatingTopicNotification(Context context, Topic topic) {
        long intervalHours = getIntervalHoursFromImportance(topic.getImportance());

        if (intervalHours < 1) intervalHours = 1; // fallback

        Data inputData = new Data.Builder()
                .putString("title", topic.getTopicName())
                .putString("message", getMessageFromStatus(topic.getProgress(), topic.getTopicName()))
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                NotificationWorker.class,
                intervalHours, TimeUnit.HOURS
        )
                .setInputData(inputData)
                .addTag("topic-" + topic.getId()) // useful for cancellation
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "topic-" + topic.getId(),
                ExistingPeriodicWorkPolicy.REPLACE,
                request
        );
    }



    private void setNull(){
        Calendar calendar=Calendar.getInstance();
        topicName.setText("");
        radioProgress.clearCheck();
        radioImportance.clearCheck();
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        Toast.makeText(getActivity(), "Topic saved", Toast.LENGTH_SHORT).show();
    }
    private void loadSubjectsIntoSpinner() {
        Amplify.DataStore.query(Subject.class,
                subjects -> {
                    List<String> subjectNames = new ArrayList<>();
                    while (subjects.hasNext()) {
                        subjectNames.add(subjects.next().getName());
                    }
                    requireActivity().runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_item, subjectNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    });
                },
                error -> Log.e("SPINNER", "Failed to load subjects", error));
    }
}
