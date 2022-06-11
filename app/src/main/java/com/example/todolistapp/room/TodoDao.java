package com.example.todolistapp.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoDao {

    @Query("SELECT * FROM todo_table")
    List<TodoEntity> getAllTodos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTodos(TodoEntity... todoEntities);

    @Update
    void updateTodos(TodoEntity... todoEntities);

    @Delete
    void deleteTodos(TodoEntity... todoEntities);
}
