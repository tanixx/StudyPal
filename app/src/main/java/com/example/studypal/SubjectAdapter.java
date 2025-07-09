package com.example.studypal;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.ImportanceLevel;
import com.amplifyframework.datastore.generated.model.Subject;
import com.amplifyframework.datastore.generated.model.Topic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    private List<SubjectWithTopics> subjectWithTopicsList;

    public SubjectAdapter(List<SubjectWithTopics> subjectWithTopicsList) {
        this.subjectWithTopicsList = subjectWithTopicsList;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_view, parent, false);
        return new SubjectViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        SubjectWithTopics item = subjectWithTopicsList.get(position);
        Subject subject = item.getSubject();
        List<Topic> topics = item.getTopicList();

        holder.subjectNameTextView.setText(subject.getName());
        holder.username.setText("Username: "); // update if you store username
        holder.email.setText("Email: "); // update if you store email

        // Dummy progress
        int totalTopics = item.getTotalTopics();
        int completedTopics = item.getCompletedTopics();
        if(totalTopics!=0) {
            int completionPercentage = (completedTopics * 100) / totalTopics;
            holder.progressTextView.setText("Progress: " + completionPercentage + "%");
            holder.progressBar.setProgress(completionPercentage);
        }
        else{
            holder.progressTextView.setText("Progress: 0%");
            holder.progressBar.setProgress(0);
        }
        // Expand/collapse
        holder.expandButton.setOnClickListener(v -> {
            if (holder.topicContainer.getVisibility() == View.GONE) {
                holder.topicContainer.setVisibility(View.VISIBLE);
                holder.expandButton.setText("Hide Topics");
            } else {
                holder.topicContainer.setVisibility(View.GONE);
                holder.expandButton.setText("Show Topics");
            }


        });
        //delete topic

        // delete subject
        holder.deleteSubject.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Subject")
                    .setMessage("Are you sure you want to delete \"" + subject.getName() + "\" and all its topics? This cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        final int p = position;

                        // Step 1: Delete related topics
                        Amplify.DataStore.query(Topic.class,
                                topic -> {
                                    List<Topic> topicsToDelete = new ArrayList<>();
                                    while (topic.hasNext()) {
                                        Topic t = topic.next();
                                        if (t.getSubjectId().equals(subject.getId())) {
                                            topicsToDelete.add(t);
                                        }
                                    }

                                    // Delete topics one by one
                                    for (Topic t : topicsToDelete) {
                                        Amplify.DataStore.delete(t,
                                                deleted -> {
                                                    Log.i("DeleteSubject", "Deleted topic: " + t.getTopicName());
                                                    cancelRepeatingTopicNotification(holder.itemView.getContext(), t);
                                                },
                                                error -> Log.e("DeleteSubject", "Failed to delete topic: " + t.getTopicName(), error)
                                        );
                                    }

                                    // Step 2: Delete the subject
                                    Amplify.DataStore.delete(subject,
                                            deleted -> {
                                                Log.i("DeleteSubject", "Deleted subject: " + subject.getName());

                                                new Handler(Looper.getMainLooper()).post(() -> {
                                                    Toast.makeText(v.getContext(), "Subject and topics deleted", Toast.LENGTH_SHORT).show();
                                                    subjectWithTopicsList.remove(p);
                                                    notifyItemRemoved(p);
                                                    notifyItemRangeChanged(p, subjectWithTopicsList.size());
                                                });
                                            },
                                            error -> {
                                                Log.e("DeleteSubject", "Failed to delete subject", error);
                                                new Handler(Looper.getMainLooper()).post(() ->
                                                        Toast.makeText(v.getContext(), "Failed to delete subject", Toast.LENGTH_SHORT).show());
                                            });
                                },
                                error -> {
                                    Log.e("DeleteSubject", "Failed to query topics", error);
                                    new Handler(Looper.getMainLooper()).post(() ->
                                            Toast.makeText(v.getContext(), "Delete failed: Could not find topics", Toast.LENGTH_SHORT).show());
                                }
                        );
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });



        holder.topicContainer.removeAllViews();
        for (int i = 0; i < topics.size(); i++) {
            Topic topic = topics.get(i);
            int topicIndex = i;

            View topicView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_subject_topic_card, holder.topicContainer, false);

            TextView tvTopic = topicView.findViewById(R.id.tvTopic);
            TextView tvDeadline = topicView.findViewById(R.id.tvDeadline);
            TextView tvProgress = topicView.findViewById(R.id.tvProgress);
            TextView tvImportance = topicView.findViewById(R.id.tvImportance);
            ImageButton deleteTopicBtn = topicView.findViewById(R.id.deleteTopic);
            ImageButton editDeadlineBtn = topicView.findViewById(R.id.editDeadline);
            ImageButton editProgressBtn = topicView.findViewById(R.id.editProgress);
            ImageButton editImportanceBtn = topicView.findViewById(R.id.editImportance);

            tvTopic.setText("Topic: " + topic.getTopicName());
            tvDeadline.setText("Deadline: " + topic.getDeadline());
            tvProgress.setText("Progress: " + topic.getProgress() + "%");
            tvImportance.setText("Importance: " + topic.getImportance());


            editImportanceBtn.setOnClickListener(v -> {
                final String[] importance = {"Low", "Moderate", "High"};
                int currentSelection = 0;
                String currentImportance = topic.getImportance();

                // Find the current selection index
                for (int j = 0; j < importance.length; j++) {
                    if (importance[j].equalsIgnoreCase(currentImportance)) {
                        currentSelection = j;
                        break;
                    }
                }

                new AlertDialog.Builder(v.getContext())
                        .setTitle("Update Topic Importance")
                        .setSingleChoiceItems(importance, currentSelection, null)
                        .setPositiveButton("Update", (dialog, which) -> {
                            AlertDialog alertDialog = (AlertDialog) dialog;
                            int selectedPosition = alertDialog.getListView().getCheckedItemPosition();
                            String selectedImportance = importance[selectedPosition];

                            Topic updatedTopic = topic.copyOfBuilder()
                                    .importance(selectedImportance) // ✅ FIXED: Changed from `.progress(...)`
                                    .build();

                            Amplify.DataStore.save(updatedTopic,
                                    success -> {
                                        Log.i("EditImportance", "Importance updated to " + selectedImportance);
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            ((TextView) topicView.findViewById(R.id.tvImportance)).setText("Importance: " + selectedImportance);
                                            Toast.makeText(v.getContext(), "Importance updated", Toast.LENGTH_SHORT).show();
                                        });
                                    },
                                    error -> {
                                        Log.e("EditImportance", "Failed to update importance", error);
                                        new Handler(Looper.getMainLooper()).post(() ->
                                                Toast.makeText(v.getContext(), "Update failed", Toast.LENGTH_SHORT).show());
                                    });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });


            editProgressBtn.setOnClickListener(v -> {
                final String[] statuses = {"Not Started", "Started", "Completed"};
                int currentSelection = 0;

                // Optional: Set current selection index based on topic.getStatus()
                String currentStatus = topic.getProgress();
                for (int j = 0; j < statuses.length; j++) {
                    if (statuses[j].equalsIgnoreCase(currentStatus)) {
                        currentSelection = j;
                        break;
                    }
                }

                new AlertDialog.Builder(v.getContext())
                        .setTitle("Update Topic Status")
                        .setSingleChoiceItems(statuses, currentSelection, null)
                        .setPositiveButton("Update", (dialog, which) -> {
                            AlertDialog alertDialog = (AlertDialog) dialog;
                            int selectedPosition = alertDialog.getListView().getCheckedItemPosition();
                            String selectedStatus = statuses[selectedPosition];

                            Topic updatedTopic = topic.copyOfBuilder()
                                    .progress(selectedStatus)
                                    .build();

                            Amplify.DataStore.save(updatedTopic,
                                    success -> {
                                        Log.i("EditStatus", "Status updated to " + selectedStatus);
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            ((TextView) topicView.findViewById(R.id.tvProgress)).setText("Status: " + selectedStatus);
                                            Toast.makeText(v.getContext(), "Status updated", Toast.LENGTH_SHORT).show();
                                        });
                                    },
                                    error -> {
                                        Log.e("EditStatus", "Failed to update status", error);
                                        new Handler(Looper.getMainLooper()).post(() ->
                                                Toast.makeText(v.getContext(), "Update failed", Toast.LENGTH_SHORT).show());
                                    });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });



            editDeadlineBtn.setOnClickListener(v -> {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        holder.itemView.getContext(),
                        (view, year, month, dayOfMonth) -> {
                            // Format the selected date
                            String newDeadline = dayOfMonth + "/" + (month + 1) + "/" + year;

                            // Create updated topic
                            Topic updatedTopic = topic.copyOfBuilder()
                                    .deadline(newDeadline)
                                    .build();

                            // Update in DataStore
                            Amplify.DataStore.save(updatedTopic,
                                    success -> {
                                        Log.i("EditDeadline", "Deadline updated");
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            tvDeadline.setText("Deadline: " + newDeadline); // refresh view
                                            Toast.makeText(v.getContext(), "Deadline updated", Toast.LENGTH_SHORT).show();
                                        });
                                    },
                                    error -> {
                                        Log.e("EditDeadline", "Update failed", error);
                                        new Handler(Looper.getMainLooper()).post(() ->
                                                Toast.makeText(v.getContext(), "Failed to update deadline", Toast.LENGTH_SHORT).show());
                                    }
                            );
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

                datePickerDialog.show();
            });



            deleteTopicBtn.setOnClickListener(v -> {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Delete Topic")
                        .setMessage("Are you sure you want to delete \"" + topic.getTopicName() + "\"? This cannot be undone.")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            Amplify.DataStore.delete(topic,
                                    deleted -> {
                                        Log.i("DeleteTopic", "Deleted topic: " + topic.getTopicName());

                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            Toast.makeText(v.getContext(), "Topic deleted", Toast.LENGTH_SHORT).show();
                                            topics.remove(topicIndex); // remove from topic list
                                            notifyItemChanged(holder.getAdapterPosition()); // refresh the card
                                        });
                                        cancelRepeatingTopicNotification(holder.itemView.getContext(), topic);
                                    },
                                    error -> {
                                        Log.e("DeleteTopic", "Failed to delete topic", error);
                                        new Handler(Looper.getMainLooper()).post(() ->
                                                Toast.makeText(v.getContext(), "Delete failed", Toast.LENGTH_SHORT).show());
                                    }
                            );
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            });

            holder.topicContainer.addView(topicView);
        }

    }

    public void cancelRepeatingTopicNotification(Context context, Topic topic) {
        WorkManager.getInstance(context).cancelAllWorkByTag("topic-" + topic.getId());
    }


    private TextView createTopicView(View parent, String text) {
        TextView topic = new TextView(parent.getContext());
        topic.setText(text);
        topic.setTextSize(14f);
        topic.setPadding(16, 8, 0, 8);
        topic.setTextColor(parent.getResources().getColor(android.R.color.darker_gray));
        return topic;
    }

    @Override
    public int getItemCount() {
        return subjectWithTopicsList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectNameTextView, progressTextView, expandButton, username, email,subject, topic, deadline, progress, importance;
        ProgressBar progressBar;
        LinearLayout topicContainer;
        ImageButton deleteSubject, editDeadline, editProgress, editImportance,deleteTopic;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteTopic=itemView.findViewById(R.id.deleteTopic);
            editDeadline=itemView.findViewById(R.id.editDeadline);
            editProgress=itemView.findViewById(R.id.editProgress);
            editImportance=itemView.findViewById(R.id.editImportance);
            deleteSubject = itemView.findViewById(R.id.deleteSubject);
            subjectNameTextView = itemView.findViewById(R.id.subjectNameTextView);
            progressTextView = itemView.findViewById(R.id.progressTextView);
            expandButton = itemView.findViewById(R.id.expandButton);
            progressBar = itemView.findViewById(R.id.progressBar);
            topicContainer = itemView.findViewById(R.id.topicContainer);
            username = itemView.findViewById(R.id.tvUsername);
            email = itemView.findViewById(R.id.tvEmail);
            topic = itemView.findViewById(R.id.tvTopic);
            deadline = itemView.findViewById(R.id.tvDeadline);
            progress = itemView.findViewById(R.id.tvProgress);
            importance = itemView.findViewById(R.id.tvImportance);
        }
    }
}
