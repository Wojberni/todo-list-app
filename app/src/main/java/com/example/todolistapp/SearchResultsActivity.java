package com.example.todolistapp;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolistapp.room.TodoDao;
import com.example.todolistapp.room.TodoDatabase;
import com.example.todolistapp.room.TodoEntity;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private TodoDao todoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
        TodoDatabase todoDatabase = TodoDatabase.getInstance(this);
        todoDao = todoDatabase.getTodoDao();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            List<TodoEntity> searchResults = todoDao.getAllTodosBySearchTerm(query);
        }
    }


}
