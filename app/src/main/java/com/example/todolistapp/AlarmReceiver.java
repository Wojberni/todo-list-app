package com.example.todolistapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;



public class AlarmReceiver extends BroadcastReceiver {

    static final int notificationId = 10;
    static final String channelId = "10";
    static final String channelName = "channel10";
    static final String channelDescription = "channel10Description";
    static final String alarmTitleExtra = "alarmTitle";
    static final String alarmDescriptionExtra = "alarmDescription";

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            createNotificationChannel(notificationManager);
            String title = intent.getStringExtra(alarmTitleExtra);
            String description = intent.getStringExtra(alarmDescriptionExtra);
            createNotification(context, title, description, notificationManager);
//        }

    }

    private void createNotification(Context context, String title, String description,
                                    NotificationManager notificationManager) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        notificationManager.notify(notificationId, notificationBuilder.build());

    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =
                    new NotificationChannel(channelId, channelName, importance);

            channel.setDescription(channelDescription);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
