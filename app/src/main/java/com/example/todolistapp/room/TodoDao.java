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

    @Query("SELECT * FROM todo_table WHERE title LIKE :searchTerm OR description LIKE :searchTerm")
    List<TodoEntity> getAllTodosBySearchTerm(String searchTerm);

    @Query("SELECT * FROM todo_table WHERE NOT done")
    List<TodoEntity> getAllTodosNotDone();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertTodos(TodoEntity... todoEntities);

    @Update
    int updateTodos(TodoEntity... todoEntities);

    @Delete
    void deleteTodos(TodoEntity... todoEntities);
}
