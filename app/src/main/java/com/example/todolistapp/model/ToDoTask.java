package com.example.todolistapp.model;

import java.io.Serializable;
import java.util.Date;

public class ToDoTask implements Serializable {
    private String title;
    private String description;
    private Date creationDate;
    private Date deadlineDate;
    private boolean done;
    private boolean notification;
    private boolean attachment;

    public ToDoTask(String title, String description,
                    Date creationDate, Date deadlineDate,
                    boolean done, boolean notification,
                    boolean attachment) {
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.deadlineDate = deadlineDate;
        this.done = done;
        this.notification = notification;
        this.attachment = attachment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAttachment() {
        return attachment;
    }

    public void setAttachment(boolean attachment) {
        this.attachment = attachment;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}
