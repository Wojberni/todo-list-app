package com.example.todolistapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {TodoEntity.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class TodoDatabase extends RoomDatabase {

    private static final String DB_NAME = "todo_db";
    private static volatile TodoDatabase instance;

    public static synchronized TodoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    public TodoDatabase() {}

    private static TodoDatabase create(final Context context) {
        return Room.databaseBuilder(context, TodoDatabase.class, DB_NAME).allowMainThreadQueries().build();
    }

    public abstract TodoDao getTodoDao();
}
