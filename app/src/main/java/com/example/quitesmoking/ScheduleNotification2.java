package com.example.quitesmoking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ScheduleNotification2 extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "quiteSmokingReminder")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Goal Succeeded")
                .setContentText("Congratulations!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(201,builder.build());
    }
}