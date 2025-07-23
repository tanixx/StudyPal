package com.example.studypal;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString("title");
        String message = getInputData().getString("message");

        showNotification(title, message);
        return Result.success();
    }

    @SuppressLint("MissingPermission")
    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "reminder_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}

