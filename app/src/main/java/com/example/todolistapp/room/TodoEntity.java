package com.example.todolistapp.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "todo_table")
public class TodoEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String category;

    @ColumnInfo(name = "creation_date")
    private Date creationDate;
    @ColumnInfo(name = "deadline_date")
    private Date deadlineDate;

    private boolean done;
    private boolean notification;

    @ColumnInfo(name = "attachment")
    private String attachmentPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }
}
