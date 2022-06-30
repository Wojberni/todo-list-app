package com.example.todolistapp.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.todolistapp.MainActivity;
import com.example.todolistapp.R;


public class AlarmReceiver extends BroadcastReceiver {

    static final String channelId = "10";
    static final String alarmTitleExtra = "alarmTitle";
    static final String alarmDescriptionExtra = "alarmDescription";
    static final String alarmIdExtra = "alarmId";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = intent.getStringExtra(alarmTitleExtra);
        String description = intent.getStringExtra(alarmDescriptionExtra);
        int notificationId = intent.getIntExtra(alarmIdExtra, 0);
        createNotification(context, title, description, notificationManager, notificationId);
    }

    private void createNotification(Context context, String title, String description,
                                    NotificationManager notificationManager, int notificationId) {

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
}
