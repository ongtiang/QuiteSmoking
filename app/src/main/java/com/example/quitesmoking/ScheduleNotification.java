package com.example.quitesmoking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ScheduleNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "quiteSmokingReminder")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Quite Smoking")
                .setContentText("Gentle reminder to quite smoking")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(200,builder.build());
    }
}

