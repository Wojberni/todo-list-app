package com.example.todolistapp.notifications;

import android.app.PendingIntent;

public class NotificationIntent {
    private PendingIntent pendingIntent;
    private int notificationId;

    public NotificationIntent(PendingIntent pendingIntent, int notificationId) {
        this.pendingIntent = pendingIntent;
        this.notificationId = notificationId;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
