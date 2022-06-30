package com.example.todolistapp.notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Date;

public class ScheduleNotification {

    static final String alarmTitleExtra = "alarmTitle";
    static final String alarmDescriptionExtra = "alarmDescription";
    static final String alarmIdExtra = "alarmId";

    static final String channelId = "10";
    static final String channelName = "channel10";
    static final String channelDescription = "channel10Description";

    public static void createNotification(Context context, int notificationId, String title, String description, Date deadlineDate){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra(alarmTitleExtra, title);
        alarmIntent.putExtra(alarmDescriptionExtra, description);
        alarmIntent.putExtra(alarmIdExtra, notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, deadlineDate.getTime(), pendingIntent);
    }

    public static void createNotificationsChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =
                    new NotificationChannel(channelId, channelName, importance);

            channel.setDescription(channelDescription);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void cancelNotification(Context context, int notificationId){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }


}
