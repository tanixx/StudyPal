package com.example.studypal;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studypal.R;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<TopicDeadline> topicList;

    public TopicAdapter(List<TopicDeadline> topicList) {
        this.topicList = topicList;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic_deadline, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        TopicDeadline topic = topicList.get(position);
        holder.bind(topic);
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDeadline, tvCountdown;
        CountDownTimer countDownTimer;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTopicTitle);
            tvDeadline = itemView.findViewById(R.id.tvDeadlineTime);
            tvCountdown = itemView.findViewById(R.id.tvCountdown);
        }

        @SuppressLint("SetTextI18n")
        public void bind(TopicDeadline topic) {
            tvTitle.setText(topic.getTitle());
            tvDeadline.setText("Deadline: " + new java.text.SimpleDateFormat("dd/MM/yy").format(topic.getDeadline()));

            if (countDownTimer != null) countDownTimer.cancel();

            long millisLeft = topic.getDeadline().getTime() - System.currentTimeMillis();

            if (millisLeft > 0) {
                countDownTimer = new CountDownTimer(millisLeft, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24;
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                        String timeLeft = String.format("Time left: %02dd %02dh %02dm %02ds",
                                days, hours, minutes, seconds);
                        tvCountdown.setText(timeLeft);
                    }

                    @Override
                    public void onFinish() {
                        tvCountdown.setText("Deadline passed");
                    }
                }.start();
            } else {
                tvCountdown.setText("Deadline passed");
            }
        }
    }
}
