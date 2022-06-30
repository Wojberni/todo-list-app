package com.example.todolistapp.notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class ScheduleNotification {

    private static ScheduleNotification INSTANCE;

    private ScheduleNotification() {
    }

    public static ScheduleNotification getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScheduleNotification();
        }
        return INSTANCE;
    }

    private List<NotificationIntent> notificationIntentList = new ArrayList<>();
    private Integer notificationOffset = 0;

    private static final String alarmTitleExtra = "alarmTitle";
    private static final String alarmDescriptionExtra = "alarmDescription";
    private static final String alarmIdExtra = "alarmId";

    private static final String channelId = "10";
    private static final String channelName = "channel10";
    private static final String channelDescription = "channel10Description";

    public void createNotification(Context context, int notificationId, String title, String description, Date deadlineDate){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra(alarmTitleExtra, title);
        alarmIntent.putExtra(alarmDescriptionExtra, description);
        alarmIntent.putExtra(alarmIdExtra, notificationId);
        deadlineDate = subtractMinutes(deadlineDate, notificationOffset);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, deadlineDate.getTime(), pendingIntent);
        notificationIntentList.add(new NotificationIntent(pendingIntent, notificationId));
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

    public void cancelNotification(Context context, int notificationId){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        notificationIntentList.stream()
                .filter(intent -> intent.getNotificationId() == notificationId)
                .findAny()
                .ifPresent(notificationIntent -> alarmManager.cancel(notificationIntent.getPendingIntent()));
    }

    public Integer getNotificationOffset() {
        return notificationOffset;
    }

    public void setNotificationOffset(Integer notificationOffset) {
        this.notificationOffset = notificationOffset;
    }

    private Date subtractMinutes(Date date, Integer minutes){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -minutes);
        return cal.getTime();
    }
}
